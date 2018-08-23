package com.biorecorder.basechart.scales;

import com.biorecorder.basechart.axis.TickFormatInfo;

public interface Scale {
    public Scale copy();

    public void setDomain(double... domain);

    public void setRange(double... range);

    public double[] getDomain();

    public double[] getRange();

    public double scale(double value);

    public double invert(double value);

    public String formatDomainValue(double value);

    public TickProvider getTickProviderByCount(int tickCount, TickFormatInfo formatInfo);

    public TickProvider getTickProviderByStep(double tickStep, TickFormatInfo formatInfo);

}

