package com.biorecorder.basechart;


/**
 * Simplified analogue of data table which
 * in fact is simply a collection of columns
 */
public interface ChartData {
    int rowCount();

    int columnCount();

    String getColumnName(int columnNumber);

    boolean isNumberColumn(int columnNumber);

    boolean isStringColumn(int columnNumber);

    boolean isRegular(int columnNumber);

    double getValue(int rowNumber, int columnNumber);

    String getLabel(int rowNumber, int columnNumber);

    BRange getColumnRange(int columnNumber);

    int nearest(int columnNumber, double value);

    ChartData slice(int fromRowNumber, int length);

    ChartData resample(int columnNumber, double interval, boolean isEqualFrequencyGrouping);

    ChartData cache();
    void disableCache();

    void update();
}

