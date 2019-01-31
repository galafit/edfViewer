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


    public ChartData getOriginalData() {
        return traceData;
    }

    public ChartData getProcessedData_(Scale xScale) {
        return traceData;
    }

    public ChartData getProcessedData(Scale xScale) {
        traceData.update();
        if((!processingConfig.isCropEnabled() && !processingConfig.isGroupEnabled()) || traceData.rowCount() <= 1) {
            return traceData;
        }

        Double xMin = xScale.getDomain()[0];
        Double xMax = xScale.getDomain()[1];
        double xStart =  xScale.getRange()[0];
        double xEnd =  xScale.getRange()[1];

        BRange dataMinMax = traceData.getColumnMinMax(0);
        double dataStart = xScale.scale(dataMinMax.getMin());
        double dataEnd = xScale.scale(dataMinMax.getMax());


        int width = 0;
        BRange intersection = BRange.intersect(new BRange(xStart, xEnd), new BRange(dataStart, dataEnd));
        if(intersection != null) {
            width = (int)intersection.length();
        }
        BRange minMax = BRange.intersect(dataMinMax, new BRange(xMin, xMax));

        if(width == 0) {
            if(processingConfig.isCropEnabled()) {
                return traceData.view(0, 0);
            } else {
                return traceData;
            }
        }


        double groupInterval = 0;
        // calculate best grouping interval
        double bestInterval = minMax.length() * pixelsPerDataPoint / width;
        int pointsInGroup = groupIntervalToPointsNumber(traceData, bestInterval);


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

         if(groupInterval == 0) {
            groupInterval = bestInterval;
         }
        System.out.println("points in group "+pointsInGroup);

        // if crop enabled
        if(processingConfig.isCropEnabled()) {
            int cropShoulder = CROP_SHOULDER * pointsInGroup;

            int minIndex = traceData.bisect(0, minMax.getMin()) - cropShoulder;
            int maxIndex = traceData.bisect(0, minMax.getMax()) + cropShoulder;
            if(minIndex < 0) {
                minIndex = 0;
            }
            if(maxIndex >= traceData.rowCount()) {
                maxIndex = traceData.rowCount() - 1;
            }

            ChartData resultantData = traceData.view(minIndex, maxIndex - minIndex);

            if(pointsInGroup > 1) { // group only visible data
                resultantData = resultantData.resample(0, groupInterval, processingConfig.getGroupingType());
            }
            return resultantData;
        }


        // if crop disabled (mostly preview case) we group ALL data
        // (so we do regroup only when it is actually needed)
        if(pointsInGroup > 1) {
            if(isEqualFrequencyGrouping) { // group by equal points number
                if(groupedData != null) {
                    int pointsInGroup1 = groupIntervalToPointsNumber(groupedData, groupInterval);
                    if(pointsInGroup1 > 1) {
                        ChartData regroupedData = groupedData.resample(0, groupInterval, processingConfig.getGroupingType());
                        // force "lazy" grouping
                        int rowCount = regroupedData.rowCount();
                        for (int i = 0; i < regroupedData.columnCount(); i++) {
                            regroupedData.getValue(rowCount - 1, i);
                        }
                        groupedData.disableCaching();
                        groupedData = regroupedData;
                    }
                } else {
                    groupedData = traceData.resample(0, groupInterval, processingConfig.getGroupingType());
                }
                return groupedData;
            } else {
                return traceData.resample(0, groupInterval, processingConfig.getGroupingType());
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
