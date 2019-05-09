package com.biorecorder.basechart;


import com.biorecorder.basechart.scales.Scale;
import com.biorecorder.basechart.scales.TimeScale;
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
    // NO REGROUPING if axis length change less then GROUPING_STABILITY
    private static final int GROUPING_STABILITY = 20; // percents

    private final ChartData traceData;
    private DataProcessingConfig processingConfig;
    private boolean isEqualFrequencyGrouping; // group by equal points number or equal "height"

    private ChartData processedData;
    private List<ChartData> groupedDataList = new ArrayList<>(1);
    private Scale prevXScale;
    private int prevPixelsPerDataPoint = -1;
    private int prevTraceDataSize;
    private List<? extends GroupInterval> groupingIntervals;

    private int[] sorter;

    public TraceDataManager(ChartData traceData) {
        this.traceData = traceData;
        setConfig(new DataProcessingConfig());
    }

    public void appendData() {
        traceData.appendData();
        if (groupingIntervals != null) {
            for (ChartData data : groupedDataList) {
                if (data != null) {
                    data.appendData();
                }
            }
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

        if (Math.abs(prevLength - length) * 100 / length > GROUPING_STABILITY) {
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

    private boolean isCropEnabled(Scale xScale) {
        double[] domain = xScale.getDomain();
        Double xMin = domain[0];
        Double xMax = domain[domain.length - 1];
        Range dataMinMax = traceData.getColumnMinMax(0);
        return  processingConfig.isCropEnabled() &&  (dataMinMax.getMin() < xMin || dataMinMax.getMax() > xMax);
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


    private IntervalInfo findGroupingInterval(Range minMax, int drawingAreaWidth, int pixelsPerDataPoint) {
        if (drawingAreaWidth < 1) {
            drawingAreaWidth = 1;
        }
        // calculate best grouping interval
        double groupInterval = minMax.length() * pixelsPerDataPoint / drawingAreaWidth;
        double dataStep = getDataAvgStep(traceData);
        // if available intervals are specified we choose the interval among the available ones
        if(groupingIntervals != null) {
            if (groupInterval > dataStep) {
                for (int i = 0; i < groupingIntervals.size(); i++) {
                    GroupInterval interval_i = groupingIntervals.get(i);
                    if (groupInterval <= interval_i.intervalLength()) {
                        if((i == 0 || groupingIntervals.get(i - 1).intervalLength() < dataStep) && groupInterval > Math.sqrt(dataStep * interval_i.intervalLength())) {
                            return new IntervalInfo(interval_i, i);
                        }
                        GroupInterval interval_i_prev = groupingIntervals.get(i - 1);
                        if (groupInterval > Math.sqrt(interval_i.intervalLength() * interval_i_prev.intervalLength())) {
                            return new IntervalInfo(interval_i, i);
                        } else {
                            return new IntervalInfo(interval_i_prev, i - 1);
                        }
                    }
                }
                // if interval is bigger then all specified intervals we take the last one
                int lastIndex = groupingIntervals.size() - 1;
                GroupInterval lastInterval = groupingIntervals.get(lastIndex);
                if(lastInterval.intervalLength() > Math.sqrt(dataStep * lastInterval.intervalLength())) {
                    return new IntervalInfo(lastInterval, lastIndex);
                }
            } else if(processingConfig.isGroupingForced()){
                return new IntervalInfo(groupingIntervals.get(0), 0);
            }
        } else { // if intervals are not specified
            if(isNextStepGrouping(dataStep, groupInterval)) {
                //round interval to integer number of points
                int pointsInGroup = roundPoints(groupIntervalToPoints(traceData, groupInterval));
                groupInterval = pointsNumberToGroupInterval(traceData, pointsInGroup);
                return new IntervalInfo(new NumberGroupInterval(groupInterval), -1);
            }
        }

        return null;
    }


    public ChartData processData(Scale xScale, int pixelsPerDataPoint) {
        if (traceData.rowCount() <= 1) {
            return traceData;
        }
        double[] range = xScale.getRange();
        double[] domain = xScale.getDomain();
        Double xMin = domain[0];
        Double xMax = domain[domain.length - 1];

        Range traceDataMinMax = traceData.getColumnMinMax(0);
        Range minMax = Range.intersect(traceDataMinMax, new Range(xMin, xMax));

        if (minMax == null) {
            return traceData.view(0, 0);
        }


        double xStart = range[0];
        double xEnd = range[range.length - 1];

        double dataStart = xScale.scale(traceDataMinMax.getMin());
        double dataEnd = xScale.scale(traceDataMinMax.getMax());

        int drawingAreaWidth = 0;
        Range intersection = Range.intersect(new Range(xStart, xEnd), new Range(dataStart, dataEnd));
        if (intersection != null) {
            drawingAreaWidth = (int)intersection.length();
        }

        if (drawingAreaWidth < 1) {
            drawingAreaWidth = 1;
            // return traceData.view(0, 0);
        }

        IntervalInfo groupingInterval = null;
        if(processingConfig.isGroupingEnabled()) {
            groupingInterval = findGroupingInterval(minMax, drawingAreaWidth, pixelsPerDataPoint);
        }


        // we do all arithmetic in long to avoid int overflow !!!
        long cropShoulder = processingConfig.getCropShoulder();
        processedData = traceData;
        boolean isAlreadyGrouped = false;

        if (groupingInterval != null) {
            cropShoulder *= roundPoints(groupingInterval.getIntervalLength());
            ChartData groupedData = findIfAlreadyGrouped(groupingInterval);
            if (groupedData != null) {
                processedData = groupedData;
                cropShoulder = processingConfig.getCropShoulder();
                isAlreadyGrouped = true;
            } else if (processingConfig.isGroupAll()) {
                processedData = groupAll(groupingInterval);
                cropShoulder = processingConfig.getCropShoulder();
                isAlreadyGrouped = true;
            }
        }


        if (processedData.rowCount() > 1 &&  isCropEnabled(xScale)) {
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
            if (!isAlreadyGrouped && groupingInterval != null) {
                processedData = group(processedData, groupingInterval.getInterval());
            }
            if (processingConfig.isCroppedDataCachingEnabled()) {

            }
        } else { // if crop disabled
            // if grouping was not done before
            if (groupingInterval != null && !isAlreadyGrouped) {
                processedData = groupAll(groupingInterval);
            }
        }

        return processedData;
    }

    private boolean isNextStepGrouping(double dataStep, double groupInterval) {
        return groupInterval > dataStep * Math.sqrt(processingConfig.getGroupingStep());
    }

    private boolean isPrevStepGrouping(double dataStep, double groupInterval) {
        return groupInterval * processingConfig.getGroupingStep() / dataStep < 1;
    }

    private ChartData findIfAlreadyGrouped(IntervalInfo intervalInfo) {
        if (intervalInfo.getIntervalIndex() < 0) {
            ChartData groupedData = groupedDataList.get(0);
            if (groupedData != null && groupedData.rowCount() > 1) {
                double groupedDataStep = getDataAvgStep(groupedData);
                if (!isNextStepGrouping(groupedDataStep, intervalInfo.getIntervalLength()) && !isPrevStepGrouping(groupedDataStep, intervalInfo.getIntervalLength())) {
                    if (traceData.rowCount() > prevTraceDataSize) {
                        groupedData.appendData();
                    }
                    return groupedData;
                }
            }
        } else {
            return groupedDataList.get(intervalInfo.getIntervalIndex());
        }
        return null;
    }


    private ChartData groupAll(IntervalInfo intervalInfo) {
        if (intervalInfo.getIntervalIndex() < 0) {
            return groupAllIfIntervalsNotSpecified(intervalInfo.getInterval());
        } else {
            return groupAllIfIntervalsSpecified(intervalInfo.getIntervalIndex());
        }
    }

    private ChartData groupAllIfIntervalsNotSpecified(GroupInterval groupInterval) {
        ChartData groupedDataNew = null;
        ChartData groupedData = groupedDataList.get(0);
        if (groupedData != null && groupedData.rowCount() > 1) {
            // calculate new grouping interval on the base of already grouped data
            double groupedDataStep = getDataAvgStep(groupedData);
            boolean isNextStepGrouping = isNextStepGrouping(groupedDataStep, groupInterval.intervalLength());
            boolean isPrevStepGrouping = isPrevStepGrouping(groupedDataStep, groupInterval.intervalLength());
            if (isNextStepGrouping) {
                int pointsInGroupOnGroupedData = roundPoints(groupIntervalToPoints(groupedData, groupInterval.intervalLength()));
                pointsInGroupOnGroupedData = Math.max(pointsInGroupOnGroupedData, processingConfig.getGroupingStep());
                if (isEqualFrequencyGrouping) {
                    // we use already grouped data for further grouping
                    if (traceData.rowCount() > prevTraceDataSize) {
                        groupedData.appendData();
                    }
                    groupedDataNew = groupedData.resampleByEqualPointsNumber(pointsInGroupOnGroupedData);
                } else {
                    double groupIntervalRound = pointsNumberToGroupInterval(groupedData, pointsInGroupOnGroupedData);
                    groupedDataNew = group(traceData, new NumberGroupInterval(groupIntervalRound));
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
            groupedDataNew = group(traceData, groupInterval);
        }
        groupedDataList.set(0, groupedDataNew);
        return groupedDataNew;
    }


    private ChartData groupAllIfIntervalsSpecified(int groupIntervalIndex) {
        ChartData groupedData = groupedDataList.get(groupIntervalIndex);
        if(groupedData != null) {
            return groupedData;
        }

        int pointsInGroup = roundPoints(groupIntervalToPoints(traceData, groupingIntervals.get(groupIntervalIndex).intervalLength()));
        GroupInterval groupInterval = groupingIntervals.get(groupIntervalIndex);
        if(isEqualFrequencyGrouping) {
            // try to use for grouping already grouped data
            for (int i = groupIntervalIndex - 1; i >= 0 ; i--) {
                ChartData groupedData_i = groupedDataList.get(i);
                if(groupedData_i != null) {
                    int pointsInGroup_i = roundPoints(groupIntervalToPoints(traceData, groupInterval.intervalLength()));
                    if (pointsInGroup % pointsInGroup_i == 0) {
                        int pointsRatio = pointsInGroup / pointsInGroup_i;
                        if (pointsRatio > 1) {
                            // regroup on the base of already grouped data
                            groupedData =  groupedData_i.resampleByEqualPointsNumber(pointsRatio);
                            break;
                        } else if (pointsRatio == 1) {
                            // use already grouped data as it is
                            groupedData = groupedData_i;
                            break;
                        }
                    }
                }
            }
        }

        if (groupedData == null) {
            groupedData = group(traceData, groupInterval);
        }
        if(!processingConfig.isSavingGroupedDataEnabled()) {
            // remove all grouped data except corresponding to (groupIntervalIndex - 1)
            for (int i = 0; i < groupedDataList.size(); i++) {
                if(groupIntervalIndex == 0 || i != groupIntervalIndex - 1) {
                    groupedDataList.set(i, null);
                }
            }
        }
        groupedDataList.set(groupIntervalIndex, groupedData);

        return groupedData;

    }

    private ChartData group(ChartData data, GroupInterval groupInterval) {
        ChartData groupedData;
        if (isEqualFrequencyGrouping) { // group by equal points number
            int points = roundPoints(groupIntervalToPoints(data, groupInterval.intervalLength()));
            if (points > 1) {
                groupedData = data.resampleByEqualPointsNumber(points);
                System.out.println("group by points "+ points);

            } else {
                groupedData = data;
            }
        } else {
            if (groupInterval instanceof TimeGroupInterval) {
                TimeGroupInterval timeGroupInterval = (TimeGroupInterval) groupInterval;
                groupedData = data.resampleByEqualTimeInterval(0, timeGroupInterval.getTimeInterval());
                System.out.println("Time interval grouping: "+timeGroupInterval.getTimeInterval());
            } else {
                groupedData = data.resampleByEqualInterval(0, groupInterval.intervalLength());
                System.out.println("Number interval "+groupInterval.intervalLength());

            }
        }

        for (int i = groupedData.rowCount()/4; i < groupedData.rowCount()/2; i++) {
            groupedData.getValue(i, 1);
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
            if (processingConfig.isGroupingForced() && groupingIntervals != null) {
                GroupInterval groupInterval = groupingIntervals.get(0);
                double pointsInGroup = groupIntervalToPoints(traceData, groupInterval.intervalLength());
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


    private double pointsNumberToGroupInterval(ChartData data, double pointsInGroup) {
        return pointsInGroup * getDataAvgStep(data);
    }

    private double groupIntervalToPoints(ChartData data, double groupInterval) {
        return groupInterval / getDataAvgStep(data);
    }

    private int roundPoints(double points) {
        double roundPrecision = 0.2;
        if (points < 1 + roundPrecision) {
            return 1;
        }
        int intPoints = (int) points;
        if((points - intPoints) > roundPrecision) {
            intPoints++;
        }
        return intPoints;
    }

    double getDataAvgStep(ChartData data) {
        int dataSize = data.rowCount();
        return (data.getValue(dataSize - 1, 0) - data.getValue(0, 0)) / (dataSize - 1);
    }

    class IntervalInfo {
        private final GroupInterval interval;
        private final int intervalIndex;

        public IntervalInfo(GroupInterval interval, int intervalIndex) {
            this.interval = interval;
            this.intervalIndex = intervalIndex;
        }

        public double getIntervalLength() {
            return interval.intervalLength();
        }

        public GroupInterval getInterval() {
            return interval;
        }

        public int getIntervalIndex() {
            return intervalIndex;
        }
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
