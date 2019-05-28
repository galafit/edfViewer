package com.biorecorder.basechart;

import com.biorecorder.basechart.graphics.BCanvas;
import com.biorecorder.basechart.graphics.BColor;
import com.biorecorder.basechart.graphics.BRectangle;
import com.biorecorder.basechart.scales.Scale;
import com.sun.istack.internal.Nullable;

/**
 * Created by galafit on 22/5/19.
 */
public interface Trace {
      TraceType traceType();

      GroupApproximation[] groupApproximations();

      int traceCount(ChartData data);

      String traceName(ChartData data, int trace);

      int markWidth();

      BRectangle tracePointHoverArea(ChartData data, int dataIndex, int trace, Scale xScale, Scale yScale);

      @Nullable Range traceYMinMax(ChartData data, int trace);

      @Nullable Range xMinMax(ChartData data);

      NamedValue[] tracePointValues(ChartData data, int dataIndex, int trace, Scale xScale, Scale yScale);

      void drawTrace(BCanvas canvas, ChartData data, int trace, BColor traceColor, int traceCount, boolean isSplit, Scale xScale, Scale yScale);
}
