package com.biorecorder.data.frame;

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
        //columnNames.add("Column " + (columns.size() - 1));
        columnNames.add("");
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
        rangeCheck(rowNumber);
        return columns.get(columnNumber).value(rowNumber);
    }

    public String getLabel(int rowNumber, int columnNumber) {
        rangeCheck(rowNumber);
        return columns.get(columnNumber).label(rowNumber);
    }

    public double getColumnMin(int columnNumber) {
        if(length < 1) {
            return Double.NaN;
        }
        return columns.get(columnNumber).min(length);
    }

    public double getColumnMax(int columnNumber) {
        if(length < 1) {
            return Double.NaN;
        }
        return columns.get(columnNumber).max(length);
    }

    public boolean isColumnIncreasing(int columnNumber) {
        if(length < 1) {
            return true;
        }
        return columns.get(columnNumber).isIncreasing(length);
    }

    public boolean isColumnDecreasing(int columnNumber) {
        if(length < 1) {
            return true;
        }
        return columns.get(columnNumber).isDecreasing(length);
    }

    public boolean isColumnRegular(int columnNumber) {
        return columns.get(columnNumber) instanceof RegularColumn;
    }


    /**
     * Binary search algorithm. The column data must be sorted!
     * Find the index of the <b>value</b> in the given column. If the column contains
     * multiple elements equal to the searched <b>value</b>, there is no guarantee which
     * one will be found. If there is no element equal to the searched value function returns
     * the insertion point for <b>value</b> in the column to maintain sorted order
     * (i.e. index of the first element in the column which is bigger than the searched value).
     * @param sorter - Default null.
     *               Optional array of integer indices that sortedIndices column data
     *               into ascending order (if data column itself is not sorted).
     *               They are typically the result of {@link #sortedIndices(int)}
     */
    public int bisect(int columnNumber, double value, int[] sorter) {
        Column column = columns.get(columnNumber);
        if(sorter != null) {
            column = column.view(sorter);
        }
        int length1 = length;
        if(sorter != null) {
            length1 = Math.min(length, sorter.length);
        }
        return column.bisect(value, 0, length1);
    }


    /**
     * This method returns a sorted view of the data frame
     * without modifying the order of the underlying data.
     * (like JTable sortedIndices in java)
     */
    public DataFrame sort(int sortColumn) {
        return view(sortedIndices(sortColumn));
    }

    /**
     * This method returns an array of row numbers (indices)
     * which represent sorted version (view) of the given column.
     * (Similar to numpy.argsort or google chart DataTable.getSortedRows -
     * https://developers.google.com/chart/interactive/docs/reference#DataTable,)
     * @return array of sorted rows (indices) for the given column.
     */
    public int[] sortedIndices(int sortColumn) {
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

    /**
     * Resample means data grouping or binning (banding)
     * with subsequent aggregation to reduce large number of data.
     * <p>
     * Binning is a way to group a number of more or less continuous values
     * into a smaller number of buckets (bins or groups).  Each group/bucket/bin defines
     * an numerical interval and usually is characterized by a traceName and two boundaries -
     * the start or lower boundary and the stop or upper one.
     * <p>
     * On the chart  every bin is represented by one value (point).
     * It may be the number of element in the bin (for histogram)
     * or the midpoint of the bin interval (avg) and so on.
     * How we will calculate the "value" of each bin is specified by the aggregating function
     * (sum, average, count, min, first, last...)
     * <p>
     * The most common "default" methods to divide data into bins:
     * <ol>
     *  <li>Equal intervals [equal width binning] - each bin has equal range value or lengths. </li>
     *  <li>Equal frequencies [equal height binning, quantiles] - each bin has equal number of elements or data points.
     *  Percentile ranks - % of the total data to group into bins, or  the number of points in bins are specified. </li>
     *  <li>Custom Edges - edge values of each bin are specified. The edge value is always the lower boundary of the bin.</li>
     *  <li>Custom Elements [list] - the elements for each bin are specified manually.</li>
     * </ol>
     * <p>
     * <a href="https://msdn.microsoft.com/library/en-us/Dn913065.aspx">MSDN: Group Data into Bins</a>,
     * <a href="https://gerardnico.com/wiki/data_mining/discretization">Discretizing and binning</a>,
     * <a href="https://docs.rapidminer.com/studio/operators/cleansing/binning/discretize_by_bins.html">discretize by bins</a>,
     * <a href="http://www.ncgia.ucsb.edu/cctp/units/unit47/html/comp_class.html">Data Classification</a>,
     * <a href="https://www.ibm.com/support/knowledgecenter/en/SSLVMB_24.0.0/spss/base/idh_webhelp_scatter_options_palette.html">Binning (Grouping) Data Values</a>,
     * <a href="http://www.jdatalab.com/data_science_and_data_mining/2017/01/30/data-binning-plot.html">Data Binning and Plotting</a>,
     * <a href="https://docs.tibco.com/pub/sfire-bauthor/7.6.0/doc/html/en-US/GUID-D82F7907-B3B4-45F6-AFDA-C3179361F455.html">Binning functions</a>,
     * <a href="https://devnet.logianalytics.com/rdPage.aspx?rdReport=Article&dnDocID=6029">Data Binning</a>,
     * <a href="http://www.cs.wustl.edu/~zhang/teaching/cs514/Spring11/Data-prep.pdf">Data Preprocessing</a>
     *
     * <p>
     * Implementation of the method implies that the data is sorted!!!
     */
    public DataFrame resampleByEqualFrequency(int points) {
        DataFrame resultantFrame = new DataFrame();
         IntSequence groupIndexes = new IntSequence() {
            @Override
            public int size() {
                if (length % points == 0) {
                    return length / points + 1;
                } else {
                    return length / points + 2;
                }
            }

            @Override
            public int get(int index) {
                if (index == size() - 1) {
                    return length;
                } else {
                    return index * points;
                }
            }
        };

        for (int i = 0; i < columns.size(); i++) {
            Column column = columns.get(i);
            AggregateFunction[] aggregations = columnAggFunctions.get(i);
            if (column instanceof RegularColumn) {
                for (AggregateFunction aggregation : aggregations) {
                    resultantFrame.columns.add(((RegularColumn) column).aggregate(aggregation, points));
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
        resultantFrame.update();
        return resultantFrame;
    }

    /**
     * Implementation of the method implies that the data is sorted!!!
     */
    public DataFrame resampleByEqualInterval(int columnNumber, double interval) {
        DataFrame resultantFrame = new DataFrame();
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

    private void rangeCheck(long rowNumber) {
        if (rowNumber >= length || rowNumber < 0)
            throw new IndexOutOfBoundsException(outOfBoundsMsg(rowNumber));
    }

    private String outOfBoundsMsg(long index) {
        return "Index: "+index+", Size: "+length;
    }

}
