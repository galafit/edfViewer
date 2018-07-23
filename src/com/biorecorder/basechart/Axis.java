package com.biorecorder.basechart;

import com.biorecorder.basechart.config.AxisConfig;
import com.biorecorder.basechart.config.AxisType;
import com.biorecorder.basechart.scales.*;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by galafit on 5/9/17.
 */
public class Axis {
    protected final int DEFAULT_TICK_COUNT = 30;

    private Scale scale;
    private Range rowMinMax; // without rounding
    private AxisConfig config;
    private List<Tick> ticks;
    private List<Double> minorTicks;
    private List<Line> tickLines;
    private List<Line> minorTickLines;
    private List<Text> tickLabels;
    private Line axisLine;
    private Text axisName;
    private TickProvider tickProvider;

    public Axis(AxisConfig config) {
        this.config = config;
        if(config.getAxisType() == AxisType.TIME) {
            scale = new TimeScale();
        } else {
            scale = new LinearScale();
        }
        rowMinMax = new Range(getMin(), getMax());
    }

    /**
     * Zoom affects only max value. Min value does not changed!!!
     * @param zoomFactor
     */
    public void zoom(double zoomFactor) {
        scale.setDomain(rowMinMax.getMin(), rowMinMax.getMax());
        int start = getStart();
        int end = getEnd();
        double min = getMin();
        int shift = (int)((end - start) * (zoomFactor - 1) / 2);
        //int newStart = getMin - shift;
        int newEnd = end + 2 * shift;
        //setStartEnd(newStart, newEnd);
        setStartEnd(start, newEnd);
       // double minNew = invert(getMin);
        double maxNew = invert(end);
        setMinMax(min, maxNew);
        scale.setRange(start, end);
    }

    public void translate(int translation) {
        setMinMax(rowMinMax.getMin(), rowMinMax.getMax());
        int start = getStart();
        int end = getEnd();
        double minNew = invert(start + translation);
        double maxNew = invert(end + translation);
        setMinMax(minNew, maxNew);
    }

    public String formatDomainValue(double value) {
        return scale.formatDomainValue(value);
    }

    public void setMinMax(Double min, Double max) {
        if(min == null && max == null) {
            return;
        }
        if (min!= null && max != null && min > max){
            String errorMessage = "Expected Min < Max. Min = {0}, Max = {1}.";
            String formattedError = MessageFormat.format(errorMessage,min,max);
            throw new IllegalArgumentException(formattedError);
        }
        if(min == null && max != null) {
            min = scale.getDomain()[0];
            if(min >= max) {
                min = max - 1;
            }
        }
        if(min != null && max == null) {
            max = scale.getDomain()[1];
            if(min >= max) {
                max = min + 1;
            }
        }

        scale.setDomain(min, max);
        rowMinMax = new Range(getMin(), getMax());
        ticks = null;
        tickProvider = null;
        axisName = null;
    }

    public void setStartEnd(float start, float end) {
        setMinMax(rowMinMax.getMin(), rowMinMax.getMax());
        scale.setRange(start, end);
        tickProvider = null;
        ticks = null;
        axisName = null;
    }

    public Scale getScale() {
        return scale;
    }


    public double getMin() {
        return scale.getDomain()[0];
    }

    public double getMax() {
        return scale.getDomain()[scale.getDomain().length -1];
    }

    public int getStart() {
        return (int)scale.getRange()[0];
    }

    public int getEnd() {
        return (int)scale.getRange()[scale.getRange().length -1];
    }

    public double scale(double value) {
        return scale.scale(value);
    }

    public double invert(float value) {
        return scale.invert(value);
    }

    public int getThickness(BCanvas canvas) {
        if (!config.isVisible()) {
            return 0;
        }

        int size = 0;
        if(config.isAxisLineVisible()) {
            size += config.getAxisLineStroke().getWidth() / 2;
        }

        if (config.isTicksVisible()) {
            size += config.getTickMarkOutsideSize();
        }
        if(config.isLabelsVisible() && !config.isLabelInside()) {
            int labelsSize = 0;
            TextMetric tm = canvas.getTextMetric(config.getLabelTextStyle());
            if(isHorizontal()) { // horizontal axis
                labelsSize = tm.height();
            } else { // vertical axis
                Tick minTick, maxTick;
                if(tickProvider == null) {
                    tickProvider = getTickProvider();
                }
                if(config.isMinMaxRoundingEnable()) {
                   minTick = tickProvider.getLowerTick(getMin());
                   maxTick = tickProvider.getUpperTick(getMax());
                } else {
                    minTick = tickProvider.getUpperTick(getMin());
                    maxTick = tickProvider.getLowerTick(getMax());
                }
                labelsSize = Math.max(tm.stringWidth(minTick.getLabel()), tm.stringWidth(maxTick.getLabel()));
            }
            size += config.getLabelPadding() + labelsSize;
        }
        if (config.getTitle() != null) {
            TextMetric tm = canvas.getTextMetric(config.getTitleTextStyle());
            size = size + config.getTitlePadding() + tm.height();
        }
        return size;
    }

    private boolean isHorizontal() {
        if(config.isTop() || config.isBottom()) {
            return true;
        }
        return false;
    }

    private TickProvider getTickProvider() {
        if(config.getTickStep() > 0) {
            return  scale.getTickProvider(config.getTickStep(), config.getTickStepUnit(), config.getLabelFormatInfo());
        }

        int fontFactor = 4;
        double tickPixelInterval = fontFactor * config.getLabelTextStyle().getSize();
        int tickCount = (int) (Math.abs(getStart() - getEnd()) / tickPixelInterval);
        tickCount = Math.max(tickCount, DEFAULT_TICK_COUNT);
        return scale.getTickProvider(tickCount, config.getLabelFormatInfo());
    }


    private Line tickToGridLine(double tickValue, int length) {
        if(config.isTop()) {
            int x = (int)scale(tickValue);
            int y1 = 0;
            int y2 = length;
            return new Line(x, y1, x, y2);
        }
        if(config.isBottom()) {
            int x = (int)scale(tickValue);
            int y1 = 0;
            int y2 = -length;
            return new Line(x, y1, x, y2);
        }
        if(config.isLeft()) {
            int y = (int)scale(tickValue);
            int x1 = 0;
            int x2 = length;
            return new Line(x1, y, x2, y);
        }
        // if config.isRight()
        int y = (int)scale(tickValue);
        int x1 = 0;
        int x2 = -length;
        return new Line(x1, y, x2, y);
    }

    private Line tickToMarkLine(Tick tick) {
        int axisWidth = config.getAxisLineStroke().getWidth();
        if(config.isTop()) {
            int x = (int)scale(tick.getValue());
            int y1 = -axisWidth / 2 - config.getTickMarkOutsideSize();
            int y2 = axisWidth / 2 + config.getTickMarkInsideSize();
            return new Line(x, y1, x, y2);
        }
        if(config.isBottom()) {
            int x = (int)scale(tick.getValue());
            int y1 = axisWidth / 2 + config.getTickMarkOutsideSize();
            int y2 = -axisWidth / 2 - config.getTickMarkInsideSize();
            return new Line(x, y1, x, y2);
        }
        if(config.isLeft()) {
            int y = (int)scale(tick.getValue());
            int x1 = -axisWidth / 2 - config.getTickMarkOutsideSize();
            int x2 = axisWidth / 2 + config.getTickMarkInsideSize();
            return new Line(x1, y, x2, y);
        }
        // if config.isRight()
        int y = (int)scale(tick.getValue());
        int x1 = axisWidth / 2 + config.getTickMarkOutsideSize();
        int x2 = -axisWidth / 2 - config.getTickMarkInsideSize();
        return new Line(x1, y, x2, y);
    }

    private Line minorTickToMarkLine(double minorTickValue) {
        int axisWidth = config.getAxisLineStroke().getWidth();
        if(config.isTop()) {
            int x = (int)scale(minorTickValue);
            int y1 = -axisWidth / 2 - config.getMinorTickMarkOutsideSize();
            int y2 = axisWidth / 2 + config.getMinorTickMarkInsideSize();
            return new Line(x, y1, x, y2);
        }
        if(config.isBottom()) {
            int x = (int)scale(minorTickValue);
            int y1 = axisWidth / 2 + config.getMinorTickMarkOutsideSize();
            int y2 = -axisWidth / 2 - config.getMinorTickMarkInsideSize();
            return new Line(x, y1, x, y2);
        }
        if(config.isLeft()) {
            int y = (int)scale(minorTickValue);
            int x1 = -axisWidth / 2 - config.getMinorTickMarkOutsideSize();
            int x2 = axisWidth / 2 + config.getMinorTickMarkInsideSize();
            return new Line(x1, y, x2, y);
        }
        // if config.isRight()
        int y = (int)scale(minorTickValue);
        int x1 = axisWidth / 2 + config.getMinorTickMarkOutsideSize();
        int x2 = -axisWidth / 2 - config.getMinorTickMarkInsideSize();
        return new Line(x1, y, x2, y);
    }


    private Text tickToLabel(Tick tick, TextMetric tm) {
        int axisWidth = config.getAxisLineStroke().getWidth();
        int labelPadding = config.getLabelPadding();
        int space = 2;// px
        int charHalfWidth = tm.stringWidth("0")/2;
        if(config.isTop()) {
            if(config.isLabelInside()) {
                int x = (int)scale(tick.getValue()) + space;
                int y = axisWidth / 2 + labelPadding;
                return new Text(tick.getLabel(), x, y, TextAnchor.START, TextAnchor.END, tm);

            } else {
                int x = (int)scale(tick.getValue()) - charHalfWidth;
                if(x < getStart()) {
                    x = getStart() + space;
                }
                int y = -axisWidth / 2 - config.getTickMarkOutsideSize() - labelPadding;
                return new Text(tick.getLabel(), x, y, TextAnchor.START, TextAnchor.START, tm);
            }
         }
        if(config.isBottom()) {
            if(config.isLabelInside()) {
                int x = (int)scale(tick.getValue()) + space;
                int y = -axisWidth / 2 - labelPadding;
                return new Text(tick.getLabel(), x, y, TextAnchor.START, TextAnchor.START, tm);

            } else {
                int x = (int)scale(tick.getValue()) - charHalfWidth;
                if(x < getStart()) {
                    x = getStart() + space;
                }
                int y = axisWidth / 2 + config.getTickMarkOutsideSize() + labelPadding;
                return new Text(tick.getLabel(), x, y, TextAnchor.START, TextAnchor.END, tm);
            }
        }
        if(config.isLeft()) {
            if(config.isLabelInside()) {
                int y = (int)scale(tick.getValue()) - space;
                int x = axisWidth / 2 + labelPadding;
                int labelHeight = tm.height();

                if(y - labelHeight/2 - 1 < getEnd()) {
                    y += labelPadding;
                    return new Text(tick.getLabel(), x, y, TextAnchor.START, TextAnchor.END, tm);
                }
                return new Text(tick.getLabel(), x, y, TextAnchor.START, TextAnchor.START, tm);

            } else {
                int y = (int)scale(tick.getValue());
                int x = -axisWidth / 2 - config.getTickMarkInsideSize() - labelPadding;
                int labelHeight = tm.height();
                if(y + labelHeight/2 + 1 > getStart()) {
                    y -= space;
                    return new Text(tick.getLabel(), x, y, TextAnchor.END, TextAnchor.START, tm);
                }
                if(y - labelHeight/2 - 1 < getEnd()) {
                    return new Text(tick.getLabel(), x, y, TextAnchor.END, TextAnchor.END, tm);
                }
                return new Text(tick.getLabel(), x, y, TextAnchor.END, TextAnchor.MIDDLE, tm);

            }
        }
        // if config.isRight()
        if(config.isLabelInside()) {
            int y = (int)scale(tick.getValue()) - space;
            int x = - axisWidth / 2 - labelPadding;
            int labelHeight = tm.height();

            if(y - labelHeight/2 - 1 < getEnd()) {
                y += space;
                return new Text(tick.getLabel(), x, y, TextAnchor.END, TextAnchor.END, tm);
            }
            return new Text(tick.getLabel(), x, y, TextAnchor.END, TextAnchor.START, tm);

        } else {
            int y = (int)scale(tick.getValue());
            int x = axisWidth / 2 + config.getTickMarkInsideSize() + labelPadding;
            int labelHeight = tm.height();
            if(y + labelHeight/2 + 1 > getStart()) {
                y -= labelPadding;
                return new Text(tick.getLabel(), x, y, TextAnchor.START, TextAnchor.START, tm);
            }
            if(y - labelHeight/2 - 1 < getEnd()) {
                return new Text(tick.getLabel(), x, y, TextAnchor.START, TextAnchor.END, tm);
            }
            return new Text(tick.getLabel(), x, y, TextAnchor.START, TextAnchor.MIDDLE, tm);

        }
    }

    private int getTicksDivider(BCanvas canvas, List<Tick> ticks) {
        if(ticks.size() < 2) {
            return 1;
        }
        TextMetric tm = canvas.getTextMetric(config.getLabelTextStyle());
        double labelsSize;
        if(isHorizontal()) {
            labelsSize = Math.max(tm.stringWidth(ticks.get(0).getLabel()), tm.stringWidth(ticks.get(ticks.size() - 1).getLabel()));
        } else {
            labelsSize = tm.height();
        }

        double tickPixelInterval = Math.abs(scale(ticks.get(0).getValue()) - scale(ticks.get(1).getValue()));
        // getMin space between labels = 1 symbols size (roughly)
        double labelSpace = 2 * config.getLabelTextStyle().getSize();
        double requiredSpace = labelsSize + labelSpace;
        int ticksDivider = (int) (requiredSpace / tickPixelInterval) + 1;
        return ticksDivider;
    }


    /**
     * Create ticks and minor ticks
     */
    private void createTicks(BCanvas canvas) {
        ticks = getTicks(canvas);
        if(config.isMinMaxRoundingEnable()) {
            getScale().setDomain(ticks.get(0).getValue(), ticks.get(ticks.size() - 1).getValue());
        }

        minorTicks = new ArrayList<Double>();
        for (int i = 0; i < ticks.size() - 1; i++) {
            double minorTickStep = (ticks.get(i+1).getValue() - ticks.get(i).getValue()) / config.getMinorGridCounter();
            double minorTickValue =  ticks.get(i).getValue();
            for (int j = 1; j < config.getMinorGridCounter(); j++) {
                minorTickValue += minorTickStep;
                if(minorTickValue >= getMin() && minorTickValue <=getMax()) {
                    minorTicks.add(minorTickValue);
                }
            }
        }

        if(ticks.get(0).getValue() < getMin()) {
            ticks.remove(0);
        }
        if(ticks.get(ticks.size() - 1).getValue() > getMax()) {
            ticks.remove(ticks.size() - 1);
        }
    }

    private List<Tick> getTicks(BCanvas canvas) {
        if(tickProvider == null) {
            tickProvider = getTickProvider();
        }
        ArrayList<Tick> allTicks = new ArrayList<Tick>();
        int maxTicksAmount = 500; // if bigger it means that there is some error

        Tick tick = tickProvider.getLowerTick(getMin());
        for (int i = 0; i < maxTicksAmount; i++) {
            if(tick.getValue() < getMax()) {
                allTicks.add(tick);
                tick = tickProvider.getNextTick();
            } else {
                break;
            }
        }
        allTicks.add(tick);
        int tickDivider = getTicksDivider(canvas, allTicks);
        int MIN_TICK_NUMBER1 = 2;
        int MIN_TICK_NUMBER2 = 4;
        double tickSpaceCount = (allTicks.size() - 1) / tickDivider;
        if(config.isMinMaxRoundingEnable()) {
            // если есть округление и тиков < MIN_TICK_NUMBER1 то оставляем только первый и последний
            if (tickSpaceCount < MIN_TICK_NUMBER1) {
                ArrayList<Tick> resultantTicks = new ArrayList<Tick>(2);
                resultantTicks.add(allTicks.get(0));
                resultantTicks.add(allTicks.get(allTicks.size() - 1));
                scale.setDomain(resultantTicks.get(0).getValue(), resultantTicks.get(resultantTicks.size() - 1).getValue());
                return resultantTicks;
            }
            // если есть округление и тиков < MIN_TICK_NUMBER2 то оставляем  первый, последний и средний
            if (tickSpaceCount < MIN_TICK_NUMBER2 && tickSpaceCount >= MIN_TICK_NUMBER1) {
                ArrayList<Tick> resultantTicks = new ArrayList<Tick>(3);
                if ((allTicks.size() - 1) % 2 != 0) {
                    tickProvider.getUpperTick(allTicks.get(allTicks.size() - 1).getValue());
                    allTicks.add(tickProvider.getNextTick());
                }
                int middleTickNumber = (allTicks.size() - 1) / 2;
                resultantTicks.add(allTicks.get(0));
                resultantTicks.add(allTicks.get(middleTickNumber));
                resultantTicks.add(allTicks.get(allTicks.size() - 1));
                scale.setDomain(resultantTicks.get(0).getValue(), resultantTicks.get(resultantTicks.size() - 1).getValue());
                return resultantTicks;
            }
            ArrayList<Tick> resultantTicks = new ArrayList<Tick>();
            // если есть округление и тиков >= MIN_TICK_NUMBER2 оставляем только тики через tickDivider
            for (int i = 0; i < allTicks.size(); i++) {
                if (i % tickDivider == 0) {
                    resultantTicks.add(allTicks.get(i));
                }
            }
            tickProvider.getLowerTick(getMax());
            if ((allTicks.size() - 1) % tickDivider > 0) {
                for (int i = 0; i <= tickDivider - (allTicks.size() - 1) % tickDivider; i++) {
                    tick = tickProvider.getNextTick();
                }
                resultantTicks.add(tick);
            }
            return resultantTicks;
        }

        // если нет округления то  увеличиваем  tick step в tickDivider раз
        ArrayList<Tick> resultantTicks = new ArrayList<Tick>();
        tickProvider.increaseTickStep(tickDivider);
        resultantTicks = new ArrayList<Tick>();

        tick = tickProvider.getLowerTick(getMin());
        for (int i = 0; i < maxTicksAmount; i++) {
            if(tick.getValue() < getMax()) {
                resultantTicks.add(tick);
                tick = tickProvider.getNextTick();
            } else {
                break;
            }
        }
        resultantTicks.add(tick);
        return resultantTicks;
    }

    private void createAxisElements(BCanvas canvas) {
        if(ticks == null) {
            createTicks(canvas);
        }
        // tick lines
        tickLines = new ArrayList<Line>();
        if(config.isTicksVisible()) {
            for (Tick tick : ticks) {
                tickLines.add(tickToMarkLine(tick));
            }
        }
        // minor tick lines
        minorTickLines = new ArrayList<Line>();
        if(config.isMinorTicksVisible()) {
            for (Double minorTick : minorTicks) {
                minorTickLines.add(minorTickToMarkLine(minorTick));
            }
        }

        // tick labels
        TextMetric tm = canvas.getTextMetric(config.getLabelTextStyle());
        tickLabels = new ArrayList<Text>();
        if(config.isLabelsVisible()) {
            for (Tick tick : ticks) {
                tickLabels.add(tickToLabel(tick, tm));
            }
        }

        // axis line
        if (isHorizontal()) {
            axisLine = new Line(getStart(), 0, getEnd(), 0);
        } else {
            axisLine = new Line(0, getStart(), 0, getEnd());
        }
    }

    private void createTitle(BCanvas canvas, int AxisThickness) {
        TextMetric tm = canvas.getTextMetric(config.getTitleTextStyle());
        if(config.isTop()) {
            int y = - AxisThickness + tm.height();
            int x = (getEnd() + getStart()) / 2;
            axisName = new Text(config.getTitle(), x, y, TextAnchor.MIDDLE, TextAnchor.START, tm);
        }
        if(config.isBottom()) {
            int y = AxisThickness - tm.height() / 2;
            int x = (getEnd() + getStart()) / 2;
            axisName = new Text(config.getTitle(), x, y, TextAnchor.MIDDLE, TextAnchor.MIDDLE, tm);
        }
        if(config.isLeft()) {
            int x = -AxisThickness;
            int y = (getEnd() + getStart()) / 2;
            axisName = new Text(config.getTitle(), x, y, TextAnchor.END, TextAnchor.MIDDLE, -90, tm);
        }
        if(config.isRight()) {
            int x = AxisThickness;
            int y = (getEnd() + getStart()) / 2;
            axisName = new Text(config.getTitle(), x, y, TextAnchor.END, TextAnchor.MIDDLE, +90, tm);
        }
    }

    private int getMaxTickLabelsWidth(TextMetric tm, List<Tick> ticks) {
        int maxSize = 0;
        for (Tick tick : ticks) {
            maxSize = Math.max(maxSize, tm.stringWidth(tick.getLabel()));
        }
        return maxSize;
    }

    public void drawGrid(BCanvas canvas, int axisOriginPoint, int length) {
        canvas.save();
        if(!config.isVisible()) {
            return;
        }
        if(ticks == null) {
            createAxisElements(canvas);
        }
        if(isHorizontal()) {
            canvas.translate(0, axisOriginPoint);
        } else {
            canvas.translate(axisOriginPoint, 0);
        }

        canvas.setColor(config.getMinorGridColor());
        canvas.setStroke(config.getMinorGridLineStroke());
        if(config.isMinorGridVisible()) {
            for (double minorTick : minorTicks) {
                Line mGridLine = tickToGridLine(minorTick, length);
                canvas.drawLine(mGridLine.getX1(), mGridLine.getY1(), mGridLine.getX2(), mGridLine.getY2());
            }
        }

        canvas.setColor(config.getGridColor());
        canvas.setStroke(config.getGridLineStroke());
        if(config.isGridVisible()) {
            for (Tick tick : ticks) {
                Line gridLine = tickToGridLine(tick.getValue(), length);
                canvas.drawLine(gridLine.getX1(), gridLine.getY1(), gridLine.getX2(), gridLine.getY2());
            }
        }
        canvas.restore();
    }

    public void drawAxis(BCanvas canvas,  int axisOriginPoint, int AxisThickness) {
        canvas.save();
         if(!config.isVisible()) {
            return;
        }
        if(ticks == null) {
            createAxisElements(canvas);
        }
        if(axisName == null) {
             createTitle(canvas, AxisThickness);
        }
        if(isHorizontal()) {
            canvas.translate(0, axisOriginPoint);
        } else {
            canvas.translate(axisOriginPoint, 0);
        }
        canvas.setColor(config.getTicksColor());
        canvas.setStroke(new BStroke(config.getTickMarkWidth()));
        for (Line tickLine : tickLines) {
            canvas.drawLine(tickLine.getX1(), tickLine.getY1(), tickLine.getX2(), tickLine.getY2());
        }
        canvas.setStroke(new BStroke(config.getMinorTickMarkWidth()));
        for (Line minorTickLine : minorTickLines) {
            canvas.drawLine(minorTickLine.getX1(), minorTickLine.getY1(), minorTickLine.getX2(), minorTickLine.getY2());
        }

        if(config.isAxisLineVisible()) {
            canvas.setColor(config.getColor());
            canvas.setStroke(config.getAxisLineStroke());
            canvas.drawLine(axisLine.getX1(), axisLine.getY1(), axisLine.getX2(), axisLine.getY2());
        }

        canvas.setStroke(new BStroke(1));
        canvas.setColor(config.getLabelsColor());
        canvas.setTextStyle(config.getLabelTextStyle());
        for (Text tickLabel : tickLabels) {
            tickLabel.draw(canvas);
        }

        if(config.getTitle() != null) {
            canvas.setColor(config.getTitleColor());
            canvas.setTextStyle(config.getTitleTextStyle());
            axisName.draw(canvas);
        }
        canvas.restore();
    }

}

