package com.biorecorder.basechart.axis;

import com.biorecorder.basechart.graphics.*;
import com.biorecorder.basechart.scales.Tick;

import java.util.List;

/**
 * Created by galafit on 9/6/19.
 */
interface AxisPainter {
      void translateCanvas(BCanvas canvas, BRectangle area);

      int labelSizeForOverlap(TextMetric tm, List<Tick> ticks);

      int labelSizeForWidth(TextMetric tm, List<Tick> ticks);

      BText tickToLabel(TextMetric tm, int tickPosition, String tickLabel, int start, int end, int tickPixelInterval, AxisConfig config, int interLabelGap, boolean isCategory);

      void drawTickMark(BCanvas canvas,  int tickPosition, int axisLineWidth, int insideSize, int outsideSize);

      void drawGridLine(BCanvas canvas, int tickPosition, BRectangle area);

      void drawAxisLine(BCanvas canvas, int start, int end);

      BText createTitle(BCanvas canvas, String title, int start, int end, int width, TextStyle textStyle);

      boolean contains(int point, int start, int end);
}
