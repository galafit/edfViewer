package com.biorecorder.basechart;

import com.biorecorder.basechart.button.StateListener;
import com.biorecorder.basechart.button.SwitchButton;
import com.biorecorder.basechart.button.ButtonGroup;
import com.biorecorder.basechart.graphics.BCanvas;
import com.biorecorder.basechart.graphics.BRectangle;
import com.biorecorder.basechart.graphics.HorizontalAlign;
import com.biorecorder.basechart.graphics.VerticalAlign;
import com.biorecorder.basechart.traces.Trace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by hdablin on 11.08.17.
 */
public class Legend {
    private ButtonGroup buttonGroup;
    private Map<Trace, SwitchButton> tracesToButtons = new HashMap<>();
    private BRectangle area;

    private LegendConfig config;
    private HorizontalAlign halign;
    private VerticalAlign valign;
    private boolean isDirty = true;

    public Legend(LegendConfig legendConfig, HorizontalAlign halign, VerticalAlign valign) {
        this.config = legendConfig;
        this.buttonGroup = new ButtonGroup();
        this.halign = halign;
        this.valign = valign;
    }

    public boolean selectItem(int x, int y) {
        for (Trace key : tracesToButtons.keySet()){
            SwitchButton button = tracesToButtons.get(key);
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
        for (Trace key : tracesToButtons.keySet()){
            SwitchButton button = tracesToButtons.get(key);
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
        tracesToButtons.put(trace, traceButton);
        buttonGroup.add(traceButton.getModel());
        traceButton.setBackgroundColor(config.getBackgroundColor());
        traceButton.setTextStyle(config.getTextStyle());
        traceButton.setMargin(config.getItemMargin());
        isDirty = true;
    }

    public void remove(Trace trace) {
        SwitchButton traceButton = tracesToButtons.get(trace);
        buttonGroup.remove(traceButton.getModel());
        tracesToButtons.remove(trace);
        isDirty = true;
    }

    private BRectangle getTraceArea(Trace trace) {
        double[] yRange = trace.getYScale().getRange();
        int yStart = (int)yRange[0];
        int yEnd = (int)yRange[yRange.length - 1];
        return new BRectangle(area.x, yEnd, area.width, yStart - yEnd);
    }

    private void arrangeButtons(BCanvas canvas) {
        Map<BRectangle, List<Trace>> areasToTraces = new HashMap<>();
        for (Trace trace : tracesToButtons.keySet()) {
            BRectangle traceArea = getTraceArea(trace);
            List<Trace> traces = areasToTraces.get(traceArea);
            if(traces == null) {
                traces = new ArrayList<>();
                areasToTraces.put(traceArea, traces);
            }
            traces.add(trace);
        }

        for (BRectangle area : areasToTraces.keySet()) {
            List<Trace> traces = areasToTraces.get(area);
            int n = 0;
            int btnsHeight = 0;
            int x;
            int y;
            if(halign == HorizontalAlign.RIGHT) {
                x = area.x + area.width;
                y = area.y;
            } else {
                x = area.x;
                y = area.y;
            }
            for (Trace trace : traces) {

                if(halign == HorizontalAlign.RIGHT) {
                    SwitchButton button = tracesToButtons.get(trace);
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

                } else {
                    SwitchButton button = tracesToButtons.get(trace);
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
                moveTracesButtons(traces, 0, area.height - btnsHeight);
            }
            if(valign == VerticalAlign.MIDDLE) {
                moveTracesButtons(traces, 0, (area.height - btnsHeight)/2);
            }
        }
        isDirty = false;
    }

    private void moveTracesButtons(List<Trace> traces, int dx, int dy) {
        if(dx != 0 || dy != 0) {
            for (Trace trace : traces) {
                tracesToButtons.get(trace).moveLocation(dx, dy);
            }
        }
    }

    public void draw(BCanvas canvas) {
        if (tracesToButtons.size() == 0) {
            return;
        }
        if(isDirty) {
            arrangeButtons(canvas);
        }
        for (Trace trace : tracesToButtons.keySet()){
            tracesToButtons.get(trace).setColor(trace.getMainColor());
        }
        canvas.setTextStyle(config.getTextStyle());
        for (Trace key : tracesToButtons.keySet()) {
           tracesToButtons.get(key).draw(canvas);
        }
    }

    private int getInterItemSpace() {
        return 0;
    }
}

