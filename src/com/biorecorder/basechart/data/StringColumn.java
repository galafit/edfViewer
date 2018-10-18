package com.biorecorder.basechart.data;


import com.biorecorder.util.series.StringSeries;

/**
 * Created by galafit on 28/9/17.
 */
class StringColumn {
    protected String name;
    protected StringSeries series;
    protected long startIndex;
    protected long length = -1;

    public StringColumn(StringSeries series) {
        this.series = series;
    }

    public long size() {
        if(length < 0) {
            return series.size();
        }
        return length;
    }

    public void setViewRange(long startIndex1, long length1) {
        startIndex = startIndex1;
        length = length1;
        if(startIndex < 0) {
            startIndex = 0;
        }
        if(startIndex >= series.size()) {
            length = 0;
        }
        if(length > series.size() - startIndex) {
            length = series.size() - startIndex;
        }
    }

    public String getString(long index) {
        return series.get(index);
    }

    public StringColumn copy() {
        return new StringColumn(series);
    }
}
