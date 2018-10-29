package com.biorecorder.basechart;

import com.biorecorder.basechart.data.SubRange;
import com.biorecorder.basechart.data.DataSeries;
import com.biorecorder.basechart.data.GroupedDataSeries;


/**
 * Created by galafit on 9/7/18.
 */
public class TraceDataManager {
    private DataSeries traceDataSeries;
    private DataProcessingConfig processingConfig;
    private int pixelsPerDataPoint = 1;
    private int width = 500;

    private GroupedDataSeries fullGroupedSeries;
    private DataSeries groupedSeriesCache;

    public TraceDataManager(DataSeries traceDataSeries, DataProcessingConfig processingConfig, int pixelsPerDataPoint) {
        this.traceDataSeries = traceDataSeries;
        this.pixelsPerDataPoint = pixelsPerDataPoint;
        this.processingConfig = processingConfig;
        if (this.pixelsPerDataPoint <= 0) {
            this.pixelsPerDataPoint = 1;
        }
    }

    public Range getDataExtremes() {
       return traceDataSeries.getXExtremes();
    }

    public DataSeries getOriginalData() {
        return traceDataSeries;
    }


    public DataSeries getProcessedData(Double xMin, Double xMax) {
        traceDataSeries.onDataAdded();
        if(fullGroupedSeries != null) {
            fullGroupedSeries.onDataAdded();
            groupedSeriesCache.removeDataPoint((int) groupedSeriesCache.size() - 1);
            for (long i = groupedSeriesCache.size() - 1; i < fullGroupedSeries.size(); i++) {
                groupedSeriesCache.addDataPoint(fullGroupedSeries.getDataPoint(i));
            }
        }

        if (traceDataSeries.size() <= 1) {
            return traceDataSeries;
        }


        long pointsInGroup = 1;
        if(processingConfig.isGroupEnabled()) {
            // calculate best avg number of points in each group
            long pointsInMinMaxInterval = Math.round((xMax - xMin) / traceDataSeries.getDataInterval());
            pointsInGroup =  pointsInMinMaxInterval * pixelsPerDataPoint / width;
        }


        // no grouping only crop
        if(pointsInGroup <= 1) { // < processingConfig.getGroupStep()
            if(!processingConfig.isCropEnabled()) {
                return traceDataSeries;
            }
            int cropShoulder = 1;
            SubRange subRange = traceDataSeries.getSubRange(xMin, xMax);
            DataSeries croppedSeries = traceDataSeries.subSeries(subRange.getStartIndex() - cropShoulder, subRange.getSize() + 2 * cropShoulder);
            return croppedSeries;
        }

        // crop and then group
        if(!processingConfig.isGroupAll() && processingConfig.isCropEnabled()) {
            long cropShoulder = 1 * pointsInGroup;
            SubRange subRange = traceDataSeries.getSubRange(xMin, xMax);
            DataSeries croppedSeries = traceDataSeries.subSeries(subRange.getStartIndex() - cropShoulder, subRange.getSize() + 2 * cropShoulder);
            // grouping and caching cropped series

            return new DataSeries(new GroupedDataSeries(croppedSeries, getGroupingInterval(pointsInGroup)));
        }

        // full group and then crop
        if(fullGroupedSeries == null) {
            fullGroupedSeries = new GroupedDataSeries(traceDataSeries, getGroupingInterval(pointsInGroup));
            groupedSeriesCache = new DataSeries(fullGroupedSeries);
        } else if(fullGroupedSeries.getNumberOfPointsInGroup() / pointsInGroup >= processingConfig.getGroupStep()) {
            fullGroupedSeries = new GroupedDataSeries(traceDataSeries, getGroupingInterval(pointsInGroup));
            groupedSeriesCache = new DataSeries(fullGroupedSeries);
        } else if(pointsInGroup / fullGroupedSeries.getNumberOfPointsInGroup() >= processingConfig.getGroupStep()) {
            fullGroupedSeries = new GroupedDataSeries(traceDataSeries, getGroupingInterval(pointsInGroup));
            groupedSeriesCache = new DataSeries(fullGroupedSeries);
        }
        if(!processingConfig.isCropEnabled()) {
           return groupedSeriesCache;
        }

        int cropShoulder = 1;
        SubRange subRange = groupedSeriesCache.getSubRange(xMin, xMax);
        DataSeries croppedSeries = groupedSeriesCache.subSeries(subRange.getStartIndex() - cropShoulder, subRange.getSize() + 2 * cropShoulder);
        return croppedSeries;

    }


    private double getGroupingInterval(long pointsInGroup) {
        return (pointsInGroup - 1) * traceDataSeries.getDataInterval();
    }


}
