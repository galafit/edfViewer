package com.biorecorder.data.frame;

import com.biorecorder.basechart.BRange;
import com.biorecorder.basechart.GroupingType;
import com.biorecorder.data.aggregation.AggregateFunction;
import com.biorecorder.data.sequence.IntSequence;

import java.util.ArrayList;
import java.util.List;


/**
 * Simplified analogue of data table which
 * in fact is simply a collection of columns
 */
public class DataFrame {
    private int length;
    protected List<Column> columns = new ArrayList<>();
    protected List<String> columnNames = new ArrayList<>();
    protected List<AggregateFunction[]> columnAggFunctions = new ArrayList<>();

    public DataFrame() {
    }

    public DataFrame(DataFrame dataFrame, int[] columnOrder) {
        for (int i = 0; i < columnOrder.length; i++) {
            columns.add(dataFrame.columns.get(columnOrder[i]));
            columnNames.add(dataFrame.columnNames.get(columnOrder[i]));
            columnAggFunctions.add(dataFrame.columnAggFunctions.get(columnOrder[i]));
        }
        update();
    }

    public void removeColumn(int columnNumber) {
        columns.remove(columnNumber);
        columnNames.remove(columnNumber);
        columnAggFunctions.remove(columnNumber);
        update();
    }

    private void addColumn(Column column) {
        columns.add(column);
        columnNames.add("Column " + (columns.size() - 1));
        AggregateFunction[] agg = new AggregateFunction[1];
        agg[0] = AggregateFunction.FIRST;
        columnAggFunctions.add(agg);
        update();
    }

    public void addColumn(IntSequence columnData) {
        addColumn(new IntColumn(columnData));
    }

    public void addColumn(double start, double step) {
        addColumn(new RegularColumn(start, step));
    }

    public void addColumn(List<Integer> columnData) {
        addColumn(new IntSequence() {
            @Override
            public int size() {
                return columnData.size();
            }

            @Override
            public int get(int index) {
                return columnData.get(index);
            }
        });
    }

    public void addColumn(int[] columnData) {
        addColumn(new IntSequence() {
            @Override
            public int size() {
                return columnData.length;
            }

            @Override
            public int get(int index) {
                return columnData[index];
            }
        });
    }

    public boolean isColumnIncreasing(int columnNumber) {
        return columns.get(columnNumber).isIncreasing(length);
    }

    public boolean isColumnDecreasing(int columnNumber) {
        return columns.get(columnNumber).isDecreasing(length);
    }


    public boolean isColumnRegular(int columnNumber) {
        return columns.get(columnNumber) instanceof RegularColumn;
    }

    public int rowCount() {
        return length;
    }

    public int columnCount() {
        return columns.size();
    }

    public void setColumnAggFunctions(int columnNumber, AggregateFunction... aggFunctions) {
        columnAggFunctions.set(columnNumber, aggFunctions);
    }

    public String getColumnName(int columnNumber) {
        return columnNames.get(columnNumber);
    }

    public void setColumnName(int columnNumber, String name) {
        columnNames.set(columnNumber, name);
    }

    public DataType getColumnType(int columnNumber) {
        return columns.get(columnNumber).dataType();
    }

    public double getValue(int rowNumber, int columnNumber) {
        return columns.get(columnNumber).value(rowNumber);
    }

    public String getLabel(int rowNumber, int columnNumber) {
        return columns.get(columnNumber).label(rowNumber);
    }

    public BRange getColumnMinMax(int columnNumber) {
        return columns.get(columnNumber).minMax(length);
    }

    /**
     * Binary search algorithm. The column data must be sorted!
     * Find the index of the <b>value</b> in the given column. If the column contains
     * multiple elements equal to the searched <b>value</b>, there is no guarantee which
     * one will be found. If there is no element equal to the searched value function returns
     * the insertion point for <b>value</b> in the column to maintain sorted order
     * (i.e. index of the first element in the column which is less than the searched value).
     */
    public int bisect(int columnNumber, double value) {
        return columns.get(columnNumber).bisect(value, 0, length);
    }


    /**
     * This method returns a sorted view of the data frame
     * without modifying the order of the underlying data.
     * (like JTable sort in java)
     */
    public DataFrame sort(int sortColumn) {
        return view(getSortedRows(sortColumn));
    }

    /**
     * This method returns an array of row numbers
     * which represent sorted version (view) of the given column.
     * (Similar to google chart DataTable.getSortedRows -
     * https://developers.google.com/chart/interactive/docs/reference#DataTable)
     * @return array of sorted rows for the given column.
     */
    public int[] getSortedRows(int sortColumn) {
        boolean isParallel = false;
        return columns.get(sortColumn).sort(0, length, isParallel);
    }

    public DataFrame slice(int fromRowNumber, int length) {
        DataFrame resultantFrame = new DataFrame();
        for (int i = 0; i < columns.size(); i++) {
            resultantFrame.columns.add(columns.get(i).slice(fromRowNumber, length));
            resultantFrame.columnNames.add(columnNames.get(i));
            resultantFrame.columnAggFunctions.add(columnAggFunctions.get(i));
        }
        resultantFrame.update();
        return resultantFrame;
    }

    public DataFrame view(int fromRowNumber, int length) {
        DataFrame resultantFrame = new DataFrame();
        for (int i = 0; i < columns.size(); i++) {
            resultantFrame.columns.add(columns.get(i).view(fromRowNumber, length));
            resultantFrame.columnNames.add(columnNames.get(i));
            resultantFrame.columnAggFunctions.add(columnAggFunctions.get(i));
        }
        resultantFrame.update();
        return resultantFrame;
    }

    public DataFrame view(int[] rowOrder) {
        DataFrame resultantFrame = new DataFrame();
        for (int i = 0; i < columns.size(); i++) {
            resultantFrame.columns.add(columns.get(i).view(rowOrder));
            resultantFrame.columnNames.add(columnNames.get(i));
            resultantFrame.columnAggFunctions.add(columnAggFunctions.get(i));
        }
        resultantFrame.update();
        return resultantFrame;
    }

    public DataFrame resample(int columnNumber, double interval, GroupingType groupingType) {
        DataFrame resultantFrame = new DataFrame();
        Column baseColumn = columns.get(columnNumber);
        boolean isEqualPoints = false;
        if (groupingType == GroupingType.EQUAL_POINTS_NUMBER ||
                groupingType == GroupingType.AUTO && baseColumn instanceof RegularColumn) {
            isEqualPoints = true;
        }

        if (isEqualPoints) {
            double dataAvgStep = (getValue(length - 1, columnNumber) - getValue(0, columnNumber)) / (length - 1);
            int pointsNumber = (int) Math.round(interval / dataAvgStep);
            IntSequence groupIndexes = new IntSequence() {
                @Override
                public int size() {
                    if (length % pointsNumber == 0) {
                        return length / pointsNumber + 1;
                    } else {
                        return length / pointsNumber + 2;
                    }
                }

                @Override
                public int get(int index) {
                    if (index == size() - 1) {
                        return length;
                    } else {
                        return index * pointsNumber;
                    }
                }
            };

            for (int i = 0; i < columns.size(); i++) {
                Column column = columns.get(i);
                AggregateFunction[] aggregations = columnAggFunctions.get(i);
                if (i == columnNumber && column instanceof RegularColumn) {
                    for (AggregateFunction aggregation : aggregations) {
                        resultantFrame.columns.add(((RegularColumn) column).aggregate(aggregation, pointsNumber));
                        resultantFrame.columnNames.add(columnNames.get(i) + "_" + aggregation.name());
                        AggregateFunction[] resultantAgg = {aggregation};
                        resultantFrame.columnAggFunctions.add(resultantAgg);
                    }
                } else {
                    for (AggregateFunction aggregation : aggregations) {
                        resultantFrame.columns.add(column.aggregate(aggregation, groupIndexes));
                        resultantFrame.columnNames.add(columnNames.get(i) + "_" + aggregation.name());
                        AggregateFunction[] resultantAgg = {aggregation};
                        resultantFrame.columnAggFunctions.add(resultantAgg);
                    }
                }
            }
        } else {
            IntSequence groupIndexes = columns.get(columnNumber).group(interval);
            for (int i = 0; i < columns.size(); i++) {
                Column column = columns.get(i);
                AggregateFunction[] aggregations = columnAggFunctions.get(i);
                for (AggregateFunction aggregation : aggregations) {
                    resultantFrame.columns.add(column.aggregate(aggregation, groupIndexes));
                    resultantFrame.columnNames.add(columnNames.get(i) + "_" + aggregation.name());
                    AggregateFunction[] resultantAgg = {aggregation};
                    resultantFrame.columnAggFunctions.add(resultantAgg);
                }
            }
        }
        resultantFrame.update();
        return resultantFrame;
    }


    public void cacheColumn(int columnNumber, int nLastExcluded) {
        columns.get(columnNumber).cache(nLastExcluded);
    }

    public void disableCaching() {
        for (Column column : columns) {
            column.disableCaching();
        }
    }

    public void update() {
        if (columns.size() == 0) {
            return;
        }
        length = columns.get(0).size();
        for (int i = 1; i < columns.size(); i++) {
            length = Math.min(length, columns.get(i).size());
        }
    }
}
