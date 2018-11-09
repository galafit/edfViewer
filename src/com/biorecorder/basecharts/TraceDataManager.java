package com.biorecorder.basecharts;

import com.biorecorder.data.frame.DataSeries;
import com.biorecorder.data.transformation.DataGroup;
import com.biorecorder.data.transformation.DataGroupByEqualIntervals;
import com.biorecorder.data.transformation.DataGroupByEqualPointsNumber;
import com.biorecorder.data.transformation.GroupInterval;


/**
 * Created by galafit on 9/7/18.
 */
public class TraceDataManager {
    private int CROP_SHOULDER = 2; // number of additional points that we leave on every side during crop
    private final DataSeries traceDataSeries;
    private final DataProcessingConfig processingConfig;
    private final int pixelsPerDataPoint;
    private final boolean isGroupByPoints;

    private int width = 500;
    private DataGroup dataGroup;


    public TraceDataManager(DataSeries traceDataSeries, DataProcessingConfig processingConfig, int pixelsPerDataPoint) {
        this.traceDataSeries = traceDataSeries;
        this.processingConfig = processingConfig;
        if (pixelsPerDataPoint > 0) {
            this.pixelsPerDataPoint = pixelsPerDataPoint;
        } else {
            this.pixelsPerDataPoint = 1;
        }

        switch (processingConfig.getGroupType()) {
            case EQUAL_POINTS_NUMBER:
                isGroupByPoints = true;
                break;

            case EQUAL_INTERVALS:
                isGroupByPoints = false;
                break;

            case AUTO:
                if(traceDataSeries.isRegular()) {
                    isGroupByPoints = true;
                } else {
                    isGroupByPoints = false;
                }
                break;

            default:
                isGroupByPoints = true;
                break;
        }
    }

    public Range getDataExtremes() {
       return traceDataSeries.getXExtremes();
    }

    public DataSeries getOriginalData() {
        return traceDataSeries;
    }


    public DataSeries getProcessedData(Double xMin, Double xMax) {
       // System.out.println(xMin + " get processed data " +xMax);

        traceDataSeries.updateSize();

        if (traceDataSeries.size() <= 1) {
            return traceDataSeries;
        }

        GroupInterval groupInterval = null;
        // calculate best grouping interval
        double bestInterval = (xMax - xMin) * pixelsPerDataPoint / width;
        long pointsInGroup = Math.round(groupIntervalToPointsNumber(bestInterval));
        if(processingConfig.isGroupEnabled()  && pointsInGroup > 1) {


            // if available intervals are specified we choose the interval among the available ones
            GroupInterval[] availableIntervals = processingConfig.getGroupIntervals();
            if(availableIntervals != null) {
                for (int i = 0; i < availableIntervals.length; i++) {
                    if(availableIntervals[i].getIntervalAsNumber() >= bestInterval) {
                        groupInterval = availableIntervals[i];
                        break;
                    }
                }
                if(groupInterval == null) {
                    groupInterval = availableIntervals[availableIntervals.length - 1];
                } // if there is no available intervals use the best grouping interval
                pointsInGroup = (long)Math.round(groupIntervalToPointsNumber(groupInterval.getIntervalAsNumber()));
            } else {
                // if available intervals are NOT specified we take interval close to the
                // "best grouping interval" but including "integer" (rounded) number of points
                groupInterval = new GroupInterval(pointsNumberToGroupInterval(pointsInGroup));
            }
         }

        // if crop enabled
        if(processingConfig.isCropEnabled()) {
            DataSeries resultantDataSeries = traceDataSeries;
            long cropShoulder = CROP_SHOULDER * pointsInGroup;
            SubRange subRange = traceDataSeries.getSubRange(xMin, xMax);
            resultantDataSeries = traceDataSeries.subSeries(subRange.getStartIndex() - cropShoulder, subRange.getSize() + 2 * cropShoulder);

            if(pointsInGroup > 1) { // group only visible
                System.out.println(pointsInGroup +" group cropped "+groupInterval.getIntervalAsNumber());
                if(isGroupByPoints) {
                    dataGroup = new DataGroupByEqualPointsNumber(pointsInGroup);
                } else {
                    dataGroup = new DataGroupByEqualIntervals(groupInterval);
                }
                dataGroup.setInputData(resultantDataSeries);
                resultantDataSeries = dataGroup.getTransformedData();
            }

            return resultantDataSeries;
        }

        // if crop disabled (mostly preview case) we group ALL points
        // (so we do regroup only when it is actually needed)
        if(pointsInGroup > 1) {
            if(isGroupByPoints) { // group by equal points number
                long previousPointsInGroup = 1;
                if(dataGroup != null) {
                    previousPointsInGroup = ((DataGroupByEqualPointsNumber) dataGroup).getPointsNumber();
                }

                boolean isRegroupRequired = false;
                if(previousPointsInGroup == 1 // no grouping before
                        // grouping before was with interval from the specified ones and != current interval
                        || processingConfig.getGroupIntervals() != null && pointsInGroup != previousPointsInGroup
                        // grouping before was with interval that is sufficient different from the current one
                        || pointsInGroup / previousPointsInGroup > processingConfig.getGroupStep()
                        || previousPointsInGroup / pointsInGroup > processingConfig.getGroupStep()) {
                    isRegroupRequired = true;
                }

                if(isRegroupRequired) {
                    dataGroup = new DataGroupByEqualPointsNumber(pointsInGroup);
                    dataGroup.setInputData(traceDataSeries);
                }
            } else { // group by equal intervals
                GroupInterval previousInterval = null;
                if(dataGroup != null) {
                    previousInterval = ((DataGroupByEqualIntervals) dataGroup).getInterval();
                }

                boolean isRegroupRequired = false;
                if(previousInterval == null // no grouping before
                        // grouping before was with interval from the specified ones and != current interval
                        || processingConfig.getGroupIntervals() != null && previousInterval != groupInterval // reference comparison is ok here course interval is from the array
                        // grouping before was with interval that is sufficient different from the current one
                        || groupInterval.getIntervalAsNumber() / previousInterval.getIntervalAsNumber() > processingConfig.getGroupStep()
                        || previousInterval.getIntervalAsNumber() / groupInterval.getIntervalAsNumber() > processingConfig.getGroupStep()) {
                    isRegroupRequired = true;
                }

                if(isRegroupRequired) {
                    System.out.println(pointsInGroup + " grouping by interval "+groupInterval.getIntervalAsNumber());
                    dataGroup = new DataGroupByEqualIntervals(groupInterval);
                    dataGroup.setInputData(traceDataSeries);
                }
            }
            return dataGroup.getTransformedData();
        }

        // disabled crop and no grouping
        return traceDataSeries;
    }

    private double pointsNumberToGroupInterval(double pointsInGroup) {
        return pointsInGroup  * traceDataSeries.getDataInterval();
    }

    private double groupIntervalToPointsNumber(double groupInterval) {
        return groupInterval / traceDataSeries.getDataInterval();
    }
}
