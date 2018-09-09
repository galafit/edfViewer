package com.biorecorder.basechart;

import com.biorecorder.basechart.axis.*;
import com.biorecorder.basechart.button.StateListener;
import com.biorecorder.basechart.button.SwitchButton;
import com.biorecorder.basechart.button.ButtonGroup;
import com.biorecorder.basechart.graphics.*;
import com.biorecorder.basechart.scales.LinearScale;
import com.biorecorder.basechart.scales.Scale;
import com.biorecorder.basechart.traces.Trace;
import com.biorecorder.basechart.data.DataSeries;

import java.util.*;
import java.util.List;

/**
 * Created by hdablin on 24.03.17.
 */
public class Chart {
    private String title;
    private static final int DEFAULT_WEIGHT = 10;

    private HorizontalAlign legendHAlign = HorizontalAlign.RIGHT;
    private VerticalAlign legendVAlign = VerticalAlign.TOP;

    private boolean isLegendVisible = true;

    /*
 * 2 X-axis: 0(even) - BOTTOM and 1(odd) - TOP
 * 2 Y-axis for every section(stack): even - LEFT and odd - RIGHT;
 * All LEFT and RIGHT Y-axis are stacked.
 * If there is no trace associated with some axis... this axis is invisible.
 **/
    private List<AxisWrapper> xAxisList = new ArrayList<>(2);
    private List<AxisWrapper> yAxisList = new ArrayList<>();

    private boolean isLeftAxisPrimary = true;
    private boolean isBottomAxisPrimary = true;

    private boolean isYMinMaxRoundingEnabled = true;
    private boolean isXMinMaxRoundingEnabled = false;

    private ArrayList<Integer> stackWeights = new ArrayList<Integer>();

    private ChartConfig chartConfig = new ChartConfig();
    private DataProcessingConfig dataProcessingConfig = new DataProcessingConfig();

    private List<Trace> traces = new ArrayList<Trace>();
    private List<Legend> legends = new ArrayList<Legend>();
    private Title titleText;
    private BRectangle fullArea;
    private BRectangle graphArea;
    private Margin margin;
    private DataManager dataManager;

    /** Dirty data in js means that the data have been changed recently and
     ** DOM haven't been re-rendered according to this changes yet.
     **/
    private boolean dataDirty = true;
    private boolean areasDirty = true;

    private Crosshair crosshair;
    private Tooltip tooltip;

    private int selectedTraceIndex = -1;
    private int hoverPointIndex = -1;
    private int hoverTraceIndex = -1;

    private ButtonGroup buttonGroup = new ButtonGroup();

    public Chart(ChartConfig chartConfig1) {
        this.chartConfig = new ChartConfig(chartConfig1);
        dataManager = new DataManager(dataProcessingConfig);
        AxisWrapper bottomAxis = new AxisWrapper(new AxisBottom(new LinearScale(), chartConfig.getBottomAxisConfig()));
        AxisWrapper topAxis = new AxisWrapper(new AxisTop(new LinearScale(), chartConfig.getTopAxisConfig()));
        if(isXMinMaxRoundingEnabled) {
            topAxis.setRoundingEnabled(true);
            bottomAxis.setRoundingEnabled(true);
        }
        if(isBottomAxisPrimary) {
            bottomAxis.setGridVisible(true);
        } else {
            topAxis.setGridVisible(true);
        }
        xAxisList.add(bottomAxis);
        xAxisList.add(topAxis);

        // tooltips
        tooltip = new Tooltip(chartConfig.getTooltipConfig());
        crosshair = new Crosshair(chartConfig.getCrossHairConfig());
    }


    Double getBestExtent(int xIndex) {
        double maxExtent = 0;
        for (int i = 0; i < traces.size(); i++) {
            if(traces.get(i).getXAxisIndex() == xIndex) {
                DataSeries traceData = dataManager.getOriginalTraceData(i);
                if(traceData.size() > 1) {
                    int pixelsInDataPoint = traces.get(i).getMarkSize();
                    if(pixelsInDataPoint == 0) {
                        pixelsInDataPoint = 1;
                    }
                    double traceExtent = traceData.getDataInterval() * fullArea.width / pixelsInDataPoint;
                    maxExtent = Math.max(maxExtent, traceExtent);
                }
            }
        }
       return maxExtent;
    }

    // for all x axis
    Range getOriginalDataMinMax() {
        Range minMax = null;
        for (int i = 0; i < traces.size(); i++) {
            Trace trace = traces.get(i);
            DataSeries traceData = trace.getData();
            trace.setData(dataManager.getOriginalTraceData(i));
            Range traceMinMax = trace.getXExtremes();
            minMax = Range.join(minMax, traceMinMax);
            // restore data
            trace.setData(traceData);
        }
        return minMax;
    }

    private void updateTraceData(int xIndex) {
        AxisWrapper xAxis = xAxisList.get(xIndex);
        for (int i = 0; i < traces.size(); i++) {
            if(traces.get(i).getXAxisIndex() == xIndex) {
                traces.get(i).setData(dataManager.getProcessedTraceData(i, xAxis.getMin(), xAxis.getMax()));
            }
        }
    }

    private void initiateTraceData() {
        for (int i = 0; i < traces.size(); i++) {
            Trace trace = traces.get(i);
            AxisWrapper xAxis = xAxisList.get(trace.getXAxisIndex());
            trace.setData(dataManager.getProcessedTraceData(i, xAxis.getMin(), xAxis.getMax()));
        }
        dataDirty = false;
    }


    /**
     * 2 Y-axis for every section(stack): even - LEFT and odd - RIGHT;
     */
    private boolean isYAxisLeft(int axisIndex) {
        if ( (axisIndex & 1) == 0 ) { // even (left)
            return true;
        }
        //odd (right)
        return false;
    }

    /**
     * X-axis: 0(even) - BOTTOM and 1(odd) - TOP
     */
    private boolean isXAxisBottom(int axisIndex) {
        if ( (axisIndex & 1) == 0 ) { // even (bottom)
            return true;
        }
        //odd (top)
        return false;
    }


    Margin getMargin(BCanvas canvas) {
        if (margin == null) {
            calculateMarginsAndAreas(canvas, chartConfig.getMargin());
        }
        return margin;
    }

    BRectangle getGraphArea(BCanvas canvas) {
        if (graphArea == null) {
            calculateMarginsAndAreas(canvas, chartConfig.getMargin());
        }
        return graphArea;
    }

    void setMargin(BCanvas canvas, Margin margin) {
        calculateMarginsAndAreas(canvas, margin);
    }

    Scale getXAxisScale(int xAxisIndex) {
        return xAxisList.get(xAxisIndex).getScale();
    }


    void calculateMarginsAndAreas(BCanvas canvas, Margin marginNew) {
        if(dataDirty) {
            initiateTraceData();
        }
        if(titleText == null) {
            titleText = new Title(title, chartConfig.getTitleConfig(), fullArea, canvas);
        }

        if (marginNew != null) {
            setYStartEnd(fullArea.y + marginNew.top(), fullArea.height - marginNew.top() - marginNew.bottom());
            setXStartEnd(fullArea.x + marginNew.left(), fullArea.width - marginNew.left() - marginNew.right());
            return;
        }

        int left = 0;
        int right = 0;
        int top = titleText.getBounds().height + xAxisList.get(1).getWidth(canvas);
        int bottom = xAxisList.get(0).getWidth(canvas);

        // if margins and graph area were not calculated before or
        // width of some xAxis was changed (never should happen if label rotation angle is 0)
        if(margin == null || margin.top() != top || margin.bottom() != bottom) {
            setYStartEnd(fullArea.y + top, fullArea.height - top - bottom);
        }

        for (int i = 0; i < yAxisList.size() / 2; i++) {
            left = Math.max(left, yAxisList.get(i * 2).getWidth(canvas));
        }
        for (int i = 0; i < yAxisList.size() / 2; i++) {
            right = Math.max(right, yAxisList.get(i * 2 + 1).getWidth(canvas));
        }

        // if margins and graph area were not calculated before or
        // width of some yAxis was changed
        if(margin == null || margin.left() != left || margin.right() != right) {
            // adjust XAxis ranges
            setXStartEnd(fullArea.x + left, fullArea.width - left - right);
            int topNew = titleText.getBounds().height + xAxisList.get(1).getWidth(canvas);
            int bottomNew = xAxisList.get(0).getWidth(canvas);
            if(topNew != top || bottomNew != bottom) {
                // adjust YAxis ranges
                setYStartEnd(fullArea.y + top, fullArea.height - top - bottom);
                top = topNew;
                bottom = bottomNew;
            }
        }

        Margin resultantMargin = new Margin(top, right, bottom, left);
        if(margin == null || !margin.equals(resultantMargin)) {
            margin = resultantMargin;
            graphArea = new BRectangle(fullArea.x + left, fullArea.y + top,
                    fullArea.width - left - right, fullArea.height - top - bottom);

            for (int stackIndex = 0; stackIndex < legends.size(); stackIndex++) {
                int legendAreaYStart = yAxisList.get(2 * stackIndex).getEnd() + chartConfig.getTopAxisConfig().getAxisLineStroke().getWidth();;
                int legendAreaYEnd = yAxisList.get(2 * stackIndex).getStart() - chartConfig.getBottomAxisConfig().getAxisLineStroke().getWidth();;
                int legendAreaXStart = graphArea.x + chartConfig.getLeftAxisConfig().getAxisLineStroke().getWidth();
                int legendAreaXEnd = graphArea.x + graphArea.width - chartConfig.getRightAxisConfig().getAxisLineStroke().getWidth();;

                BRectangle legendArea = new BRectangle(legendAreaXStart, legendAreaYStart, legendAreaXEnd - legendAreaXStart, legendAreaYEnd - legendAreaYStart);
                legends.get(stackIndex).setArea(legendArea);
            }

        }
        areasDirty = false;
    }

    public int getStacksSumWeight() {
        int weightSum = 0;
        for (Integer weight : stackWeights) {
            weightSum += weight;
        }
        return weightSum;
    }


    private void setXStartEnd(int areaX, int areaWidth) {
        for (AxisWrapper axis : xAxisList) {
            axis.setStartEnd(areaX, areaX + areaWidth);
        }
    }


    private void setYStartEnd(int areaY, int areaHeight) {
        int weightSum = getStacksSumWeight();

        int weightSumTillYAxis = 0;
        int stackCount = yAxisList.size() / 2;
        for (int stack = 0; stack < stackCount; stack++) {
            int yAxisWeight = stackWeights.get(stack);
            int axisHeight = areaHeight * yAxisWeight / weightSum;
            int end = areaY + areaHeight * weightSumTillYAxis / weightSum;
            int start = end + axisHeight;

            yAxisList.get(stack * 2).setStartEnd(start, end);
            yAxisList.get(stack * 2 + 1).setStartEnd(start, end);

            weightSumTillYAxis += stackWeights.get(stack);
        }
    }



    boolean isXAxisUsed(int xAxisIndex) {
        for (Trace trace : traces) {
           if(trace.getXAxisIndex() == xAxisIndex) {
               return true;
           }
        }
        return false;
    }


    public void draw(BCanvas canvas) {
        if (areasDirty) {
            calculateMarginsAndAreas(canvas, chartConfig.getMargin());
        }

        canvas.setColor(chartConfig.getMarginColor());
        canvas.fillRect(fullArea.x, fullArea.y, fullArea.width, fullArea.height);

        canvas.setColor(chartConfig.getBackgroundColor());
        canvas.fillRect(graphArea.x, graphArea.y, graphArea.width, graphArea.height);

        canvas.enableAntiAliasAndHinting();

        int topPosition = graphArea.y;
        int bottomPosition = graphArea.y + graphArea.height;
        int leftPosition = graphArea.x;
        int rightPosition = graphArea.x + graphArea.width;

        for (int i = 0; i < xAxisList.size() / 2; i++) {
            xAxisList.get(i * 2).drawGrid(canvas, bottomPosition, graphArea.height);
            xAxisList.get(i * 2 + 1).drawGrid(canvas, topPosition, graphArea.height);
        }

        for (int i = 0; i < yAxisList.size() / 2; i++) {
            yAxisList.get(i * 2).drawGrid(canvas, leftPosition, graphArea.width);
            yAxisList.get(i * 2 + 1).drawGrid(canvas, rightPosition, graphArea.width);
        }

        for (int i = 0; i < xAxisList.size() / 2; i++) {
            xAxisList.get(i * 2).drawAxis(canvas, bottomPosition);
            xAxisList.get(i * 2 + 1).drawAxis(canvas, topPosition);
        }

        for (int i = 0; i < yAxisList.size() / 2; i++) {
            yAxisList.get(i * 2).drawAxis(canvas, leftPosition);
            yAxisList.get(i * 2 + 1).drawAxis(canvas, rightPosition);
        }

        canvas.save();
        canvas.setClip(graphArea.x, graphArea.y, graphArea.width, graphArea.height);

        for (Trace trace : traces) {
            trace.draw(canvas, xAxisList.get(trace.getXAxisIndex()).getScale(), yAxisList.get(trace.getYAxisIndex()).getScale());
        }
        canvas.restore();

        titleText.draw(canvas);

        if(isLegendVisible) {
            for (Legend legend : legends) {
                legend.draw(canvas);
            }
        }

        if (hoverTraceIndex >= 0 && hoverPointIndex >= 0) {
            crosshair.draw(canvas, graphArea);
            tooltip.draw(canvas, fullArea);
        }
    }


    /**
     * =======================Base methods to interact==========================
     **/


    public ChartConfig getConfig() {
        return chartConfig;
    }


    public void setConfig(ChartConfig chartConfig1) {
        this.chartConfig = new ChartConfig(chartConfig1);
        // axis
        for (int i = 0; i < xAxisList.size(); i++) {
            if(isXAxisBottom(i)) {
                xAxisList.get(i).setConfig(chartConfig.getBottomAxisConfig());
            } else {
                xAxisList.get(i).setConfig(chartConfig.getTopAxisConfig());
            }
        }

        for (int i = 0; i < yAxisList.size(); i++) {
           if(isYAxisLeft(i)) {
               yAxisList.get(i).setConfig(chartConfig.getLeftAxisConfig());
           } else {
               yAxisList.get(i).setConfig(chartConfig.getRightAxisConfig());
           }
        }
        // tooltips
        tooltip = new Tooltip(chartConfig.getTooltipConfig());
        crosshair = new Crosshair(chartConfig.getCrossHairConfig());

        //legends
        for (Legend legend : legends) {
            legend.setConfig(chartConfig.getLegendConfig());
        }

        int colorsCount = chartConfig.getTraceColors().length;
        for (int i = 0; i < traces.size(); i++) {
            BColor traceColor = chartConfig.getTraceColors()[i % colorsCount];
            traces.get(i).setMainColor(traceColor);
            int stackNumber = traces.get(i).getYAxisIndex() / 2;
            legends.get(stackNumber).getButton(i).setColor(traceColor);
        }

        // title
        titleText = null;

        areasDirty = true;
    }


    public void addStack(int weight) {
        AxisWrapper leftAxis = new AxisWrapper(new AxisLeft(new LinearScale(), chartConfig.getLeftAxisConfig()));
        AxisWrapper rightAxis = new AxisWrapper(new AxisRight(new LinearScale(), chartConfig.getRightAxisConfig()));
        if(isYMinMaxRoundingEnabled) {
            leftAxis.setRoundingEnabled(true);
            rightAxis.setRoundingEnabled(true);
        }
        if(isLeftAxisPrimary) {
            leftAxis.setGridVisible(true);
        } else {
            rightAxis.setGridVisible(true);
        }
        yAxisList.add(leftAxis);
        yAxisList.add(rightAxis);
        stackWeights.add(weight);

        legends.add(new Legend(chartConfig.getLegendConfig(), legendHAlign, legendVAlign, buttonGroup));
        areasDirty = true;
    }

    public void addStack() {
        addStack(DEFAULT_WEIGHT);
    }


    /**
     * add trace to the stack with the given number
     * @param stackNumber
     * @param trace
     * @param traceData
     * @param isXAxisOpposite
     * @param isYAxisOpposite
     */
    public void addTrace(int stackNumber, Trace trace, DataSeries traceData,  boolean isXAxisOpposite, boolean isYAxisOpposite) {
        dataManager.addTrace(traceData.copy(), trace.getMarkSize());

        boolean isBottomXAxis = true;
        boolean isLeftYAxis = true;
        if (isXAxisOpposite && isBottomAxisPrimary) {
            isBottomXAxis = false;
        }
        if (!isXAxisOpposite && !isBottomAxisPrimary) {
            isBottomXAxis = false;
        }
        if (isYAxisOpposite && isLeftAxisPrimary) {
            isLeftYAxis = false;
        }
        if (!isYAxisOpposite && !isLeftAxisPrimary) {
            isLeftYAxis = false;
        }
        int xAxisIndex = isBottomXAxis ? 0 : 1;
        int yAxisIndex = isLeftYAxis ? stackNumber * 2 : stackNumber * 2 + 1;
        trace.setXAxisIndex(xAxisIndex);
        trace.setYAxisIndex(yAxisIndex);
        yAxisList.get(yAxisIndex).setVisible(true);
       // xAxisList.get(xAxisIndex).setVisible(true);

        if(trace.getName() == null) {
            trace.setName("Trace " + traces.size());
        }
        if(trace.getMainColor() == null) {
            BColor[] traceColors = chartConfig.getTraceColors();
            trace.setMainColor(traceColors[traces.size() % traceColors.length]);
        }

        traces.add(trace);

        double xMin = xAxisList.get(xAxisIndex).getMin();
        double xMax = xAxisList.get(xAxisIndex).getMax();
        if(!dataDirty) {
            trace.setData(dataManager.getProcessedTraceData(traces.size() - 1, xMin, xMax));
        }

        // add trace legend button
        SwitchButton legendButton = new SwitchButton(trace.getMainColor(), trace.getName());
        final int traceIndex = traces.size() - 1;
        legendButton.addListener(new StateListener() {
            @Override
            public void stateChanged(boolean isSelected) {
                if(isSelected) {
                    selectedTraceIndex = traceIndex;
                }
                if(!isSelected && selectedTraceIndex == traceIndex) {
                    selectedTraceIndex = -1;
                }
            }
        });
        legends.get(stackNumber).add(traceIndex, legendButton);
    }


    public void setArea(BRectangle area) {
        fullArea = area;
        margin = null;
        setXStartEnd(area.x, area.width);
        setYStartEnd(area.y, area.height);
        areasDirty = true;
    }

    public int traceCount() {
        return traces.size();
    }

    public boolean selectTrace(int x, int y) {
        for (Legend legend : legends) {
            if(legend.selectItem(x, y)) {
                return true;
            }
        }
        return false;
    }

    public boolean selectTrace(int traceIndex) {
        if(selectedTraceIndex != traceIndex) {
            selectedTraceIndex = traceIndex;
            return true;
        }
        return false;
    }

    public int getSelectedTraceIndex() {
        return selectedTraceIndex;
    }


    public int xAxisCount() {
        return xAxisList.size();
    }

    public int yAxisCount() {
        return yAxisList.size();
    }


    public void setXMinMax(int xAxisIndex, double min, double max) {
        xAxisList.get(xAxisIndex).setMinMax(min, max);
        updateTraceData(xAxisIndex);
        areasDirty = true;
    }

    public Range getXMinMax(int xAxisIndex) {
        return new Range(xAxisList.get(xAxisIndex).getMin(), xAxisList.get(xAxisIndex).getMax());
    }

    public Range getYMinMax(int yAxisIndex) {
        return new Range(yAxisList.get(yAxisIndex).getMin(), yAxisList.get(yAxisIndex).getMax());
    }

    public void setYMinMax(int yAxisIndex, double min, double max) {
        yAxisList.get(yAxisIndex).setMinMax(min, max);
        areasDirty = true;
    }


    public RangeInt getYStartEnd(int yAxisIndex) {
        return new RangeInt(yAxisList.get(yAxisIndex).getEnd(), yAxisList.get(yAxisIndex).getStart());
    }

    public int getXIndex(int x, int y) {
        if (fullArea.y + fullArea.height / 2 <= y) {
            for (int i = 0; i < traces.size(); i++) {
                if (getTraceXIndex(i) == 0) {
                    return 0; // bottom Axis
                }
            }
            return 1; // top Axis
        }
        if (fullArea.y <= y && y <= fullArea.y + fullArea.height / 2) {
            for (int i = 0; i < traces.size(); i++) {
                if (getTraceXIndex(i) == 1) {
                    return 1; // top Axis
                }
            }
            return 0; // bottom Axis
        }
        return -1;
    }

    /**
     * Find and return Y axis used by the traces belonging to the stack containing point (x, y)
     */
    public int getYIndex(int x, int y) {
        for (int stackIndex = 0; stackIndex < yAxisList.size() / 2; stackIndex++) {
            if (yAxisList.get(2 * stackIndex).getEnd() <= y && yAxisList.get(2 * stackIndex).getStart() >= y) {
                int stackLeftYAxisIndex = 2 * stackIndex;
                int stackRightYAxisIndex = 2 * stackIndex + 1;
                if (fullArea.x <= x && x <= fullArea.x + fullArea.width / 2) { // left half
                    for (int i = 0; i < traces.size(); i++) {
                        if (getTraceYIndex(i) == stackLeftYAxisIndex) { // if leftAxis is used by some trace
                            return stackLeftYAxisIndex;
                        }
                    }
                    return stackRightYAxisIndex;
                }
                if (fullArea.x + fullArea.width / 2 <= x && x <= fullArea.x + fullArea.width) { // right half
                    for (int i = 0; i < traces.size(); i++) {
                        if (getTraceYIndex(i) == stackRightYAxisIndex) { // if rightAxis is used by some trace
                            return stackRightYAxisIndex;
                        }
                    }
                    return stackLeftYAxisIndex;
                }
            }
        }
        return -1;
    }


    public void zoomY(int yAxisIndex, double zoomFactor) {
        Scale zoomedScale = yAxisList.get(yAxisIndex).zoom(zoomFactor);
        double zoomedMin = zoomedScale.getDomain()[0];
        double zoomedMax = zoomedScale.getDomain()[zoomedScale.getDomain().length - 1];
        setYMinMax(yAxisIndex, zoomedMin, zoomedMax);
    }

    public void zoomX(int xAxisIndex, double zoomFactor) {
        Scale zoomedScale =  xAxisList.get(xAxisIndex).zoom(zoomFactor);
        double zoomedMin = zoomedScale.getDomain()[0];
        double zoomedMax = zoomedScale.getDomain()[zoomedScale.getDomain().length - 1];
        setXMinMax(xAxisIndex, zoomedMin, zoomedMax);
    }

    public void translateY(int yAxisIndex, int translation) {
        Scale translatedScale =  yAxisList.get(yAxisIndex).translate(translation);
        double translatedMin = translatedScale.getDomain()[0];
        double translatedMax = translatedScale.getDomain()[translatedScale.getDomain().length - 1];
        setYMinMax(yAxisIndex, translatedMin, translatedMax);
    }

    public void translateX(int xAxisIndex, int translation) {
        Scale translatedScale = xAxisList.get(xAxisIndex).translate(translation);
        double translatedMin = translatedScale.getDomain()[0];
        double translatedMax = translatedScale.getDomain()[translatedScale.getDomain().length - 1];
        setXMinMax(xAxisIndex, translatedMin, translatedMax);
    }

    public void autoScaleX(int xIndex) {
        Range tracesMinMax = null;
        for (int i = 0; i < traces.size(); i++) {
            if (getTraceXIndex(i) == xIndex) {
                Trace trace = traces.get(i);
                trace.setData(dataManager.getOriginalTraceData(i));
                tracesMinMax = Range.join(tracesMinMax, traces.get(i).getXExtremes());
            }
        }
        if(tracesMinMax != null) {
            xAxisList.get(xIndex).setMinMax(tracesMinMax.getMin(), tracesMinMax.getMax());
            areasDirty = true;
        }
        dataDirty = false;
    }

    public void autoScaleY(int yIndex) {
        if(dataDirty) {
            initiateTraceData();
        }
        Range tracesMinMax = null;
        for (int i = 0; i < traces.size(); i++) {
            if (getTraceYIndex(i) == yIndex) {
                tracesMinMax = Range.join(tracesMinMax, traces.get(i).getYExtremes());
            }
        }
        if(tracesMinMax != null) {
            setYMinMax(yIndex, tracesMinMax.getMin(), tracesMinMax.getMax());
        }
    }

    public int getTraceYIndex(int traceIndex) {
        return traces.get(traceIndex).getYAxisIndex();
    }

    public int getTraceXIndex(int traceIndex) {
        return traces.get(traceIndex).getXAxisIndex();
    }


    public boolean hoverOff() {
        if (hoverPointIndex >= 0) {
            hoverPointIndex = -1;
            return true;
        }
        return false;
    }

    public boolean hoverOn(int x, int y, int traceIndex) {
        if(!graphArea.contains(x, y)) {
            return false;
        }
        if(traceIndex >= 0) {
            hoverTraceIndex = traceIndex;
        } else {
            for (int stackIndex = 0; stackIndex < yAxisList.size() / 2; stackIndex++) {
                if (yAxisList.get(2 * stackIndex).getEnd() <= y && yAxisList.get(2 * stackIndex).getStart() >= y) {
                    for (int i = 0; i < traces.size(); i++) {
                        if (getTraceYIndex(i) == 2 * stackIndex || getTraceYIndex(i) == 2 * stackIndex + 1) {
                            hoverTraceIndex = i;
                            break;
                        }
                    }
                }
            }
        }

        if (hoverTraceIndex >= 0) {
            Trace trace = traces.get(hoverTraceIndex);
            Scale xScale = xAxisList.get(trace.getXAxisIndex()).getScale();
            Scale yScale = yAxisList.get(trace.getYAxisIndex()).getScale();

            int nearestIndex = (int)trace.findNearestData(x, y, xScale, yScale);
            if (hoverPointIndex != nearestIndex) {
                hoverPointIndex = nearestIndex;
                if (hoverPointIndex >= 0) {
                    TooltipInfo tooltipInfo = new TooltipInfo();
                    tooltipInfo.addItems(traces.get(hoverTraceIndex).getInfo(hoverPointIndex, xScale, yScale));
                    BPoint dataPosition = traces.get(hoverTraceIndex).getDataPosition(hoverPointIndex, xScale, yScale);
                    tooltip.setTooltipInfo(tooltipInfo);
                    tooltip.setXY(dataPosition.getX(), yAxisList.get(getTraceYIndex(hoverTraceIndex)).getEnd());
                    crosshair.setXY(dataPosition.getX(), dataPosition.getY());
                }
                return true;
            }
        }
        return false;
    }

    class AxisWrapper {
        private Axis axis;
        private boolean isVisible = false;
        private boolean isGridVisible = false;
        private boolean isRoundingEnabled = false;
        // need this field to implement smooth zooming and translate when minMaxRounding enabled
        private Range rowMinMax; // without rounding
        private boolean roundingDirty = true;


        public AxisWrapper(Axis axis) {
            this.axis = axis;
            rowMinMax = new Range(axis.getMin(), axis.getMax());
        }

        private void setRoundingDirty() {
            roundingDirty = true;
            axis.setMinMax(rowMinMax.getMin(), rowMinMax.getMax());
        }

        private boolean isDirty() {
            if(isRoundingEnabled  && roundingDirty) {
                return true;
            }
            return false;
        }

        public void setRoundingEnabled(boolean roundingEnabled) {
            isRoundingEnabled = roundingEnabled;
            setRoundingDirty();
        }

        public void setTitle(String title) {
            axis.setTitle(title);
        }

        public void setTickInterval(double tickInterval) {
            axis.setTickInterval(tickInterval);
            setRoundingDirty();
        }

        public void setMinorTickIntervalCount(int minorTickIntervalCount) {
            axis.setMinorTickIntervalCount(minorTickIntervalCount);
        }

        public void setTickFormatInfo(TickFormatInfo tickFormatInfo) {
            axis.setTickFormatInfo(tickFormatInfo);
            setRoundingDirty();
        }

        public void setScale(Scale scale) {
            axis.setScale(scale);
        }

        public Scale getScale() {
            return axis.getScale();
        }


        public void setConfig(AxisConfig config) {
            axis.setConfig(config);
            setRoundingDirty();
        }


        public Scale zoom(double zoomFactor) {
            // to have smooth zooming we do it on row domain values instead of rounded ones !!!
            axis.setMinMax(rowMinMax.getMin(), rowMinMax.getMax());
            return axis.zoom(zoomFactor);
        }


        public Scale translate(int translation) {
            // to have smooth translating we do it on row domain values instead of rounded ones !!!
            axis.setMinMax(rowMinMax.getMin(), rowMinMax.getMax());
            Scale scale = axis.translate(translation);
            return scale;
        }

        public void setMinMax(double min, double max) {
            axis.setMinMax(min, max);
            rowMinMax = new Range(min, max);
            roundingDirty = true;
        }

        public void setStartEnd(double start, double end) {
            axis.setStartEnd(start, end);
            setRoundingDirty();
        }

        public double getMin() {
            return axis.getMin();
        }

        public double getMax() {
            return axis.getMax();
        }

        public int getStart() {
            return (int)axis.getStart();
        }

        public int getEnd() {
            return (int)axis.getEnd();
        }

        public double scale(double value) {
            return axis.scale(value);
        }

        public double invert(float value) {
            return axis.invert(value);
        }

        public int getWidth(BCanvas canvas) {
            if(isVisible) {
                if(isDirty()) {
                    axis.roundMinMax(canvas);
                    roundingDirty = false;
                }
                return axis.getWidth(canvas);
            }
            return 0;
        }

        public void drawGrid(BCanvas canvas, int axisOriginPoint, int length) {
            if(isVisible  && isGridVisible) {
                if(isDirty()) {
                    axis.roundMinMax(canvas);
                    roundingDirty = false;
                }
                axis.drawGrid(canvas, axisOriginPoint, length);
            }
        }

        public void drawAxis(BCanvas canvas, int axisOriginPoint) {
            if(isVisible) {
                if(isDirty()) {
                    axis.roundMinMax(canvas);
                    roundingDirty = false;
                }
                axis.drawAxis(canvas, axisOriginPoint);
            }
        }

        public void setVisible(boolean visible) {
            isVisible = visible;
        }

        public void setGridVisible(boolean gridVisible) {
            isGridVisible = gridVisible;
        }
    }
}
