package com.biorecorder.basechart;

import com.biorecorder.basechart.config.traces.TraceConfig;
import com.biorecorder.basechart.traces.Trace;

/**
 * Created by galafit on 28/1/18.
 */
public interface TraceFactory {
    public Trace getTrace(TraceConfig traceConfig);
}

