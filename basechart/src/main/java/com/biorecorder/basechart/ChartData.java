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

    boolean isColumnRegular(int columnNumber);

    boolean isColumnIncreasing(int columnNumber);

    double getValue(int rowNumber, int columnNumber);

    String getLabel(int rowNumber, int columnNumber);

    BRange getColumnMinMax(int columnNumber);

    int nearest(int columnNumber, double value);

    ChartData view(int fromRowNumber, int length);

    ChartData sort(int sortColumn);

    ChartData resample(int columnNumber, double interval, GroupingType groupingType);

    void cache();
    void disableCaching();

    void update();
}

