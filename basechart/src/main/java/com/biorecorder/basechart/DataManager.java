package com.biorecorder.basechart;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by galafit on 18/7/18.
 */
public class DataManager {
    private List<TraceDataManager> traceDataManagers = new ArrayList<>();
    private DataProcessingConfig processingConfig;

    public DataManager(DataProcessingConfig processingConfig) {
        this.processingConfig = processingConfig;
    }

    public void addTrace(ChartData traceData, int pixelsInDataPoint) {
        traceDataManagers.add(new TraceDataManager(traceData, processingConfig, pixelsInDataPoint));
    }

    public ChartData getOriginalTraceData(int traceNumber) {
        return traceDataManagers.get(traceNumber).getOriginalData();
    }

    public ChartData getProcessedTraceData(int traceNumber, Double min, Double max) {
       return traceDataManagers.get(traceNumber).getProcessedData(min, max);
    }
}
