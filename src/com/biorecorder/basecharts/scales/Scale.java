package com.biorecorder.basecharts.scales;

import com.biorecorder.basecharts.axis.TickFormatInfo;

public interface Scale {
    public Scale copy();

    public void setDomain(double... domain);

    public void setRange(double... range);

    public double[] getDomain();

    public double[] getRange();

    public double scale(double value);

    public double invert(double value);

    public String formatDomainValue(double value);

    public TickProvider getTickProviderByIntervalCount(int tickIntervalCount, TickFormatInfo formatInfo);

    public TickProvider getTickProviderByInterval(double tickInterval, TickFormatInfo formatInfo);

}

