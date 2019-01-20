package com.biorecorder.basechart;


/**
 * Created by galafit on 9/7/18.
 */
public class TraceDataManager {
    private int CROP_SHOULDER = 2; // number of additional points that we leave on every side during crop
    private final ChartData traceData;
    private final DataProcessingConfig processingConfig;
    private final int pixelsPerDataPoint;
    private final boolean isEqualFrequencyGrouping; // group by equal points number or equal "height"

    private int width = 500;

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
                if(traceData.isRegular(0)) {
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

    public ChartData getProcessedData(Double xMin, Double xMax) {
        traceData.update();

        if (traceData.rowCount() <= 1) {
            return traceData;
        }

        double groupInterval = 0;
        // calculate best grouping interval
        double bestInterval = (xMax - xMin) * pixelsPerDataPoint / width;
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

        // if crop enabled
        if(processingConfig.isCropEnabled()) {
            int cropShoulder = CROP_SHOULDER * pointsInGroup;

            int minIndex = traceData.nearest(0, xMin) - cropShoulder;
            int maxIndex = traceData.nearest(0, xMax) + cropShoulder;

            ChartData resultantData = traceData.slice(minIndex, maxIndex - minIndex);

            if(pointsInGroup > 1) { // group only visible data
                resultantData = resultantData.resample(0, groupInterval, isEqualFrequencyGrouping);
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
                        ChartData regroupedData = groupedData.resample(0, groupInterval, isEqualFrequencyGrouping);
                        // force "lazy" grouping
                        int rowCount = regroupedData.rowCount();
                        for (int i = 0; i < regroupedData.columnCount(); i++) {
                            regroupedData.getValue(rowCount - 1, i);
                        }
                        groupedData.disableCache();
                        groupedData = regroupedData;
                    }
                } else {
                    groupedData = traceData.resample(0, groupInterval, isEqualFrequencyGrouping);
                }
                return groupedData;
            } else {
                return traceData.resample(0, groupInterval, isEqualFrequencyGrouping);
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
