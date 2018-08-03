package com.biorecorder.basechart.data;

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
 * (sum, average, count, getMin, max, first, last, center...)
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
    private LongSeries groupIndexes;
    double groupingInterval;

    public GroupedDataSeries(DataSeries inDataSeries, double groupingInterval) {
        this.inDataSeries = inDataSeries;
        this.groupingInterval = groupingInterval;

        if(inDataSeries.xColumn instanceof RegularColumn) {
            double dataInterval = ((RegularColumn)inDataSeries.xColumn).getDataInterval();
            int numberOfPointsToGroup = (int)(groupingInterval / dataInterval);
            if(numberOfPointsToGroup < 1) {
                numberOfPointsToGroup = 1;
            }
            groupIndexes = new RegularGroupIndexes(numberOfPointsToGroup);
        } else {
            groupIndexes = new IrregularGroupIndexes();
        }
        NumberColumn[] groupedColumns = inDataSeries.xColumn.group(groupIndexes);
        xColumn = groupedColumns[0];
        for (int i = 0; i < inDataSeries.yColumns.size(); i++) {
            groupedColumns = inDataSeries.yColumns.get(i).group(groupIndexes);
            for (NumberColumn column : groupedColumns) {
                yColumns.add(column);
            }
        }
    }

    public double getGroupingInterval() {
        return groupingInterval;
    }

    public int getNumberOfPointsInGroup() {
        int number = (int)Math.round(inDataSeries.getXExtremes().length() / groupingInterval);
        return number;
    }

    public void updateGroups() {
        if(groupIndexes instanceof  IrregularGroupIndexes) {
            ((IrregularGroupIndexes)groupIndexes).clear();
        }
        xColumn.clearCache();
        for (NumberColumn yColumn : yColumns) {
            yColumn.clearCache();
        }
    }


    class RegularGroupIndexes implements LongSeries {
        private int groupPointsNumber;

        public RegularGroupIndexes(int groupPointsNumber) {
            this.groupPointsNumber = groupPointsNumber;
        }

        @Override
        public long size() {
            long inDataSize = inDataSeries.size();
            if(inDataSize % groupPointsNumber == 0) {
                return inDataSize / groupPointsNumber + 1;
            } else {
                return inDataSize / groupPointsNumber + 2;
            }
        }

        @Override
        public long get(long index) {
            return Math.min(index * groupPointsNumber, inDataSeries.size());
        }
    }


    class IrregularGroupIndexes implements LongSeries {
        LongArrayList groupIndexesList;

        public IrregularGroupIndexes() {
            LongArrayList groupIndexesList = new LongArrayList();
            groupIndexesList.add(0);
        }

        public void clear() {
            groupIndexesList.clear();
        }

        @Override
        public long get(long index) {
            return groupIndexesList.get(index);
        }

        @Override
        public long size() {
            long size = inDataSeries.size();
            long from = groupIndexesList.get(groupIndexesList.size() - 1);

            if(size > from) {
                if(from > 0) {
                    // delete last "closing" group
                    groupIndexesList.remove((int)groupIndexesList.size() - 1);
                    from = groupIndexesList.get(groupIndexesList.size() - 1);
                }

                double groupStart = (int)((inDataSeries.xColumn.value(from) / groupingInterval)) * groupingInterval;
                groupStart += groupingInterval;
                for (long i = from;  i < size; i++) {
                    if (inDataSeries.xColumn.value(i) >= groupStart) {
                        groupIndexesList.add(i);
                        groupStart += groupingInterval; // often situation

                        if(inDataSeries.xColumn.value(i) > groupStart) { // rare situation
                            groupStart = (int)((inDataSeries.xColumn.value(from) / groupingInterval)) * groupingInterval;
                            groupStart += groupingInterval;
                        }
                    }
                }
                // add last "closing" group
                groupIndexesList.add(size);
            }
            return groupIndexesList.size();
        }

    }
}
