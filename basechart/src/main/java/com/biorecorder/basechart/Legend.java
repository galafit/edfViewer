package com.biorecorder.basechart;

import com.biorecorder.basechart.button.StateListener;
import com.biorecorder.basechart.button.SwitchButton;
import com.biorecorder.basechart.button.ButtonGroup;
import com.biorecorder.basechart.graphics.*;

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

    private LegendConfig config;
    private boolean isDirty = true;

    public Legend(LegendConfig legendConfig) {
        this.config = legendConfig;
        this.buttonGroup = new ButtonGroup();
    }

    public boolean isEnabled() {
        return config.isEnabled();
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

    public void setCurveName(Trace trace, int curveNumber, String name) {
        for (TraceCurve key : traceCurvesToButtons.keySet()){
            if(key.getTrace() == trace && key.getCurve() == curveNumber) {
                SwitchButton button = traceCurvesToButtons.get(key);
                button.setLabel(name);
                isDirty = true;
                return;
            }
        }
    }


    public void setConfig(LegendConfig legendConfig) {
        config = legendConfig;
        for (TraceCurve key : traceCurvesToButtons.keySet()){
            SwitchButton button = traceCurvesToButtons.get(key);
            button.setBackgroundColor(config.getBackgroundColor());
            button.setTextStyle(config.getTextStyle());
            button.setMargin(config.getPadding());
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
        traceButton.setMargin(config.getPadding());
        isDirty = true;
    }

    public void remove(Trace trace) {
        for (int i = 0; i < trace.curveCount(); i++) {
            TraceCurve traceCurve = new TraceCurve(trace, i);
            SwitchButton traceButton = traceCurvesToButtons.get(traceCurve);
            buttonGroup.remove(traceButton.getModel());
            traceCurvesToButtons.remove(traceCurve);
        }

        isDirty = true;
    }

    private BRectangle getTraceArea(TraceCurve traceCurve) {
        if(!config.isAttachedToStacks()) {
            return  area;
        }
        double[] yRange = traceCurve.getTrace().getYScale(traceCurve.getCurve()).getRange();
        int yStart = (int)yRange[0];
        int yEnd = (int)yRange[yRange.length - 1];
        return new BRectangle(area.x, yEnd, area.width, Math.abs(yStart - yEnd));
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
        Insets margin = config.getMargin();
        for (BRectangle area : areasToTraces.keySet()) {
            List<TraceCurve> traceCurves = areasToTraces.get(area);
            height = 0;
            int width = 0;
            int x = area.x;
            int y = area.y;
            for (TraceCurve traceCurve : traceCurves) {
                SwitchButton button = traceCurvesToButtons.get(traceCurve);
                BRectangle btnArea = button.getBounds(canvas);
                if(height == 0) {
                    height = btnArea.height;
                    lineButtons.clear();
                }
                if(lineButtons.size() > 0 && x + config.getInterItemSpace() + btnArea.width >= area.x + area.width - margin.left() - margin.right()) {
                    width += (lineButtons.size() - 1) * config.getInterItemSpace();
                    if(config.getHorizontalAlign() == HorizontalAlign.LEFT) {
                        moveButtons(lineButtons, margin.left(),0);
                    }
                    if(config.getHorizontalAlign() == HorizontalAlign.RIGHT) {
                        moveButtons(lineButtons, area.width - width - margin.right(),0);
                    }
                    if(config.getHorizontalAlign() == HorizontalAlign.CENTER) {
                        moveButtons(lineButtons, (area.width - width) / 2,0);
                    }

                    x = area.x;
                    y += btnArea.height + config.getInterLineSpace();
                    button.setLocation(x, y, canvas);

                    x += btnArea.width + config.getInterItemSpace();
                    height += btnArea.height + config.getInterLineSpace();
                    width = btnArea.width;
                    lineButtons.clear();
                    lineButtons.add(button);
                } else {
                    button.setLocation(x, y, canvas);
                    x += config.getInterItemSpace() + btnArea.width;
                    width += btnArea.width;
                    lineButtons.add(button);
                }
            }
            width += (lineButtons.size() - 1) * config.getInterItemSpace();
            if(config.getHorizontalAlign() == HorizontalAlign.LEFT) {
                moveButtons(lineButtons, margin.left(),0);
            }
            if(config.getHorizontalAlign() == HorizontalAlign.RIGHT) {
                moveButtons(lineButtons, area.width - width - margin.right(),0);
            }
            if(config.getHorizontalAlign() == HorizontalAlign.CENTER) {
                moveButtons(lineButtons, (area.width - width) / 2,0);
            }

            if(config.getVerticalAlign() == VerticalAlign.TOP) {
                moveTracesButtons(traceCurves, 0, margin.top());
            }
            if(config.getVerticalAlign() == VerticalAlign.BOTTOM) {
                moveTracesButtons(traceCurves, 0, area.height - height - margin.bottom());
            }
            if(config.getVerticalAlign() == VerticalAlign.MIDDLE) {
                moveTracesButtons(traceCurves, 0, (area.height - height)/2);
            }
        }
        height += margin.top() + margin.bottom();
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
            traceCurvesToButtons.get(traceCurve).setColor(traceCurve.getTrace().getCurveColor(traceCurve.getCurve()));
        }
        canvas.setTextStyle(config.getTextStyle());
        for (TraceCurve key : traceCurvesToButtons.keySet()) {
           traceCurvesToButtons.get(key).draw(canvas);
        }
    }
}

