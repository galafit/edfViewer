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
    private LegendConfig config;
    private HorizontalAlign halign;
    private VerticalAlign valign;
    private BRectangle area;
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
        this.area = area;
        isDirty = true;
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

    private void createButtons(BCanvas canvas) {
        if(halign == HorizontalAlign.RIGHT) {
            int x = area.x + area.width;
            int y = area.y;
            for (Integer key : buttons.keySet()) {
                SwitchButton button = buttons.get(key);
                BRectangle btnArea = button.getBounds(canvas);
                if(x - btnArea.width <= area.x) {
                    x = area.x + area.width;
                    y += btnArea.height;
                    button.setLocation(x - btnArea.width, y, canvas);
                } else {
                    button.setLocation(x - btnArea.width, y, canvas);
                    x -= btnArea.width - getInterItemSpace();
                }
            }
        }
        if(halign == HorizontalAlign.LEFT) {
             int x = area.x;
            int y = area.y;
            for (Integer key : buttons.keySet()) {
                SwitchButton button = buttons.get(key);
                BRectangle btnArea = button.getBounds(canvas);
                if(x + btnArea.width >= area.x + area.width) {
                    x = area.x;
                    y += btnArea.height;
                    button.setLocation(x, y, canvas);
                } else {
                    button.setLocation(x, y, canvas);
                    x += btnArea.width + getInterItemSpace();
                }
            }
        }
        isDirty = false;
    }


    public void draw(BCanvas canvas) {
        if (buttons.size() == 0) {
            return;
        }
        if(isDirty) {
            createButtons(canvas);
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

