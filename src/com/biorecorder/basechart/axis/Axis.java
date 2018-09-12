package com.biorecorder.basechart.axis;

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
    private final int[] TICKS_AVAILABLE_SKIP_STEPS = {2, 4, 5, 8, 10, 16,  32, 50, 64, 100, 128}; // used to skip ticks if they overlap
    private int MAX_TICKS_COUNT = 500; // if bigger it means that there is some error
    private final double REQUIRED_SPACE_FOR_3_TICKS_RATIO = 2.2;


    private final String TOO_MANY_TICKS_MSG = "Too many ticks: {0}. Expected < {1}";

    // used to calculate ticks count. If <= 0 will not be taken into account
    private int roundingAccuracyPct = 0; // percents for min

    protected String title;
    private Scale scale;
    protected AxisConfig config;
    protected boolean isTickLabelInside = true;

    private double tickInterval = -1; // in axis domain units

    private int minorTickIntervalCount = 0; // number of minor intervals in one major interval

    private TickFormatInfo tickFormatInfo = new TickFormatInfo();

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
    }

    private void setDirty() {
        tickProvider = null;
        isDirty = true;
        width = -1;
    }

    public void setTitle(String title) {
        this.title = title;
        setDirty();
    }

    public void setTickInterval(double tickInterval) {
        this.tickInterval = tickInterval;
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

    public void setTickLabelInside(boolean tickLabelInside) {
        isTickLabelInside = tickLabelInside;
        setDirty();
    }

    /**
     * Specify maximum distance between axis start and minTick (or axis end and maxTick)
     * in relation to axis length (percents)
     * Ticks count is calculated on the base of the given rounding accuracy.
     * If rounding accuracy <= 0 it will not be taken into account!!!
     * @param roundingAccuracyPct - rounding accuracy percents
     */
    public void setRoundingAccuracyPct(int roundingAccuracyPct) {
        this.roundingAccuracyPct = roundingAccuracyPct;
    }

    /**
     * set Axis scale. Inner scale is a COPY of the given scale
     * to prevent direct access from outside
     *
     * @param scale
     */
    public void setScale(Scale scale) {
        this.scale = scale.copy();
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

        double start = getStart();
        double end = getEnd();

        double zoomedLength = (end - start) * zoomFactor;
        double zoomedEnd = start + zoomedLength;
        zoomedScale.setRange(start, zoomedEnd);
        double maxNew = zoomedScale.invert(end);
        zoomedScale.setDomain(getMin(), maxNew);

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

        double start = getStart();
        double end = getEnd();
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

    public void setMinMax(double min, double max) {
        if(min == getMin() && max == getMax()) {
            return;
        }
        if (min > max) {
            String errorMessage = "Expected Min < Max. Min = {0}, Max = {1}.";
            String formattedError = MessageFormat.format(errorMessage, min, max);
            throw new IllegalArgumentException(formattedError);
        }
        scale.setDomain(min, max);
        setDirty();
    }

    public void setStartEnd(double start, double end) {
        if((int)(start) == (int) getStart() && (int) end == (int) getEnd()) {
            return;
        }
        scale.setRange(start,  end);
        setDirty();
    }

    public double getMin() {
        return scale.getDomain()[0];
    }

    public double getMax() {
        return scale.getDomain()[scale.getDomain().length - 1];
    }

    public double getStart() {
        return  scale.getRange()[0];
    }

    public double getEnd() {
        return scale.getRange()[scale.getRange().length - 1];
    }

    public double scale(double value) {
        return scale.scale(value);
    }

    public double invert(float value) {
        return scale.invert(value);
    }

    public double getLength() {
        return  Math.abs(getEnd() - getStart());
    }

    public int getWidth(BCanvas canvas) {
        if(width < 0) { // calculate width
            width = 0;
            width += config.getAxisLineStroke().getWidth() / 2;

            width += config.getTickMarkOutsideSize();

            if (!isTickLabelInside) {
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
        // Calculate required space to avoid labels overlapping.
        // Simplified algorithm assumes that the biggest tick size are on the axis edges
        // (it is reasonable for all axis except the category one that at the moment not used)
        String longestLabel = (tickMin.getLabel().length() > tickMax.getLabel().length()) ? tickMin.getLabel() : tickMax.getLabel();
        int requiredSpace = requiredSpaceForTickLabel(tm, 0, longestLabel);

        if(ticksSkipStep > 1) {
            double tickPixelInterval =   Math.abs(scale(tickMinNext.getValue()) - scale(tickMin.getValue()));
            int tickIntervalCount = (int) Math.round(Math.abs(scale(tickMax.getValue()) - scale(tickMin.getValue())) / tickPixelInterval);

            if(tickIntervalCount/ticksSkipStep <= 3) {
                if( getLength() / requiredSpace  >= REQUIRED_SPACE_FOR_3_TICKS_RATIO) { // 3 ticks
                    if(tickIntervalCount % 2 != 0) {
                        tickIntervalCount++;
                        tickMax = tickProvider.getNextTick();
                    }
                    ticksSkipStep = tickIntervalCount / 2;
                } else { // 2 ticks
                    ticksSkipStep = tickIntervalCount;
                }
            } else {
                int roundExtraTicksCount = ticksSkipStep - tickIntervalCount % ticksSkipStep;
                if(roundExtraTicksCount < ticksSkipStep) {
                    for (int i = 0; i < roundExtraTicksCount; i++) {
                        tickMax = tickProvider.getNextTick();
                        tickIntervalCount++;
                    }
                }
            }
        }
        scale.setDomain(tickMin.getValue(), tickMax.getValue());
    }

    public void drawGrid(BCanvas canvas, int axisOriginPoint, int length) {
        canvas.save();
        if(isDirty) {
            createAxisElements(canvas);
        }

        translateCanvas(canvas, axisOriginPoint);

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

        canvas.restore();
    }

    public void drawAxis(BCanvas canvas,  int axisOriginPoint) {
        canvas.save();

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

        canvas.setStroke(new BStroke(1));
        canvas.setColor(config.getTickLabelColor());
        canvas.setTextStyle(config.getTickLabelTextStyle());
        for (Text tickLabel : tickLabels) {
            tickLabel.draw(canvas);
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

    private boolean isTickIntervalSpecified() {
        return tickInterval > 0;
    }

    private int requiredSpaceForTickLabel(TextMetric tm, int rotation, String label) {
        int labelsGap = (2 * config.getTickLabelTextStyle().getSize()); // min gap between labels = 2 symbols size (roughly)
        int labelSize = labelSizeForOverlap(tm, 0, label);

        int requiredSpace = labelSize  + labelsGap;

        // first and last labels are usually shifted to avoid its cutting on the edge
        // so we need additional extra space
       // requiredSpace += labelSize / 2;
        return requiredSpace;
    }

    private void configTickProvider(TextMetric tm) {

        int tickIntervalCountByRoundingUncertainty = 0;
        if(roundingAccuracyPct > 0) {
            tickIntervalCountByRoundingUncertainty = (int)Math.round(100.0 / roundingAccuracyPct);
        }
        if (isTickIntervalSpecified()) {
            tickProvider =  scale.getTickProviderByInterval(tickInterval, tickFormatInfo);
        } else {
            int fontFactor = 4;
            double tickPixelInterval = fontFactor * config.getTickLabelTextStyle().getSize();
            int tickIntervalCount = (int)(getLength() / tickPixelInterval);

            // ensure that number of tick intervals is sufficient to get the specified rounding uncertainty
            tickIntervalCount = Math.max(tickIntervalCount, tickIntervalCountByRoundingUncertainty);

            if(tickIntervalCount < 1) {
                tickIntervalCount = 1;
            }

            tickProvider =  scale.getTickProviderByIntervalCount(tickIntervalCount, tickFormatInfo);
        }

        double min = getMin();
        double max = getMax();

        Tick tickMin = tickProvider.getUpperTick(min);
        Tick tickMinNext = tickProvider.getNextTick();
        Tick tickMax = tickProvider.getLowerTick(max);

        // Calculate required space to avoid labels overlapping.
        // Simplified algorithm assumes that the biggest tick size are on the axis edges
        // (it is reasonable for all axis except the category one that at the moment not used)
        String longestLabel = (tickMin.getLabel().length() > tickMax.getLabel().length()) ? tickMin.getLabel() : tickMax.getLabel();
        int requiredSpace = requiredSpaceForTickLabel(tm, 0, longestLabel);
        double tickPixelInterval = Math.abs(scale(tickMinNext.getValue()) - scale(tickMin.getValue()));

        // real resultant number of tick intervals
        int tickIntervalCount = (int) Math.round(Math.abs(scale(tickMax.getValue()) - scale(tickMin.getValue())) / tickPixelInterval);

        // Calculate how many ticks need to be skipped to avoid labels overlapping.
        // When ticksSkipStep = n, only every n'th label on the axis will be shown.
        // For example if ticksSkipStep = 2 every other label will be shown.
        ticksSkipStep = 1;
        if(tickPixelInterval < requiredSpace) {
            ticksSkipStep =  (int)(requiredSpace / tickPixelInterval);
            if(ticksSkipStep * tickPixelInterval < requiredSpace) {
                ticksSkipStep++;
            }

            // choose "nice" ticksSkipStep from available ones
            for (int i = 0; i < TICKS_AVAILABLE_SKIP_STEPS.length; i++) {
                if (ticksSkipStep <= TICKS_AVAILABLE_SKIP_STEPS[i]) {
                    ticksSkipStep = TICKS_AVAILABLE_SKIP_STEPS[i];
                    break;
                }
            }

            if(ticksSkipStep > tickIntervalCount) {
                ticksSkipStep = tickIntervalCount;
            }
        }

        if(ticksSkipStep > 1) {
            if(isTickIntervalSpecified() || roundingAccuracyPct <= 0) {
                tickProvider.increaseTickInterval(ticksSkipStep);
                ticksSkipStep = 1;
            }
        }

        if(tickIntervalCount/ticksSkipStep <= 3) {
            if( getLength() / requiredSpace  >= REQUIRED_SPACE_FOR_3_TICKS_RATIO) { // 3 ticks
                ticksSkipStep = tickIntervalCount / 2;
            } else { // 2 ticks
                ticksSkipStep = tickIntervalCount;
            }
        }

        if(ticksSkipStep < 1) {
            ticksSkipStep = 1;
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
            tickLabels.add(tickToLabel(tm, (int) scale(currentTick.getValue()), currentTick.getLabel()));
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
