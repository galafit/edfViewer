package com.biorecorder.basechart.data;

import com.biorecorder.basechart.Range;

import java.text.MessageFormat;
import java.util.ArrayList;

/**
 * Created by galafit on 19/9/17.
 */
public class DataSeries  {
    boolean isOrdered = true;
    ArrayList<NumberColumn> yColumns = new ArrayList<NumberColumn>();
    NumberColumn xColumn = new RegularColumn();
    StringColumn annotationColumn;

    public boolean isOrdered() {
        if (isOrdered || xColumn instanceof RegularColumn) {
            return true;
        }
        return false;
    }

    NumberColumn getXColumn() {
        return xColumn;
    }

    NumberColumn getYColumn(int columnNumber) {
       return yColumns.get(columnNumber);
    }

    public void setXData(double xStartValue, double xInterval) {
        xColumn = new RegularColumn(xStartValue, xInterval);
    }

    public void setXData(IntSeries series) {
        xColumn = new IntColumn(series);
    }

  /*  public void setXData(FloatSeries series) {
        xColumn = new FloatColumn(series);
    }*/

    public void addYData(IntSeries series) {
        yColumns.add(new IntColumn(series));
    }

  /*  public void addYData(FloatSeries series) {
        yColumns.add(new FloatColumn(series));
    }*/

    public void setAnnotations(StringSeries series) {
        annotationColumn = new StringColumn(series);
    }

    public void removeYData(int columnNumber) {
        yColumns.remove(columnNumber);
    }

    public int YColumnsCount() {
        return yColumns.size();
    }

    public double getYValue(int columnNumber, long index) {
        return yColumns.get(columnNumber).value(index);
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
        long size = size();
        if (size == 0) {
            return null;
        }
        if (isOrdered()) {
            double min = xColumn.value(0);
            double max = xColumn.value(size() - 1);
            return new Range(min, max);
        }

        return xColumn.extremes(size);

    }

    public Range getYExtremes(int yColumnNumber) {
        long size = size();
        if (size == 0) {
            return null;
        }
        return yColumns.get(yColumnNumber).extremes(size);
    }

    public long size() {
        if (yColumns.size() == 0 && annotationColumn == null) {
            return 0;
        }
        long size = 0;
        if(xColumn instanceof RegularColumn) {
           size = xColumn.size();
        }

        for (NumberColumn column : yColumns) {
            size = Math.min(size, column.size());
        }
        if (annotationColumn != null) {
            size = Math.min(size, annotationColumn.size());
        }

        return size;
    }

    /**
     * @param xValue
     * @return index of nearest data item
     */
    public long findNearestData(double xValue) {
        long size = size();

        long lowerBoundIndex = xColumn.lowerBound(xValue, size);
        if (lowerBoundIndex < 0) {
            return 0;
        }
        if (lowerBoundIndex >= size() - 1) {
            return size() - 1;
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
    }

    public SubRange getSubRange(Double startXValue, Double endXValue) {
        if (startXValue != null && endXValue != null && endXValue < startXValue) {
            String errorMessage = "Range error. Expected: StartValue <= EndValue. StartValue = {0}, EndValue = {1}.";
            String formattedError = MessageFormat.format(errorMessage, startXValue, endXValue);
            throw new IllegalArgumentException(formattedError);
        }

        long size = size();
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

        long startIndex = xColumn.upperBound(startXValue, size);
        long endIndex = xColumn.lowerBound(endXValue, size);
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
        return copySeries;
    }

    public double getAverageDataInterval() {
        if (size() > 1) {
            if (xColumn instanceof RegularColumn) {
                return ((RegularColumn) xColumn).getDataInterval();
            }
            return getXExtremes().length() / (size() - 1);
        }
        return -1;
    }

    /**********************************************************************
     *     Helper Methods to add data
     *********************************************************************/
    void setXData(NumberColumn numberColumn) {
        xColumn = numberColumn;
    }

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

    void addYData(NumberColumn numberColumn) {
        yColumns.add(numberColumn);
    }

    public void addYData(int[] data) {
        yColumns.add(new IntColumn(data));
    }

  /*  public void addYData(float[] data) {
        yColumns.add(new FloatColumn(data));
    }

    public void addYData(List<? extends Number> data) {
        if (data.size() > 0 && data.get(0) instanceof Integer) {
            yColumns.add(new IntColumn((List<Integer>) data));
        } else {
            yColumns.add(new FloatColumn((List<Float>) data));
        }
    }*/
}
