package com.biorecorder.basechart;


import com.biorecorder.basechart.scales.Scale;

/**
 * Created by galafit on 9/7/18.
 */
public class TraceDataManager {
    private int CROP_SHOULDER = 2; // number of additional points that we leave on every side during crop
    private final ChartData traceData;
    private final DataProcessingConfig processingConfig;
    private final int pixelsPerDataPoint;
    private final boolean isEqualFrequencyGrouping; // group by equal points number or equal "height"

    private ChartData groupedData;


    public TraceDataManager(ChartData traceData, DataProcessingConfig processingConfig, int pixelsPerDataPoint) {
        this.traceData = traceData;
        this.processingConfig = processingConfig;
        if (pixelsPerDataPoint > 0) {
            this.pixelsPerDataPoint = pixelsPerDataPoint;
        } else {
            this.pixelsPerDataPoint = 1;
        }

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
    }

    public BRange getFullXMinMax() {
        if(traceData.columnCount() == 0) {
            return null;
        }
        return traceData.getColumnMinMax(0);
    }

    public double getBestExtent(int drawingAreaWidth) {
        if (traceData.rowCount() > 1) {
            double traceExtent = getDataAvgStep(traceData) * drawingAreaWidth / pixelsPerDataPoint;
            return traceExtent;
        }
        return 0;
    }

    public boolean isDataProcessingEnabled() {
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


    public ChartData getProcessedData(Scale xScale) {

        if(!isDataProcessingEnabled()){
            // No processing
            return traceData;
        }

        Double xMin = xScale.getDomain()[0];
        Double xMax = xScale.getDomain()[1];
        double xStart =  xScale.getRange()[0];
        double xEnd =  xScale.getRange()[1];

        BRange dataMinMax = traceData.getColumnMinMax(0);
        double dataStart = xScale.scale(dataMinMax.getMin());
        double dataEnd = xScale.scale(dataMinMax.getMax());


        int drawingAreaWidth = 0;
        BRange intersection = BRange.intersect(new BRange(xStart, xEnd), new BRange(dataStart, dataEnd));
        if(intersection != null) {
            drawingAreaWidth = (int)intersection.length();
        }
        BRange minMax = BRange.intersect(dataMinMax, new BRange(xMin, xMax));

        if(drawingAreaWidth == 0) {
            if(processingConfig.isCropEnabled()) {
                return traceData.view(0, 0);
            } else {
                return traceData;
            }
        }


        // calculate best grouping interval
        double bestInterval = minMax.length() * pixelsPerDataPoint / drawingAreaWidth;
        double groupInterval = bestInterval;
        int pointsInGroup = groupIntervalToPointsNumber(traceData, groupInterval);

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

       // System.out.println("points in group "+pointsInGroup);

        // if crop enabled
        if(processingConfig.isCropEnabled()) {
            int cropShoulder = CROP_SHOULDER * pointsInGroup;

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
            return resultantData;
        }


        // if crop disabled (mostly preview case) we group ALL data
        // (so when it is possible we use already grouped data for further grouping)
        if(pointsInGroup > 1) {
            if(isEqualFrequencyGrouping) { // group by equal points number
                if(groupedData != null) {
                    int pointsInGroup1 = groupIntervalToPointsNumber(groupedData, groupInterval);
                    if(pointsInGroup1 > 1) {
                        ChartData regroupedData = groupedData.resampleByEqualFrequency(pointsInGroup1);
                        // force "lazy" grouping
                        int rowCount = regroupedData.rowCount();
                        for (int i = 0; i < regroupedData.columnCount(); i++) {
                            regroupedData.getValue(rowCount - 1, i);
                        }
                        groupedData.disableCaching();
                        groupedData = regroupedData;
                    }
                } else {
                    groupedData = traceData.resampleByEqualFrequency(pointsInGroup);
                }
                return groupedData;
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
        return (data.getValue(dataSize - 1, 0) - data.getValue(0, 0)) / (dataSize - 1);
    }
}
