package com.biorecorder.basechart;

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
        if (this.pixelsInDataPoint <= 0) {
            this.pixelsInDataPoint = 1;
        }
    }

    public Range getDataExtremes() {
       return traceDataSeries.getXExtremes();
    }

    public DataSeries getOriginalData() {
        return traceDataSeries;
    }

    public DataSeries getProcessedData_(Double xMin, Double xMax) {
        return traceDataSeries;
    }

    public DataSeries getProcessedData(Double xMin, Double xMax) {
        if (traceDataSeries.size() == 0) {
            return traceDataSeries;
        }

        if (!processingConfig.isCropEnabled() && !processingConfig.isGroupingEnabled()) {
            return traceDataSeries;
        }
        SubRange subRange = traceDataSeries.getSubRange(xMin, xMax);

        boolean isFullGrouping = false;
        if (croppedSeries == null) {
            croppedSeries = traceDataSeries.copy();
        }
        if (processingConfig.isCropEnabled()) {
            croppedSeries.setViewRange(subRange.getStartIndex(), subRange.getSize());
        } else {
            isFullGrouping = true;
        }

        if (!processingConfig.isGroupingEnabled()) {
            return croppedSeries;
        }


        // calculate best avg number of points in each group
        int pointsInGroup = 1;
        if(subRange.getSize() > 0) {
            Range dataMinMax = croppedSeries.getXExtremes();
            double dataWidth  = (width * dataMinMax.length() / (xMax - xMin));
            pointsInGroup = (int) Math.round((double) (subRange.getSize() * pixelsInDataPoint) / dataWidth);
        }
        if(pointsInGroup < 1) {
            pointsInGroup = 1;
        }

        // add shoulder: 2 grouped points from both sides
        subRange = new SubRange(subRange.getStartIndex() - 2 * pointsInGroup, subRange.getSize() + 4 * pointsInGroup);
        croppedSeries.setViewRange(subRange.getStartIndex(), subRange.getSize());
        double groupingInterval = getGroupingInterval(pointsInGroup);
        if (pointsInGroup < 2) { // no grouping
            groupedSeries = null;
            return croppedSeries;
        }

        // first grouping
        if (groupedSeries == null) {
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
                groupedSeries.onDataChanged();
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
