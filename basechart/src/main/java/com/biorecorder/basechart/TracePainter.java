package com.biorecorder.basechart;

import com.biorecorder.basechart.graphics.BCanvas;
import com.biorecorder.basechart.graphics.BColor;
import com.biorecorder.basechart.graphics.BRectangle;
import com.biorecorder.basechart.scales.Scale;
import com.sun.istack.internal.Nullable;

/**
 * Created by galafit on 22/5/19.
 */
public interface TracePainter {
      TraceType traceType();

      GroupApproximation[] groupApproximations();

      int curveCount(ChartData data);

      String curveName(ChartData data, int curve);

      int markWidth();

      BRectangle curvePointHoverArea(ChartData data, int dataIndex, int curve, Scale xScale, Scale yScale);

      @Nullable Range curveYMinMax(ChartData data, int curve);

      @Nullable Range xMinMax(ChartData data);

      NamedValue[] curvePointValues(ChartData data, int dataIndex, int curve, Scale xScale, Scale yScale);

      void drawCurve(BCanvas canvas, ChartData data, int curve, BColor curveColor, int curveCount, boolean isSplit, Scale xScale, Scale yScale);
}
