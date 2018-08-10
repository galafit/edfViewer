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

    public Range getDataRange(Double min1, Double max1, boolean isBestSizeEnabled) {

        if(min1 != null && max1 != null) {
            return new Range(min1, max1);
        }

        Double min = min1;
        Double max = max1;

        double screenLength = 1;
        if(traceDataSeries.size() == 0) {
            if(min != null && max == null) {
                max = min + screenLength;
                return new Range(min, max);
            }
            if(min == null && max != null) {
                min = max - screenLength;
                return new Range(min, max);
            }
            if(min == null && max == null) {
                min = 0.0;
                max = screenLength;
                return new Range(min, max);
            }
        }

        Range traceMinMax = traceDataSeries.getXExtremes();

        if(isBestSizeEnabled && traceDataSeries.size() > 1) {
            screenLength = traceDataSeries.getDataInterval() * width / pixelsInDataPoint;
            if(min != null && max == null) {
                max = min + screenLength;
                return new Range(min, max);
            }
            if(min == null && max != null) {
                min = max - screenLength;
                return new Range(min, max);
            }

            if(min == null && max == null) {
                min = traceMinMax.getMin();
                max = min + screenLength;
                return new Range(min, max);
            }
        }

        // usual scaling
        if(min != null && max == null) {
            max = traceMinMax.getMax();
            if(max <= min) {
                max = min + screenLength;
            }
            return new Range(min, max);
        }
        if(min == null && max != null) {
            min = traceMinMax.getMin();
            if(min >= max) {
                min = max - screenLength;
            }
            return new Range(min, max);
        }
        // if min == null && max == null
        if(traceMinMax.length() > 0) {
            return traceMinMax;
        } else {
            min = traceMinMax.getMin();
            max = min + screenLength;
            return new Range(min, max);
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
