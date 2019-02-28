package com.biorecorder.basechart.scales;

import com.biorecorder.basechart.axis.TickFormatInfo;
import com.sun.istack.internal.Nullable;

public interface Scale {
    public Scale copy();

    public void setDomain(double... domain);

    public void setRange(double... range);

    public double[] getDomain();

    public double[] getRange();

    public double scale(double value);

    public double invert(double value);

    /**
     * Format domain value according to the one "point precision"
     * cutting unnecessary double digits that exceeds that "point precision"
     */
    public String formatDomainValue(double value);

    public TickProvider getTickProviderByIntervalCount(int tickIntervalCount, @Nullable TickFormatInfo formatInfo);

    public TickProvider getTickProviderByInterval(double tickInterval, @Nullable TickFormatInfo formatInfo);

}

