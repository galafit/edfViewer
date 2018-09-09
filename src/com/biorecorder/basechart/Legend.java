package com.biorecorder.basechart;

import com.biorecorder.basechart.button.SwitchButton;
import com.biorecorder.basechart.button.ButtonGroup;
import com.biorecorder.basechart.graphics.BCanvas;
import com.biorecorder.basechart.graphics.BRectangle;
import com.biorecorder.basechart.graphics.HorizontalAlign;
import com.biorecorder.basechart.graphics.VerticalAlign;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by hdablin on 11.08.17.
 */
public class Legend {
    private ButtonGroup buttonGroup;
    private Map<Integer, SwitchButton> buttons = new HashMap<>();
    private BRectangle bounds;

    private LegendConfig config;
    private HorizontalAlign halign;
    private VerticalAlign valign;
    private boolean isDirty = true;

    public Legend(LegendConfig legendConfig, HorizontalAlign halign, VerticalAlign valign, ButtonGroup buttonGroup) {
        this.config = legendConfig;
        this.buttonGroup = buttonGroup;
        this.halign = halign;
        this.valign = valign;
    }

    public boolean selectItem(int x, int y) {
        for (Integer key : buttons.keySet()){
            SwitchButton button = buttons.get(key);
            if(button.contains(x, y)) {
                button.switchState();
                return true;
            }
        }
        return false;
    }

    public  void setArea(BRectangle area) {
        if(bounds == null || bounds.width > area.width) {
            bounds = area;
            isDirty = true;
        }
        int dx = 0;
        int dy = 0;
        if(halign == HorizontalAlign.LEFT) {
            dx = area.x - bounds.x;
        }
        if(halign == HorizontalAlign.RIGHT) {
            dx = area.x + area.width - bounds.width - bounds.x;
        }
        dy = area.y - bounds.y;
        if(dx != 0 || dy != 0) {
            moveBounds(dx, dy);
        }

        bounds = area;
    }

    private void moveBounds(int dx, int dy) {
        if(dx != 0 || dy != 0) {
            bounds.x += dx;
            bounds.y += dy;
            for (Integer key : buttons.keySet()){
                buttons.get(key).moveLocation(dx, dy);
            }
        }
    }

    public void setConfig(LegendConfig legendConfig) {
        config = legendConfig;
        for (Integer key : buttons.keySet()){
            SwitchButton button = buttons.get(key);
            button.setBackgroundColor(config.getBackgroundColor());
            button.setTextStyle(config.getTextStyle());
            button.setMargin(config.getItemMargin());
        }
        isDirty = true;
    }

    public void add(int traceNumber, SwitchButton legendButton) {
        buttons.put(traceNumber, legendButton);
        buttonGroup.add(legendButton.getModel());
        legendButton.setBackgroundColor(config.getBackgroundColor());
        legendButton.setTextStyle(config.getTextStyle());
        legendButton.setMargin(config.getItemMargin());
        isDirty = true;
    }

    public SwitchButton getButton(int traceNumber) {
        return buttons.get(traceNumber);
    }

    private void arrangeButtons(BCanvas canvas) {
        BRectangle area = bounds;
        if(halign == HorizontalAlign.RIGHT) {
            int x = area.x + area.width;
            int y = area.y;
            int width = 0;
            int height = 0;
            for (Integer key : buttons.keySet()) {
                SwitchButton button = buttons.get(key);
                BRectangle btnArea = button.getBounds(canvas);
                if(height == 0) {
                    height = btnArea.height;
                }
                if(x - btnArea.width <= area.x) {
                    x = area.x + area.width;
                    y += btnArea.height;
                    height += btnArea.height;
                    button.setLocation(x - btnArea.width, y, canvas);
                } else {
                    button.setLocation(x - btnArea.width, y, canvas);
                    x -= btnArea.width;
                    width = Math.max(width, area.x + area.width - x);
                    x -= getInterItemSpace();
                }
            }
            bounds = new BRectangle(area.x + area.width - width, area.y, width, height);
        }
        if(halign == HorizontalAlign.LEFT) {
            int x = area.x;
            int y = area.y;
            int width = 0;
            int height = 0;
            for (Integer key : buttons.keySet()) {
                SwitchButton button = buttons.get(key);
                BRectangle btnArea = button.getBounds(canvas);
                if(height == 0) {
                    height = btnArea.height;
                }
                if(x + btnArea.width >= area.x + area.width) {
                    x = area.x;
                    y += btnArea.height;
                    height += btnArea.height;
                    button.setLocation(x, y, canvas);
                } else {
                    button.setLocation(x, y, canvas);
                    x += btnArea.width;
                    width = Math.max(width, x);
                    x += getInterItemSpace();
                }
            }
            bounds = new BRectangle(area.x, area.y, width, height);

        }
        isDirty = false;
    }


    public void draw(BCanvas canvas) {
        if (buttons.size() == 0) {
            return;
        }
        if(isDirty) {
            arrangeButtons(canvas);
        }
        canvas.setTextStyle(config.getTextStyle());
        for (Integer key : buttons.keySet()) {
           buttons.get(key).draw(canvas);
        }
    }

    private int getInterItemSpace() {
        return 0;
    }
}

