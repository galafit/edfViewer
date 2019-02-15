package com.biorecorder.basechart;

import com.biorecorder.basechart.button.StateListener;
import com.biorecorder.basechart.button.SwitchButton;
import com.biorecorder.basechart.button.ButtonGroup;
import com.biorecorder.basechart.graphics.BCanvas;
import com.biorecorder.basechart.graphics.BRectangle;
import com.biorecorder.basechart.graphics.HorizontalAlign;
import com.biorecorder.basechart.graphics.VerticalAlign;
import com.biorecorder.basechart.traces.Trace;
import com.biorecorder.basechart.traces.TraceCurve;

import java.util.*;


/**
 * Created by hdablin on 11.08.17.
 */
public class Legend {
    private ButtonGroup buttonGroup;
    // only LinkedHashMap will iterate in the order in which the entries were put into the map
    private Map<TraceCurve, SwitchButton> traceCurvesToButtons = new LinkedHashMap<>();
    private BRectangle area;
    private int height;

    private int spacing = 2; //px

    private LegendConfig config;
    private boolean isDirty = true;

    public Legend(LegendConfig legendConfig) {
        this.config = legendConfig;
        this.buttonGroup = new ButtonGroup();
    }

    public int getHeight(BCanvas canvas) {
        if(!config.isAttachedToStacks()) {
            if(isDirty) {
                arrangeButtons(canvas);
            }
            return height;
        }
        return 0;
    }

    public boolean isAttachedToStacks() {
        return config.isAttachedToStacks();
    }

    public boolean isTop() {
        if(config.getVerticalAlign() == VerticalAlign.TOP) {
            return true;
        }
        return false;
    }

    public boolean isBottom() {
        if(config.getVerticalAlign() == VerticalAlign.BOTTOM) {
            return true;
        }
        return false;
    }

    public boolean selectItem(int x, int y) {
        for (TraceCurve key : traceCurvesToButtons.keySet()){
            SwitchButton button = traceCurvesToButtons.get(key);
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
        for (TraceCurve key : traceCurvesToButtons.keySet()){
            SwitchButton button = traceCurvesToButtons.get(key);
            button.setBackgroundColor(config.getBackgroundColor());
            button.setTextStyle(config.getTextStyle());
            button.setMargin(config.getItemMargin());
        }
        isDirty = true;
    }

    public void add(Trace trace, int curveNumber, StateListener traceSelectionListener) {
        // add curve legend button
        TraceCurve traceCurve = new TraceCurve(trace, curveNumber);
        SwitchButton traceButton = new SwitchButton(trace.getCurveName(curveNumber));
        traceButton.addListener(traceSelectionListener);
        traceCurvesToButtons.put(traceCurve, traceButton);
        buttonGroup.add(traceButton.getModel());
        traceButton.setBackgroundColor(config.getBackgroundColor());
        traceButton.setTextStyle(config.getTextStyle());
        traceButton.setMargin(config.getItemMargin());
        isDirty = true;
    }

    public void remove(Trace trace) {
        SwitchButton traceButton = traceCurvesToButtons.get(trace);
        buttonGroup.remove(traceButton.getModel());
        traceCurvesToButtons.remove(trace);
        isDirty = true;
    }

    private BRectangle getTraceArea(TraceCurve traceCurve) {
        if(!config.isAttachedToStacks()) {
            return  area;
        }
        double[] yRange = traceCurve.getTrace().getYScale(traceCurve.getCurveNumber()).getRange();
        int yStart = (int)yRange[0];
        int yEnd = (int)yRange[yRange.length - 1];
        return new BRectangle(area.x, yEnd, area.width, yStart - yEnd);
    }

    private void arrangeButtons(BCanvas canvas) {
        // only LinkedHashMap will iterate in the order in which the entries were put into the map
        Map<BRectangle, List<TraceCurve>> areasToTraces = new LinkedHashMap<>();
        for (TraceCurve traceCurve : traceCurvesToButtons.keySet()) {
            BRectangle traceArea = getTraceArea(traceCurve);
            List<TraceCurve> traceCurves = areasToTraces.get(traceArea);
            if(traceCurves == null) {
                traceCurves = new ArrayList<>();
                areasToTraces.put(traceArea, traceCurves);
            }
            traceCurves.add(traceCurve);
        }

        List<SwitchButton> lineButtons = new ArrayList<SwitchButton>();

        for (BRectangle area : areasToTraces.keySet()) {
            List<TraceCurve> traceCurves = areasToTraces.get(area);
            height = 0;
            int width = 0;
            int x = area.x + spacing;
            int y = area.y + spacing;
            for (TraceCurve traceCurve : traceCurves) {
                SwitchButton button = traceCurvesToButtons.get(traceCurve);
                BRectangle btnArea = button.getBounds(canvas);
                if(height == 0) {
                    height = btnArea.height;
                    lineButtons.clear();
                }
                if(lineButtons.size() > 0 && x + getInterItemSpace() + btnArea.width >= area.x + area.width) {
                    width += (lineButtons.size() - 1) * getInterItemSpace();
                    if(config.getHorizontalAlign() == HorizontalAlign.RIGHT) {
                        moveButtons(lineButtons, area.width - width - 2 * spacing,0);
                    }
                    if(config.getHorizontalAlign() == HorizontalAlign.CENTER) {
                        moveButtons(lineButtons, (area.width - width) / 2 - spacing,0);
                    }

                    x = area.x + spacing;
                    y += btnArea.height + getInterLineSpace();
                    button.setLocation(x, y, canvas);

                    x += btnArea.width + getInterItemSpace();
                    height += btnArea.height + getInterLineSpace();
                    width = btnArea.width;
                    lineButtons.clear();
                    lineButtons.add(button);
                } else {
                    button.setLocation(x, y, canvas);
                    x += getInterItemSpace() + btnArea.width;
                    width += btnArea.width;
                    lineButtons.add(button);
                }
            }
            width += (lineButtons.size() - 1) * getInterItemSpace();
            if(config.getHorizontalAlign() == HorizontalAlign.RIGHT) {
                moveButtons(lineButtons, area.width - width - 2 * spacing,0);
            }
            if(config.getHorizontalAlign() == HorizontalAlign.CENTER) {
                moveButtons(lineButtons, (area.width - width) / 2 - spacing,0);
            }
            if(config.getVerticalAlign() == VerticalAlign.BOTTOM) {
                moveTracesButtons(traceCurves, 0, area.height - height - 2 * spacing);
            }
            if(config.getVerticalAlign() == VerticalAlign.MIDDLE) {
                moveTracesButtons(traceCurves, 0, (area.height - height)/2 - spacing);
            }
        }
        isDirty = false;
    }

    private void moveTracesButtons(List<TraceCurve> curves, int dx, int dy) {
        if(dx != 0 || dy != 0) {
            for (TraceCurve curve : curves) {
                traceCurvesToButtons.get(curve).moveLocation(dx, dy);
            }
        }
    }
    private void moveButtons(List<SwitchButton> buttons, int dx, int dy) {
        if(dx != 0 || dy != 0) {
            for (SwitchButton button : buttons) {
                button.moveLocation(dx, dy);
            }
        }
    }



    public void draw(BCanvas canvas) {
        if (traceCurvesToButtons.size() == 0) {
            return;
        }
        if(isDirty) {
            arrangeButtons(canvas);
        }
        for (TraceCurve traceCurve : traceCurvesToButtons.keySet()){
            traceCurvesToButtons.get(traceCurve).setColor(traceCurve.getTrace().getCurveMainColor(traceCurve.getCurveNumber()));
        }
        canvas.setTextStyle(config.getTextStyle());
        for (TraceCurve key : traceCurvesToButtons.keySet()) {
           traceCurvesToButtons.get(key).draw(canvas);
        }
    }

    private int getInterItemSpace() {
        return 0;
    }
    private int getInterLineSpace() {
        return 1;
    }
}

