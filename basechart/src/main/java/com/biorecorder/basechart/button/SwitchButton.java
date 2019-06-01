package com.biorecorder.basechart.button;

import com.biorecorder.basechart.Insets;
import com.biorecorder.basechart.graphics.TextMetric;
import com.biorecorder.basechart.TextStyle;
import com.biorecorder.basechart.graphics.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by galafit on 18/12/17.
 */
public class SwitchButton {
    private ButtonGroup group;
    private List<StateListener> selectionListeners = new ArrayList<StateListener>();

    private BColor color = BColor.BLACK_LIGHT;
    private String label = "";
    private BColor backgroundColor = BColor.WHITE_DARK;
    private TextStyle textStyle = new TextStyle(TextStyle.DEFAULT, TextStyle.NORMAL, 12);
    private Insets margin = new Insets((int)(textStyle.getSize() * 0.2),
            (int)(textStyle.getSize() * 0.2),
            (int)(textStyle.getSize() * 0.2),
            (int)(textStyle.getSize() * 0.2));

    private BRectangle bounds;

    public SwitchButton(String label) {
        if(label != null) {
            this.label = label;
        }
    }

    public void addListener(StateListener listener) {
        selectionListeners.add(listener);
    }

    public void setLabel(String label) {
        if(label != null) {
            this.label = label;
            bounds = null;
        }
    }

    public void setGroup(ButtonGroup group) {
        this.group = group;
    }

    public void setSelected(boolean isSelected) {
        if (group != null) {
            // use the group model instead
            boolean oldSelection = isSelected();
            if (oldSelection != isSelected) {
                group.setSelected(this, isSelected);
            }
            if (oldSelection != isSelected()) {
                for (StateListener listener : selectionListeners) {
                    listener.stateChanged(isSelected);
                }
            }
        }
    }

    public boolean isSelected() {
        if(group != null) {
            return group.isSelected(this);
        }
        return false;
    }


    public void switchState() {
        if(isSelected()) {
            setSelected(false);
        } else {
            setSelected(true);
        }
    }

    public boolean contains(int x, int y) {
        if(bounds != null && bounds.contains(x, y)) {
            return true;
        }
        return false;
    }

    public void setLocation(int x, int y, BCanvas canvas) {
        if(bounds == null) {
            createBounds(canvas);
        }
        bounds.x = x;
        bounds.y = y;
    }

    public void moveLocation(int dx, int dy) {
        if(bounds == null) {
            return;
        }
        bounds.x += dx;
        bounds.y += dy;
    }

    public String getLabel() {
        return label;
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
        // draw backgroundColor
        canvas.setColor(backgroundColor);
        canvas.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
        canvas.setColor(color);

        // draw item
        TextMetric tm = canvas.getTextMetric(textStyle);
        int x = bounds.x + margin.left();
        int y = bounds.y + margin.top() + tm.ascent();
        canvas.drawString(label, x, y);

        if(isSelected()) {
            // draw border
            canvas.drawRect(bounds.x, bounds.y, bounds.width - 1, bounds.height);
            // draw selection marker
            x = bounds.x + margin.left() + tm.stringWidth(label) + getCheckMarkPadding();
            y = bounds.y + bounds.height/2;

            int x1 = x + getCheckMarkSize()/2;
            int y1 = bounds.y + bounds.height - margin.bottom();

            int x2 = x + getCheckMarkSize();
            int y2 = bounds.y + margin.top();

            canvas.drawLine(x, y, x1, y1);
            canvas.drawLine(x1, y1, x2, y2);
        }

    }

    private int getItemWidth(TextMetric tm) {
        return tm.stringWidth(label) + getCheckMarkSize() + getCheckMarkPadding()
                + margin.left() + margin.right();

    }

    private int getItemHeight(TextMetric tm) {
        return tm.height() + margin.top() + margin.bottom();

    }

    private int getCheckMarkSize() {
        return (int) (textStyle.getSize() * 0.8);
    }

    private int getCheckMarkPadding() {
        return (int) (textStyle.getSize() * 0.5);
    }

    public void setBackgroundColor(BColor backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public void setTextStyle(TextStyle textStyle) {
        this.textStyle = textStyle;
        bounds = null;
    }

    public void setMargin(Insets margin) {
        this.margin = margin;
        bounds = null;
    }

    public void setColor(BColor color) {
        this.color = color;
    }
}
