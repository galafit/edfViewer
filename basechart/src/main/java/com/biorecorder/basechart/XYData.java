package com.biorecorder.basechart;

import com.biorecorder.data.aggregation.AggregateFunction;
import com.biorecorder.data.frame.DataFrame;
import com.biorecorder.data.sequence.IntSequence;

import java.util.List;

/**
 * Created by galafit on 21/1/19.
 */
public class XYData implements ChartData {
    private DataFrame dataFrame;

    public XYData(DataFrame dataFrame) {
        this.dataFrame = dataFrame;
    }

    public XYData() {
        dataFrame = new DataFrame();
    }

    public XYData(double start, double step) {
        dataFrame = new DataFrame();
        dataFrame.addColumn(start, step);
    }

    public void addColumn(IntSequence columnData) {
        dataFrame.addColumn(columnData);
    }

    public void addColumn(double start, double step) {
        dataFrame.addColumn(start, step);
    }

    public void addColumn(List<Integer> columnData) {
        dataFrame.addColumn(columnData);
    }

    public void addColumn(int[] columnData) {
       dataFrame.addColumn(columnData);
    }

   public void setColumnName(int columnNumber, String columnName) {
        dataFrame.setColumnName(columnNumber, columnName);
   }

    public void setColumnAggFunctions(int columnNumber, AggregateFunction... aggFunctions) {
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
        return dataFrame.getColumnType(columnNumber).isNumber();
    }

    @Override
    public boolean isColumnRegular(int columnNumber) {
        return dataFrame.isColumnRegular(columnNumber);
    }

    @Override
    public boolean isColumnIncreasing(int columnNumber) {
        return dataFrame.isColumnIncreasing(columnNumber);
    }

    @Override
    public ChartData sort(int sortColumn) {
        return new XYData(dataFrame.sort(sortColumn));
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
    public BRange getColumnMinMax(int columnNumber) {
        return dataFrame.getColumnMinMax(columnNumber);
    }

    @Override
    public int bisect(int columnNumber, double value) {
        return dataFrame.bisect(columnNumber, value);
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
                dataFrame.cacheColumn(i, 1);
            }
        }
    }

    @Override
    public void disableCaching() {
        dataFrame.disableCaching();

    }

    @Override
    public void update() {
        dataFrame.update();
    }
}
