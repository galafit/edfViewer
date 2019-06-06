package com.biorecorder.basechart;

import com.biorecorder.basechart.graphics.BCanvas;
import com.biorecorder.basechart.graphics.BRectangle;
import com.biorecorder.basechart.graphics.DashStyle;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by galafit on 19/8/17.
 */
public class Crosshair {
    private CrosshairConfig crossHairConfig;
    private int x;
    private List<Integer> yList = new ArrayList<>(1);

    public Crosshair(CrosshairConfig crossHairConfig, int x) {
        this.crossHairConfig = crossHairConfig;
        this.x = x;
    }

    public void addY(int y) {
       yList.add(y);
    }

    public void draw(BCanvas canvas, BRectangle area){
        canvas.setStroke(crossHairConfig.getLineWidth(), DashStyle.SOLID);
        canvas.setColor(crossHairConfig.getLineColor());
        canvas.drawLine(x,area.y, x,area.y + area.height);
        for (Integer y : yList) {
            canvas.drawLine(area.x, y, area.x + area.width,y);
        }
    }
}
