package com.biorecorder.basechart.axis;

import com.biorecorder.basechart.graphics.BText;
import com.biorecorder.basechart.graphics.TextMetric;
import com.biorecorder.basechart.Range;
import com.biorecorder.basechart.graphics.*;
import com.biorecorder.basechart.utils.StringUtils;
import com.biorecorder.data.list.IntArrayList;
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
    // private final int[] TICKS_AVAILABLE_SKIP_STEPS = {2, 4, 5, 8, 10, 16,  32, 50, 64, 100, 128}; // used to skip ticks if they overlap
    private final int[] TICKS_AVAILABLE_SKIP_STEPS = {2, 4, 8, 16, 32, 64, 128}; // used to skip ticks if they overlap

    private int MAX_TICKS_COUNT = 500; // if bigger it means that there is some error
    private final double REQUIRED_SPACE_FOR_3_TICKS_RATIO = 2.2;

    private final String TOO_MANY_TICKS_MSG = "Too many ticks: {0}. Expected < {1}";

    protected String title;
    protected AxisConfig config;

    private Scale scale;

    private TickProvider tickProvider;
    private int ticksSkipStep = 1;
    private List<BText> tickLabels = new ArrayList<>();
    private IntArrayList tickPositions = new IntArrayList();
    private IntArrayList minorTickPositions = new IntArrayList();
    private BText titleText;

    private boolean isTicksDirty = true;
    private int width = -1;

    public Axis(Scale scale, AxisConfig axisConfig) {
        this.scale = scale.copy();
        this.config = axisConfig;
    }

    private void setTicksDirty() {
        tickProvider = null;
        isTicksDirty = true;
        width = -1;
    }

    private boolean isTicksDirty() {
        return isTicksDirty;
    }

    private boolean isTooShort() {
        int lengthMin = config.getTickLabelTextStyle().getSize() * 3;
        if(length() > lengthMin) {
            return false;
        }
        return true;
    }


    public void setTitle(String title) {
        this.title = title;
        width = -1;
        titleText = null;
    }


    /**
     * set Axis scale. Inner scale is a COPY of the given scale
     * to prevent direct access from outside
     *
     * @param scale
     */
    public void setScale(Scale scale) {
        this.scale = scale;
        setTicksDirty();
    }

    /**
     * get the COPY of inner scale
     *
     * @return copy of inner scale
     */
    public Scale getScale() {
        return scale;
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
        titleText = null;
        setTicksDirty();
    }

    public boolean isTickLabelOutside() {
        return config.isTickLabelOutside();
    }

    public String getTitle() {
        return title;
    }

    public void setTickAccuracy(int tickAccuracy) {
        config.setTickAccuracy(tickAccuracy);
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
        if(zoomFactor <= 0) {
            String errMsg = "Zoom factor = " + zoomFactor + "  Expected > 0";
            throw new IllegalArgumentException(errMsg);
        }
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
     * Format domain value according to the minMax one "point precision"
     * cutting unnecessary double digits that exceeds that "point precision"
     */
    public String formatDomainValue(double value) {
        return scale.formatDomainValue(value);
    }

    public boolean setMinMax(Range minMax) {
        if (minMax.getMin() == getMin() && minMax.getMax() == getMax()) {
            return false;
        }

        scale.setDomain(minMax.getMin(), minMax.getMax());
        setTicksDirty();
        return true;
    }

    public boolean setStartEnd(double start, double end) {
        if(Double.isInfinite(start)) {
            String errMsg = "Start is infinity";
            throw new IllegalArgumentException(errMsg);
        }
        if(Double.isInfinite(end)) {
            String errMsg = "End is infinity";
            throw new IllegalArgumentException(errMsg);
        }
        if (start == getStart() && end == getEnd()) {
            return false;
        }
        scale.setRange(start, end);
        setTicksDirty();
        return true;
    }

    public double getMin() {
        return scale.getDomain()[0];
    }

    public double getMax() {
        return scale.getDomain()[scale.getDomain().length - 1];
    }

    public double getStart() {
        return scale.getRange()[0];
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

    public double length() {
        return Math.abs(getEnd() - getStart());
    }

    public int getWidth(BCanvas canvas) {
        if(isTooShort()) {
            return config.getAxisLineStroke().getWidth() / 2;
        }
        if (width < 0) { // calculate width
            width = 0;
            width += config.getAxisLineStroke().getWidth() / 2;

            width += config.getTickMarkOutsideSize();

            if (config.isTickLabelOutside()) {
                if (isTicksDirty()) {
                    createTicks(canvas);
                }
                if (tickLabels.size() > 0) {
                    TextMetric tm = canvas.getTextMetric(config.getTickLabelTextStyle());
                    String minTickLabel = tickLabels.get(0).getText();
                    String maxTickLabel = tickLabels.get(tickLabels.size() - 1).getText();

                    String longestLabel = minTickLabel.length() > maxTickLabel.length() ? minTickLabel : maxTickLabel;
                    width += config.getTickPadding() + labelSizeForWidth(tm, 0, longestLabel);

                }
            }
            if (! StringUtils.isNullOrBlank(title)) {
                TextMetric tm = canvas.getTextMetric(config.getTitleTextStyle());
                width += config.getTitlePadding() + tm.height();
            }
        }
        return width;
    }

    public void roundMinMax(BCanvas canvas) {
        if(isTooShort()) {
            return;
        }
        TextMetric tm = canvas.getTextMetric(config.getTickLabelTextStyle());
        if (tickProvider == null) {
            configTickProvider(tm);
        }

        double min = getMin();
        double max = getMax();

        Tick tickMin = tickProvider.getLowerTick(min);
        Tick tickMinNext = tickProvider.getNextTick();
        Tick tickMax = tickProvider.getUpperTick(max);

        // Calculate required space to avoid labels overlapping.
        // Simplified algorithm assumes that the biggest tick rowCount are on the axis edges
        // (it is reasonable for all axis except the category one that at the moment not used)
        String longestLabel = (tickMin.getLabel().length() > tickMax.getLabel().length()) ? tickMin.getLabel() : tickMax.getLabel();
        int requiredSpace = requiredSpaceForTickLabel(tm, 0, longestLabel);

        if (ticksSkipStep > 1) {
            double tickPixelInterval = Math.abs(scale(tickMinNext.getTickValue().getValue()) - scale(tickMin.getTickValue().getValue()));
            int tickIntervalCount = (int) Math.round(Math.abs(scale(tickMax.getTickValue().getValue()) - scale(tickMin.getTickValue().getValue())) / tickPixelInterval);
            if (tickIntervalCount / ticksSkipStep < 3) {
                if (length() / requiredSpace >= REQUIRED_SPACE_FOR_3_TICKS_RATIO) { // 3 ticks
                    if (tickIntervalCount % 2 != 0) {
                        tickIntervalCount++;
                        tickMax = tickProvider.getNextTick();
                    }
                    ticksSkipStep = tickIntervalCount / 2;
                } else { // 2 ticks
                    ticksSkipStep = tickIntervalCount;
                }
            } else {
                int roundExtraTicksCount = ticksSkipStep - tickIntervalCount % ticksSkipStep;
                if (roundExtraTicksCount < ticksSkipStep) {
                    for (int i = 0; i < roundExtraTicksCount; i++) {
                        tickMax = tickProvider.getNextTick();
                        tickIntervalCount++;
                    }
                }
            }
        }
        scale.setDomain(tickMin.getTickValue().getValue(), tickMax.getTickValue().getValue());
    }


    public void drawGrid(BCanvas canvas, BRectangle area) {
        if(isTooShort() || config.getGridLineStroke().getWidth() == 0) {
            return;
        }
        canvas.save();
        if (isTicksDirty()) {
            createTicks(canvas);
        }

        translateCanvas(canvas, area);

        canvas.setColor(config.getGridColor());
        canvas.setStroke(config.getGridLineStroke());
        for (int i = 0; i < tickPositions.size(); i++) {
            drawGridLine(canvas, tickPositions.get(i), area);
        }

        canvas.setColor(config.getMinorGridColor());
        canvas.setStroke(config.getMinorGridLineStroke());
        for (int i = 0; i < minorTickPositions.size(); i++) {
            drawGridLine(canvas, minorTickPositions.get(i), area);
        }

        canvas.restore();
    }

    public void drawAxis(BCanvas canvas, BRectangle area) {
        canvas.save();

        if (isTicksDirty()) {
            createTicks(canvas);
        }
        translateCanvas(canvas, area);

        if(! isTooShort()) {
            if (config.getTickMarkInsideSize() > 0 || config.getTickMarkOutsideSize() > 0) {
                canvas.setColor(config.getTickMarkColor());
                canvas.setStroke(new BStroke(config.getTickMarkWidth()));
                for (int i = 0; i < tickPositions.size(); i++) {
                    drawTickMark(canvas, tickPositions.get(i), config.getTickMarkInsideSize(), config.getTickMarkOutsideSize());
                }
            }

            if (config.getMinorTickMarkInsideSize() > 0 || config.getMinorTickMarkOutsideSize() > 0) {
                canvas.setColor(config.getMinorTickMarkColor());
                canvas.setStroke(new BStroke(config.getMinorTickMarkWidth()));
                for (int i = 0; i < minorTickPositions.size(); i++) {
                    drawTickMark(canvas, minorTickPositions.get(i), config.getMinorTickMarkInsideSize(), config.getMinorTickMarkOutsideSize());
                }
            }

            canvas.setStroke(new BStroke(1));
            canvas.setColor(config.getTickLabelColor());
            canvas.setTextStyle(config.getTickLabelTextStyle());
            for (BText tickLabel : tickLabels) {
                tickLabel.draw(canvas);
            }

            if (! StringUtils.isNullOrBlank(title)) {
                if(titleText == null) {
                    titleText = createTitle(canvas);
                }
                canvas.setColor(config.getTitleColor());
                canvas.setTextStyle(config.getTitleTextStyle());
                titleText.draw(canvas);
            }
        }

        if (config.getAxisLineStroke().getWidth() > 0) {
            canvas.setColor(config.getAxisLineColor());
            canvas.setStroke(config.getAxisLineStroke());
            drawAxisLine(canvas);
        }

        canvas.restore();
    }

    private boolean isTickIntervalSpecified() {
        return config.getTickInterval() > 0;
    }

    private int requiredSpaceForTickLabel(TextMetric tm, int rotation, String label) {
        int labelsGap = (2 * config.getTickLabelTextStyle().getSize()); // min gap between labels = 2 symbols rowCount (roughly)
        int labelSize = labelSizeForOverlap(tm, 0, label);

        int requiredSpace = labelSize + labelsGap;

        // first and last labels are usually shifted to avoid its cutting on the edge
        // so we need additional extra space
        // requiredSpace += labelSize / 2;
        return requiredSpace;
    }

    private void configTickProvider(TextMetric tm) {
        if(length() < 1) {
            return;
        }
        int tickIntervalCountByRoundingUncertainty = 0;
        if (config.getTickAccuracy() > 0) {
            tickIntervalCountByRoundingUncertainty = (int) Math.round(100.0 / config.getTickAccuracy());
        }
        if (isTickIntervalSpecified()) {
            tickProvider = scale.getTickProviderByInterval(config.getTickInterval(), config.getTickLabelPrefixAndSuffix());
        } else {
            int fontFactor = 4;
            double tickPixelInterval = fontFactor * config.getTickLabelTextStyle().getSize();
            int tickIntervalCount = (int) (length() / tickPixelInterval);

            // ensure that number of tick intervals is sufficient to get the specified rounding uncertainty
            tickIntervalCount = Math.max(tickIntervalCount, tickIntervalCountByRoundingUncertainty);

            if (tickIntervalCount < 1) {
                tickIntervalCount = 1;
            }

            tickProvider = scale.getTickProviderByIntervalCount(tickIntervalCount, config.getTickLabelPrefixAndSuffix());
        }

        double min = getMin();
        double max = getMax();

        Tick tickMin = tickProvider.getUpperTick(min);
        Tick tickMinNext = tickProvider.getNextTick();
        Tick tickMax = tickProvider.getLowerTick(max);

        // Calculate required space to avoid labels overlapping.
        // Simplified algorithm assumes that the biggest tick rowCount are on the axis edges
        // (it is reasonable for all axis except the category one that at the moment not used)
        String longestLabel = (tickMin.getLabel().length() > tickMax.getLabel().length()) ? tickMin.getLabel() : tickMax.getLabel();
        int requiredSpace = requiredSpaceForTickLabel(tm, 0, longestLabel);

        double tickPixelInterval = Math.abs(scale(tickMinNext.getTickValue().getValue()) - scale(tickMin.getTickValue().getValue()));

        // real resultant number of tick intervals
        int tickIntervalCount = (int) Math.round(Math.abs(scale(tickMax.getTickValue().getValue()) - scale(tickMin.getTickValue().getValue())) / tickPixelInterval);
        // Calculate how many ticks need to be skipped to avoid labels overlapping.
        // When ticksSkipStep = n, only every n'th label on the axis will be shown.
        // For example if ticksSkipStep = 2 every other label will be shown.
        ticksSkipStep = 1;
        if (tickPixelInterval < requiredSpace) {
            ticksSkipStep = (int) (requiredSpace / tickPixelInterval);
            if (ticksSkipStep * tickPixelInterval < requiredSpace) {
                ticksSkipStep++;
            }

            // choose "nice" ticksSkipStep from available ones
            for (int i = 0; i < TICKS_AVAILABLE_SKIP_STEPS.length; i++) {
                if (ticksSkipStep <= TICKS_AVAILABLE_SKIP_STEPS[i]) {
                    ticksSkipStep = TICKS_AVAILABLE_SKIP_STEPS[i];
                    break;
                }
            }

            if (ticksSkipStep > tickIntervalCount) {
                ticksSkipStep = tickIntervalCount;
            }
        }

        if (ticksSkipStep > 1) {
            if (isTickIntervalSpecified() || config.getTickAccuracy() <= 0) {
                tickProvider.increaseTickInterval(ticksSkipStep);
                ticksSkipStep = 1;
            }
        }

        if (tickIntervalCount / ticksSkipStep < 3) {
            if (length() / requiredSpace >= REQUIRED_SPACE_FOR_3_TICKS_RATIO) { // 3 ticks
                ticksSkipStep = tickIntervalCount / 2;
            } else { // 2 ticks
                ticksSkipStep = tickIntervalCount;
            }
        }

        if (ticksSkipStep < 1) {
            ticksSkipStep = 1;
        }
    }

    private void createTicks(BCanvas canvas) {
        if(isTooShort()) {
            return;
        }
        TextMetric tm = canvas.getTextMetric(config.getTickLabelTextStyle());
        if (tickProvider == null) {
            configTickProvider(tm);
        }

        tickPositions.clear();
        minorTickPositions.clear();
        tickLabels.clear();

        double min = getMin();
        double max = getMax();
        int minorTickIntervalCount = config.getMinorTickIntervalCount();

        Tick currentTick = tickProvider.getUpperTick(min);
        Tick nextTick = tickProvider.getNextTick();
        tickProvider.getPreviousTick();

        int tickPixelInterval = ticksSkipStep * ((int)Math.abs(scale(currentTick.getTickValue().getValue()) - scale(nextTick.getTickValue().getValue())));

        while (currentTick.getTickValue().compare(max) <= 0) {
            int position = (int) Math.round(scale(currentTick.getTickValue().getValue()));
            // tick position
            tickPositions.add(position);
            // tick label
            tickLabels.add(tickToLabel(tm, position, currentTick.getLabel(), tickPixelInterval));
            for (int i = 0; i < ticksSkipStep; i++) {
                nextTick = tickProvider.getNextTick();
            }

            if (minorTickIntervalCount > 0) {
                // minor tick positions
                double minorTickInterval = (nextTick.getTickValue().getValue() - currentTick.getTickValue().getValue()) / minorTickIntervalCount;
                double minorTickValue = currentTick.getTickValue().getValue();
                for (int i = 1; i < minorTickIntervalCount; i++) {
                    minorTickValue += minorTickInterval;
                    if (minorTickValue <= max) {
                        minorTickPositions.add((int) Math.round(scale(minorTickValue)));
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

        if (minorTickIntervalCount > 0) {
            // add minor ticks that are located between minorTick and min
            currentTick = tickProvider.getUpperTick(min);
            Tick previousTick = null;
            for (int i = 0; i < ticksSkipStep; i++) {
                previousTick = tickProvider.getPreviousTick();
            }
            double minorTickInterval = (currentTick.getTickValue().getValue() - previousTick.getTickValue().getValue()) / minorTickIntervalCount;
            double minorTickValue = currentTick.getTickValue().getValue();
            IntArrayList positions = new IntArrayList();
            for (int i = 1; i < minorTickIntervalCount; i++) {
                minorTickValue -= minorTickInterval;
                if (minorTickValue >= min) {
                    positions.add((int) Math.round(scale(minorTickValue)));
                } else {
                    break;
                }
            }
            if (positions.size() > 0) {
                // additionalPositions need to be reversed
                int[] minorTickAdditionalPositions = new int[positions.size()];
                for (int i = 0; i < positions.size(); i++) {
                    minorTickAdditionalPositions[i] = positions.get(positions.size() - 1 - i);
                }
                minorTickPositions.add(0, minorTickAdditionalPositions);
            }
        }

        isTicksDirty = false;
    }

    protected abstract void translateCanvas(BCanvas canvas, BRectangle area);

    protected abstract int labelSizeForWidth(TextMetric tm, int angle, String label);

    protected abstract int labelSizeForOverlap(TextMetric tm, int angle, String label);

    protected abstract BText tickToLabel(TextMetric tm, int tickPosition, String tickLabel, int tickPixelInterval);

    protected abstract void drawTickMark(BCanvas canvas, int tickPosition, int insideSize, int outsideSize);

    protected abstract void drawGridLine(BCanvas canvas, int tickPosition, BRectangle area);

    protected abstract void drawAxisLine(BCanvas canvas);

    protected abstract BText createTitle(BCanvas canvas);

}
