package com.biorecorder.basechart;

import com.biorecorder.basechart.scales.Scale;

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

    public double getBestExtent(int traceNumber, int drawingAreaWidth) {
        return traceDataManagers.get(traceNumber).getBestExtent(drawingAreaWidth);
    }

    public BRange getTraceFullXMinMax(int traceNumber) {
        return traceDataManagers.get(traceNumber).getFullXMinMax();
    }

    public ChartData getTraceData(int traceNumber, Scale scale) {
       return traceDataManagers.get(traceNumber).getProcessedData(scale);
    }

}
