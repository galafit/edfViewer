package com.biorecorder.basechart.axis;

import com.biorecorder.basechart.graphics.*;
import com.biorecorder.basechart.scales.Tick;

import java.util.List;

/**
 * Created by galafit on 9/6/19.
 */
interface Orientation {
      int labelSizeForOverlap(TextMetric tm, List<Tick> ticks);

      int labelSizeForWidth(TextMetric tm, List<Tick> ticks);

      void translateCanvas(BCanvas canvas, BRectangle area);

      BText createTickLabel(TextMetric tm, int tickPosition, String tickLabel, int start, int end, int tickPixelInterval, AxisConfig config, int interLabelGap, boolean isCategory);

      BLine createTickLine(int tickPosition, int axisLineWidth, int insideSize, int outsideSize);

      BLine createGridLine(int tickPosition, BRectangle area);

      BLine createAxisLine(int start, int end);

      BText createTitle(String title, TextMetric tm, int start, int end, int width);

      boolean contains(int point, int start, int end);
}
