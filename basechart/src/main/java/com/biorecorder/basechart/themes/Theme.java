package com.biorecorder.basechart.themes;

import com.biorecorder.basechart.ChartConfig;
import com.biorecorder.basechart.NavigableChartConfig;

/**
 * Created by galafit on 24/2/19.
 */
public interface Theme {
    ChartConfig getChartConfig();
    NavigableChartConfig getNavigableChartConfig();
}
