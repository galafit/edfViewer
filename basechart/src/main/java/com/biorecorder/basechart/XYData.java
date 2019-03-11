package com.biorecorder.basechart;

import com.biorecorder.data.aggregation.AggregateFunction;
import com.biorecorder.data.frame.DataFrame;
import com.biorecorder.data.frame.Stats;
import com.biorecorder.data.sequence.IntSequence;

import java.util.List;

/**
 * Created by galafit on 21/1/19.
 */
public class XYData implements ChartData {
    private DataFrame dataFrame;

    @Override
    public void addDataAppendListener(DataAppendListener listener) {

    }

    public XYData(DataFrame dataFrame) {
        this.dataFrame = dataFrame;
    }

    public XYData() {
        dataFrame = new DataFrame();
    }

    public XYData(double start, double step) {
        dataFrame = new DataFrame();
        addColumn(start, step);
    }

    private void onColumnAdded() {
        int lastColumn = dataFrame.columnCount() - 1;
        AggregateFunction agg = AggregateFunction.AVERAGE;
        if(lastColumn == 0) {
            agg = AggregateFunction.FIRST;
        }
        dataFrame.setColumnName(lastColumn, "");
        dataFrame.setColumnAggFunctions(lastColumn, agg);
    }

    public void addColumn(IntSequence columnData) {
        dataFrame.addColumn(columnData);
        onColumnAdded();
    }

    public void addColumn(double start, double step) {
        dataFrame.addColumn(start, step);
        onColumnAdded();
    }

    public void addColumn(List<Integer> columnData) {
        dataFrame.addColumn(columnData);
        onColumnAdded();
    }

    public void addColumn(int[] columnData) {
        dataFrame.addColumn(columnData);
        onColumnAdded();
    }

    public void setColumnName(int columnNumber, String columnName) {
        dataFrame.setColumnName(columnNumber, columnName);
   }

    public void setColumnAggFunctions(int columnNumber, AggregateFunction... aggFunctions) throws IllegalArgumentException {
        if(aggFunctions.length == 0) {
            String errMsg = "No aggregate function is specified for column " + columnNumber;
            throw new IllegalArgumentException(errMsg);
        }
        dataFrame.setColumnAggFunctions(columnNumber, aggFunctions);
    }

    @Override
    public int rowCount() {
        return dataFrame.rowCount();
    }

    @Override
    public int columnCount() {
        return dataFrame.columnCount();
    }

    @Override
    public String getColumnName(int columnNumber) {
        return dataFrame.getColumnName(columnNumber);
    }

    @Override
    public boolean isNumberColumn(int columnNumber) {
        return dataFrame.columnType(columnNumber).isNumber();
    }

    @Override
    public boolean isColumnRegular(int columnNumber) {
        return dataFrame.isColumnRegular(columnNumber);
    }

    @Override
    public boolean isColumnIncreasing(int columnNumber) {
        return dataFrame.stats(columnNumber).isIncreasing();
    }

    @Override
    public int[] sortedIndices(int sortColumn) {
        return dataFrame.sortedIndices(sortColumn);
    }

    @Override
    public double getValue(int rowNumber, int columnNumber) {
        return dataFrame.getValue(rowNumber, columnNumber);
    }

    @Override
    public String getLabel(int rowNumber, int columnNumber) {
        return dataFrame.getLabel(rowNumber, columnNumber);
    }

    @Override
    public Range getColumnMinMax(int columnNumber) {
        Stats stats = dataFrame.stats(columnNumber);
        if(stats == null) {
            return null;
        }
        return new Range(stats.min(), stats.max());
    }

    @Override
    public int bisect(int columnNumber, double value, int[] sorter) {
        return dataFrame.bisect(columnNumber, value, sorter);
    }

    @Override
    public ChartData view(int fromRowNumber, int length) {
        return new XYData(dataFrame.view(fromRowNumber, length));
    }

    @Override
    public ChartData resampleByEqualFrequency(int points) {
        return new XYData(dataFrame.resampleByEqualFrequency(points));
    }

    @Override
    public ChartData resampleByEqualInterval(int columnNumber, double interval) {
        return new XYData(dataFrame.resampleByEqualInterval(columnNumber, interval));
    }

    @Override
    public void cache() {
        for (int i = 0; i < dataFrame.columnCount(); i++) {
            if(!dataFrame.isColumnRegular(i)) {
                dataFrame.cacheColumn(i);
            }
        }
    }

    @Override
    public void disableCaching() {
        dataFrame.disableCaching();

    }

}
