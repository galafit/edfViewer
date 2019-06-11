package com.biorecorder.basechart.traces;

import com.biorecorder.basechart.data.ChartData;
import com.biorecorder.basechart.data.GroupApproximation;
import com.biorecorder.basechart.graphics.*;
import com.biorecorder.basechart.scales.Scale;
import com.sun.istack.internal.Nullable;

/**
 * https://js.devexpress.com/Documentation/ApiReference/Data_Visualization_Widgets/dxChart/Configuration/argumentAxis/.
 * Normally xAxis is argument axis and yAxis - value axis.
 * And only for inverted traces vice versa
 */
public interface TracePainter {
      /**
       * LineTrace, AreaTrace, ColumnTrace... - TraceType.LINEAR
       * ScatterTrace, BubblePrice... - TraceType.SCATTER
       */
      TraceType traceType();

      GroupApproximation[] groupApproximations();

      int traceCount(ChartData data);

      String traceName(ChartData data, int trace);

      int markWidth();

      BPoint tracePointCrosshair(ChartData data, int dataIndex, int trace,  Scale xScale, Scale yScale);

      BRectangle tracePointHoverArea(ChartData data, int dataIndex, int trace,  Scale xScale, Scale yScale);

      @Nullable
      Range traceYMinMax(ChartData data, int trace);

      @Nullable Range xMinMax(ChartData data);

      NamedValue[] tracePointValues(ChartData data, int dataIndex, int trace, Scale xtScale, Scale yScale);

      void drawTrace(BCanvas canvas, ChartData data, int trace, BColor traceColor, int traceCount, boolean isSplit,  Scale xScale, Scale yScale);
}
