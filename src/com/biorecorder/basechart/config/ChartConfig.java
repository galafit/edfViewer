package com.biorecorder.basechart.config;

import com.biorecorder.basechart.*;
import com.biorecorder.basechart.config.traces.TraceConfig;
import com.biorecorder.basechart.RangeInt;
import com.biorecorder.basechart.data.Data;
import com.biorecorder.basechart.data.DataSeries;
import com.biorecorder.basechart.graphics.BColor;
import com.biorecorder.basechart.graphics.BRectangle;
import com.biorecorder.basechart.graphics.BStroke;
import com.biorecorder.basechart.graphics.TextStyle;

import java.util.*;

/**
 * Created by galafit on 18/8/17.
 */
public class ChartConfig {
    private String title;
    private BColor background;
    private BColor marginColor;
    private Margin margin;
    private TextStyle titleTextStyle = new TextStyle(TextStyle.DEFAULT, TextStyle.BOLD, 14);
    private BColor titleColor = BColor.BLACK;
    private LegendConfig legendConfig = new LegendConfig();
    private TooltipConfig tooltipConfig = new TooltipConfig();
    private CrosshairConfig crosshairConfig = new CrosshairConfig();

    private DataProcessingConfig dataProcessingConfig = new DataProcessingConfig();

    public DataProcessingConfig getDataProcessingConfig() {
        return dataProcessingConfig;
    }

    public BColor getTitleColor() {
        return titleColor;
    }

    public void setTitleColor(BColor titleColor) {
        this.titleColor = titleColor;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BColor getBackground() {
        return background;
    }

    public void setBackground(BColor background) {
        this.background = background;
    }

    public BColor getMarginColor() {
        return marginColor;
    }

    public void setMarginColor(BColor marginColor) {
        this.marginColor = marginColor;
    }

    public Margin getMargin() {
        return margin;
    }

    public void setMargin(Margin margin) {
        this.margin = margin;
    }

    public TextStyle getTitleTextStyle() {
        return titleTextStyle;
    }

    public LegendConfig getLegendConfig() {
        return legendConfig;
    }

    public TooltipConfig getTooltipConfig() {
        return tooltipConfig;
    }

    public CrosshairConfig getCrosshairConfig() {
        return crosshairConfig;
    }
}
