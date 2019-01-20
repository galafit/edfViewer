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

    private void addColumn(Column column) {
        columns.add(column);
        columnNames.add("Column "+ (columns.size() - 1));
        AggregateFunction[] agg = new AggregateFunction[1];
        agg[0] = AggregateFunction.FIRST;
        columnAggFunctions.add(agg);
        update();
    }

    public void removeColumn(int columnNumber) {
        columns.remove(columnNumber);
        columnNames.remove(columnNumber);
        columnAggFunctions.remove(columnNumber);
        update();
    }

    public void addColumn(IntSequence columnData) {
        addColumn(new IntColumn(columnData));
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

    public int rowCount() {
        return length;
    }

    public int columnCount() {
        return columns.size();
    }

    public String getColumnName(int columnNumber) {
        return columnNames.get(columnNumber);
    }

    public void setColumnName(int columnNumber, String name) {
        columnNames.set(columnNumber, name);
    }

    DataType getColumnType(int columnNumber) {
        return columns.get(columnNumber).dataType();
    }

    double getValue(int rowNumber, int columnNumber) {
        return columns.get(columnNumber).value(rowNumber);
    }

    public String getLabel(int rowNumber, int columnNumber) {
        return columns.get(columnNumber).label(rowNumber);
    }

    public BRange getColumnRange(int columnNumber) {
        return columns.get(columnNumber).range(0, length);
    }

    public int nearest(int columnNumber, double value) {
        return columns.get(columnNumber).nearest(value, 0, length);
    }

    public DataFrame resample(int columnNumber, double interval, GroupingType groupingType) {
        DataFrame resultantFrame = new DataFrame();
        Column baseColumn = columns.get(columnNumber);
        boolean isEqualPoints = false;
        if(groupingType == GroupingType.EQUAL_POINTS_NUMBER ||
                groupingType == GroupingType.AUTO && baseColumn instanceof RegularColumn) {
            isEqualPoints = true;
        }

        if(isEqualPoints) {
            double dataAvgStep = (getValue(length - 1, columnNumber) - getValue(0, columnNumber)) / (length - 1);
            int pointsNumber = (int) Math.round(interval / dataAvgStep);

            IntSequence groupIndexes = new IntSequence() {
                @Override
                public int size() {
                    if(length % pointsNumber == 0) {
                        return length / pointsNumber + 1;
                    } else {
                        return length / pointsNumber + 2;
                    }
                }

                @Override
                public int get(int index) {
                    if(index == size() - 1) {
                        return length;
                    } else {
                        return index * pointsNumber;
                    }
                }
            };

            for (int i = 0; i < columns.size(); i++) {
                Column column = columns.get(i);
                AggregateFunction[] aggregations = columnAggFunctions.get(i);
                if(i == columnNumber && column instanceof RegularColumn) {
                    for (AggregateFunction aggregation : aggregations) {
                        resultantFrame.columns.add(((RegularColumn)column).aggregate(aggregation, pointsNumber));
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
        return resultantFrame;
    }


    public DataFrame view(int fromRowNumber, int length) {
        DataFrame resultantFrame = new DataFrame();
        for (int i = 0; i < columns.size(); i++) {
            resultantFrame.columns.add(columns.get(i).view(fromRowNumber, length));
            resultantFrame.columnNames.add(columnNames.get(i));
            resultantFrame.columnAggFunctions.add(columnAggFunctions.get(i));
        }
        return resultantFrame;
    }

    public DataFrame slice(int fromRowNumber, int length) {
        DataFrame resultantFrame = new DataFrame();
        for (int i = 0; i < columns.size(); i++) {
            resultantFrame.columns.add(columns.get(i).slice(fromRowNumber, length));
            resultantFrame.columnNames.add(columnNames.get(i));
            resultantFrame.columnAggFunctions.add(columnAggFunctions.get(i));
        }
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
        for (Column column : columns) {
            length = Math.max(length, column.size());
        }
    }
}
