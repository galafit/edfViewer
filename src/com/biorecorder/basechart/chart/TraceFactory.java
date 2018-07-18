package com.biorecorder.basechart.chart;

import com.biorecorder.basechart.chart.config.traces.TraceConfig;
import com.biorecorder.basechart.chart.traces.Trace;

/**
 * Created by galafit on 28/1/18.
 */
public interface TraceFactory {
    public Trace getTrace(TraceConfig traceConfig);
}

