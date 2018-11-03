package com.biorecorder.basechart;

import com.biorecorder.basechart.data.*;


/**
 * Created by galafit on 9/7/18.
 */
public class TraceDataManager {
    private DataSeries traceDataSeries;
    private DataProcessingConfig processingConfig;
    private int CROP_SHOULDER = 2; // number of additional points that we leave on every side during crop
    private int pixelsPerDataPoint = 1;
    private int width = 500;

    private DataGroup dataGroup;


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
        traceDataSeries.updateSize();

        if (traceDataSeries.size() <= 1) {
            return traceDataSeries;
        }

        long pointsInGroup = 1;
        if(processingConfig.isGroupEnabled()) {
            // calculate best avg number of points in each group
            long pointsInMinMaxInterval = Math.round((xMax - xMin) / traceDataSeries.getDataInterval());
            pointsInGroup = pointsInMinMaxInterval * pixelsPerDataPoint / width;
        }

        // no grouping only crop
        if(pointsInGroup <= 1) {
            if(!processingConfig.isCropEnabled()) {
                return traceDataSeries;
            }

            SubRange subRange = traceDataSeries.getSubRange(xMin, xMax);
            DataSeries croppedSeries = traceDataSeries.subSeries(subRange.getStartIndex() - CROP_SHOULDER, subRange.getSize() + 2 * CROP_SHOULDER);
            return croppedSeries;
        }

        // crop and then group
        if(processingConfig.isCropEnabled()) {
            long cropShoulder = CROP_SHOULDER * pointsInGroup;
            SubRange subRange = traceDataSeries.getSubRange(xMin, xMax);
            DataSeries croppedSeries = traceDataSeries.subSeries(subRange.getStartIndex() - cropShoulder, subRange.getSize() + 2 * cropShoulder);
            // grouping and caching cropped series
            dataGroup = new DataGroupByEqualPointsNumber(pointsInGroup);
            dataGroup.setInputData(croppedSeries);
            return dataGroup.getTransformedData();
        } else { // crop disabled so we group ALL points not only visible ones
            if(dataGroup == null) {
                dataGroup = new DataGroupByEqualPointsNumber(pointsInGroup);
                dataGroup.setInputData(traceDataSeries);
            }
            return dataGroup.getTransformedData();
        }
    }


    private double pointsNumberToGroupInterval(long pointsInGroup) {
        return (pointsInGroup - 1) * traceDataSeries.getDataInterval();
    }

    private double groupIntervalToPointsNumber(double groupInterval) {
        return groupInterval / traceDataSeries.getDataInterval() + 1;
    }
}
