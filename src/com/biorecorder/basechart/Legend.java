package com.biorecorder.basechart;

import com.biorecorder.basechart.button.ToggleBtn;
import com.biorecorder.basechart.button.BtnGroup;
import com.biorecorder.basechart.graphics.BCanvas;
import com.biorecorder.basechart.graphics.BRectangle;
import com.biorecorder.basechart.graphics.HorizontalAlign;
import com.biorecorder.basechart.graphics.VerticalAlign;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by hdablin on 11.08.17.
 */
public class Legend {
    private BtnGroup buttonGroup;
    private List<ToggleBtn> buttons = new ArrayList<ToggleBtn>();
    private LegendConfig legendConfig;
    private HorizontalAlign halign;
    private VerticalAlign valign;
    private BRectangle area;
    private boolean isDirty = true;

    public Legend(LegendConfig legendConfig, HorizontalAlign halign, VerticalAlign valign, BtnGroup buttonGroup) {
        this.legendConfig = legendConfig;
        this.buttonGroup = buttonGroup;
        this.halign = halign;
        this.valign = valign;
    }

    public boolean toggle(int x, int y) {
        for (ToggleBtn button : buttons) {
           if(button.contains(x, y)) {
               button.toggle();
               return true;
           }
        }
        return false;
    }

    public  void setArea(BRectangle area) {
        this.area = area;
        isDirty = true;
    }

    public void add(ToggleBtn legendButton) {
        buttons.add(legendButton);
        buttonGroup.add(legendButton.getModel());
        legendButton.setBackground(legendConfig.getBackgroundColor());
        legendButton.setTextStyle(legendConfig.getTextStyle());
    }

    private void createButtons(BCanvas canvas) {
        if(halign == HorizontalAlign.RIGHT) {
            int x = area.x + area.width;
            int y = area.y;
            for (int i = 0; i < buttons.size(); i++) {
                BRectangle btnArea = buttons.get(i).getBounds(canvas);
                if(x - btnArea.width <= area.x) {
                    x = area.x + area.width;
                    y += btnArea.height;
                    buttons.get(i).setLocation(x - btnArea.width, y, canvas);
                } else {
                    buttons.get(i).setLocation(x - btnArea.width, y, canvas);
                    x -= btnArea.width - getInterItemSpace();
                }
            }
        }
        if(halign == HorizontalAlign.LEFT) {
            int x = area.x;
            int y = area.y;
            for (int i = 0; i < buttons.size(); i++) {
                BRectangle btnArea = buttons.get(i).getBounds(canvas);
                if(x + btnArea.width >= area.x + area.width) {
                    x = area.x;
                    y += btnArea.height;
                    buttons.get(i).setLocation(x, y, canvas);
                } else {
                    buttons.get(i).setLocation(x, y, canvas);
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
        canvas.setTextStyle(legendConfig.getTextStyle());
        for (int i = 0; i < buttons.size(); i++) {
           buttons.get(i).draw(canvas);
        }
    }

    private int getInterItemSpace() {
        return 0;
    }
}

