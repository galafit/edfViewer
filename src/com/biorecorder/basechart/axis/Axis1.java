package com.biorecorder.basechart.axis;

import com.biorecorder.basechart.Range;
import com.biorecorder.basechart.Text;
import com.biorecorder.basechart.graphics.BCanvas;
import com.biorecorder.basechart.graphics.BLine;
import com.biorecorder.basechart.graphics.TextMetric;
import com.biorecorder.basechart.scales.Scale;
import com.biorecorder.basechart.scales.Tick;
import com.biorecorder.basechart.scales.TickProvider;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Axis is visual representation of Scale that generates and draws
 * visual elements such as axis lines, labels, and ticks.
 * Also it is a wrapper class that gives simplified access to the Scale methods
 * and some advanced functionality such as Zooming and Translating. See
 * {@link #setConfig(AxisConfig)}
 */
public abstract class Axis1 {
    protected final int DEFAULT_TICK_COUNT = 16;
    protected final int[] AVAILABLE_TICKS_SKIP_STEPS = {2, 4, 5, 8}; // used to skip ticks if they overlap

    protected String title;
    protected Scale scale;
    protected AxisConfig config = new AxisConfig();

    // need this field to implement smooth zooming when minMaxRounding enabled
    protected Range rowMinMax; // without rounding

    protected Text titleText;
    protected Tick[] ticks;
    protected List<Double> minorTicks;
    protected List<BLine> tickLines;
    protected List<BLine> minorTickLines;
    protected List<Text> tickLabels;
    protected BLine axisLine;
    protected TickProvider tickProvider;

    public Axis1(Scale scale) {
        this.scale = scale.copy();
        rowMinMax = new Range(getMin(), getMax());
    }

    private void setDirty() {
        tickProvider = null;
        ticks = null;
        titleText = null;
        scale.setDomain(rowMinMax.getMin(), rowMinMax.getMax());
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        setDirty();
    }

    /**
     * set Axis scale. Inner scale is a COPY of the given scale
     * to prevent direct access from outside
     * @param scale
     */
    public void setScale(Scale scale) {
        this.scale = scale.copy();
        rowMinMax = new Range(getMin(), getMax());
        setDirty();
    }

    /**
     * get the COPY of inner scale
     * @return copy of inner scale
     */
    public Scale getScale() {
        return scale.copy();
    }

    /**
     * get the COPY of inner config. To change axis config
     * use {@link #setConfig(AxisConfig)}
     *
     * @return copy of inner config
     */
    public AxisConfig getConfig() {
        return new AxisConfig(config);
    }

    /**
     * set Axis config. Inner config is a COPY of the given config
     * to prevent direct access from outside
     * @param config
     */
    public void setConfig(AxisConfig config) {
        // set a copy to safely change
        this.config = new AxisConfig(config);
        setDirty();
    }

    /**
     * Zoom does not affect the axis scale!
     * It copies the axis scales and transforms its domain respectively.
     * Note: zoom affects only max value, min value does not change!!!
     * @param zoomFactor
     * @return new scale with transformed domain
     */
    public Scale zoom(double zoomFactor) {
        Scale zoomedScale = scale.copy();
        double min = rowMinMax.getMin();
        double max = rowMinMax.getMax();
        zoomedScale.setDomain(min, max);

        int start = getStart();
        int end = getEnd();

        double zoomedLength = (end - start) * zoomFactor;
        double zoomedEnd = start + zoomedLength;
        zoomedScale.setRange(start, zoomedEnd);
        double maxNew = zoomedScale.invert(end);
        zoomedScale.setDomain(min, maxNew);

        return zoomedScale;
    }


    /**
     * Zoom does not affect the axis scale!
     * It copies the axis scales and transforms its domain respectively.
     * @param translation
     * @return new scale with transformed domain
     */
    public Scale translate(int translation) {
        Scale translatedScale = scale.copy();
        double min = rowMinMax.getMin();
        double max = rowMinMax.getMax();
        translatedScale.setDomain(min, max);

        int start = getStart();
        int end = getEnd();
        double minNew = translatedScale.invert(start + translation);
        double maxNew = translatedScale.invert(end + translation);
        translatedScale.setDomain(minNew, maxNew);
        return translatedScale;
    }

    /**
     * Format domain value according to the range one "point precision"
     * cutting unnecessary double digits that exceeds that "point precision"
     */
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
        double[] domain = scale.getDomain();
        if(min == null && max != null) {
            min = domain[0];
            if(min >= max) {
                min = max - 1;
            }
        }
        if(min != null && max == null) {

            max = domain[domain.length - 1];
            if(min >= max) {
                max = min + 1;
            }
        }

        scale.setDomain(min, max);
        rowMinMax = new Range(getMin(), getMax());
        setDirty();
    }

    public void setStartEnd(double start, double end) {
        scale.setRange(start, end);
        setDirty();
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

    protected TickProvider getAndConfigTickProvider() {
        if(config.getTickStep() > 0) {
             return  scale.getTickProviderByStep(config.getTickStep(), config.getTickFormatInfo());
        }

        int fontFactor = 4;
        double tickPixelInterval = fontFactor * config.getTickLabelTextStyle().getSize();
        int tickCount = (int) (Math.abs(getStart() - getEnd()) / tickPixelInterval);
        tickCount = Math.max(tickCount, DEFAULT_TICK_COUNT);
        return scale.getTickProviderByCount(tickCount, config.getTickFormatInfo());
    }


    public int getWidth(BCanvas canvas) {
        if (!config.isVisible()) {
            return 0;
        }

        int size = 0;
        if(config.isAxisLineVisible()) {
            size += config.getStyle().getAxisLineStroke().getWidth() / 2;
        }

        if (config.isTicksVisible()) {
            size += config.getTickMarkOutsideSize();
        }
        if(config.isTickLabelsVisible() && !config.isTickLabelInside()) {
            size += config.getTickPadding() + getMaxTickLabelSize(canvas);

        }
        if (config.isTitleVisible() && titleText != null) {
            TextMetric tm = canvas.getTextMetric(config.getTitleTextStyle());
            size = size + config.getTitlePadding() + tm.height();
        }
        return size;
    }

    /**
     * check if ticks overlaps and return the min "skip step"
     * that permit fix overlapping.
     *
     * When step = n, only every n'th label on the axis will be shown.
     * When step = 2 every other label will be shown.
     */
    private int getTicksSkipStepToAvoidOverlap(BCanvas canvas, double tickPixelInterval) {
        double maxLabelSize = getMaxTickLabelSize(canvas);

        // assuming that min gap between labels = 2 symbols size (roughly)
        double minLabelGap = 2 * config.getTickLabelTextStyle().getSize();
        double requiredSpace = maxLabelSize + minLabelGap;

        // first and last labels we usually shift to avoid its cutting on the edge
        // to do that we need additional extra space
        requiredSpace += maxLabelSize / 2;

        double minStep = requiredSpace / tickPixelInterval;
        int skipStep = (int) minStep;
        if(skipStep > skipStep) {
            skipStep++;
        }
        return skipStep;
    }

    /**
     * Create ticks and minor ticks
     */
    private void createTicks(BCanvas canvas) {
        if(tickProvider == null) {
            tickProvider = getAndConfigTickProvider();
        }

        int maxTicksAmount = 500; // if bigger it means that there is some error

        double min = getMin();
        double max = getMax();

        List<Tick> ticksList = new ArrayList<Tick>();
        String errMsg = "Too many ticks. Ticks amount = " + ticksList.size() + " Permitted ticks amount <  "+ maxTicksAmount;
        Tick tick = tickProvider.getUpperTick(min);
        while(tick.getValue() <= max) {
            ticksList.add(tick);
            tick = tickProvider.getNextTick();
            if(ticksList.size() > maxTicksAmount) {
                 throw new RuntimeException(errMsg);
            }
        }

        // check tick labels overlapping
        double tickPixelInterval = Math.abs(scale(ticksList.get(0).getValue()) - scale(ticksList.get(1).getValue()));
        int ticksSkipStep = getTicksSkipStepToAvoidOverlap(canvas, tickPixelInterval);

        // choose ticksSkipStep from available ones
        for (int i = 0; i < AVAILABLE_TICKS_SKIP_STEPS.length ; i++) {
            if(ticksSkipStep <= AVAILABLE_TICKS_SKIP_STEPS[i]) {
                ticksSkipStep = AVAILABLE_TICKS_SKIP_STEPS[i];
                break;
            }
        }

        int skippedTickCount = ticksList.size() / ticksSkipStep + 1;
        // always add round min and max ticks to be able create minor grid
        if(skippedTickCount - 1 >= 3) { // create ticks for increased tickStep
           tickProvider.increaseTickStep(ticksSkipStep);
           ticksList.clear();
            tick = tickProvider.getLowerTick(min);
            while(tick.getValue() < max) {
                ticksList.add(tick);
                tick = tickProvider.getNextTick();
                if(ticksList.size() > maxTicksAmount) {
                    throw new RuntimeException(errMsg);
                }
            }
            // add closing max tick
            ticksList.add(tick);
            ticks = (Tick[]) ticksList.toArray();
        } else { // just add first and last ticks
            ticks = new Tick[2];
            ticks[0] = ticksList.get(0);
            ticks[1] = ticksList.get(ticksList.size() - 1);
        }
    }

    public void roundMinMax(BCanvas canvas) {
        if(ticks == null) {
            createTicks(canvas);
        }
        double min = getMin();
        double max = getMax();
        if(ticks[0].getValue() == min && ticks[ticks.length - 1].getValue() == max) {
            return;
        }

        int TICK_NUMBER_THRESHOLD = 5;

        if(ticks.length > TICK_NUMBER_THRESHOLD) {
            // add round min and max
            List<Tick> tickList = new ArrayList<>(ticks.length + 2);
            if(ticks[0].getValue() > min) {
                tickList.add(tickProvider.getLowerTick(min));
            }
            for (int i = 0; i < ticks.length; i++) {
                tickList.add(ticks[i]);
            }
            if(ticks[ticks.length - 1].getValue() < max) {
                tickList.add(tickProvider.getUpperTick(max));
            }
            ticks = (Tick[])tickList.toArray();
        } else {
            // create new precise ticks
            int maxTicksAmount = 500; // if bigger it means that there is some error

            TickProvider tickProvider = getAndConfigTickProvider();
            List<Tick> ticksList = new ArrayList<Tick>();
            Tick tick = tickProvider.getLowerTick(min);
            while(tick.getValue() < max) {
                ticksList.add(tick);
                tick = tickProvider.getNextTick();
                if(ticksList.size() > maxTicksAmount) {
                    String errMsg = "Too many ticks. Ticks amount = " + ticksList.size() + " Permitted ticks amount <  "+ maxTicksAmount;
                    throw new RuntimeException(errMsg);
                }
            }
            // add closing max tick
            ticksList.add(tick);

            // check tick labels overlapping
            double tickPixelInterval = Math.abs(scale(ticksList.get(0).getValue()) - scale(ticksList.get(1).getValue()));
            int ticksSkipStep = getTicksSkipStepToAvoidOverlap(canvas, tickPixelInterval);
            int skippedTickCount = ticksList.size() / ticksSkipStep + 1;
            if(skippedTickCount > 2) {
                if(ticksList.size() % 2 == 0) {
                    ticksList.add(tickProvider.getNextTick());
                }
                ticks = new Tick[3];
                ticks[0] = ticksList.get(0);
                ticks[1] = ticksList.get(ticksList.size() / 2 );
                ticks[2] = ticksList.get(ticksList.size() - 1);
            } else {
                ticks = new Tick[2];
                ticks[0] = ticksList.get(0);
                ticks[1] = ticksList.get(ticksList.size() - 1);
            }
        }
        scale.setDomain(ticks[0].getValue(), ticks[ticks.length - 1].getValue());
    }




    protected abstract int getMaxTickLabelSize(BCanvas canvas);



}
