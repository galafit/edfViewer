package com.biorecorder.basechart;

import com.biorecorder.basechart.graphics.BCanvas;
import com.biorecorder.basechart.graphics.BColor;
import com.biorecorder.basechart.graphics.BRectangle;
import com.biorecorder.basechart.scales.Scale;
import com.sun.istack.internal.Nullable;

/**
 * https://js.devexpress.com/Documentation/ApiReference/Data_Visualization_Widgets/dxChart/Configuration/argumentAxis/.
 */
public interface Trace {
      TraceType traceType();

      GroupApproximation[] groupApproximations();

      int traceCount(ChartData data);

      String traceName(ChartData data, int trace);

      int markWidth();

      BRectangle tracePointHoverArea(ChartData data, int dataIndex, int trace,  Scale argumentScale, Scale valueScale);

      @Nullable Range traceMinMax(ChartData data, int trace);

      @Nullable Range argumentMinMax(ChartData data);

      NamedValue[] tracePointValues(ChartData data, int dataIndex, int trace, Scale argumentScale, Scale valueScale);

      void drawTrace(BCanvas canvas, ChartData data, int trace, BColor traceColor, int traceCount, boolean isSplit,  Scale argumentScale, Scale valueScale);
}
