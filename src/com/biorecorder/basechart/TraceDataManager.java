package com.biorecorder.basechart;

import com.biorecorder.basechart.config.DataProcessingConfig;
import com.biorecorder.basechart.data.SubRange;
import com.biorecorder.basechart.data.DataSeries;
import com.biorecorder.basechart.data.GroupedDataSeries;

import java.util.ArrayList;


/**
 * Created by galafit on 9/7/18.
 */
public class TraceDataManager {
    private DataSeries traceDataSeries;
    private ArrayList<GroupedDataSeries> fullyGroupedSeries = new ArrayList<>(0);
    private DataProcessingConfig processingConfig;
    private int pixelsInDataPoint = 1;
    private int width = 1500;

    private DataSeries croppedSeries;
    private GroupedDataSeries groupedSeries;

    public TraceDataManager(DataSeries traceDataSeries, int pixelsInDataPoint) {
        this.traceDataSeries = traceDataSeries;
        this.pixelsInDataPoint = pixelsInDataPoint;
        if(this.pixelsInDataPoint <= 0) {
            this.pixelsInDataPoint = 1;
        }
    }

    public DataSeries cropData(int width) {
        if (croppedSeries == null) {
            croppedSeries = traceDataSeries.copy();
        }
        if (processingConfig.isCropEnabled()) {
            croppedSeries.setViewRange(0, width);
        }
        if(processingConfig.isDataExpensive()) {
            croppedSeries.setCachingEnabled(true);
        }
        return croppedSeries;
    }


    public DataSeries processData(Double min, Double max) {
        if (traceDataSeries.size() == 0) {
            return traceDataSeries;
        }

        if (!processingConfig.isCropEnabled() && !processingConfig.isGroupingEnabled()) {
            return traceDataSeries;
        }

        SubRange subRange = traceDataSeries.getSubRange(min, max);
        boolean isFullGrouping = false;
        if (croppedSeries == null) {
            croppedSeries = traceDataSeries.copy();
        }
        if (processingConfig.isCropEnabled()) {
            croppedSeries.setViewRange(subRange.getStartIndex(), subRange.getLength());
        } else {
            isFullGrouping = true;
        }

        if (!processingConfig.isGroupingEnabled()) {
            return croppedSeries;
        }

        // calculate best avg number of points in each group
        int pointsInGroup = (int) Math.round((double) (subRange.getLength()) / (width * pixelsInDataPoint));
        double groupingInterval = getGroupingInterval(pointsInGroup);
        if (pointsInGroup < 2) { // no grouping
            groupedSeries = null;
            return croppedSeries;
        }

        // first grouping
        if (groupedSeries == null) {
            groupedSeries = new GroupedDataSeries(croppedSeries, groupingInterval);
            if (processingConfig.isDataExpensive() || isFullGrouping) {
                groupedSeries.setCachingEnabled(true);
            }
            return groupedSeries;
        }

        // re-grouping
        double previousGroupingInterval = groupedSeries.getAverageDataInterval();
        int factor = (int) Math.round(groupingInterval / previousGroupingInterval);
        if (isFullGrouping) {
            // new grouping on the base of previously grouped data
            if (factor > 1) {
                GroupedDataSeries previousGroupedSeries = groupedSeries;
                groupedSeries = new GroupedDataSeries(previousGroupedSeries, factor * previousGroupingInterval);
                groupedSeries.setCachingEnabled(true);
                // activate "lazy" grouping recalculation on the base of cached grouped data
                for (int i = 0; i < groupedSeries.size(); i++) {
                    for (int yColumnNumber = 0; yColumnNumber < groupedSeries.YColumnsCount(); yColumnNumber++) {
                        groupedSeries.getYValue(i, yColumnNumber);
                    }
                }
                // remove caching from previousGroupedSeries
                previousGroupedSeries.setCachingEnabled(false);
            }
        } else {
            if (factor == 1) { // scrolling
                groupedSeries.updateGroups();
            } else { // simple regrouping
                groupedSeries = new GroupedDataSeries(croppedSeries, groupingInterval);
            }
        }
        return groupedSeries;
    }

    private double getGroupingInterval(int pointsInGroup) {
        return pointsInGroup * traceDataSeries.getXExtremes().length() / traceDataSeries.size();
    }
}
