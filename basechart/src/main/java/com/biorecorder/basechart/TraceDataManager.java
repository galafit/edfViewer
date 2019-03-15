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
    private boolean isWholeDataProcessed;
    private Scale prevXScale;
    private int prevPixelsPerDataPoint = -1;
    private int prevTraceDataSize;

    private int[] sorter;


    public TraceDataManager(ChartData traceData) {
        this.traceData = traceData;
        setConfig(new DataProcessingConfig());
    }

    public void appendData() {
       traceData.appendData();
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
        ChartData data = traceData;
        if(processedData != null) {
            data = processedData;
        }
        if (sorter == null) {
            if (!data.isColumnIncreasing(0)) {
                sorter = data.sortedIndices(0);
            }
        }

        int nearest = data.bisect(0, xValue, sorter);

        if (nearest >= data.rowCount()) {
            nearest = data.rowCount() - 1;
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
            if(Math.abs(data.getValue(nearest_prev, 0) - xValue) < Math.abs(data.getValue(nearest, 0) - xValue)) {
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
        if(!isDataProcessingEnabled()){ // No processing
            processedData = null;
            prevTraceDataSize = traceData.rowCount();
            return traceData;
        }

        int pixelsPerDataPoint = 1;
        if (markSize > 0) {
            pixelsPerDataPoint = markSize;
        }
        if(!isProcessedDataOk(xScale, pixelsPerDataPoint)) {
            processedData = processData(xScale, pixelsPerDataPoint);
            prevXScale = xScale.copy();
            prevPixelsPerDataPoint = pixelsPerDataPoint;
        }
        prevTraceDataSize = traceData.rowCount();
        return processedData;
    }

    private boolean isProcessedDataOk(Scale xScale, int pixelsPerDataPoint) {
        if(processedData == null) {
            return false;
        }
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

        if(traceData.rowCount() != prevTraceDataSize) {
            double[] domain = xScale.getDomain();
            Double xMax = domain[domain.length - 1];
            if(traceData.getValue(prevTraceDataSize - 1, 0)  < xMax) {
                return false;
            }
        }

        return true;
    }

    private int length(Scale scale) {
        double[] range = scale.getRange();
        return (int) Math.abs(range[range.length - 1] - range[0]);
    }


    public ChartData processData(Scale xScale, int pixelsPerDataPoint) {
        double[] range = xScale.getRange();
        double[] domain = xScale.getDomain();
        Double xMin = domain[0];
        Double xMax = domain[domain.length - 1];
        double xStart =  range[0];
        double xEnd =  range[range.length - 1];

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
        if(processingConfig.isCropEnabled() &&  (dataMinMax.getMin() < xMin || dataMinMax.getMax() > xMax)) {
            // we crop data first
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
            // group only visible data
            if(pointsInGroup > 1) {
                if(isEqualFrequencyGrouping) {
                    resultantData = resultantData.resampleByEqualFrequency(pointsInGroup);
                } else {
                    resultantData = resultantData.resampleByEqualInterval(0, groupInterval);
                }
            }
            isWholeDataProcessed = false;
            return resultantData;
        } else { // if crop disabled
            if(pointsInGroup > 1) {
                ChartData resultantData;
                if(isEqualFrequencyGrouping) { // group by equal points number
                    if(processedData != null && isWholeDataProcessed) {
                        // we try to use already grouped data as it is or for further grouping
                        processedData.appendData();
                        int pointsInGroup1 = groupIntervalToPointsNumber(processedData, groupInterval);
                        if(pointsInGroup1 > 1) {
                            ChartData regroupedData = processedData.resampleByEqualFrequency(pointsInGroup1);
                            // force "lazy" grouping
                            int rowCount = regroupedData.rowCount();
                            for (int i = 0; i < regroupedData.columnCount(); i++) {
                                regroupedData.getValue(rowCount - 1, i);
                            }
                            processedData.disableCaching();
                            System.out.println("regrouping "+pointsInGroup1);
                            resultantData = regroupedData;
                        } else {
                            System.out.println(" no reprocess");
                            resultantData = processedData;
                        }
                    } else {
                        System.out.println(" group by equal freq: "+pointsInGroup);
                        resultantData = traceData.resampleByEqualFrequency(pointsInGroup);
                    }
                } else {
                    System.out.println(" group by equal interval: "+groupInterval);
                    resultantData =  traceData.resampleByEqualInterval(0, groupInterval);
                }
                isWholeDataProcessed = true;
                return resultantData;
            }

            // disabled crop and no grouping
            return traceData;
        }
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
