package com.biorecorder.basechart.chart;

import com.biorecorder.basechart.chart.config.DataProcessingConfig;
import com.biorecorder.basechart.data.SubsetRange;
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
    }

    public DataSeries processData(Double min, Double max) {
        if(traceDataSeries.size() == 0) {
            return traceDataSeries;
        }


        // calculate best avg number of points in each group
        SubsetRange subsetRange = traceDataSeries.getSubsetRange(xMinMax.getMin(), xMinMax.getMax());
        int pointsInGroup = (int) Math.round((double)(subsetRange.getLength()) / (width * pixelsInDataPoint));
        double groupingInterval = getGroupingInterval(pointsInGroup);
        if( !processingConfig.isCropEnabled()) {
            if( !processingConfig.isGroupingEnabled()) {
               return traceDataSeries;
            } else {
                if(groupedSeries == null || !processingConfig.isGroupingSuperpositionEnabled()) { // first grouping or grouping superposition disabled
                    groupedSeries = new GroupedDataSeries(traceDataSeries, groupingInterval);
                    if(processingConfig.isDataExpencive()) {
                        groupedSeries.setCachingEnabled(true);
                    }
                } else { // new grouping on the base of previously grouped data
                    double previousGroupingInterval = groupedSeries.getAverageDataInterval();
                    int factor = (int)Math.round(groupingInterval / previousGroupingInterval);
                    if(factor > 1) {
                        GroupedDataSeries previousGroupedSeries = groupedSeries;
                        groupedSeries = new GroupedDataSeries(previousGroupedSeries, factor * previousGroupingInterval);
                        groupedSeries.setCachingEnabled(true);
                        // activate "lazy" grouping recalculation on the base of cached grouped data
                        for (int i = 0; i < groupedSeries.size(); i++) {
                            for (int yColumnNumber = 0; yColumnNumber < groupedSeries.YColumnsCount() ; yColumnNumber++) {
                                groupedSeries.getYValue(i, yColumnNumber);
                            }
                        }
                        // remove caching from previousGroupedSeries
                        previousGroupedSeries.setCachingEnabled(false);
                    }
                }
                return groupedSeries;
            }
        } else {
            if(croppedSeries == null) {
                croppedSeries = traceDataSeries.copy();
            }
            croppedSeries.setViewRange(subsetRange);

            if(pointsInGroup < 2) { // no grouping
                groupedSeries = null;
                return croppedSeries;
            } else { // grouping
                if(groupedSeries == null)  {
                    groupedSeries = new GroupedDataSeries(croppedSeries, groupingInterval);
                }  else {
                    int factor = (int)Math.round(groupingInterval / groupedSeries.getAverageDataInterval());
                    if(factor == 1) { // just scrolling
                        groupedSeries.updateGroups();
                    } else { // regrouping
                        groupedSeries = new GroupedDataSeries(croppedSeries, groupingInterval);
                    }
                }
                return groupedSeries;
            }
        }
    }

    private double getGroupingInterval(int pointsInGroup) {
        return pointsInGroup * traceDataSeries.getXExtremes().length() / traceDataSeries.size();
    }
}
