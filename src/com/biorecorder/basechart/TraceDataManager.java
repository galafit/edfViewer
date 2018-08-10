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
    private int width = 500;

    private DataSeries croppedSeries;
    private GroupedDataSeries groupedSeries;

    public TraceDataManager(DataSeries traceDataSeries, DataProcessingConfig processingConfig, int pixelsInDataPoint) {
        this.traceDataSeries = traceDataSeries;
        this.pixelsInDataPoint = pixelsInDataPoint;
        this.processingConfig = processingConfig;
        if(this.pixelsInDataPoint <= 0) {
            this.pixelsInDataPoint = 1;
        }
    }

    public DataSeries getOriginalData() {
        return traceDataSeries;
    }


    public DataSeries getProcessData(Double min, Double max) {
        if (traceDataSeries.size() == 0) {
            return traceDataSeries;
        }

        if (!processingConfig.isCropEnabled() && !processingConfig.isGroupingEnabled()) {
            return traceDataSeries;
        }
        SubRange subRange = traceDataSeries.getSubRange(min, max);
        if(processingConfig.isCropToAvailableSpaceEnabled()) {
            if(min == null || max == null) {
                subRange = new SubRange(subRange.getStartIndex(), width);
            }
        }
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
        int pointsInGroup = (int) Math.round((double) (subRange.getLength() * pixelsInDataPoint) / width);
        double groupingInterval = getGroupingInterval(pointsInGroup);
        if (pointsInGroup < 2) { // no grouping
            groupedSeries = null;
            return croppedSeries;
        }

        // first grouping
        if (groupedSeries == null) {
            System.out.println("first grouping "+groupingInterval+ " "+min+ " "+max);
            groupedSeries = new GroupedDataSeries(croppedSeries, groupingInterval);
            if (processingConfig.isDataExpensive() || isFullGrouping) {
                groupedSeries.enableCaching(false);
            }
            return groupedSeries;
        }

        // re-grouping
        double previousGroupingInterval = groupedSeries.getDataInterval();
        int factor = (int) Math.round(groupingInterval / previousGroupingInterval);
        if (isFullGrouping) {
            // new grouping on the base of previously grouped data
            if (factor > 1) {
                groupedSeries.multiplyGroupingInterval(factor);
            }
        } else {
            if (factor == 1) { // scrolling
                System.out.println(" scrolling "+min+ " "+max);
                groupedSeries.updateGroups();
            } else { // simple regrouping
                System.out.println(groupingInterval+" re grouping "+ factor);
                groupedSeries = new GroupedDataSeries(croppedSeries, groupingInterval);
            }
        }
        return groupedSeries;
    }

    private double getGroupingInterval(int pointsInGroup) {
        return pointsInGroup * traceDataSeries.getXExtremes().length() / traceDataSeries.size();
    }
}
