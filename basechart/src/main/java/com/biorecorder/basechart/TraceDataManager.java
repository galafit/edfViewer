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
                if (traceData.isColumnRegular(0)) {
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
        if (traceData.columnCount() == 0) {
            return null;
        }
        return traceData.getColumnMinMax(0);
    }

    public int nearest(double xValue) {
        // "lazy" sorting solo when "nearest" is called
        ChartData data = traceData;
        if (processedData != null) {
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
        if (nearest > 0) {
            nearest_prev = nearest - 1;
        }

        if (sorter != null) {
            nearest = sorter[nearest];
            nearest_prev = sorter[nearest_prev];
        }
        if (nearest != nearest_prev) {
            if (Math.abs(data.getValue(nearest_prev, 0) - xValue) < Math.abs(data.getValue(nearest, 0) - xValue)) {
                nearest = nearest_prev;
            }
        }

        return nearest;
    }

    private boolean isDataProcessingEnabled() {
        if ((!processingConfig.isCropEnabled() && !processingConfig.isGroupEnabled())
                || traceData.rowCount() <= 1
                || traceData.columnCount() == 0
                || !traceData.isColumnIncreasing(0)) // if data not sorted (not increasing)
        {
            // No processing
            return false;
        }
        return true;
    }

    public ChartData getData(Scale xScale, int markSize) {
        if (!isDataProcessingEnabled()) { // No processing
            processedData = null;
            prevTraceDataSize = traceData.rowCount();
            return traceData;
        }

        int pixelsPerDataPoint = 1;
        if (markSize > 0) {
            pixelsPerDataPoint = markSize;
        }
        if (!isProcessedDataOk(xScale, pixelsPerDataPoint)) {
            processedData = processData(xScale, pixelsPerDataPoint);
            prevXScale = xScale.copy();
            prevPixelsPerDataPoint = pixelsPerDataPoint;
        }
        prevTraceDataSize = traceData.rowCount();
        return processedData;
    }

    private boolean isProcessedDataOk(Scale xScale, int pixelsPerDataPoint) {
        if (processedData == null) {
            return false;
        }
        if (prevPixelsPerDataPoint != pixelsPerDataPoint) {
            return false;
        }
        if (prevXScale == null) {
            return false;
        }
        if (!prevXScale.getClass().equals(xScale.getClass())) {
            return false;
        }

        if (!Arrays.equals(prevXScale.getDomain(), xScale.getDomain())) {
            return false;
        }

        int prevLength = length(prevXScale);
        int length = length(xScale);
        if (prevLength == 0 || length == 0) {
            return false;
        }

        if (Math.abs(prevLength - length) * 100 / length > processingConfig.getLengthChangeMax()) {
            return false;
        }

        if (traceData.rowCount() != prevTraceDataSize) {
            double[] domain = xScale.getDomain();
            Double xMax = domain[domain.length - 1];
            if (traceData.getValue(prevTraceDataSize - 1, 0) < xMax) {
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
        double xStart = range[0];
        double xEnd = range[range.length - 1];

        Range dataMinMax = traceData.getColumnMinMax(0);
        double dataStart = xScale.scale(dataMinMax.getMin());
        double dataEnd = xScale.scale(dataMinMax.getMax());

        double drawingAreaWidth = 0;
        Range intersection = Range.intersect(new Range(xStart, xEnd), new Range(dataStart, dataEnd));
        if (intersection != null) {
            drawingAreaWidth = intersection.length();
        }
        Range minMax = Range.intersect(dataMinMax, new Range(xMin, xMax));


        if (drawingAreaWidth < 1) {
            return traceData.view(0, 0);
        }

        double[] availableIntervals = processingConfig.getGroupIntervals();

        // calculate best grouping interval
        double bestInterval = minMax.length() * pixelsPerDataPoint / drawingAreaWidth;
        double pointsInGroup = groupIntervalToPointsNumber(traceData, bestInterval);
        int pointsInGroupRound = roundPointsNumber(pointsInGroup);
        System.out.println(bestInterval+ "  " + pointsInGroup+" pointsInGroupRound "+pointsInGroupRound);

        double groupInterval = 0;
        if (processingConfig.isGroupEnabled()) {
            if(pointsInGroupRound <= 1 &&  isGroupIntervalSpecified() && processingConfig.isGroupingForced()) {
                groupInterval = availableIntervals[0];
            }
            if(pointsInGroupRound > 1) {
                groupInterval = bestInterval;
                // if available intervals are specified we choose the interval among the available ones
                if (isGroupIntervalSpecified()) {
                    for (int i = 0; i < availableIntervals.length; i++) {
                        if (availableIntervals[i] >= bestInterval) {
                            groupInterval = availableIntervals[i];
                            break;
                        }
                    }
                    if (groupInterval > availableIntervals[availableIntervals.length - 1]) {
                        groupInterval = availableIntervals[availableIntervals.length - 1];
                    }
                }
            }
        }
        pointsInGroup = groupIntervalToPointsNumber(traceData, groupInterval);
        pointsInGroupRound = roundPointsNumber(pointsInGroup);

        // adjust group interval to int number of points in group
        if(groupInterval > 0 && !isGroupIntervalSpecified()) {
            groupInterval = pointsNumberToGroupInterval(traceData, pointsInGroupRound);
        }

        // if crop enabled
        if (processingConfig.isCropEnabled() && (dataMinMax.getMin() < xMin || dataMinMax.getMax() > xMax)) {
            // we crop data first
            int cropShoulder = processingConfig.getCropShoulder() * pointsInGroupRound;

            int minIndex = 0;
            if (dataMinMax.getMin() < xMin) {
                minIndex = traceData.bisect(0, minMax.getMin(), null) - cropShoulder;
            }

            int maxIndex = traceData.rowCount() - 1;
            if (dataMinMax.getMax() > xMax) {
                maxIndex = traceData.bisect(0, minMax.getMax(), null) + cropShoulder;
            }
            if (minIndex < 0) {
                minIndex = 0;
            }
            if (maxIndex >= traceData.rowCount()) {
                maxIndex = traceData.rowCount() - 1;
            }

            ChartData resultantData = traceData.view(minIndex, maxIndex - minIndex);
            // group only visible data
            if (pointsInGroupRound > 1) {
                resultantData = group(resultantData, groupInterval);
            }
            isWholeDataProcessed = false;
            System.out.println("crop and group "+pointsInGroupRound);
            return resultantData;
        } else { // if crop disabled
            if (pointsInGroupRound > 1) {
                ChartData groupedData;
                if (processedData != null && isWholeDataProcessed) {
                    // we try to use already grouped data
                    double pointsInGroup1 = groupIntervalToPointsNumber(processedData, groupInterval);
                    int groupingStep = processingConfig.getGroupingStep();
                    if(isGroupIntervalSpecified()) {
                        groupingStep = 1;
                    }
                    boolean isNextStepGrouping = roundPointsNumber(pointsInGroup1/groupingStep) >= 1;
                    boolean isPrevStepGrouping = roundPointsNumber(pointsInGroup1 * groupingStep) <= 1;

                    if(isNextStepGrouping && isEqualFrequencyGrouping) {
                        // we use already grouped data for further grouping
                        groupedData = processedData.resampleByEqualFrequency(roundPointsNumber(pointsInGroup1));
                        groupedData.cache();
                        // force "lazy" grouping
                        int rowCount = groupedData.rowCount();
                        for (int i = 0; i < groupedData.columnCount(); i++) {
                            groupedData.getValue(rowCount - 1, i);
                        }
                        processedData.disableCaching();
                        System.out.println("regroup "+roundPointsNumber(pointsInGroup1));
                        return  groupedData;

                    }
                    if(!isNextStepGrouping && !!isPrevStepGrouping) {
                        // no resample
                        groupedData = processedData;
                        System.out.println("the same data "+pointsInGroupRound);
                        return groupedData;
                    }
                }
                groupedData = group(traceData, groupInterval);
                return groupedData;

            }
            // disabled crop and no grouping
            isWholeDataProcessed = false;
            return traceData;
        }
    }

    private ChartData group(ChartData data, double groupInterval) {
        ChartData groupedData;
        if (isEqualFrequencyGrouping) { // group by equal points number
            groupedData = data.resampleByEqualFrequency(roundPointsNumber(groupIntervalToPointsNumber(data, groupInterval)));

        } else {
            groupedData = data.resampleByEqualInterval(0, groupInterval);
        }
        groupedData.cache();
        return groupedData;

    }

    public double getBestExtent(int drawingAreaWidth, int markSize) {
        if (traceData.rowCount() > 1) {
            double traceExtent = getDataAvgStep(traceData) * drawingAreaWidth;
            if (markSize > 0) {
                traceExtent = traceExtent / markSize;
            }
            if(processingConfig.isGroupingForced() && isGroupIntervalSpecified()) {
                double groupInterval = processingConfig.getGroupIntervals()[0];
                double pointsInGroup = groupIntervalToPointsNumber(traceData, groupInterval);
                if(roundPointsNumber(pointsInGroup) > 1) {
                    traceExtent *= pointsInGroup;
                }
            }
            return traceExtent;
        }
        return 0;
    }


    private boolean isGroupIntervalSpecified() {
        if(processingConfig.getGroupIntervals() != null && processingConfig.getGroupIntervals().length > 0) {
            return true;
        }
        return false;
    }

    private double pointsNumberToGroupInterval(ChartData data, double pointsInGroup) {
        return pointsInGroup * getDataAvgStep(data);
    }

    private double groupIntervalToPointsNumber(ChartData data, double groupInterval) {
       return groupInterval / getDataAvgStep(data);
    }

    private int roundPointsNumber(double points) {
        double precision = 0.2;

        int points_ceil = (int) Math.ceil(points);
        if (points_ceil - points > 1 - precision) {
            points_ceil--;
        }
        return points_ceil;
    }

    double getDataAvgStep(ChartData data) {
        int dataSize = data.rowCount();
        return (data.getValue(dataSize - 1, 0) - data.getValue(0, 0)) / (dataSize - 1);
    }
}
