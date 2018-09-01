package com.biorecorder.basechart.axis;

import com.biorecorder.basechart.Range;
import com.biorecorder.basechart.graphics.Text;
import com.biorecorder.basechart.graphics.BCanvas;
import com.biorecorder.basechart.graphics.BStroke;
import com.biorecorder.basechart.graphics.TextMetric;
import com.biorecorder.basechart.lists.IntArrayList;
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
public abstract class Axis {
    private final int[] TICKS_AVAILABLE_SKIP_STEPS = {2, 4, 5, 8, 10, 16, 20, 32, 40, 64, 80, 100}; // used to skip ticks if they overlap
    private final double TICKS_ROUNDING_UNCERTAINTY = 0.2; // 20% for one side
    private int MAX_TICKS_COUNT = 500; // if bigger it means that there is some error

    private final String TOO_MANY_TICKS_MSG = "Too many ticks: {0}. Expected < {1}";

    protected String title;
    private Scale scale;
    protected AxisConfig config;

    private boolean isVisible = false;
    private boolean isGridVisible = false;
    private boolean isTickLabelVisible = true;

    private double tickInterval = -1; // in axis domain units
    private boolean isMinMaxRoundingEnabled = false;

    private int minorTickIntervalCount = 3; // number of minor intervals in one major interval

    private TickFormatInfo tickFormatInfo = new TickFormatInfo();

    
    // need this field to implement smooth zooming when minMaxRounding enabled
    private Range rowMinMax; // without rounding

    private TickProvider tickProvider;

    private int ticksSkipStep = 1;
    private List<Text> tickLabels = new ArrayList<>();
    private IntArrayList tickPositions = new IntArrayList();
    private IntArrayList minorTickPositions = new IntArrayList();
    private Text titleText;

    private boolean isDirty = true;
    private int width = -1;

    public Axis(Scale scale, AxisConfig axisConfig) {
        this.scale = scale.copy();
        this.config = axisConfig;
        rowMinMax = new Range(getMin(), getMax());
    }

    private void setDirty() {
        tickProvider = null;
        isDirty = true;
        width = -1;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public boolean isMinMaxRoundingEnabled() {
        return isMinMaxRoundingEnabled;
    }

    public void setTitle(String title) {
        this.title = title;
        setDirty();
    }

    public void setTickInterval(double tickInterval) {
        this.tickInterval = tickInterval;
        setDirty();
    }

    public void setMinMaxRoundingEnabled(boolean minMaxRoundingEnabled) {
        isMinMaxRoundingEnabled = minMaxRoundingEnabled;
        setDirty();
    }

    public void setMinorTickIntervalCount(int minorTickIntervalCount) {
        this.minorTickIntervalCount = minorTickIntervalCount;
        setDirty();
    }

    public void setTickFormatInfo(TickFormatInfo tickFormatInfo) {
        this.tickFormatInfo = tickFormatInfo;
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


    public void setVisible(boolean isVisible) {
       this.isVisible = isVisible;
       setDirty();
    }

    public void setGridVisible(boolean isVisible) {
        isGridVisible = isVisible;
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
        rowMinMax = new Range(min, max);
        scale.setDomain(min, max);
        setDirty();
    }

    public void setStartEnd(double start, double end) {
        scale.setRange((int) start, (int) end);
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

    public int getLength() {
        return Math.abs(getEnd() - getStart());
    }

    public int getWidth(BCanvas canvas) {
        if (!isVisible) {
            return 0;
        }
        if(width < 0) { // calculate width
            width = 0;
            width += config.getAxisLineStroke().getWidth() / 2;

            width += config.getTickMarkOutsideSize();

            if (isTickLabelVisible && ! config.isTickLabelInside()) {
                TextMetric tm = canvas.getTextMetric(config.getTickLabelTextStyle());
                if(tickProvider == null) {
                    configTickProvider(tm);
                }

                Tick minTick = tickProvider.getUpperTick(getMin());
                Tick maxTick = tickProvider.getLowerTick(getMax());

                String longestLabel = minTick.getLabel().length() > maxTick.getLabel().length() ? minTick.getLabel() : maxTick.getLabel();
                width += config.getTickPadding() + labelSizeForWidth(tm, 0, longestLabel);
            }
            if (title != null) {
                TextMetric tm = canvas.getTextMetric(config.getTitleTextStyle());
                width += config.getTitlePadding() + tm.height();
            }
        }
        return width;
    }

    public void roundMinMax(BCanvas canvas) {
        TextMetric tm = canvas.getTextMetric(config.getTickLabelTextStyle());
        if(tickProvider == null) {
            configTickProvider(tm);
        }

        double min = getMin();
        double max = getMax();

        Tick tickMin = tickProvider.getLowerTick(min);
        Tick tickMinNext = tickProvider.getNextTick();
        Tick tickMax = tickProvider.getUpperTick(max);

        if (tickMin.getValue() == min && tickMax.getValue() == max) {
            return;
        }
        if(ticksSkipStep > 1) {
            // adjust ticksSkipStep for "precise" ticks
            // (when only min, max and may be middle ticks are displayed)

            double ticksPixelInterval =  Math.abs(scale(tickMinNext.getValue()) - scale(tickMin.getValue()));
            int tickIntervalCount = (int) Math.round(Math.abs(scale(tickMax.getValue()) - scale(tickMin.getValue())) / ticksPixelInterval);
            if(ticksSkipStep < tickIntervalCount - 2) { // 3 ticks: min, max and middle
                if(tickIntervalCount % 2 != 0) {
                    tickIntervalCount++;
                    tickMax = tickProvider.getNextTick();
                }
                ticksSkipStep = tickIntervalCount / 2;
            } else {
               ticksSkipStep = tickIntervalCount; // 2 ticks: only min and max
            }
        }

        scale.setDomain(tickMin.getValue(), tickMax.getValue());
    }

    public void drawGrid(BCanvas canvas, int axisOriginPoint, int length) {
        canvas.save();
        if(! isVisible) {
            return;
        }
        if(isDirty) {
            createAxisElements(canvas);
        }

        translateCanvas(canvas, axisOriginPoint);

        if(isGridVisible) {
            canvas.setColor(config.getGridColor());
            canvas.setStroke(config.getGridLineStroke());
            for (int i = 0; i < tickPositions.size(); i++) {
               drawGridLine(canvas, tickPositions.get(i), length);
            }

            canvas.setColor(config.getMinorGridColor());
            canvas.setStroke(config.getMinorGridLineStroke());
            for (int i = 0; i < minorTickPositions.size(); i++) {
                drawGridLine(canvas, minorTickPositions.get(i), length);
            }
        }

        canvas.restore();
    }

    public void drawAxis(BCanvas canvas,  int axisOriginPoint) {
        canvas.save();
        if(! isVisible) {
            return;
        }
        if(isDirty) {
            createAxisElements(canvas);
        }
        translateCanvas(canvas, axisOriginPoint);

        if(config.getTickMarkInsideSize() > 0 || config.getTickMarkOutsideSize() > 0) {
            canvas.setColor(config.getTickMarkColor());
            canvas.setStroke(new BStroke(config.getTickMarkWidth()));
            for (int i = 0; i < tickPositions.size(); i++) {
                drawTickMark(canvas, tickPositions.get(i), config.getTickMarkInsideSize(), config.getTickMarkOutsideSize());
            }
        }

        if(config.getMinorTickMarkInsideSize() > 0 || config.getMinorTickMarkOutsideSize() > 0) {
            canvas.setColor(config.getMinorTickMarkColor());
            canvas.setStroke(new BStroke(config.getMinorTickMarkWidth()));
            for (int i = 0; i < minorTickPositions.size(); i++) {
                drawTickMark(canvas, minorTickPositions.get(i), config.getMinorTickMarkInsideSize(), config.getMinorTickMarkOutsideSize());
            }
        }

        if(isTickLabelVisible) {
            canvas.setStroke(new BStroke(1));
            canvas.setColor(config.getTickLabelColor());
            canvas.setTextStyle(config.getTickLabelTextStyle());
            for (Text tickLabel : tickLabels) {
                tickLabel.draw(canvas);
            }
        }

        if(config.getAxisLineStroke().getWidth() > 0) {
            canvas.setColor(config.getAxisLineColor());
            canvas.setStroke(config.getAxisLineStroke());
            drawAxisLine(canvas);
        }

        if(title != null) {
            canvas.setColor(config.getTitleColor());
            canvas.setTextStyle(config.getTitleTextStyle());
            titleText.draw(canvas);
        }
        canvas.restore();
    }

    private boolean isTicksStepSpecified() {
        return tickInterval > 0;
    }

    private int requiredSpaceForTickLabel(TextMetric tm, int rotation, String label) {
        int labelsGap = 2 * config.getTickLabelTextStyle().getSize(); // min gap between labels = 2 symbols size (roughly)
        int labelSize = labelSizeForOverlap(tm, 0, label);

        int requiredSpace = labelSize  + labelsGap;

        // first and last labels are usually shifted to avoid its cutting on the edge
        // so we need additional extra space
        requiredSpace += labelSize / 2;
        return requiredSpace;
    }

    private int roundingUncertaintyToTicksCount() {
        // min number of ticks interval to have rounding within the desirable uncertainty
        int minTicksIntervalsCount = (int)(1 / TICKS_ROUNDING_UNCERTAINTY) + 1;

        // multiply by 2 because we can add 2 additional ticks on every axis side
        // to get a number of ticks multiples of 2 or 3
        minTicksIntervalsCount *= 2;

        int ticksCount = minTicksIntervalsCount + 1;

        // multiply by 2 because provider can give less number of ticks
        ticksCount *= 2;

        return ticksCount;
    }

    private void configTickProvider(TextMetric tm) {
        int ticksCount;
        if (isTicksStepSpecified()) {
            tickProvider =  scale.getTickProviderByStep(tickInterval, tickFormatInfo);
        } else {
            int fontFactor = 4;
            int tickPixelInterval = fontFactor * config.getTickLabelTextStyle().getSize();
            ticksCount = getLength() / tickPixelInterval;

            // ensure that number of ticks is sufficient to get the specified rounding uncertainty
            ticksCount = Math.max(ticksCount, roundingUncertaintyToTicksCount());

            if(ticksCount < 2) {
                ticksCount = 2;
            }
            tickProvider =  scale.getTickProviderByCount(ticksCount, tickFormatInfo);
        }

        double min = getMin();
        double max = getMax();

        Tick tickMin = tickProvider.getLowerTick(min);
        Tick tickMinNext = tickProvider.getNextTick();
        Tick tickMax = tickProvider.getUpperTick(max);

        // Calculate required space to avoid labels overlapping.
        // Simplified algorithm assumes that the biggest tick size are on the axis edges
        // (it is reasonable for all axis except the category one that at the moment not used)
        String longestLabel = (tickMin.getLabel().length() > tickMax.getLabel().length()) ? tickMin.getLabel() : tickMax.getLabel();
        int requiredSpace = requiredSpaceForTickLabel(tm, 0, longestLabel);
        double ticksPixelInterval =  Math.abs(scale(tickMinNext.getValue()) - scale(tickMin.getValue()));

        // Calculate how many ticks need to be skipped to avoid labels overlapping.
        // When ticksSkipStep = n, only every n'th label on the axis will be shown.
        // For example if ticksSkipStep = 2 every other label will be shown.
        ticksSkipStep = 1;
        if(ticksPixelInterval < requiredSpace) {
            ticksSkipStep = (int) Math.round(requiredSpace / ticksPixelInterval);
            // choose "nice" ticksSkipStep from available ones
            for (int i = 0; i < TICKS_AVAILABLE_SKIP_STEPS.length; i++) {
                if (ticksSkipStep <= TICKS_AVAILABLE_SKIP_STEPS[i]) {
                    ticksSkipStep = TICKS_AVAILABLE_SKIP_STEPS[i];
                    break;
                }
            }
        }

        // check if increased tick step will be ok for rounding with the specified uncertainty
        boolean isIncreasedTickStepOk = false;
        if(ticksSkipStep * ticksPixelInterval / getLength() < TICKS_ROUNDING_UNCERTAINTY){
            isIncreasedTickStepOk = true;
        }

        if(isTicksStepSpecified() || isIncreasedTickStepOk) {
            this.tickProvider.increaseTickStep(ticksSkipStep);
            ticksSkipStep = 1;
        }

        if(ticksSkipStep > 1) { // means precise ticks. We should skip all ticks except min, max and may be middle tick
            tickMin = tickProvider.getUpperTick(min);
            tickMax = tickProvider.getLowerTick(max);
            int tickIntervalCount = (int) Math.round(Math.abs(scale(tickMax.getValue()) - scale(tickMin.getValue())) / ticksPixelInterval);

            // check if there is space for 3 ticks
            if(ticksSkipStep < tickIntervalCount / 2 && tickIntervalCount > 3  && ((tickIntervalCount / 2) * 2) * ticksPixelInterval > 3 * requiredSpace ) { // min, max and middle tick
                ticksSkipStep = tickIntervalCount / 2;
            } else { // only min and max ticks
                ticksSkipStep = tickIntervalCount;
            }
        }
    }

    private void createAxisElements(BCanvas canvas) {
        TextMetric tm = canvas.getTextMetric(config.getTickLabelTextStyle());
        if(tickProvider == null) {
            configTickProvider(tm);
        }

        tickPositions.clear();
        minorTickPositions.clear();
        tickLabels.clear();

        double min = getMin();
        double max = getMax();

        Tick currentTick = tickProvider.getUpperTick(min);
        Tick nextTick = null;

        while (currentTick.getValue() <= max) {
            // tick position
            tickPositions.add((int)scale(currentTick.getValue()));

            // tick label
            if(isTickLabelVisible) {
                tickLabels.add(tickToLabel(tm, (int) scale(currentTick.getValue()), currentTick.getLabel()));
            }

            for (int i = 0; i < ticksSkipStep; i++) {
                nextTick = tickProvider.getNextTick();
            }

            if(minorTickIntervalCount > 0) {
                // minor tick positions
                double minorTickInterval = (nextTick.getValue() - currentTick.getValue()) / minorTickIntervalCount;
                double minorTickValue = currentTick.getValue();
                for (int i = 1; i < minorTickIntervalCount; i++) {
                    minorTickValue += minorTickInterval;
                    if(minorTickValue <= max) {
                        minorTickPositions.add((int)scale(minorTickValue));
                    } else {
                        break;
                    }
                }
            }

            if (tickPositions.size() > MAX_TICKS_COUNT) {
                String errMsg = MessageFormat.format(TOO_MANY_TICKS_MSG, tickPositions.size(), MAX_TICKS_COUNT);
                throw new RuntimeException(errMsg);
            }
            currentTick = nextTick;
        }

        if(minorTickIntervalCount > 0) {
            // add minor ticks that are located between minorTick and min
            currentTick = tickProvider.getUpperTick(min);
            Tick previousTick = null;
            for (int i = 0; i < ticksSkipStep; i++) {
                previousTick = tickProvider.getPreviousTick();
            }
            double minorTickInterval = (currentTick.getValue() - previousTick.getValue()) / minorTickIntervalCount;
            double minorTickValue = currentTick.getValue();
            IntArrayList positions = new IntArrayList();
            for (int i = 1; i < minorTickIntervalCount; i++) {
                minorTickValue -= minorTickInterval;
                if(minorTickValue >= min) {
                    positions.add((int)scale(minorTickValue));
                } else {
                    break;
                }
            }
            if(positions.size() > 0) {
                // additionalPositions need to be reversed
                int[] minorTickAdditionalPositions = new int[(int)positions.size()];
                for (int i = 0; i < positions.size(); i++) {
                    minorTickAdditionalPositions[i] = positions.get(positions.size() - 1 - i);
                }
                minorTickPositions.add(0, minorTickAdditionalPositions);
            }
        }

        // title
        if(title != null) {
            titleText = createTitle(canvas);
        }

        isDirty = false;
    }

    protected abstract void translateCanvas(BCanvas canvas, int axisOriginPoint);

    protected abstract int labelSizeForWidth(TextMetric tm, int angle, String label);

    protected abstract int labelSizeForOverlap(TextMetric tm, int angle, String label);

    protected abstract Text tickToLabel(TextMetric tm, int tickPosition, String tickLabel);

    protected abstract void drawTickMark(BCanvas canvas, int tickPosition, int insideSize, int outsideSize);

    protected abstract void drawGridLine(BCanvas canvas, int tickPosition, int length);

    protected abstract void drawAxisLine(BCanvas canvas);

    protected abstract Text createTitle(BCanvas canvas);

}
