package com.biorecorder.basechart;

import com.biorecorder.basechart.axis.AxisWrapper;
import com.biorecorder.basechart.graphics.BCanvas;
import com.biorecorder.basechart.graphics.BRectangle;

/**
 * Created by galafit on 7/6/19.
 */
public class Crosshair {
     int position;
     AxisWrapper axis;

     public Crosshair(AxisWrapper axis, int position) {
          this.position = position;
          this.axis = axis;
     }

     public void draw(BCanvas canvas, BRectangle area) {
          axis.drawCrosshair(canvas, area, position);
     }
}
