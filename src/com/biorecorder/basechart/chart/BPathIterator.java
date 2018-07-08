package com.biorecorder.basechart.chart;

/**
 * Created by galafit on 21/1/18.
 */
public interface BPathIterator {
    public static final int SEG_MOVETO = 0;
    public static final int SEG_LINETO = 1;
    public static final int SEG_QUADTO = 2;
    public static final int SEG_CUBICTO = 3;
    public static final int SEG_CLOSE = 4;

    public boolean hasNext();
    public int next(float[] coords);
}
