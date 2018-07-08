package com.biorecorder.basechart.chart.scales;

import com.biorecorder.basechart.chart.config.LabelFormatInfo;

public interface Scale {
    public void setDomain(double... domain);

    public void setRange(float... range);

    public double[] getDomain();

    public float[] getRange();

    public float scale(double value);

    public double invert(float value);

    public String formatDomainValue(double value);

    public TickProvider getTickProvider(int tickCount, LabelFormatInfo labelFormatInfo);

    public TickProvider getTickProvider(double tickStep, Unit tickUnit, LabelFormatInfo labelFormatInfo);

}

