package com.biorecorder.basechart;


import com.biorecorder.basechart.scales.Scale;
import com.biorecorder.basechart.scales.TimeScale;
import com.biorecorder.data.frame.Interval;
import com.biorecorder.data.frame.TimeInterval;
import com.biorecorder.data.sequence.StringSequence;
import com.biorecorder.data.utils.PrimitiveUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by galafit on 9/7/18.
 */
public class TraceDataManager {
    private final ChartData traceData;
    private DataProcessingConfig processingConfig;
    private boolean isEqualFrequencyGrouping; // group by equal points number or equal "height"

    private ChartData processedData;
    private List<ChartData> groupedDataList = new ArrayList<>(1);
    private Scale prevXScale;
    private int prevPixelsPerDataPoint = -1;
    private int prevTraceDataSize;
    private double roundPrecision = 0.2;
    private List<? extends GroupInterval> groupingIntervals;


    private int[] sorter;

    public TraceDataManager(ChartData traceData) {
        this.traceData = traceData;
        setConfig(new DataProcessingConfig());
    }

    public void appendData() {
        traceData.appendData();
        if (isGroupIntervalsSpecified()) {
            for (ChartData data : groupedDataList) {
                if (data != null) {
                    data.appendData();
                }
            }
        }
    }

    private void createGroupingIntervals(Scale xScale) {
        double[] specifiedIntervals = processingConfig.getGroupingIntervals();
        if (xScale instanceof TimeScale) {
            if (specifiedIntervals != null && specifiedIntervals.length != 0) {
                List<TimeGroupInterval> timeGroupingIntervals = new ArrayList<>(specifiedIntervals.length);
                for (double interval : specifiedIntervals) {
                    TimeGroupInterval timeInterval = new TimeGroupInterval(TimeInterval.getClosest(Math.round(interval), true));
                    if (timeGroupingIntervals.size() == 0 || !timeGroupingIntervals.get(timeGroupingIntervals.size() - 1).equals(timeInterval)) {
                        timeGroupingIntervals.add(timeInterval);
                    }
                }
                groupingIntervals = timeGroupingIntervals;
            } else {
                if (!isEqualFrequencyGrouping) {
                    // for grouping all available TimeIntervals will be used
                    TimeInterval[] timeIntervals = TimeInterval.values();
                    List<TimeGroupInterval> timeGroupingIntervals = new ArrayList<>(timeIntervals.length);
                    for (TimeInterval timeInterval : timeIntervals) {
                        timeGroupingIntervals.add(new TimeGroupInterval(timeInterval));
                    }
                    groupingIntervals = timeGroupingIntervals;
                } else {
                    groupingIntervals = null;
                }
            }
        } else {
            if (specifiedIntervals != null && specifiedIntervals.length != 0) {
                List<NumberGroupInterval> intervals = new ArrayList<>(specifiedIntervals.length);
                for (double interval : specifiedIntervals) {
                    intervals.add(new NumberGroupInterval(interval));
                }
                groupingIntervals = intervals;

            } else {
                groupingIntervals = null;
            }
        }
        int capacity = 1;
        if (groupingIntervals != null) {
            capacity = groupingIntervals.size();
        }

        groupedDataList = new ArrayList<>(capacity);
        for (int i = 0; i < capacity; i++) {
            groupedDataList.add(null);
        }
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
        processedData = null;
    }

    public StringSequence getLabelsIfXColumnIsString() {
        if (!traceData.isNumberColumn(0)) {
            return new StringSequence() {
                @Override
                public int size() {
                    return traceData.rowCount();
                }

                @Override
                public String get(int index) {
                    return traceData.getLabel(index, 0);
                }
            };
        }

        return null;
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
        if (data.rowCount() == 0) {
            return -1;
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
        if ((!processingConfig.isCropEnabled() && !processingConfig.isGroupingEnabled())
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
            return traceData;
        }

        int pixelsPerDataPoint = 1;
        if (markSize > 0) {
            pixelsPerDataPoint = markSize;
        }
        if (prevXScale == null || !prevXScale.getClass().equals(xScale.getClass()) &&
                prevXScale instanceof TimeScale || xScale instanceof TimeScale) {
            createGroupingIntervals(xScale);
        }

        if (!isProcessedDataOk(xScale, pixelsPerDataPoint)) {
            processedData = processData(xScale, pixelsPerDataPoint);
            prevXScale = xScale.copy();
            prevPixelsPerDataPoint = pixelsPerDataPoint;
            prevTraceDataSize = traceData.rowCount();
        }
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

        if (Math.abs(prevLength - length) * 100 / length > processingConfig.getGroupingStability()) {
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
        if (traceData.rowCount() <= 1) {
            return traceData;
        }
        double[] range = xScale.getRange();
        double[] domain = xScale.getDomain();
        Double xMin = domain[0];
        Double xMax = domain[domain.length - 1];
        double xStart = range[0];
        double xEnd = range[range.length - 1];

        Range traceDataMinMax = traceData.getColumnMinMax(0);
        double dataStart = xScale.scale(traceDataMinMax.getMin());
        double dataEnd = xScale.scale(traceDataMinMax.getMax());

        double drawingAreaWidth = 0;
        Range intersection = Range.intersect(new Range(xStart, xEnd), new Range(dataStart, dataEnd));
        if (intersection != null) {
            drawingAreaWidth = intersection.length();
        }
        Range minMax = Range.intersect(traceDataMinMax, new Range(xMin, xMax));

        if (minMax == null) {
            return traceData.view(0, 0);
        }

        if (drawingAreaWidth < 1) {
            drawingAreaWidth = 1;
            // return traceData.view(0, 0);
        }

        double groupInterval = 0;
        double pointsInGroup = 1;
        int pointsInGroupRound = 1;
        int groupIntervalIndex = -1;

        boolean isGroupingEnabled = false;
        if (processingConfig.isGroupingEnabled()) {
            // calculate best grouping interval
            groupInterval = minMax.length() * pixelsPerDataPoint / drawingAreaWidth;
            ;
            pointsInGroup = groupIntervalToPoints(traceData, groupInterval);
            pointsInGroupRound = roundPoints(pointsInGroup);
            if (pointsInGroupRound > 1) {
                // if available intervals are specified we choose the interval among the available ones
                if (isGroupIntervalsSpecified()) {
                    double precision = 0.1;
                    for (int i = 0; i < groupingIntervals.size(); i++) {
                        double interval_i = groupingIntervals.get(i).intervalLength();
                        if (Math.abs(groupInterval - interval_i) < precision * interval_i || groupInterval < interval_i) {
                            groupInterval = interval_i;
                            groupIntervalIndex = i;
                            break;
                        }
                    }
                    if (groupIntervalIndex < 0) {
                        groupInterval = groupingIntervals.get(groupingIntervals.size() - 1).intervalLength();
                        groupIntervalIndex = groupingIntervals.size() - 1;
                    }
                    pointsInGroup = groupIntervalToPoints(traceData, groupInterval);
                    pointsInGroupRound = roundPoints(pointsInGroup);
                } else {
                    // if intervals are not specified we adjust group interval to int number of points in group
                    groupInterval = pointsNumberToGroupInterval(traceData, pointsInGroupRound);
                }
                isGroupingEnabled = true;
            } else {
                if (processingConfig.isGroupingForced() && isGroupIntervalsSpecified()) {
                    groupInterval = groupingIntervals.get(0).intervalLength();
                    groupIntervalIndex = 0;
                    pointsInGroup = groupIntervalToPoints(traceData, groupInterval);
                    pointsInGroupRound = roundPoints(pointsInGroup);
                    isGroupingEnabled = true;
                }
            }
        }


        // we do all arithmetic in long to avoid int overflow !!!
        long cropShoulder = processingConfig.getCropShoulder() * pointsInGroupRound;
        processedData = traceData;
        boolean isAlreadyGrouped = false;

        if (isGroupingEnabled) {
            ChartData groupedData = findIfAlreadyGrouped(groupInterval, groupIntervalIndex);
            if (groupedData != null) {
                processedData = groupedData;
                cropShoulder = processingConfig.getCropShoulder();
                isAlreadyGrouped = true;
            } else if (processingConfig.isGroupAll()) {
                processedData = groupAll(groupInterval, groupIntervalIndex);
                cropShoulder = processingConfig.getCropShoulder();
                isAlreadyGrouped = true;
            }
        }

        // if crop enabled
        if (processedData.rowCount() > 1 && processingConfig.isCropEnabled() && (traceDataMinMax.getMin() < xMin || traceDataMinMax.getMax() > xMax)) {
            long minIndex = 0;
            if (traceDataMinMax.getMin() < xMin) {
                minIndex = processedData.bisect(0, minMax.getMin(), null) - cropShoulder;
            }

            long maxIndex = processedData.rowCount() - 1;
            if (traceDataMinMax.getMax() > xMax) {
                maxIndex = processedData.bisect(0, minMax.getMax(), null) + cropShoulder;
            }
            if (minIndex < 0) {
                minIndex = 0;
            }

            if (maxIndex >= processedData.rowCount()) {
                maxIndex = processedData.rowCount() - 1;
            }

            processedData = processedData.view(PrimitiveUtils.long2int(minIndex), PrimitiveUtils.long2int(maxIndex - minIndex));
            // if data was not grouped before we group only visible data
            if (isGroupingEnabled && !isAlreadyGrouped) {
                GroupInterval interval;
                if (groupIntervalIndex >= 0) {
                    interval = groupingIntervals.get(groupIntervalIndex);
                } else {
                    interval = new NumberGroupInterval(groupInterval);
                }
                processedData = group(processedData, interval);
            }
            if (processingConfig.isCroppedDataCachingEnabled()) {
                processedData.cache();
            }
        } else { // if crop disabled
            // if grouping was not done before
            if (isGroupingEnabled && !isAlreadyGrouped) {
                processedData = groupAll(groupInterval, groupIntervalIndex);
            }
        }

        return processedData;
    }

    private ChartData findIfAlreadyGrouped(double groupInterval, int groupIntervalIndex) {
        if (groupIntervalIndex < 0) {
            ChartData groupedData = groupedDataList.get(0);
            if (groupedData != null && groupedData.rowCount() > 1) {
                double groupedPointsInGroup = groupIntervalToPoints(groupedData, groupInterval);
                int groupedPointsInGroupRound = roundPoints(groupedPointsInGroup);

                boolean isNextStepGrouping = groupedPointsInGroupRound > 1 && groupedPointsInGroupRound >= processingConfig.getReGroupingStep();
                boolean isPrevStepGrouping = (1 - groupedPointsInGroup) > roundPrecision && groupedPointsInGroup < 1.0 / processingConfig.getReGroupingStep();

                if (!isNextStepGrouping && !isPrevStepGrouping) {
                    if (traceData.rowCount() > prevTraceDataSize) {
                        groupedData.appendData();
                    }
                    return groupedData;
                }
            }
        } else {
            return groupedDataList.get(groupIntervalIndex);
        }
        return null;
    }


    private ChartData groupAll(double groupInterval, int groupIntervalIndex) {
        if (groupIntervalIndex < 0) {
            return groupAllIfIntervalsNotSpecified(groupInterval);
        } else {
            return groupAllIfIntervalsSpecified(groupIntervalIndex);
        }
    }

    private ChartData groupAllIfIntervalsNotSpecified(double groupInterval) {
        ChartData groupedDataNew = null;
        ChartData groupedData = groupedDataList.get(0);
        if (groupedData != null && groupedData.rowCount() > 1) {
            // we try to use already grouped data
            double groupedPointsInGroup = groupIntervalToPoints(groupedData, groupInterval);
            int groupedPointsInGroupRound = roundPoints(groupedPointsInGroup);

            boolean isNextStepGrouping = groupedPointsInGroupRound > 1 && groupedPointsInGroupRound >= processingConfig.getReGroupingStep();
            boolean isPrevStepGrouping = (1 - groupedPointsInGroup) > roundPrecision && groupedPointsInGroup < 1.0 / processingConfig.getReGroupingStep();

            if (isNextStepGrouping) {
                if (isEqualFrequencyGrouping) {
                    // we use already grouped data for further grouping
                    if (traceData.rowCount() > prevTraceDataSize) {
                        groupedData.appendData();
                    }
                    groupedDataNew = groupedData.resampleByEqualPointsNumber(groupedPointsInGroupRound);
                    groupedDataNew.cache();

                    // force "lazy" grouping
                    int rowCount = groupedDataNew.rowCount();
                    if (rowCount > 0) {
                        for (int i = 0; i < groupedDataNew.columnCount(); i++) {
                            groupedDataNew.getValue(rowCount - 1, i);
                        }
                    }
                    // Very important to clean memory!!!
                    groupedData.disableCaching();
                } else {
                    double nextStepInterval = pointsNumberToGroupInterval(groupedData, groupedPointsInGroupRound);
                    groupedDataNew = traceData.resampleByEqualInterval(0, nextStepInterval);
                    groupedDataNew.cache();
                }
            }

            if (!isNextStepGrouping && !isPrevStepGrouping) {
                // no resample (we use already grouped data as it is)
                groupedDataNew = groupedDataList.get(0);
                if (traceData.rowCount() > prevTraceDataSize) {
                    groupedDataNew.appendData();
                }
            }
        }

        if (groupedDataNew == null) {
            groupedDataNew = group(traceData, new NumberGroupInterval(groupInterval));
        }

        groupedDataList.set(0, groupedDataNew);
        return groupedDataNew;
    }


    private ChartData groupAllIfIntervalsSpecified(int groupIntervalIndex) {
        if (groupedDataList.get(groupIntervalIndex) == null) {
            ChartData groupedData = null;
            if (isEqualFrequencyGrouping && groupIntervalIndex > 0 && groupedDataList.get(groupIntervalIndex - 1) != null) {
                int pointsInGroup = roundPoints(groupIntervalToPoints(traceData, groupingIntervals.get(groupIntervalIndex).intervalLength()));
                if (pointsInGroup > 1) {
                    int prevPointsInGroup = roundPoints(groupIntervalToPoints(traceData, groupingIntervals.get(groupIntervalIndex - 1).intervalLength()));
                    if (pointsInGroup % prevPointsInGroup == 0) {
                        int pointsRatio = pointsInGroup / prevPointsInGroup;
                        if (pointsRatio > 1) {
                            // regroup on the base of already grouped data
                            groupedData = groupedDataList.get(groupIntervalIndex - 1).resampleByEqualPointsNumber(pointsRatio);
                            groupedData.cache();
                        } else if (pointsRatio == 1) {
                            // use already grouped data as it is
                            groupedData = groupedDataList.get(groupIntervalIndex - 1);
                        }
                    }
                }
            }
            if (groupedData == null) {
                groupedData = group(traceData, groupingIntervals.get(groupIntervalIndex));
            }
            groupedDataList.set(groupIntervalIndex, groupedData);
        }

        return groupedDataList.get(groupIntervalIndex);
    }

    private ChartData group(ChartData data, GroupInterval groupInterval) {
        ChartData groupedData;
        if (isEqualFrequencyGrouping) { // group by equal points number
            int points = roundPoints(groupIntervalToPoints(data, groupInterval.intervalLength()));
            if (points > 1) {
                groupedData = data.resampleByEqualPointsNumber(points);
                groupedData.cache();
                System.out.println("group by points "+groupInterval.intervalLength());

            } else {
                groupedData = data;
            }
        } else {
            if (groupInterval instanceof TimeGroupInterval) {
                TimeGroupInterval timeGroupInterval = (TimeGroupInterval) groupInterval;
                groupedData = data.resampleByEqualTimeInterval(0, timeGroupInterval.getTimeInterval());
                System.out.println("Time interval "+timeGroupInterval.intervalLength());
            } else {
                groupedData = data.resampleByEqualInterval(0, groupInterval.intervalLength());
            }
            groupedData.cache();
        }
        return groupedData;

    }

    public double getBestExtent(double drawingAreaWidth, int markSize) {
        if (traceData.rowCount() > 1) {
            Range dataMinMax = traceData.getColumnMinMax(0);
            if (markSize <= 0) {
                markSize = 1;
            }
            double traceExtent = (dataMinMax.length() * drawingAreaWidth) / (traceData.rowCount() * markSize);
            if (processingConfig.isGroupingForced() && isGroupIntervalsSpecified()) {
                double groupInterval = processingConfig.getGroupingIntervals()[0];
                double pointsInGroup = groupIntervalToPoints(traceData, groupInterval);
                if (pointsInGroup > 1) {
                    if (isEqualFrequencyGrouping) {
                        traceExtent *= roundPoints(pointsInGroup);
                    } else {
                        traceExtent *= pointsInGroup;
                    }
                }
            }
            return traceExtent;
        }
        return 0;
    }


    private boolean isGroupIntervalsSpecified() {
        return groupingIntervals != null;
    }

    private double pointsNumberToGroupInterval(ChartData data, double pointsInGroup) {
        return pointsInGroup * getDataAvgStep(data);
    }

    private double groupIntervalToPoints(ChartData data, double groupInterval) {
        return groupInterval / getDataAvgStep(data);
    }

    private int roundPoints(double points) {
        if (points < 1 + roundPrecision) {
            return 1;
        }

        int points_ceil = (int) Math.ceil(points);
        if (points_ceil - points > 1 - roundPrecision) {
            points_ceil--;
        }
        return points_ceil;
    }

    double getDataAvgStep(ChartData data) {
        int dataSize = data.rowCount();
        return (data.getValue(dataSize - 1, 0) - data.getValue(0, 0)) / (dataSize - 1);
    }

    interface GroupInterval {
        double intervalLength();
    }

    class NumberGroupInterval implements GroupInterval {
        private final double interval;

        public NumberGroupInterval(double interval) {
            this.interval = interval;
        }

        @Override
        public double intervalLength() {
            return interval;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) return true;
            if (!(o instanceof NumberGroupInterval)) {
                return false;
            }

            NumberGroupInterval numberGroupInterval = (NumberGroupInterval) o;

            return numberGroupInterval.interval == interval;
        }
    }

    class TimeGroupInterval implements GroupInterval {
        private final TimeInterval timeInterval;

        public TimeGroupInterval(TimeInterval timeInterval) {
            this.timeInterval = timeInterval;
        }

        @Override
        public double intervalLength() {
            return timeInterval.toMilliseconds();
        }

        public TimeInterval getTimeInterval() {
            return timeInterval;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) return true;
            if (!(o instanceof TimeGroupInterval)) {
                return false;
            }

            TimeGroupInterval timeGroupInterval = (TimeGroupInterval) o;

            return timeGroupInterval.timeInterval.equals(timeInterval);
        }
    }
}
