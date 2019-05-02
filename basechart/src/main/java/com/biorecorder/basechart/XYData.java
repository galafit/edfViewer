package com.biorecorder.basechart;

import com.biorecorder.data.frame.*;
import com.biorecorder.data.sequence.IntSequence;

import java.util.List;

/**
 * Created by galafit on 21/1/19.
 */
public class XYData implements ChartData {
    private DataFrame dataFrame;

   private XYData(DataFrame dataFrame) {
        this.dataFrame = dataFrame;
    }

    public XYData(boolean isDataAppendMode) {
        dataFrame = new DataFrame(isDataAppendMode);
    }

    public XYData(double xStart, double xStep, boolean isDataAppendMode) {
        dataFrame = new DataFrame(isDataAppendMode);
        dataFrame.addColumn(xStart, xStep);
        onColumnAdded();
    }

    public XYData(List<String> xData, boolean isDataAppendMode) {
        dataFrame = new DataFrame(isDataAppendMode);
        dataFrame.addColumn(xData);
        onColumnAdded();
    }

    private void onColumnAdded() {
        onColumnAdded(dataFrame.columnCount() - 1);
    }

    private void onColumnAdded(int columnNumber) {
        Aggregation agg = Aggregation.AVERAGE;
        if(columnNumber == 0) {
            agg = Aggregation.FIRST;
        }
        dataFrame.setColumnName(columnNumber, "");
        dataFrame.setColumnAggFunctions(columnNumber, agg);
    }

    public void addColumn(Function function, int argColumnNumber) {
        dataFrame.addColumn(function, argColumnNumber);
        onColumnAdded();
    }

    public void addColumn(IntSequence columnData) {
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

    public void setColumnAggFunctions(int columnNumber, Aggregation aggFunction) throws IllegalArgumentException {
        if(aggFunction == null) {
            String errMsg = "Aggregate function must be not null";
            throw new IllegalArgumentException(errMsg);
        }
        dataFrame.setColumnAggFunctions(columnNumber, aggFunction);
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
        Stats stats = dataFrame.stats(columnNumber);
        if(stats == null) {
            return false;
        }
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
    public ChartData resampleByEqualPointsNumber(int points) {
        return new XYData(dataFrame.resampleByEqualPointsNumber(points));
    }

    @Override
    public ChartData resampleByEqualInterval(int columnNumber, double interval) {
        return new XYData(dataFrame.resampleByEqualInterval(columnNumber, interval));
    }

    @Override
    public ChartData resampleByEqualTimeInterval(int columnNumber, TimeInterval timeInterval) {
        return new XYData(dataFrame.resampleByEqualTimeInterval(columnNumber, timeInterval));
    }

    @Override
    public void cache() {
        for (int i = 0; i < dataFrame.columnCount(); i++) {
            if(!dataFrame.isColumnRegular(i) && !dataFrame.isColumnFunction(i)) {
                dataFrame.cacheColumn(i);
            }
        }
    }

    @Override
    public void disableCaching() {
        for (int i = 0; i < dataFrame.columnCount(); i++) {
            dataFrame.disableCaching(i);
        }

    }

    @Override
    public void appendData() {
        dataFrame.appendData();
    }

}
