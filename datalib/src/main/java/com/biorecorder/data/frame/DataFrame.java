package com.biorecorder.data.frame;

import com.biorecorder.data.frame.impl.IntColumn;
import com.biorecorder.data.list.IntArrayList;
import com.biorecorder.data.sequence.IntSequence;
import com.biorecorder.data.sequence.StringSequence;

import java.util.*;


/**
 * Simplified analogue of data table which
 * in fact is simply a collection of columns
 */
public class DataFrame {
    private IntWrapper length = new IntWrapper(0);
    protected List<Column> columns = new ArrayList<>();
    protected List<String> columnNames = new ArrayList<>();
    protected List<Aggregation[]> columnAggFunctions = new ArrayList<>();
    boolean isDataAppendMode = true;

    public DataFrame(boolean isDataAppendMode) {
        this.isDataAppendMode = isDataAppendMode;
    }


    public DataFrame(DataFrame dataFrame, int[] columnOrder) throws IllegalArgumentException {
        length = dataFrame.length;
        isDataAppendMode = dataFrame.isDataAppendMode;
        for (int i : columnOrder) {
            Column columnToAdd = dataFrame.columns.get(i);
            if(columnToAdd instanceof FunctionColumn) {
                // check that columnOrder contains the column used by this one
                boolean isArgColumnPresent = false;
                for (int j = 0; j < columnOrder.length; j++) {
                    if (((FunctionColumn)columnToAdd).getArgColumn() == dataFrame.columns.get(columnOrder[j])) {
                        isArgColumnPresent = true;
                        break;
                    }
                }
                if(!isArgColumnPresent) {
                    String errMsg = "Column: " + i + " can not be added because it depends upon column that is not presented in the given columnOrder";
                    throw new IllegalArgumentException(errMsg);
                }
            }

            columns.add(columnToAdd);
            columnNames.add(dataFrame.columnNames.get(i));
            columnAggFunctions.add(dataFrame.columnAggFunctions.get(i));
        }
    }

    public void removeColumn(int columnNumber) throws IllegalArgumentException {
        Column columnToRemove = columns.get(columnNumber);
        for (int i = 0; i < columns.size(); i++) {
            Column column = columns.get(i);
            if(column instanceof FunctionColumn) {
                if(((FunctionColumn) column).getArgColumn() == columnToRemove) {
                    String errMsg = "Column: " + columnNumber + " is used by function column: " + i + " and can not be removed";
                    throw new IllegalArgumentException(errMsg);
                }
            }
        }
        columns.remove(columnNumber);
        columnNames.remove(columnNumber);
        columnAggFunctions.remove(columnNumber);
        appendData();
    }

    private void addColumn(Column column) {
        columns.add(column);
        columnNames.add("Column " + (columns.size() - 1));
        Aggregation[] agg = new Aggregation[0];
        columnAggFunctions.add(agg);
        appendData();
    }

    public void addColumn(Function function, int argColumnNumber) {
        addColumn(new FunctionColumn(function, columns.get(argColumnNumber)));
    }

    public void addColumn(IntSequence data) {
        addColumn(new IntColumn(data));
    }

    public void addColumn(double start, double step) {
        addColumn(new RegularColumn(start, step));
    }

    public void addColumn(double start, double step, int size) {
        addColumn(new RegularColumn(start, step, size));
    }

    public void addColumn(int[] data) {
        addColumn(new IntSequence() {
            @Override
            public int size() {
                return data.length;
            }

            @Override
            public int get(int index) {
                return data[index];
            }
        });
    }

    public void addColumn(StringSequence data) {
        addColumn(new StringColumn(data));
    }

    public  void addColumn(List<String> data) {
        addColumn(new StringSequence() {
            @Override
            public int size() {
                return data.size();
            }

            @Override
            public String get(int index) {
                return data.get(index);
            }
        });
    }

    public void addColumn(String[] data) {
        addColumn(new StringSequence() {
            @Override
            public int size() {
                return data.length;
            }

            @Override
            public String get(int index) {
                return data[index];
            }
        });
    }

    public int rowCount() {
        return length.getValue();
    }

    public int columnCount() {
        return columns.size();
    }

    public void setColumnAggFunctions(int columnNumber, Aggregation... aggFunctions) {
        columnAggFunctions.set(columnNumber, aggFunctions);
    }

    public String getColumnName(int columnNumber) {
        return columnNames.get(columnNumber);
    }

    public void setColumnName(int columnNumber, String name) {
        columnNames.set(columnNumber, name);
    }

    public DataType columnType(int columnNumber) {
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

    public Stats stats(int columnNumber) {
        if (length.getValue() < 1) {
            return null;
        }
        return columns.get(columnNumber).stats(length.getValue());
    }

    public boolean isColumnRegular(int columnNumber) {
        return columns.get(columnNumber) instanceof RegularColumn;
    }

    public boolean isColumnFunction(int columnNumber) {
        return columns.get(columnNumber) instanceof FunctionColumn;
    }

    /**
     * Binary search algorithm. The column data must be sorted!
     * Find the index of the <b>value</b> in the given column. If the column contains
     * multiple elements equal to the searched <b>value</b>, there is no guarantee which
     * one will be found. If there is no element equal to the searched value function returns
     * the insertion point for <b>value</b> in the column to maintain sorted order
     * (i.e. index of the first element in the column which is bigger than the searched value).
     *
     * @param sorter - Default null.
     *               Optional array of integer indices that sortedIndices column data
     *               into ascending order (if data column itself is not sorted).
     *               They are typically the result of {@link #sortedIndices(int)}
     */
    public int bisect(int columnNumber, double value, int[] sorter) {
        Column column = columns.get(columnNumber);
        if (sorter != null) {
            column = column.view(sorter);
        }
        int length1 = length.getValue();
        if (sorter != null) {
            length1 = Math.min(length.getValue(), sorter.length);
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
     *
     * @return array of sorted rows (indices) for the given column.
     */
    public int[] sortedIndices(int sortColumn) {
        boolean isParallel = false;
        return columns.get(sortColumn).sort(0, length.getValue(), isParallel);
    }

    public DataFrame slice(int fromRowNumber, int length) {
        DataFrame resultantFrame = new DataFrame(false);
        for (int i = 0; i < columns.size(); i++) {
            resultantFrame.columns.add(columns.get(i).slice(fromRowNumber, length));
            resultantFrame.columnNames.add(columnNames.get(i));
            resultantFrame.columnAggFunctions.add(columnAggFunctions.get(i));
        }
        resultantFrame.appendData();
        return resultantFrame;
    }

    public DataFrame view(int fromRowNumber, int length) {
        DataFrame resultantFrame = new DataFrame(false);
        List<Integer> functionColumns = new ArrayList<>();
        // create view on all columns except the function columns
        for (int i = 0; i < columns.size(); i++) {
            Column column = columns.get(i);
            if(column instanceof FunctionColumn) {
                functionColumns.add(i);
                resultantFrame.columns.add(null);
            } else {
                resultantFrame.columns.add(column.view(fromRowNumber, length));
            }
            resultantFrame.columnNames.add(columnNames.get(i));
            resultantFrame.columnAggFunctions.add(columnAggFunctions.get(i));
        }
        // put new function columns
        for (Integer i : functionColumns) {
          FunctionColumn fc = (FunctionColumn) columns.get(i);
            for (int j = 0; j < columns.size(); j++) {
                Column column = columns.get(j);
                if(fc.getArgColumn() == column) {
                    resultantFrame.columns.set(i, new FunctionColumn(fc.getFunction(), resultantFrame.columns.get(j)));
                    break;
                }
            }
        }

        resultantFrame.appendData();
        return resultantFrame;
    }

    public DataFrame view(int[] rowOrder) {
        DataFrame resultantFrame = new DataFrame(false);
        List<Integer> functionColumns = new ArrayList<>();
        // create view on all columns except the function columns
        for (int i = 0; i < columns.size(); i++) {
            Column column = columns.get(i);
            if(column instanceof FunctionColumn) {
                resultantFrame.columns.add(null);
            } else {
                resultantFrame.columns.add(column.view(rowOrder));
            }
            resultantFrame.columnNames.add(columnNames.get(i));
            resultantFrame.columnAggFunctions.add(columnAggFunctions.get(i));
        }

        // put new function columns
        for (Integer i : functionColumns) {
            FunctionColumn fc = (FunctionColumn) columns.get(i);
            for (int j = 0; j < columns.size(); j++) {
                Column column = columns.get(j);
                if(fc.getArgColumn() == column) {
                    resultantFrame.columns.set(i, new FunctionColumn(fc.getFunction(), resultantFrame.columns.get(j)));
                    break;
                }
            }
        }
        resultantFrame.appendData();
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
     * How we will calculateStats the "value" of each bin is specified by the aggregating function
     * (sum, average, count, min, first, last...)
     * <p>
     * The most common "default" methods to divide data into bins:
     * <ol>
     * <li>Equal intervals [equal width binning] - each bin has equal range value or lengths. </li>
     * <li>Equal frequencies [equal height binning, quantiles] - each bin has equal number of elements or data points.
     * Percentile ranks - % of the total data to group into bins, or  the number of points in bins are specified. </li>
     * <li>Custom Edges - edge values of each bin are specified. The edge value is always the lower boundary of the bin.</li>
     * <li>Custom Elements [list] - the elements for each bin are specified manually.</li>
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
     * <p>
     * <p>
     * Implementation of the method implies that the data is sorted!!!
     * <p>
     * Resampling will be done only with columns for which at least
     * one aggregating function is specified!!!
     * If columns has no resample functions resultant dataframe will be empty
     */
    public DataFrame resampleByEqualFrequency(int points) {
        return resample(null, points);
    }

    /**
     * Implementation of the method implies that the data is sorted!!!
     * <p>
     * Resampling will be done only with columns for which at least
     * one aggregating function is specified!!!
     * If columns has no resample functions resultant dataframe will be empty
     */
    public DataFrame resampleByEqualInterval(int columnNumber, double interval) {
        IntSequence groupIndexes = columns.get(columnNumber).group(interval, length);
        return resample(groupIndexes, 1);
    }

    public DataFrame resample(IntSequence groupIndexes, int points) {
        // create maps
        Map<Integer, Integer> functionColToArgCol = new HashMap<>();
        Map<Integer, int[]> colToResultantCols = new HashMap<>();
        int count = 0;
        for (int i = 0; i < columns.size(); i++) {
            Column column = columns.get(i);
            int aggregations = columnAggFunctions.get(i).length;
            if(column instanceof FunctionColumn) {
                Column argColumn = ((FunctionColumn) column).getArgColumn();
                for (int j = 0; j < columns.size(); j++) {
                    if(argColumn == columns.get(j)) {
                        aggregations = columnAggFunctions.get(j).length;
                        functionColToArgCol.put(i, j);
                        break;
                    }
                }
            }

            int[] resultantColumns = new int[aggregations];
            for (int j = 0; j < resultantColumns.length; j++) {
                resultantColumns[j] = count + j;
            }
            colToResultantCols.put(i, resultantColumns);
            count += aggregations;
        }

        // resample all columns except function columns
        DataFrame resultantFrame = new DataFrame(isDataAppendMode) {
            @Override
            public void appendData() {
                DataFrame.this.appendData();
                super.appendData();
            }
        };

        for (int i = 0; i < columns.size(); i++) {
            Column column = columns.get(i);
            if(column instanceof FunctionColumn) {
                Aggregation[] aggregations = columnAggFunctions.get(functionColToArgCol.get(i));
                for (Aggregation aggregation : aggregations) {
                    resultantFrame.columns.add(null);
                    resultantFrame.columnNames.add(columnNames.get(i) + "_" + aggregation.name());
                    Aggregation[] resultantAgg = new Aggregation[0];
                    resultantFrame.columnAggFunctions.add(resultantAgg);
                }
            } else {
                Aggregation[] aggregations = columnAggFunctions.get(i);
                for (Aggregation aggregation : aggregations) {
                    if(groupIndexes != null) {
                        resultantFrame.columns.add(column.resample(aggregation, groupIndexes, isDataAppendMode));
                    } else {
                        resultantFrame.columns.add(column.resample(aggregation, points, length, isDataAppendMode));
                    }
                    resultantFrame.columnNames.add(columnNames.get(i) + "_" + aggregation.name());
                    Aggregation[] resultantAgg = {aggregation};
                    resultantFrame.columnAggFunctions.add(resultantAgg);
                }
            }
        }
        // put new function columns (as functions on aggregated data)
        for (Integer fCol : functionColToArgCol.keySet()) {
            Function function = ((FunctionColumn) columns.get(fCol)).getFunction();
            int argCol = functionColToArgCol.get(fCol);
            int[] resultantArgCols = colToResultantCols.get(argCol);
            int[] resultantFCols = colToResultantCols.get(fCol);
            for (int i = 0; i < resultantFCols.length; i++) {
                resultantFrame.columns.set(resultantFCols[i], new FunctionColumn(function, resultantFrame.columns.get(resultantArgCols[i])));
            }
        }
        resultantFrame.appendData();
        return resultantFrame;
    }

    public void cacheColumn(int columnNumber) {
        columns.get(columnNumber).cache();
    }

    public void disableCaching(int columnNumber) {
        columns.get(columnNumber).disableCaching();
    }

    public void appendData() {
        if (columns.size() == 0) {
            length.setValue(0);
            return;
        }
        length.setValue(columns.get(0).size());
        for (int i = 1; i < columns.size(); i++) {
            length.setValue(Math.min(length.getValue(), columns.get(i).size()));
        }
    }

    private void rangeCheck(long rowNumber) {
        if (rowNumber >= length.getValue() || rowNumber < 0)
            throw new IndexOutOfBoundsException(outOfBoundsMsg(rowNumber));
    }

    private String outOfBoundsMsg(long index) {
        return "Index: " + index + ", Size: " + length.getValue();
    }


    public static void main(String[] args) {
        DataFrame df = new DataFrame(true);
        int[] xData = {2, 4, 5, 9, 12, 33, 34, 35, 40};
        int[] yData = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        IntArrayList xList = new IntArrayList(xData);
        IntArrayList yList = new IntArrayList(yData);

        df.addColumn(xList);
        df.addColumn(yList);
        df.setColumnAggFunctions(0, Aggregation.FIRST);
        df.setColumnAggFunctions(1, Aggregation.AVERAGE);

        DataFrame df1 = df.resampleByEqualFrequency(4);
        DataFrame df2 = df.resampleByEqualInterval(0, 4);


        int[] expectedX1 = {2, 12, 40};
        int[] expectedY1 = {2, 6, 9};

        int[] expectedX2 = {2, 4, 9, 12, 33, 40};
        int[] expectedY2 = {1, 2, 4, 5,  7,   9};

        for (int i = 0; i < df1.rowCount(); i++) {
            if (df1.getValue(i, 0) != expectedX1[i]) {
                String errMsg = "ResampleByEqualFrequency error: " + i + " expected x =  " + expectedX1[i] + "  resultant x = " + df1.getValue(i, 0);
                throw new RuntimeException(errMsg);
            }
            if (df1.getValue(i, 1) != expectedY1[i]) {
                String errMsg = "ResampleByEqualFrequency error: " + i + " expected y =  " + expectedY1[i] + "  resultant y = " + df1.getValue(i, 1);
                throw new RuntimeException(errMsg);
            }
        }

        System.out.println("ResampleByEqualFrequency is OK " );

        for (int i = 0; i < df2.rowCount(); i++) {
            if (df2.getValue(i, 0) != expectedX2[i]) {
                String errMsg = "ResampleByEqualInterval error: " + i + " expected x =  " + expectedX2[i] + "  resultant x = " + df2.getValue(i, 0);
                throw new RuntimeException(errMsg);
            }
            if (df2.getValue(i, 1) != expectedY2[i]) {
                String errMsg = "ResampleByEqualInterval error: " + i + " expected y =  " + expectedY2[i] + "  resultant y = " + df2.getValue(i, 1);
                throw new RuntimeException(errMsg);
            }
        }
        System.out.println("ResampleByEqualInterval is OK");

        // Test appendData

        xList.add(42);
        xList.add(50);

        yList.add(1);
        yList.add(2);
        df.appendData();
        df1.appendData();
        df2.appendData();

        int[] expectedX1_ = {2, 12, 40};
        int[] expectedY1_ = {2, 6, 4};

        int[] expectedX2_ = {2, 4, 9, 12, 33, 40, 50};
        int[] expectedY2_ = {1, 2, 4, 5,  7,  5, 2};

        for (int i = 0; i < df1.rowCount(); i++) {
            if (df1.getValue(i, 0) != expectedX1_[i]) {
                String errMsg = "ResampleByEqualFrequency UPDATE error: " + i + " expected x =  " + expectedX1_[i] + "  resultant x = " + df1.getValue(i, 0);
                throw new RuntimeException(errMsg);
            }
            if (df1.getValue(i, 1) != expectedY1_[i]) {
                String errMsg = "ResampleByEqualFrequency UPDATE error: " + i + " expected y =  " + expectedY1_[i] + "  resultant y = " + df1.getValue(i, 1);
                throw new RuntimeException(errMsg);
            }
        }
        System.out.println("ResampleByEqualFrequency UPDATE is OK");

        for (int i = 0; i < df2.rowCount(); i++) {
            if (df2.getValue(i, 0) != expectedX2_[i]) {
                String errMsg = "ResampleByEqualInterval UPDATE error: " + i + " expected x =  " + expectedX2_[i] + "  resultant x = " + df2.getValue(i, 0);
                throw new RuntimeException(errMsg);
            }
            if (df2.getValue(i, 1) != expectedY2_[i]) {
                String errMsg = "ResampleByEqualInterval UPDATE error: " + i + " expected y =  " + expectedY2_[i] + "  resultant y = " + df2.getValue(i, 1);
                throw new RuntimeException(errMsg);
            }
        }
        System.out.println("ResampleByEqualInterval UPDATE is OK");

        System.out.println("String sort test");
        DataFrame sf = new DataFrame(false);
        String[] labels = {"mama", "baba", "papa", "deda"};
        sf.addColumn(labels);
        int[] values = {1, 2, 3, 4, 5};
        sf.addColumn(values);

        DataFrame sf1 = sf.sort(0);
        for (int i = 0; i < sf1.rowCount(); i++) {
            System.out.println(sf1.getValue(i, 0) +  "  " + sf1.getLabel(i, 0)+ "  " +  sf1.getValue(i, 1));
        }
    }
}
