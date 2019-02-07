package com.biorecorder.basechart;

import com.biorecorder.basechart.button.StateListener;
import com.biorecorder.basechart.button.SwitchButton;
import com.biorecorder.basechart.button.ButtonGroup;
import com.biorecorder.basechart.graphics.BCanvas;
import com.biorecorder.basechart.graphics.BRectangle;
import com.biorecorder.basechart.graphics.HorizontalAlign;
import com.biorecorder.basechart.graphics.VerticalAlign;
import com.biorecorder.basechart.traces.Trace;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by hdablin on 11.08.17.
 */
public class Legend {
    private ButtonGroup buttonGroup;
    private Map<Trace, SwitchButton> buttons = new HashMap<>();
    private BRectangle area;

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
        for (Trace key : buttons.keySet()){
            SwitchButton button = buttons.get(key);
            if(button.contains(x, y)) {
                button.switchState();
                return true;
            }
        }
        return false;
    }

    public  void setArea(BRectangle area) {
        this.area = area;
        isDirty = true;
    }


    public void setConfig(LegendConfig legendConfig) {
        config = legendConfig;
        for (Trace key : buttons.keySet()){
            SwitchButton button = buttons.get(key);
            button.setBackgroundColor(config.getBackgroundColor());
            button.setTextStyle(config.getTextStyle());
            button.setMargin(config.getItemMargin());
        }
        isDirty = true;
    }

    public void add(Trace trace, StateListener traceSelectionListener) {
        // add trace legend button
        SwitchButton traceButton = new SwitchButton(trace.getName());
        traceButton.addListener(traceSelectionListener);
        buttons.put(trace, traceButton);
        buttonGroup.add(traceButton.getModel());
        traceButton.setBackgroundColor(config.getBackgroundColor());
        traceButton.setTextStyle(config.getTextStyle());
        traceButton.setMargin(config.getItemMargin());
        isDirty = true;
    }

    public SwitchButton getButton(int traceNumber) {
        return buttons.get(traceNumber);
    }

    private void arrangeButtons(BCanvas canvas) {
        int n = 0;
        int btnsHeight = 0;
        if(halign == HorizontalAlign.RIGHT) {
            int x = area.x + area.width;
            int y = area.y;
            for (Trace key : buttons.keySet()) {
                SwitchButton button = buttons.get(key);
                BRectangle btnArea = button.getBounds(canvas);
                if(btnsHeight == 0) {
                    btnsHeight = btnArea.height;
                }
                if(n > 0 && x - btnArea.width <= area.x) {
                    x = area.x + area.width;
                    y += btnArea.height;
                    button.setLocation(x - btnArea.width, y, canvas);
                    x -= btnArea.width - getInterItemSpace();
                    btnsHeight += btnArea.height;
                    n = 1;
                } else {
                    button.setLocation(x - btnArea.width, y, canvas);
                    x -= btnArea.width - getInterItemSpace();
                    n++;
                }
            }
        }
        if(halign == HorizontalAlign.LEFT) {
            int x = area.x;
            int y = area.y;
            for (Trace key : buttons.keySet()) {
                SwitchButton button = buttons.get(key);
                BRectangle btnArea = button.getBounds(canvas);
                if(btnsHeight == 0) {
                    btnsHeight = btnArea.height;
                }
                if(n > 0 && x + btnArea.width >= area.x + area.width) {
                    x = area.x;
                    y += btnArea.height;
                    button.setLocation(x, y, canvas);
                    x += btnArea.width + getInterItemSpace();
                    btnsHeight += btnArea.height;
                    n = 1;
                 } else {
                    button.setLocation(x, y, canvas);
                    x += btnArea.width + getInterItemSpace();
                    n++;
                }
            }
        }
        if(valign == VerticalAlign.BOTTOM) {
            moveButtons(0, area.height - btnsHeight);
        }
        if(valign == VerticalAlign.MIDDLE) {
            moveButtons(0, (area.height - btnsHeight)/2);
        }
        isDirty = false;
    }

    private void moveButtons(int dx, int dy) {
        if(dx != 0 || dy != 0) {
            for (Trace key : buttons.keySet()){
                buttons.get(key).moveLocation(dx, dy);
            }
        }
    }

    public void draw(BCanvas canvas) {
        if (buttons.size() == 0) {
            return;
        }
        if(isDirty) {
            arrangeButtons(canvas);
        }
        for (Trace trace : buttons.keySet()){
            buttons.get(trace).setColor(trace.getMainColor());
        }
        canvas.setTextStyle(config.getTextStyle());
        for (Trace key : buttons.keySet()) {
           buttons.get(key).draw(canvas);
        }
    }

    private int getInterItemSpace() {
        return 0;
    }
}

