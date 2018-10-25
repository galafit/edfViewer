package com.biorecorder.basechart.data;

import com.biorecorder.basechart.Range;
import com.biorecorder.util.series.IntSeries;
import com.biorecorder.util.series.StringSeries;

import java.text.MessageFormat;
import java.util.ArrayList;

/**
 * Created by galafit on 19/9/17.
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
        calculateSize();
    }

    private void addYData(NumberColumn column) {
        yColumns.add(column);
        calculateSize();
    }

    public void setXData(double xStartValue, double xInterval) {
        xColumn = new RegularColumn(xStartValue, xInterval);
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
        calculateSize();
    }

    public void removeYData(int yNumber) {
        yColumns.remove(yNumber);
        calculateSize();
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
            double max = xColumn.value(size() - 1);
            return new Range(min, max);
        }

        return xColumn.extremes(0, size);
    }

    public Range getYExtremes(int yNumber) {
        if (size == 0) {
            return null;
        }
        return yColumns.get(yNumber).extremes(0, size);
    }

    public long size() {
       return size;
    }

    protected void calculateSize() {
        size = xColumn.size();
        for (int i = 0; i < yColumns.size(); i++) {
            size = Math.min(size, yColumns.get(i).size());
        }
        if(annotationColumn != null) {
            size = Math.min(size, annotationColumn.size());
        }
    }

    public void onDataAdded() {
        calculateSize();
    }

    public void onDataChanged() {
        xColumn.clear();
        for (NumberColumn yColumn : yColumns) {
            yColumn.clear();
        }
        calculateSize();
    }

    /**
     * @param xValue
     * @return index of nearest data item
     */
    public long findNearestData(double xValue) {

        long lowerBoundIndex = xColumn.lowerBound(xValue, 0, size);
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

    public void enableCaching(boolean isLastElementCacheable) {
        xColumn.enableCaching(isLastElementCacheable);
        for (NumberColumn yColumn : yColumns) {
            yColumn.enableCaching(isLastElementCacheable);
        }
    }

    public void disableCaching() {
        xColumn.disableCaching();
        for (NumberColumn yColumn : yColumns) {
            yColumn.disableCaching();
        }
    }

    public void setViewRange(long startIndex, long length) {
        xColumn.setViewRange(startIndex, length);
        for (NumberColumn yColumn : yColumns) {
            yColumn.setViewRange(startIndex, length);
        }

        if(annotationColumn != null) {
            annotationColumn.setViewRange(startIndex, length);
        }
        calculateSize();
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

        long startIndex = xColumn.upperBound(startXValue, 0, size);
        long endIndex = xColumn.lowerBound(endXValue, 0, size);
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
        copySeries.onDataAdded();
        return copySeries;
    }

    public double getDataInterval() {
        if (size > 1) {
            return getXExtremes().length() / (size - 1);
        }
        return -1;
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
