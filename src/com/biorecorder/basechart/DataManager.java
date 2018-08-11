package com.biorecorder.basechart;

import com.biorecorder.basechart.config.DataConfig;
import com.biorecorder.basechart.config.DataProcessingConfig;
import com.biorecorder.basechart.data.Data;
import com.biorecorder.basechart.data.DataSeries;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by galafit on 18/7/18.
 */
public class DataManager {
    private Data data;
    private List<TraceDataManager> traceDataManagers = new ArrayList<>();
    private DataProcessingConfig processingConfig;

    public DataManager(Data data, DataProcessingConfig processingConfig) {
        this.data = data;
        this.processingConfig = processingConfig;
    }

    public void addTrace(DataConfig traceDataConfig, int pixelsInDataPoint) {
        traceDataManagers.add(new TraceDataManager(data.getDataSeries(traceDataConfig), processingConfig, pixelsInDataPoint));
    }

    public DataSeries getOriginalTraceData(int traceNumber) {
        return traceDataManagers.get(traceNumber).getOriginalData();
    }

    public DataSeries getProcessedTraceData(int traceNumber, Double min, Double max) {
       return traceDataManagers.get(traceNumber).getProcessedData(min, max);
    }
}
