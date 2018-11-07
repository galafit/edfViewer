package com.biorecorder.data;

import com.biorecorder.basechart.Range;
import com.biorecorder.data.grouping.GroupApproximation;
import com.biorecorder.data.series.IntSeries;
import com.biorecorder.data.series.LongSeries;
import com.biorecorder.data.series.StringSeries;

import java.text.MessageFormat;
import java.util.ArrayList;

/**
 * Simplified analogue of data table which
 * in fact is simply a collection of columns
 */
public class DataSeries  {
    protected boolean isOrdered = true;
    protected ArrayList<NumberColumn> yColumns = new ArrayList<NumberColumn>();
    protected NumberColumn xColumn = new RegularColumn();
    protected StringColumn annotationColumn;
    private long size;


    public boolean isRegular() {
        if(xColumn instanceof RegularColumn) {
            return true;
        }
        return false;
    }

    public boolean isOrdered() {
        if (isOrdered || xColumn instanceof RegularColumn) {
            return true;
        }
        return false;
    }

    private void setXData(NumberColumn column) {
        xColumn = column;
        xColumn.setGroupApproximations(GroupApproximation.OPEN);
        updateSize();
    }

    private void addYData(NumberColumn column) {
        yColumns.add(column);
        updateSize();
    }

    public void addDataPoint(DataPoint data) throws UnsupportedOperationException {
        xColumn.add(data.getXValue());
        for (int i = 0; i < yColumns.size(); i++) {
            yColumns.get(i).add(data.getYValues()[i]);
        }
        if(annotationColumn != null) {
            annotationColumn.add(data.getLabel());
        }
    }

    public void removeDataPoint(int index) {
        xColumn.remove(index);
        for (int i = 0; i < yColumns.size(); i++) {
            yColumns.get(i).remove(index);
        }
        if(annotationColumn != null) {
            annotationColumn.remove(index);
        }
    }

    public DataPoint getDataPoint(long index) {
        DataPoint dataPoint = new DataPoint();
        dataPoint.setXValue(xColumn.value(index));
        double[] yValues = new double[yColumns.size()];
        for (int i = 0; i < yColumns.size(); i++) {
            yValues[i] = yColumns.get(i).value(index);
        }
        dataPoint.setYValues(yValues);
        if(annotationColumn != null) {
            dataPoint.setLabel(annotationColumn.getString(index));
        }
        return dataPoint;
    }

    public void setXData(double xStartValue, double xInterval) {
        setXData(new RegularColumn(xStartValue, xInterval));
    }

    public void setXData(IntSeries series) {
        setXData(new IntColumn(series));
    }

  /*  public void setXData(FloatSeries series) {
        setXData(new FloatColumn(series));
    }*/

    public void addYData(IntSeries series) {
        addYData(new IntColumn(series));
    }

  /*  public void addYData(FloatSeries series) {
        addYData(new FloatColumn(series));
    }*/

    public void setAnnotations(StringSeries series) {
        annotationColumn = new StringColumn(series);
        updateSize();
    }

    public void removeYData(int yNumber) {
        yColumns.remove(yNumber);
        updateSize();
    }

    public int yCount() {
        return yColumns.size();
    }

    public double getYValue(int yNumber, long index) {
        return yColumns.get(yNumber).value(index);
    }

    public double getXValue(long index) {
        return xColumn.value(index);
    }

    public String getAnnotation(long index) {
        if (annotationColumn != null && index < annotationColumn.size()) {
            return annotationColumn.getString(index);
        }
        return null;
    }

    public Range getXExtremes() {
        if (size == 0) {
            return null;
        }
        if (isOrdered()) {
            double min = xColumn.value(0);
            double max = xColumn.value(size - 1);
            return new Range(min, max);
        }

        NumberColumn subColumn = xColumn.subColumn(0, size);
        return subColumn.extremes();
    }

    public Range getYExtremes(int yNumber) {
        if (size == 0) {
            return null;
        }

        NumberColumn subColumn = yColumns.get(yNumber).subColumn(0, size);
        return subColumn.extremes();
    }

    public long size() {
       return size;
    }

    public void updateSize() {
        size = xColumn.size();
        for (int i = 0; i < yColumns.size(); i++) {
            size = Math.min(size, yColumns.get(i).size());
        }
        if(annotationColumn != null) {
            size = Math.min(size, annotationColumn.size());
        }
    }

    /**
     * @param xValue
     * @return index of nearest data item
     */
    public long findNearestData(double xValue) {
        long lowerBoundIndex = xColumn.lowerBound(xValue);
        if (lowerBoundIndex < 0) {
            return 0;
        }
        if (lowerBoundIndex >= size - 1) {
            return size - 1;
        }
        double distance1 = xValue - getXValue(lowerBoundIndex);
        double distance2 = getXValue(lowerBoundIndex + 1) - xValue;
        long nearestIndex = (distance1 <= distance2) ? lowerBoundIndex : lowerBoundIndex + 1;
        return nearestIndex;
    }

    public DataSeries subSeries(long fromIndex, long length) {
        if (fromIndex < 0) {
            fromIndex = 0;
        }
        if (fromIndex >= size) {
            length = 0;
        }
        if (length > size - fromIndex) {
            length = size - fromIndex;
        }
        DataSeries subSeries = new DataSeries();
        subSeries.xColumn = xColumn.subColumn(fromIndex, length);
        for (NumberColumn yColumn : yColumns) {
            subSeries.yColumns.add(yColumn.subColumn(fromIndex, length));
        }
        if(annotationColumn != null) {
            subSeries.annotationColumn = annotationColumn.subColumn(fromIndex, length);
        }
        subSeries.updateSize();
        return subSeries;
    }


    public SubRange getSubRange(Double startXValue, Double endXValue) {
        if (startXValue != null && endXValue != null && endXValue < startXValue) {
            String errorMessage = "Range error. Expected: StartValue <= EndValue. StartValue = {0}, EndValue = {1}.";
            String formattedError = MessageFormat.format(errorMessage, startXValue, endXValue);
            throw new IllegalArgumentException(formattedError);
        }

        if(size == 0) {
            return new SubRange(0, 0);
        }
        if(startXValue == null) {
            startXValue = xColumn.value(0);
        }
        if(endXValue == null) {
            endXValue = xColumn.value(size - 1);
        }
        if ((startXValue > xColumn.value(size - 1) || endXValue < xColumn.value(0))) {
            return new SubRange(0, 0);
        }
        if (startXValue <= xColumn.value(0) && endXValue >= xColumn.value(size - 1)) {
            return new SubRange(0, size);
        }

        long startIndex = xColumn.upperBound(startXValue);
        long endIndex = xColumn.lowerBound(endXValue);
        long length;

        if (startIndex < 0) {
            startIndex = 0;
        }
        if (endIndex >= size) {
            endIndex = size - 1;
        }
        length = endIndex - startIndex + 1;

        return new SubRange(startIndex, (int) length);
    }

    public DataSeries copy() {
        DataSeries copySeries = new DataSeries();
        copySeries.xColumn = xColumn.copy();
        for (NumberColumn yColumn : yColumns) {
            copySeries.yColumns.add(yColumn.copy());
        }
        if(annotationColumn != null) {
            copySeries.annotationColumn = annotationColumn.copy();
        }
        copySeries.updateSize();
        return copySeries;
    }

    public DataSeries cache() {
        DataSeries cacheSeries = new DataSeries();
        cacheSeries.xColumn = xColumn.cache();
        for (NumberColumn yColumn : yColumns) {
            cacheSeries.yColumns.add(yColumn.cache());
        }

        if(annotationColumn != null) {
            cacheSeries.annotationColumn = annotationColumn.cache();
        }
        cacheSeries.updateSize();
        return cacheSeries;
    }

    public DataSeries group(LongSeries groupStartIndexes) {
        DataSeries groupSeries = new DataSeries();
        for (int i = 0; i < yColumns.size(); i++) {
            NumberColumn[] groupedColumns = yColumns.get(i).group(groupStartIndexes);
            for (NumberColumn column : groupedColumns) {
                groupSeries.yColumns.add(column);
            }
        }
        groupSeries.xColumn = xColumn.group(groupStartIndexes)[0];
        groupSeries.updateSize();
        return groupSeries;
    }

    public double getDataInterval() throws IllegalStateException {
        if(xColumn instanceof RegularColumn) {
            return ((RegularColumn) xColumn).getDataInterval();
        }

        if(size <= 1) {
            String errMsg = "Data interval can not be calculated. DataSeries size = " + size;
            throw new IllegalStateException(errMsg);
        }

        return getXExtremes().length() / (size - 1);
    }

    /**********************************************************************
     *     Helper Methods to add1 data
     *********************************************************************/

    public void setXData(int[] data) {
        xColumn = new IntColumn(data);
    }

  /*  public void setXData(float[] data) {
        xColumn = new FloatColumn(data);
    }

    public void setXData(List<? extends Number> data) {
        if (data.size() > 0 && data.get(0) instanceof Integer) {
            xColumn = new IntColumn((List<Integer>) data);
        } else {
            xColumn = new FloatColumn((List<Float>) data);
        }
    }*/


    public void addYData(int[] data) {
        addYData(new IntColumn(data));
    }

  /*  public void addYData(float[] data) {
        yColumns.add1(new FloatColumn(data));
    }

    public void addYData(List<? extends Number> data) {
        if (data.size() > 0 && data.get(0) instanceof Integer) {
            yColumns.add1(new IntColumn((List<Integer>) data));
        } else {
            yColumns.add1(new FloatColumn((List<Float>) data));
        }
    }*/
}
