package com.biorecorder.basechart.themes;

import com.biorecorder.basechart.ChartConfig;
import com.biorecorder.basechart.NavigableChartConfig;

/**
 * Created by galafit on 24/2/19.
 */
public interface Theme {
    int TICK_ACCURACY_IF_ROUNDING_ENABLED = 10;
    ChartConfig getChartConfig();
    NavigableChartConfig getNavigableChartConfig();
}
