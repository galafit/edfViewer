package com.biorecorder.basechart.data;

import com.biorecorder.basechart.data.grouping.GroupingType;

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
 * (sum, average, count, getStart, max, first, last, center...)
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
    private GroupingType groupingType;
    int numberOfPointsInEachGroup;

    public GroupedDataSeries(DataSeries inDataSeries, GroupingType groupingType, int numberOfPointsInEachGroup) {
        this.inDataSeries = inDataSeries;
        this.groupingType = groupingType;
        this.numberOfPointsInEachGroup = numberOfPointsInEachGroup;

        groupIndexes = new LongSeries() {
            @Override
            public long size() {
                return inDataSeries.size() / numberOfPointsInEachGroup;
            }

            @Override
            public long get(long index) {
                return index * numberOfPointsInEachGroup;
            }
        };

        for (int i = 0; i < inDataSeries.getYColumnsCount(); i++) {
            NumberColumn[] groupedColumns = inDataSeries.yColumns.get(i).group(groupIndexes);
            for (NumberColumn column : groupedColumns) {
                yColumns.add(column);
            }
        }

        NumberColumn[] groupedColumns = inDataSeries.xColumn.group(groupIndexes);
        xColumn = groupedColumns[0];
    }
}
