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
    protected final int[] TICKS_AVAILABLE_SKIP_STEPS = {2, 4, 5, 8, 10, 16, 20, 32, 40, 64, 80, 100}; // used to skip ticks if they overlap
    protected final double TICKS_ROUNDING_UNCERTAINTY = 0.2; // 20% for one side
    protected int MAX_TICKS_COUNT = 500; // if bigger it means that there is some error

    private final String TOO_MANY_TICKS_MSG = "Too many ticks: {0}. Expected < {1}";

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
     *
     * @param scale
     */
    public void setScale(Scale scale) {
        this.scale = scale.copy();
        rowMinMax = new Range(getMin(), getMax());
        setDirty();
    }

    /**
     * get the COPY of inner scale
     *
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
     *
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
     *
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
     *
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
        if (min == null && max == null) {
            return;
        }
        if (min != null && max != null && min > max) {
            String errorMessage = "Expected Min < Max. Min = {0}, Max = {1}.";
            String formattedError = MessageFormat.format(errorMessage, min, max);
            throw new IllegalArgumentException(formattedError);
        }
        double[] domain = scale.getDomain();
        if (min == null && max != null) {
            min = domain[0];
            if (min >= max) {
                min = max - 1;
            }
        }
        if (min != null && max == null) {

            max = domain[domain.length - 1];
            if (min >= max) {
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
        return scale.getDomain()[scale.getDomain().length - 1];
    }

    public int getStart() {
        return (int) scale.getRange()[0];
    }

    public int getEnd() {
        return (int) scale.getRange()[scale.getRange().length - 1];
    }

    public double scale(double value) {
        return scale.scale(value);
    }

    public double invert(float value) {
        return scale.invert(value);
    }



    public int getWidth(BCanvas canvas) {
        if (!config.isVisible()) {
            return 0;
        }

        int size = 0;
        if (config.isAxisLineVisible()) {
            size += config.getStyle().getAxisLineStroke().getWidth() / 2;
        }

        if (config.isTicksVisible()) {
            size += config.getTickMarkOutsideSize();
        }
        if (config.isTickLabelsVisible() && !config.isTickLabelInside()) {
            TextMetric tm = canvas.getTextMetric(config.getTickLabelTextStyle());
            size += config.getTickPadding() + labelsSizeForWidth(tm, 0);
        }
        if (config.isTitleVisible() && titleText != null) {
            TextMetric tm = canvas.getTextMetric(config.getTitleTextStyle());
            size = size + config.getTitlePadding() + tm.height();
        }
        return size;
    }


    protected abstract int ticksLabelSizeForWidth(TextMetric tm, int angle);

    protected abstract int labelSizeForOverlap(TextMetric tm, int angle, String label);


    private int requiredSpaceForTickLabel(TextMetric tm, int rotation, String label) {
        int labelsGap = 2 * config.getTickLabelTextStyle().getSize(); // min gap between labels = 2 symbols size (roughly)
        int labelSize = labelSizeForOverlap(tm, 0, label);

        int requiredSpace = labelSize  + labelsGap;

        // first and last labels are usually shifted to avoid its cutting on the edge
        // so we need additional extra space
        requiredSpace += labelSize / 2;
        return requiredSpace;
    }


    /**
     * Create ticks and minor ticks
     */
    protected void createTicks(TextMetric tm) {
        double min = getMin();
        double max = getMax();
        // get tick provider
        TickProvider tickProvider;
        int ticksCount;
        if (config.getTickStep() > 0) {
            tickProvider =  scale.getTickProviderByStep(config.getTickStep(), config.getTickFormatInfo());
        } else {
            int fontFactor = 4;
            double tickPixelInterval = fontFactor * config.getTickLabelTextStyle().getSize();
            ticksCount = (int) (Math.abs(getStart() - getEnd()) / tickPixelInterval);
            if(ticksCount < 1) {
                ticksCount = 1;
            }
            tickProvider =  scale.getTickProviderByCount(ticksCount, config.getTickFormatInfo());
        }

        Tick tickMin = tickProvider.getLowerTick(min);
        Tick tickMinNext = tickProvider.getNextTick();
        Tick tickMax = tickProvider.getUpperTick(max);

        // Calculate required space to avoid labels overlapping.
        // Simplified algorithm assumes that the biggest tick size are on the axis edges
        // (it is reasonable for all axis except the category one that at the moment not used)
        String longestLabel = (tickMin.getLabel().length() > tickMax.getLabel().length()) ? tickMin.getLabel() : tickMax.getLabel();
        int requiredSpace = requiredSpaceForTickLabel(tm, 0, longestLabel);
        int ticksPixelInterval = (int) Math.abs(scale(tickMinNext.getValue()) - scale(tickMin.getValue()));

        // Calculate how many ticks need to be skipped to avoid labels overlapping.
        // When ticksSkipStep = n, only every n'th label on the axis will be shown.
        // For example if ticksSkipStep = 2 every other label will be shown.
        if(ticksPixelInterval < requiredSpace) {
            int ticksSkipStep = requiredSpace / ticksPixelInterval;
            // choose "nice" ticksSkipStep from available ones
            for (int i = 0; i < TICKS_AVAILABLE_SKIP_STEPS.length; i++) {
                if (ticksSkipStep <= TICKS_AVAILABLE_SKIP_STEPS[i]) {
                    ticksSkipStep = TICKS_AVAILABLE_SKIP_STEPS[i];
                    break;
                }
            }
            tickProvider.increaseTickStep(ticksSkipStep);
        }

        // always add round min and max ticks to be able create minor grid
        List<Tick> ticksList = new ArrayList<Tick>();
        Tick tick = tickProvider.getLowerTick(min);
        while (tick.getValue() < max) {
            ticksList.add(tick);
            tick = tickProvider.getNextTick();
            if (ticksList.size() > MAX_TICKS_COUNT) {
                String errMsg = MessageFormat.format(TOO_MANY_TICKS_MSG, ticksList.size(), maxTicksAmount);
                throw new RuntimeException(errMsg);
            }
        }
        // add closing max tick
        ticksList.add(tick);
        ticks = (Tick[]) ticksList.toArray();
    }

    public void roundTicks(TextMetric tm) {
        if (ticks == null) {
            createTicks(tm);
        }
        double min = getMin();
        double max = getMax();
        if (ticks[0].getValue() == min && ticks[ticks.length - 1].getValue() == max) {
            return;
        }

        double roundingExtraLength = (min - ticks[0].getValue()) + (ticks[ticks.length - 1].getValue() - max);
        double axisLength = max - min;


        double tickRoundingUncertaintySum = TICKS_ROUNDING_UNCERTAINTY * 2; // multiply by 2 because rounding on both axis sides
        if(roundingExtraLength / axisLength > tickRoundingUncertaintySum) {
            // we need create more ticks to have rounding with the desirable uncertainty
            int minTicksIntervalsCount = (int)(1 / TICKS_ROUNDING_UNCERTAINTY) + 1;

            // multiply by 2 because we can add 2 additional ticks on every axis side
            // to get a number of ticks multiples of 2 or 3
            int minTicksCount = (minTicksIntervalsCount + 1) * 2;

            int ticksCount = 2 * minTicksCount; // multiply by 2 because provider can give less number of ticks

            tickProvider = scale.getTickProviderByCount(ticksCount, config.getTickFormatInfo());

            List<Tick> ticksList = new ArrayList<>();
            Tick tick = tickProvider.getLowerTick(min);
            while (tick.getValue() < max) {
                ticksList.add(tick);
                tick = tickProvider.getNextTick();
                if (ticksList.size() > MAX_TICKS_COUNT) {
                    String errMsg = MessageFormat.format(TOO_MANY_TICKS_MSG, ticksList.size(), maxTicksAmount);
                    throw new RuntimeException(errMsg);
                }
            }
            // add closing max tick
            ticksList.add(tick);

            // check tick labels overlapping
            double tickPixelInterval = Math.abs(scale(ticksList.get(0).getValue()) - scale(ticksList.get(1).getValue()));
            int ticksSkipStep = getTicksSkipStepToAvoidOverlap(tm, tickPixelInterval);
            int skippedTickCount = ticksList.size() / ticksSkipStep + 1;
            if (skippedTickCount > 2) {
                if (ticksList.size() % 2 == 0) {
                    ticksList.add(tickProvider.getNextTick());
                }
                ticks = new Tick[3];
                ticks[0] = ticksList.get(0);
                ticks[1] = ticksList.get(ticksList.size() / 2);
                ticks[2] = ticksList.get(ticksList.size() - 1);
            } else {
                ticks = new Tick[2];
                ticks[0] = ticksList.get(0);
                ticks[1] = ticksList.get(ticksList.size() - 1);
            }

        }
    }

    public void roundMinMax() {
        scale.setDomain(ticks[0].getValue(), ticks[ticks.length - 1].getValue());
    }
}
