package com.biorecorder.basechart.data;

import com.biorecorder.basechart.Range;

/**
 * This class potentially permits for every grouped point (bin)
 * take its start, end and all original points the group includes (if we will need it)
 * <p>
 * Data grouping or binning (banding)
 * serves to reduce large number of data.
 * <p>
 * Binning is a way to group a number of more or less continuous values
 * into a smaller number of buckets (bins or groups).  Each group/bucket/bin defines
 * an numerical interval and usually is characterized by a name and two boundaries -
 * the start or lower boundary and the stop or upper one.
 * <p>
 * On the chart every bin is represented by one value (point).
 * It may be the number of element in the bin (for histogram)
 * or the midpoint of the bin interval (avg) and so on.
 * How we will calculate the "value" of each bin is specified by the grouping function
 * (sum, average, count, getMin, join, first, last, center...)
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
 */
public class GroupedDataSeries extends DataSeries {
    private DataSeries inDataSeries;
    private GroupStartIndexes groupStartIndexes;

    public GroupedDataSeries(DataSeries inDataSeries, double groupingInterval) {
        this.inDataSeries = inDataSeries;
        if(inDataSeries.isRegular()) {
            groupStartIndexes = new RegularGroupStartIndexes(groupingInterval);
            xColumn = new RegularColumn(inDataSeries.getXValue(0), inDataSeries.getDataInterval() * groupStartIndexes.getNumberOfPointsInGroup());
        } else {
            groupStartIndexes = new IrregularGroupStartIndexes(groupingInterval);
            NumberColumn[] groupedColumns = inDataSeries.xColumn.group(groupStartIndexes);
            xColumn = groupedColumns[0];
        }

        for (int i = 0; i < inDataSeries.yColumns.size(); i++) {
            NumberColumn[] groupedColumns = inDataSeries.yColumns.get(i).group(groupStartIndexes);
            for (NumberColumn column : groupedColumns) {
                yColumns.add(column);
            }
        }
        System.out.println(inDataSeries.size() + "  "+groupStartIndexes.size() +" create "+groupStartIndexes.get(groupStartIndexes.size() - 1));

    }

    /**
     * This method can be used only if grouping superposition
     * can be applied to the grouping function. For example:
     * Min, Max, First, Last. Superposition for Average available
     * only for regular series (when each group has the same number of points)
     * @param factor - multiply factor
     */
    public void multiplyGroupingInterval(int factor) {
        GroupedDataSeries resultantGroupedSeries = new GroupedDataSeries(this, getGroupingInterval() * factor);
        resultantGroupedSeries.enableCaching(false);

        // force calculating and caching all grouped elements
        long lastElement = resultantGroupedSeries.size() - 1;
        for (int i = 0; i < resultantGroupedSeries.YColumnsCount(); i++) {
            resultantGroupedSeries.getYValue(i, lastElement);
        }
        resultantGroupedSeries.getXValue(lastElement);

        // make group indexes superposition and copy grouped data
        groupStartIndexes.superposition(resultantGroupedSeries.getGroupStartIndexes());

         if(inDataSeries.isRegular()) {
            xColumn = new RegularColumn(inDataSeries.getXValue(0), inDataSeries.getDataInterval() * groupStartIndexes.getNumberOfPointsInGroup());
        } else {
            xColumn.cache(resultantGroupedSeries.getXColumn());
        }
        for (int i = 0; i < yColumns.size(); i++) {
           yColumns.get(i).cache(resultantGroupedSeries.getYColumn(i));
        }
    }

    public GroupStartIndexes getGroupStartIndexes() {
        return groupStartIndexes;
    }

    public void setGroupingInterval(double groupingInterval) {
        groupStartIndexes.setGroupingInterval(groupingInterval);
        if(inDataSeries.isRegular()) {
            xColumn = new RegularColumn(inDataSeries.getXValue(0), inDataSeries.getDataInterval() * groupStartIndexes.getNumberOfPointsInGroup());
        }
    }

    public double getGroupingInterval() {
        return groupStartIndexes.getGroupingInterval();
    }

    public int getNumberOfPointsInGroup() {
        return groupStartIndexes.getNumberOfPointsInGroup();
    }

    public void updateGroups() {
        System.out.println(inDataSeries.size() + "  "+groupStartIndexes.size() +" create "+groupStartIndexes.get(groupStartIndexes.size() - 1));
        groupStartIndexes.clear();
        xColumn.clear();
        if(inDataSeries.isRegular()) {
            xColumn = new RegularColumn(inDataSeries.getXValue(0), inDataSeries.getDataInterval() * groupStartIndexes.getNumberOfPointsInGroup());
        }
        for (NumberColumn yColumn : yColumns) {
            yColumn.clear();
        }
      //  System.out.println(groupStartIndexes.size() +" update "+groupStartIndexes.get(groupStartIndexes.size() - 1));
    }

    private double pointsNumberToGroupingInterval(int numberOfPointsInGroup) {
        return numberOfPointsInGroup * inDataSeries.getDataInterval();
    }

    private int groupingIntervalToPointsNumber(double groupingInterval) {
        return (int)Math.round(groupingInterval / inDataSeries.getDataInterval());
    }

    interface GroupStartIndexes extends LongSeries {
        void setGroupingInterval(double groupingInterval);
        double getGroupingInterval();
        int getNumberOfPointsInGroup();
        void clear();
        void setGroupStart(int groupIndex, long groupStartIndex);
        void superposition(GroupStartIndexes upperLevelGroupStartIndexes);
    }

    class RegularGroupStartIndexes implements GroupStartIndexes {
        private int groupPointsNumber;
        private long size;

        public RegularGroupStartIndexes(double groupingInterval) {
            setGroupingInterval(groupingInterval);
        }

        public void setGroupPointsNumber(int groupPointsNumber) {
            if(groupPointsNumber < 2) {
                throw new IllegalArgumentException("Number of points in group = "+ groupPointsNumber+ ". Must be > 1");
            }
            this.groupPointsNumber = groupPointsNumber;
        }

        @Override
        public void setGroupingInterval(double groupingInterval) {
            setGroupPointsNumber(groupingIntervalToPointsNumber(groupingInterval));
        }

        @Override
        public double getGroupingInterval() {
            return groupingIntervalToPointsNumber(groupPointsNumber);
        }

        @Override
        public int getNumberOfPointsInGroup() {
            return groupPointsNumber;
        }


        @Override
        public long size() {
            long inDataSize = inDataSeries.size();
            if(inDataSize % groupPointsNumber == 0) {
                size = inDataSize / groupPointsNumber + 1;
            } else {
                size = inDataSize / groupPointsNumber + 2;
            }
            System.out.println(size + " size "+ inDataSeries.size() + " "+groupPointsNumber);
            return size;
        }

        @Override
        public long get(long index) {
            if(index < size - 1) {
                return index * groupPointsNumber;
            } else {
                return inDataSeries.size();
            }
        }

        @Override
        public void clear() {
            // do nothing;
        }

        @Override
        public void setGroupStart(int groupIndex, long groupStartIndex) {
            // do nothing;
        }

        @Override
        public void superposition(GroupStartIndexes upperLevelGroupStartIndexes) {
            setGroupingInterval(groupingIntervalToPointsNumber(upperLevelGroupStartIndexes.getGroupingInterval()));
        }
    }

    class IrregularGroupStartIndexes implements GroupStartIndexes {
        private double groupingInterval;
        LongArrayList groupStartsList;


        public IrregularGroupStartIndexes(double groupingInterval) {
            LongArrayList groupIndexesList = new LongArrayList();
            setGroupingInterval(groupingInterval);
        }

        @Override
        public void superposition(GroupStartIndexes upperLevelGroupStartIndexes) {
            // superposition
            for (int i = 0; i < upperLevelGroupStartIndexes.size(); i++) {
               upperLevelGroupStartIndexes.setGroupStart(i, groupStartsList.get(upperLevelGroupStartIndexes.get(i)));
            }
            // copy
            groupStartsList.clear();
            for (int i = 0; i < upperLevelGroupStartIndexes.size(); i++) {
                groupStartsList.add(upperLevelGroupStartIndexes.get(i));
            }
        }

        @Override
        public void clear() {
            groupStartsList.clear();
            groupStartsList.add(0);
        }

        @Override
        public void setGroupStart(int groupIndex, long groupStartIndex) {
            groupStartsList.set(groupIndex, groupStartIndex);
        }


        @Override
        public void setGroupingInterval(double groupingInterval) {
            clear();
            this.groupingInterval = groupingInterval;
        }

        @Override
        public double getGroupingInterval() {
            return groupingInterval;
        }

        @Override
        public int getNumberOfPointsInGroup() {
            return groupingIntervalToPointsNumber(groupingInterval);
        }

        @Override
        public long get(long index) {
            return groupStartsList.get(index);
        }

        @Override
        public long size() {
            long size = inDataSeries.size();
            long from = groupStartsList.get(groupStartsList.size() - 1);

            if(size > from) {
                if(from > 0) {
                    // delete last "closing" group
                    groupStartsList.remove((int) groupStartsList.size() - 1);
                    from = groupStartsList.get(groupStartsList.size() - 1);
                }

                double groupStart = (int)((inDataSeries.xColumn.value(from) / groupingInterval)) * groupingInterval;
                groupStart += groupingInterval;
                for (long i = from;  i < size; i++) {
                    if (inDataSeries.xColumn.value(i) >= groupStart) {
                        groupStartsList.add(i);
                        groupStart += groupingInterval; // often situation

                        if(inDataSeries.xColumn.value(i) > groupStart) { // rare situation
                            groupStart = (int)((inDataSeries.xColumn.value(from) / groupingInterval)) * groupingInterval;
                            groupStart += groupingInterval;
                        }
                    }
                }
                // add last "closing" group
                groupStartsList.add(size);
            }
            return groupStartsList.size();
        }
    }
}
