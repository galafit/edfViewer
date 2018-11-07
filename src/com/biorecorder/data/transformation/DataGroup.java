package com.biorecorder.data.transformation;

import com.biorecorder.data.DataSeries;
import com.biorecorder.data.series.LongSeries;
import com.biorecorder.data.transformation.DataTransform;

/**
 * This class potentially permits (if we will need it)
 * take start and end of every grouped point (bin) and
 * also all original points that group includes
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
public abstract class DataGroup implements DataTransform {
    protected DataSeries inDataSeries;
    protected GroupStartIndexes groupStartIndexes;
    protected DataSeries groupedSeries;
    protected DataSeries cachingSeries;

    @Override
    public void setInputData(DataSeries inDataSeries) {
        this.inDataSeries = inDataSeries;
        groupedSeries = null;
    }

    /**
     * Return resultant transformed DataSeries
     * (updated every time when data is added to the inputDataSeries)
     * @throws IllegalStateException if input DataSeries is not specified
     */
    @Override
    public DataSeries getTransformedData() throws IllegalStateException{
        if(inDataSeries == null) {
            throw new IllegalStateException("Input DataSeries is not specified");
        }
        inDataSeries.updateSize();
        groupStartIndexes.updateSize();
        if(groupedSeries == null) {
            groupedSeries = inDataSeries.group(groupStartIndexes);
            cachingSeries = groupedSeries.cache();
        }
        groupedSeries.updateSize();
        cachingSeries.removeDataPoint((int) cachingSeries.size() - 1);
        for (long i = cachingSeries.size() - 1; i < groupedSeries.size(); i++) {
            cachingSeries.addDataPoint(groupedSeries.getDataPoint(i));
        }
        return cachingSeries;
    }

    interface GroupStartIndexes extends LongSeries {
       void updateSize();
    }
}
