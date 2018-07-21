package com.biorecorder.basechart;


/**
 * Created by galafit on 26/11/17.
 */
public class ChartWithDataManager {
    private ChartConfig config;
    private int minPixelsPerDataItem = 1;
    private float currentPreviewGroupingInterval = 0;

    private int chartGroupingStep = 2;

    private ScrollableChart scrollableChart;
    private BRectangle area;

    private List<BaseData> previewOriginalData;
    private List<BaseData> chartOriginalData;

    private ArrayList<BaseData> previewProcessedData;
    private ArrayList<BaseData> chartProcessedData;
    private boolean isChartConfigured = false;
    private boolean isAllScrollsAtTheEnd = true;
    private TraceFactory traceFactory;

    public ChartWithDataManager(ChartConfig config, BRectangle area) {
        this(config, area, new DefaultTraceFactory());
    }


    public ChartWithDataManager(ChartConfig config, BRectangle area, TraceFactory traceFactory) {
        this.area = area;
        this.config = config;
        System.out.println("areaWidth: "+area.width);

        chartOriginalData = config.getChartData();
        previewOriginalData = config.getPreviewData();

        chartProcessedData = new ArrayList<BaseData>();
        for (BaseData data : chartOriginalData) {
            if (!config.isChartGroupedDatCachingEnable()) {
                for (int i = 0; i < data.YColumnsCount(); i++) {
                    data.setYGroupingType(GroupingType.FIRST, i);
                }
            }
            chartProcessedData.add(data);
        }

        previewProcessedData = new ArrayList<BaseData>();
        for (BaseData data : previewOriginalData) {
            previewProcessedData.add(data);
        }
        this.traceFactory = traceFactory;
        scrollableChart = createScrollableChart(traceFactory);
    }

    private ScrollableChart createScrollableChart(TraceFactory traceFactory) {
        if (config.isPreviewEnable()) {
            boolean isOk = true;
            // create list of x axis used by some traces
            List<Integer> usedXAxisIndexes = new ArrayList<>();
            for (int i = 0; i < config.getChartConfig().getTraceCount(); i++) {
                int xAxisIndex = config.getChartConfig().getTraceXIndex(i);
                if (!usedXAxisIndexes.contains(xAxisIndex)) {
                    usedXAxisIndexes.add(xAxisIndex);
                }
            }

            for (Integer xAxisIndex : usedXAxisIndexes) {
                if (config.getScrollExtent(xAxisIndex) == null || config.getScrollExtent(xAxisIndex) == 0) {
                    double extent = calculateChartExtent(xAxisIndex);
                    config.addScroll(xAxisIndex, extent);
                    if (extent == 0) {
                        isOk = false;
                    }
                }
            }

            Range previewMinMax = config.getPreviewMinMax();
            if (previewMinMax == null || previewMinMax.length() == 0) {
                previewMinMax = calculateInitialPreviewMinMax();
                config.setPreviewMinMax(previewMinMax);
                if (previewMinMax == null || previewMinMax.length() == 0) {
                    isOk = false;
                }
            }
            isChartConfigured = isOk;
        } else {
            isChartConfigured = true;
        }

        Range previewMinMax = config.getPreviewMinMax();
        if (previewMinMax != null) {
            if (config.isCropEnable()) { // cropData
                for (int i = 0; i < config.getChartConfig().getXAxisCount(); i++) {
                    Double scrollExtent = config.getScrollExtent(i);
                    if (scrollExtent != null) {
                        cropChartData(i, new Range(previewMinMax.getMin(), previewMinMax.getMin() + scrollExtent));
                    }
                }
            }

            if (config.isGroupingEnable()) {
                groupPreviewData(previewMinMax);
            }
        }

        ScrollableChart chart = new ScrollableChart(config, createChartData(), createPreviewData(), area, traceFactory);
        for (Integer xAxisIndex : chart.getXAxisWithScroll()) {
            chart.addScrollListener(xAxisIndex, new ScrollListener() {
                @Override
                public void onScrollChanged(double scrollValue, double scrollExtent) {
                    if (config.isGroupingEnable()) {
                        groupChartData(xAxisIndex, scrollExtent);
                    }
                    if (config.isCropEnable()) {
                        cropChartData(xAxisIndex, new Range(scrollValue, scrollValue + scrollExtent));
                    }
                    chart.setChartData(createChartData());
                    isAllScrollsAtTheEnd = isScrollAtTheEnd(xAxisIndex);
                    if (config.isAutosSaleEnableDuringScroll()) {
                        autoScaleChartY();
                    }
                }
            });
        }
        return chart;
    }

    private boolean isScrollAtTheEnd(int xAxisIndex) {
        double dataMax = getChartDataMinMax().getMax();
        double scrollEnd = scrollableChart.getScrollValue(xAxisIndex) + scrollableChart.getScrollExtent(xAxisIndex);
        if (dataMax - scrollEnd > 0) {
            return false;
        }
        return true;
    }

    private List<DataSet> createChartData() {
        List<DataSet> chartData = new ArrayList<DataSet>();
        for (BaseData chartProcessedDatum : chartProcessedData) {
            chartData.add(chartProcessedDatum.getDataSet());
        }
        return chartData;
    }

    private List<DataSet> createPreviewData() {
        List<DataSet> previewData = new ArrayList<DataSet>();
        for (BaseData previewProcessedDatum : previewProcessedData) {
            previewData.add(previewProcessedDatum.getDataSet());
        }
        return previewData;
    }

    public ScrollableChart getScrollableChart() {
        return scrollableChart;
    }


    private void cropChartData(int xAxisIndex, Range scrollExtremes) {
        SimpleChartConfig chartConfig = config.getChartConfig();
        for (int traceIndex = 0; traceIndex < chartConfig.getTraceCount(); traceIndex++) {
            int traceDataIndex = chartConfig.getTraceDataIndex(traceIndex);
            if (chartConfig.getTraceXIndex(traceIndex) == xAxisIndex) {
                BaseData traceDataSet = chartProcessedData.get(traceDataIndex);
                BaseData subset = traceDataSet.getSubset(scrollExtremes.getMin(), scrollExtremes.getMax());
                chartProcessedData.set(traceDataIndex, subset);
            }
        }
    }

    private void groupChartData(int xAxisIndex, double scrollExtent) {
        double bestGroupingInterval = minPixelsPerDataItem * scrollExtent / area.width;
        boolean isCachingEnable = config.isChartGroupedDatCachingEnable();
        SimpleChartConfig chartConfig = config.getChartConfig();
        for (int traceIndex = 0; traceIndex < chartConfig.getTraceCount(); traceIndex++) {
            int traceDataIndex = chartConfig.getTraceDataIndex(traceIndex);
            if (chartConfig.getTraceXIndex(traceIndex) == xAxisIndex) {
                BaseData traceData = chartProcessedData.get(traceIndex);
                double dataInterval = traceData.getAverageDataInterval();
                if (dataInterval > 0) {
                    int numberOfDataItemsToGroup = (int) (bestGroupingInterval / dataInterval);
                    if (numberOfDataItemsToGroup >= chartGroupingStep) {
                        chartProcessedData.set(traceIndex, traceData.groupByNumber(numberOfDataItemsToGroup, isCachingEnable));
                    }
                    if (numberOfDataItemsToGroup <= 1.0 / chartGroupingStep) {
                        BaseData traceOriginalData = chartOriginalData.get(traceDataIndex);
                        double originalDataInterval = traceOriginalData.getAverageDataInterval();
                        numberOfDataItemsToGroup = (int) (bestGroupingInterval / originalDataInterval);
                        chartProcessedData.set(traceIndex, traceOriginalData.groupByNumber(numberOfDataItemsToGroup, isCachingEnable));
                    }
                }
            }
        }
    }

    private void groupPreviewData(Range previewMinMax) {
        double bestGroupingInterval = minPixelsPerDataItem * previewMinMax.length() / area.width;
        // choose the first interval in the list >= bestGroupingInterval
        if (currentPreviewGroupingInterval < bestGroupingInterval) {
            for (float interval : config.getPreviewGroupingIntervals()) {
                currentPreviewGroupingInterval = interval;
                if (interval >= bestGroupingInterval) {
                    break;
                }
            }
        }
        double groupingInterval = currentPreviewGroupingInterval;
        if (groupingInterval <= 0) {
            groupingInterval = bestGroupingInterval;
        }
        boolean isCachingEnable = true;
        for (int i = 0; i < previewProcessedData.size(); i++) {
            double dataInterval = previewProcessedData.get(i).getAverageDataInterval();
            if (dataInterval > 0) {
                int numberOfDataItemsToGroup = (int) (groupingInterval / dataInterval);
                previewProcessedData.set(i, (previewProcessedData.get(i)).groupByNumber(numberOfDataItemsToGroup, isCachingEnable));
            }
        }
    }

    private void autoScaleChartY() {
        for (int i = 0; i < scrollableChart.getChartYAxisCounter(); i++) {
            scrollableChart.autoScaleChartY(i);
        }
    }

    private void autoScalePreviewY() {
        for (int i = 0; i < scrollableChart.getPreviewYAxisCounter(); i++) {
            scrollableChart.autoScalePreviewY(i);
        }
    }

    private Range calculateInitialPreviewMinMax() {
        Range chartFullMinMax = null;
        for (BaseData chartData : chartOriginalData) {
            chartFullMinMax = Range.max(chartFullMinMax, chartData.getXExtremes());
        }
        if (chartFullMinMax != null) {
            // in the case if basechart has a small number of points
            // (number of basedata points < area.width)
            double previewExtent = 0;
            for (double scrollExtent : config.getScrollsExtents()) {
                previewExtent = Math.max(previewExtent, scrollExtent);
            }

            if (config.getPreviewGroupingIntervals().size() > 0) {
                float groupingInterval = config.getPreviewGroupingIntervals().get(0);
                previewExtent = Math.max(previewExtent, groupingInterval * area.width / minPixelsPerDataItem);
            }

            double min = chartFullMinMax.getMin();
            double maxLength = Math.max(previewExtent, chartFullMinMax.length());
            chartFullMinMax = new Range(min, min + maxLength);
        }
        return chartFullMinMax;
    }


    private double calculateChartExtent(int xAxisIndex) {
        SimpleChartConfig chartConfig = config.getChartConfig();
        double dataIntervalMin = 0;
        for (int traceIndex = 0; traceIndex < chartConfig.getTraceCount(); traceIndex++) {
            int traceDataIndex = chartConfig.getTraceDataIndex(traceIndex);
            if (chartConfig.getTraceXIndex(traceIndex) == xAxisIndex) {
                BaseData traceData = chartOriginalData.get(traceDataIndex);
                double dataItemInterval = traceData.getAverageDataInterval();
                if (dataItemInterval > 0) {
                    dataIntervalMin = (dataIntervalMin == 0) ? dataItemInterval : Math.min(dataIntervalMin, dataItemInterval);
                }
            }
        }
        double extent = dataIntervalMin * area.width / minPixelsPerDataItem;
        return extent;
    }

    private Range getChartDataMinMax() {
        Range minMax = null;
        SimpleChartConfig chartConfig = config.getChartConfig();
        for (int traceIndex = 0; traceIndex < chartConfig.getTraceCount(); traceIndex++) {
            int traceDataIndex = chartConfig.getTraceDataIndex(traceIndex);
            BaseData traceData = chartOriginalData.get(traceDataIndex);
            minMax = Range.max(minMax, traceData.getXExtremes());
        }
        return minMax;
    }


    public void update() {
        if (!isChartConfigured) {
            scrollableChart = createScrollableChart(traceFactory);
        } else {
            Range previewMinMax = Range.max(scrollableChart.getPreviewXMinMax(), getChartDataMinMax());
            groupPreviewData(previewMinMax);
            scrollableChart.setPreviewMinMax(previewMinMax);
            scrollableChart.setPreviewData(createPreviewData());

            if(config.isAutoScrollEnable() && isAllScrollsAtTheEnd) {
                autoScroll();
            }
        }
    }

    private boolean autoScroll() {
        Range dataMinMax = getChartDataMinMax();
        double minExtent = 0;
        for (Integer xAxisIndex : scrollableChart.getXAxisWithScroll()) {
            minExtent = (minExtent == 0) ? scrollableChart.getScrollExtent(xAxisIndex) : Math.min(minExtent, scrollableChart.getScrollExtent(xAxisIndex));
        }
        return scrollableChart.setScrollsValue(dataMinMax.getMax() - minExtent);
    }
}
