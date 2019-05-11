package com.biorecorder.basechart;


import com.biorecorder.data.frame.TimeInterval;
import com.biorecorder.data.frame.TimeUnit;

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

    double value(int rowNumber, int columnNumber);

    String label(int rowNumber, int columnNumber);

    Range columnMinMax(int columnNumber);

    int bisect(int columnNumber, double value, int[] sorter);

    ChartData view(int fromRowNumber, int length);

    ChartData view(int fromRowNumber);

    ChartData slice(int fromRowNumber, int length);

    ChartData concat(ChartData data);

    int[] sortedIndices(int sortColumn);

    ChartData resampleByEqualPointsNumber(int points);

    ChartData resampleByEqualInterval(int columnNumber, double interval);

    ChartData resampleByEqualTimeInterval(int columnNumber, TimeInterval timeInterval);

    void appendData();
}

