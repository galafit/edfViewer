package com.biorecorder.basechart;

import com.biorecorder.basechart.config.DataConfig;
import com.biorecorder.basechart.data.Data;
import com.biorecorder.basechart.data.DataSeries;

import java.util.HashMap;

/**
 * Created by galafit on 18/7/18.
 */
public class DataManager {
    private Data data;
    private HashMap<Integer, TraceDataManager> traceDataManagers = new HashMap<>();

    public DataManager(Data data) {
        this.data = data;
    }

    public void addTrace(int traceNumber, DataConfig traceDataConfig, int pixelsInDataPoint) {
        traceDataManagers.put(traceNumber, new TraceDataManager(data.getDataSeries(traceDataConfig), pixelsInDataPoint));

    }

    public DataSeries getCroppedTraceData(int traceNumber, int width) {
        return traceDataManagers.get(traceNumber).cropData(width);
    }

    public DataSeries getProcessedTraceData(int traceNumber, Double min, Double max) {
       return traceDataManagers.get(traceNumber).processData(min, max);
    }
}
