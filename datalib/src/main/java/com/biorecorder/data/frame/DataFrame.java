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

    public DataFrame(DataFrame dataFrame, int[] columnsOrder) {
        for (int i = 0; i < columnsOrder.length; i++) {
            columns.add(dataFrame.columns.get(columnsOrder[i]));
            columnNames.add(dataFrame.columnNames.get(columnsOrder[i]));
            columnAggFunctions.add(dataFrame.columnAggFunctions.get(columnsOrder[i]));
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
        columnNames.add("Column "+ (columns.size() - 1));
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


    public boolean isRegularColumn(int columnNumber) {
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

    public BRange getColumnRange(int columnNumber) {
        return columns.get(columnNumber).minMax(length);
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

    public DataFrame sortedView(int columnNumber) {
       int[] sorted = columns.get(columnNumber).sort(length);
        DataFrame resultantFrame = new DataFrame();
        for (int i = 0; i < columns.size(); i++) {
            resultantFrame.columns.add(columns.get(i).view(sorted));
            resultantFrame.columnNames.add(columnNames.get(i));
            resultantFrame.columnAggFunctions.add(columnAggFunctions.get(i));
        }
        resultantFrame.update();
        return resultantFrame;
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


    public void cacheColumn(int columnNumber, int nLastExcluded) {
        columns.get(columnNumber).cache(nLastExcluded);
    }

    public void disableCaching() {
        for (Column column : columns) {
            column.disableCaching();
        }
    }

    public void update() {
        if(columns.size() == 0) {
            return;
        }
        length = columns.get(0).size();
        for (int i = 1; i < columns.size(); i++) {
            length = Math.min(length, columns.get(i).size());
        }
    }

    public static void main(String [ ] args) {
        System.out.println("Sort test");

        int[] arr = {5, 2, 4, 1, 3, 8, 100, 1, 5, 3, 20};

        DataFrame frame = new DataFrame();
        frame.addColumn(arr);

        System.out.println("\nOriginal frame:");
        for (int i = 0; i < frame.rowCount(); i++) {
            System.out.println(i + "  "+ frame.getValue(i, 0));
        }
        int from = 2;
        int length = 8;
        DataFrame sortedSubFrame = frame.view(from,  length).sortedView(0);
        System.out.println("\nResultant sorted sub frame: "+ "from = "+ from + "  length = " + length);
        for (int i = 0; i < sortedSubFrame.rowCount(); i++) {
            System.out.println(i + "  "+ sortedSubFrame.getValue(i, 0));
        }
    }
}
