package com.biorecorder.basechart;

import com.biorecorder.basechart.config.traces.BooleanTraceConfig;
import com.biorecorder.basechart.config.traces.LineTraceConfig;
import com.biorecorder.basechart.config.traces.TraceConfig;
import com.biorecorder.basechart.traces.BooleanTrace;
import com.biorecorder.basechart.traces.LineTrace;
import com.biorecorder.basechart.traces.Trace;

/**
 * Created by galafit on 30/9/17.
 */
public class DefaultTraceFactory implements TraceFactory {
    @Override
    public Trace getTrace(TraceConfig traceConfig) {
        if(traceConfig instanceof LineTraceConfig) {
            return new LineTrace((LineTraceConfig) traceConfig);
        }
        if(traceConfig instanceof BooleanTraceConfig) {
            return new BooleanTrace((BooleanTraceConfig) traceConfig);
        }
        return null;
    }
}
