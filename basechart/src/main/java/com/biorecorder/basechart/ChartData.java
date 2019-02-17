package com.biorecorder.basechart;


/**
 * Simplified analogue of data table which
 * in fact is simply a collection of columns
 */
public interface ChartData {
    void addDataAppendListener(DataAppendListener listener);

    int rowCount();

    int columnCount();

    String getColumnName(int columnNumber);

    void setColumnName(int columnNumber, String name);

    boolean isNumberColumn(int columnNumber);

    boolean isColumnRegular(int columnNumber);

    boolean isColumnIncreasing(int columnNumber);

    double getValue(int rowNumber, int columnNumber);

    String getLabel(int rowNumber, int columnNumber);

    BRange getColumnMinMax(int columnNumber);

    int bisect(int columnNumber, double value, int[] sorter);

    ChartData view(int fromRowNumber, int length);

    int[] sortedIndices(int sortColumn);

    ChartData resampleByEqualFrequency(int points);

    ChartData resampleByEqualInterval(int columnNumber, double interval);

    void cache();
    void disableCaching();

}

