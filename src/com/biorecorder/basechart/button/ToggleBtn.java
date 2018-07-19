package com.biorecorder.basechart.button;

import com.biorecorder.basechart.*;
import com.biorecorder.basechart.chart.*;

/**
 * Created by galafit on 18/12/17.
 */
public class ToggleBtn {
    private BtnModel model = new BtnModel();
    private BColor color = BColor.BLACK;
    private String label = "";
    private BColor background = BColor.LIGHT_GRAY;
    private boolean isVisible = true;
    private TextStyle textStyle = new TextStyle(TextStyle.DEFAULT, TextStyle.NORMAL, 12);
    private Margin margin = new Margin((int)(textStyle.getSize() * 0),
            (int)(textStyle.getSize() * 1),
            (int)(textStyle.getSize() * 0.5),
            (int)(textStyle.getSize() * 1));
    private BRectangle bounds;

    public ToggleBtn(BColor color, String label) {
        this.color = color;
        this.label = label;
    }

    public void toggle() {
        if(model.isSelected()) {
            model.setSelected(false);
        } else {
            model.setSelected(true);
        }
    }

    public boolean contains(int x, int y) {
        if(bounds != null && bounds.contains(x, y)) {
            return true;
        }
        return false;
    }

    public void addListener(StateListener listener) {
        model.addListener(listener);
    }

    public BtnModel getModel() {
        return model;
    }

    public void setLocation(int x, int y, BCanvas canvas) {
        if(bounds == null) {
            createBounds(canvas);
        }
        bounds.x = x;
        bounds.y = y;
    }

    public BRectangle getBounds(BCanvas canvas) {
        if(bounds == null) {
            createBounds(canvas);
        }
        return bounds;
    }

    private void createBounds(BCanvas canvas) {
        TextMetric tm = canvas.getTextMetric(textStyle);
        bounds = new BRectangle(0, 0, getItemWidth(tm), getItemHeight(tm));
    }


    public void draw(BCanvas canvas) {
        if(bounds == null) {
            createBounds(canvas);
        }
        // draw background
        canvas.setColor(background);
        canvas.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
        canvas.setColor(color);

        // draw item
        TextMetric tm = canvas.getTextMetric(textStyle);
        int x = bounds.x + getPadding();
        int y = bounds.y + getPadding() + tm.ascent();
        canvas.drawString(label, x, y);

        if(model.isSelected()) {
            // draw border
            canvas.drawRect(bounds.x, bounds.y, bounds.width - 1, bounds.height);
            // draw selection marker
            x = bounds.x + getPadding() + tm.stringWidth(label) + getColorMarkerPadding();
            y = bounds.y + bounds.height/2;

            int x1 = x + getColorMarkerSize()/2;
            int y1 = bounds.y + bounds.height - getPadding();

            int x2 = x + getColorMarkerSize();
            int y2 = bounds.y + getPadding();

            canvas.drawLine(x, y, x1, y1);
            canvas.drawLine(x1, y1, x2, y2);
        }

    }

    private int getItemWidth(TextMetric tm) {
        return tm.stringWidth(label) + getColorMarkerSize() + getColorMarkerPadding()
                + 2 * getPadding();

    }

    private int getItemHeight(TextMetric tm) {
        return tm.height() + 2 * getPadding();

    }

    private int getPadding() {
        return (int) (textStyle.getSize() * 0.2);
    }

    private int getColorMarkerSize() {
        return (int) (textStyle.getSize() * 0.8);
    }

    private int getColorMarkerPadding() {
        return (int) (textStyle.getSize() * 0.5);
    }

    public void setBackground(BColor background) {
        this.background = background;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public void setTextStyle(TextStyle textStyle) {
        this.textStyle = textStyle;
    }

    public void setMargin(Margin margin) {
        this.margin = margin;
    }
}
