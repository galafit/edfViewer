package com.biorecorder.basechart.chart;

import com.biorecorder.basechart.data.DataSeries;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by galafit on 18/7/18.
 */
public class DataManager {
    private List<DataSeries> data;
    private ArrayList<TraceDataManager> traceDataManagers;

    public DataManager(List<DataSeries> data) {
        this.data = data;
    }

    public DataSeries getTraceData();
}
