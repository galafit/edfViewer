package com.biorecorder.basechart.chart;

import com.biorecorder.basechart.chart.config.traces.BooleanTraceConfig;
import com.biorecorder.basechart.chart.config.traces.LineTraceConfig;
import com.biorecorder.basechart.chart.config.traces.TraceConfig;
import com.biorecorder.basechart.chart.traces.BooleanTrace;
import com.biorecorder.basechart.chart.traces.LineTrace;
import com.biorecorder.basechart.chart.traces.Trace;

/**
 * Created by galafit on 30/9/17.
 */
public class DefaultTraceFactory implements TraceFactory {
    @Override
    public Trace getTrace(TraceConfig config) {
        if(config instanceof  LineTraceConfig) {
            return new LineTrace((LineTraceConfig) config);
        }
        if(config instanceof BooleanTraceConfig) {
            return new BooleanTrace((BooleanTraceConfig) config);
        }
        return null;
    }
}
