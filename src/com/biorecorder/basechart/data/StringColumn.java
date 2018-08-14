package com.biorecorder.basechart.data;


import com.biorecorder.basechart.series.StringSeries;

/**
 * Created by galafit on 28/9/17.
 */
class StringColumn {
    private StringSeries series;

    public StringColumn(StringSeries series) {
        this.series = series;
    }

    public long size() {
        return series.size();
    }

    public String getString(long index) {
        return series.get(index);
    }

    public StringColumn copy() {
        return new StringColumn(series);
    }
}
