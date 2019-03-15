package com.biorecorder.basechart;


import com.biorecorder.basechart.scales.Scale;

import java.util.Arrays;

/**
 * Created by galafit on 9/7/18.
 */
public class TraceDataManager {
    private final ChartData traceData;
    private DataProcessingConfig processingConfig;
    private boolean isEqualFrequencyGrouping; // group by equal points number or equal "height"
    private ChartData processedData;
    private Scale prevXScale;
    private int prevPixelsPerDataPoint = -1;

    private int[] sorter;


    public TraceDataManager(ChartData traceData) {
        this.traceData = traceData;
        setConfig(new DataProcessingConfig());
    }

    public void addDataAppendListener(DataAppendListener listener) {
        traceData.addDataAppendListener(listener);
    }

    public void setConfig(DataProcessingConfig processingConfig) {
        this.processingConfig = processingConfig;
        switch (processingConfig.getGroupingType()) {
            case EQUAL_POINTS_NUMBER:
                isEqualFrequencyGrouping = true;
                break;

            case EQUAL_INTERVALS:
                isEqualFrequencyGrouping = false;
                break;

            case AUTO:
                if(traceData.isColumnRegular(0)) {
                    isEqualFrequencyGrouping = true;
                } else {
                    isEqualFrequencyGrouping = false;
                }
                break;

            default:
                isEqualFrequencyGrouping = true;
                break;
        }
        processedData = traceData;
        prevXScale = null;
    }

    public Range getFullXMinMax() {
        if(traceData.columnCount() == 0) {
            return null;
        }
        return traceData.getColumnMinMax(0);
    }

    public double getBestExtent(int drawingAreaWidth, int markSize) {
        if (traceData.rowCount() > 1) {
            double traceExtent = getDataAvgStep(traceData) * drawingAreaWidth;
            if(markSize > 0) {
                traceExtent = traceExtent / markSize;
            }
            return traceExtent;
        }
        return 0;
    }

    public int nearest(double xValue) {
        // "lazy" sorting solo when "nearest" is called
        if (sorter == null) {
            if (!processedData.isColumnIncreasing(0)) {
                sorter = processedData.sortedIndices(0);
                System.out.println("sort");
            }
        }

        int nearest = processedData.bisect(0, xValue, sorter);

        if (nearest >= processedData.rowCount()) {
            nearest = processedData.rowCount() - 1;
        }

        int nearest_prev = nearest;
        if (nearest > 0){
            nearest_prev = nearest - 1;
        }

        if(sorter != null) {
            nearest = sorter[nearest];
            nearest_prev = sorter[nearest_prev];
        }
        if(nearest != nearest_prev) {
            if(Math.abs(processedData.getValue(nearest_prev, 0) - xValue) < Math.abs(processedData.getValue(nearest, 0) - xValue)) {
                nearest = nearest_prev;
            }
        }

        return nearest;
    }

    private boolean isDataProcessingEnabled() {
        if((!processingConfig.isCropEnabled() && !processingConfig.isGroupEnabled())
                || traceData.rowCount() <= 1
                || traceData.columnCount() == 0
                || !traceData.isColumnIncreasing(0) ) // if data not sorted (not increasing)
        {
            // No processing
            return false;
        }
        return true;
    }

    public ChartData getData(Scale xScale, int markSize) {
        int pixelsPerDataPoint = 1;
        if (markSize > 0) {
            pixelsPerDataPoint = markSize;
        }
        if(!isProcessedDataOk(xScale, pixelsPerDataPoint)) {
            processedData = processData(xScale, pixelsPerDataPoint);
            prevXScale = xScale.copy();
            prevPixelsPerDataPoint = pixelsPerDataPoint;
        }
        return processedData;
    }

    private boolean isProcessedDataOk(Scale xScale, int pixelsPerDataPoint) {
        if(prevPixelsPerDataPoint != pixelsPerDataPoint) {
            return false;
        }
        if(prevXScale == null) {
            return false;
        }
        if(!prevXScale.getClass().equals(xScale.getClass())) {
           return false;
        }

        if(!Arrays.equals(prevXScale.getDomain(), xScale.getDomain())) {
           return false;
        }

        int prevLength = length(prevXScale);
        int length = length(xScale);
        if(prevLength == 0 || length == 0) {
            return false;
        }

        if(Math.abs(prevLength - length) * 100 / length > processingConfig.getLengthChangeMax()) {
            return false;
        }

        return true;
    }

    private int length(Scale scale) {
        double[] range = scale.getRange();
        return (int) Math.abs(range[range.length - 1] - range[0]);
    }


    public ChartData processData(Scale xScale, int pixelsPerDataPoint) {
        if(!isDataProcessingEnabled()){
            // No processing
            return traceData;
        }

        Double xMin = xScale.getDomain()[0];
        Double xMax = xScale.getDomain()[1];
        double xStart =  xScale.getRange()[0];
        double xEnd =  xScale.getRange()[1];

        Range dataMinMax = traceData.getColumnMinMax(0);
        double dataStart = xScale.scale(dataMinMax.getMin());
        double dataEnd = xScale.scale(dataMinMax.getMax());


        int drawingAreaWidth = 0;
        Range intersection = Range.intersect(new Range(xStart, xEnd), new Range(dataStart, dataEnd));
        if(intersection != null) {
            drawingAreaWidth = (int)intersection.length();
        }
        Range minMax = Range.intersect(dataMinMax, new Range(xMin, xMax));


        if(drawingAreaWidth < 1) {
            return traceData.view(0, 0);
        }

        // calculate best grouping interval
        double bestInterval = minMax.length() * pixelsPerDataPoint / drawingAreaWidth;
        int pointsInGroup = groupIntervalToPointsNumber(traceData, bestInterval);
        // adjust interval to int number of pointsInGroup
        bestInterval = pointsNumberToGroupInterval(traceData, pointsInGroup);

        double groupInterval = bestInterval;

        if(processingConfig.isGroupEnabled()  && pointsInGroup > 1) {

            // if available intervals are specified we choose the interval among the available ones
            double[] availableIntervals = processingConfig.getGroupIntervals();
            if(availableIntervals != null) {
                for (int i = 0; i < availableIntervals.length; i++) {
                    if(availableIntervals[i] >= bestInterval) {
                        groupInterval = availableIntervals[i];
                        break;
                    }
                }
                if(groupInterval == 0) {
                    groupInterval = availableIntervals[availableIntervals.length - 1];
                }
                pointsInGroup = groupIntervalToPointsNumber(traceData, groupInterval);
            }
         }


        // if crop enabled
        if(processingConfig.isCropEnabled()) {
            int cropShoulder = processingConfig.getCropShoulder() * pointsInGroup;

            int minIndex = 0;
            if(dataMinMax.getMin() < xMin) {
                minIndex = traceData.bisect(0, minMax.getMin(), null) - cropShoulder;
            }

            int maxIndex = traceData.rowCount() - 1;
            if(dataMinMax.getMax() > xMax) {
                maxIndex = traceData.bisect(0, minMax.getMax(), null) + cropShoulder;
            }
            if(minIndex < 0) {
                minIndex = 0;
            }
            if(maxIndex >= traceData.rowCount()) {
                maxIndex = traceData.rowCount() - 1;
            }

            ChartData resultantData = traceData.view(minIndex, maxIndex - minIndex);
            if(pointsInGroup > 1) { // group only visible data
                if(isEqualFrequencyGrouping) {
                    resultantData = resultantData.resampleByEqualFrequency(pointsInGroup);
                } else {
                    resultantData = resultantData.resampleByEqualInterval(0, groupInterval);
                }
            }
            System.out.println(isEqualFrequencyGrouping+" processing with crop  points in group " + pointsInGroup + "  "+ groupInterval + "  " +resultantData.getColumnMinMax(0));
            return resultantData;
        }


        // if crop disabled (mostly preview case) we group ALL data
        // (so when it is possible we use already grouped data for further grouping)
        if(pointsInGroup > 1) {
            if(isEqualFrequencyGrouping) { // group by equal points number
                if(processedData != null) {
                    int pointsInGroup1 = groupIntervalToPointsNumber(processedData, groupInterval);
                    if(pointsInGroup1 > 1) {
                        ChartData regroupedData = processedData.resampleByEqualFrequency(pointsInGroup1);
                        // force "lazy" grouping
                        int rowCount = regroupedData.rowCount();
                        for (int i = 0; i < regroupedData.columnCount(); i++) {
                            regroupedData.getValue(rowCount - 1, i);
                        }
                        processedData.disableCaching();
                        processedData = regroupedData;
                    }
                } else {
                    processedData = traceData.resampleByEqualFrequency(pointsInGroup);
                }
                return processedData;
            } else {
                 return traceData.resampleByEqualInterval(0, groupInterval);
            }
        }

        // disabled crop and no grouping
        return traceData;
    }

    private double pointsNumberToGroupInterval(ChartData data, double pointsInGroup) {
        return pointsInGroup  * getDataAvgStep(data);
    }

    private int groupIntervalToPointsNumber(ChartData data, double groupInterval) {
        return (int) Math.round(groupInterval / getDataAvgStep(data));
    }

    double getDataAvgStep(ChartData data) {
        int dataSize = data.rowCount();
        return  (data.getValue(dataSize - 1, 0) - data.getValue(0, 0)) / (dataSize - 1);
    }
}
