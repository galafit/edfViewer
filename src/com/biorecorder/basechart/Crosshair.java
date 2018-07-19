package com.biorecorder.basechart;

import com.biorecorder.basechart.config.CrosshairConfig;

/**
 * Created by galafit on 19/8/17.
 */
public class Crosshair {
    private CrosshairConfig crosshairConfig;
    private int x, y;

    public Crosshair(CrosshairConfig crosshairConfig) {
        this.crosshairConfig = crosshairConfig;
    }

    public void setXY(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void draw(BCanvas canvas, BRectangle area){
        canvas.setStroke(new BStroke(crosshairConfig.getLineWidth()));
        canvas.setColor(crosshairConfig.getLineColor());
        canvas.drawLine(x,area.y, x,area.y + area.height);
        canvas.drawLine(area.x, y, area.x + area.width,y);
    }
}
