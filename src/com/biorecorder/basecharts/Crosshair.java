package com.biorecorder.basecharts;

import com.biorecorder.basecharts.graphics.BCanvas;
import com.biorecorder.basecharts.graphics.BRectangle;
import com.biorecorder.basecharts.graphics.BStroke;

/**
 * Created by galafit on 19/8/17.
 */
public class Crosshair {
    private CrossHairConfig crossHairConfig;
    private int x, y;

    public Crosshair(CrossHairConfig crossHairConfig) {
        this.crossHairConfig = crossHairConfig;
    }

    public void setConfig(CrossHairConfig crossHairConfig) {
        this.crossHairConfig = crossHairConfig;
    }

    public void setXY(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void draw(BCanvas canvas, BRectangle area){
        canvas.setStroke(new BStroke(crossHairConfig.getLineWidth()));
        canvas.setColor(crossHairConfig.getLineColor());
        canvas.drawLine(x,area.y, x,area.y + area.height);
        canvas.drawLine(area.x, y, area.x + area.width,y);
    }
}
