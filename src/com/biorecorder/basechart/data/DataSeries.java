package com.biorecorder.basechart.data;

import com.biorecorder.basechart.chart.Range;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by galafit on 19/9/17.
 */
public class DataSeries {
    boolean isOrdered = true;
    ArrayList<NumberColumn> yColumns = new ArrayList<NumberColumn>();
    NumberColumn xColumn = new RegularColumn();
    StringColumn annotationColumn;

    // for subsets
    long startIndex = 0;
    long length = -1;


    public boolean isOrdered() {
        if (isOrdered || xColumn instanceof RegularColumn) {
            return true;
        }
        return false;
    }

    public void setXData(double startXValue, double dataInterval) {
        xColumn = new RegularColumn(startXValue, dataInterval);
    }

    public void setXData(IntSeries series) {
        xColumn = new IntColumn(series);
    }

    public void setXData(FloatSeries series) {
        xColumn = new FloatColumn(series);
    }

    public void addYData(IntSeries series) {
        yColumns.add(new IntColumn(series));
    }

    public void addYData(FloatSeries series) {
        yColumns.add(new FloatColumn(series));
    }

    public void setAnnotations(StringSeries series) {
        annotationColumn = new StringColumn(series);
    }

    public void removeYData(int columnNumber) {
        yColumns.remove(columnNumber);
    }

    public int getYColumnsCount() {
        return yColumns.size();
    }

    public double getYValue(long index, int columnNumber) {
        return yColumns.get(columnNumber).value(index + startIndex);
    }

    public double getXValue(long index) {
        return xColumn.value(index + startIndex);
    }

    public String getAnnotation(long index) {
        if (annotationColumn != null && index < annotationColumn.size()) {
            return annotationColumn.getString(index);
        }
        return null;
    }

    public Range getXExtremes() {
        if (size() == 0) {
            if(xColumn instanceof RegularColumn){
               double startValue =  ((RegularColumn) xColumn).getStartValue();
               return new Range(startValue, startValue);
            }
            return null;
        }
        if (isOrdered()) {
            double min = xColumn.value(startIndex);
            double max = xColumn.value(startIndex + size() - 1);
            return new Range(min, max);
        }
        long size = size();
        if (size > Integer.MAX_VALUE) {
            String errorMessage = "Error. Size must be integer during calculating X extremes for non ordered series. Size = {0}, Integer.MAX_VALUE = {1}.";
            String formattedError = MessageFormat.format(errorMessage, size, Integer.MAX_VALUE);
            throw new IllegalArgumentException(formattedError);
        }
        return xColumn.extremes(startIndex, (int) size);

    }

    public Range getYExtremes(int yColumnNumber) {
        if (size() == 0) {
            return null;
        }
        long size = size();
        if (size > Integer.MAX_VALUE) {
            String errorMessage = "Error. Size must be integer during calculating Y extremes. Size = {0}, Integer.MAX_VALUE = {1}.";
            String formattedError = MessageFormat.format(errorMessage, size, Integer.MAX_VALUE);
            throw new IllegalArgumentException(formattedError);
        }
        return yColumns.get(yColumnNumber).extremes(startIndex, (int) size);
    }

    public long size() {
        if (length < 0) {
            return fullSize();
        }
        return length;

    }

    private long fullSize() {
        if (yColumns.size() == 0 && annotationColumn == null) {
            return 0;
        }
        long size = xColumn.size();
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
     * @return index of nearest com.biorecorder.basechart.data item
     */
    public long findNearestData(double xValue) {
        long size = size();
        if (size > Integer.MAX_VALUE) {
            String errorMessage = "Error. Size must be integer during finding nearest com.biorecorder.basechart.data. Size = {0}, Integer.MAX_VALUE = {1}.";
            String formattedError = MessageFormat.format(errorMessage, size, Integer.MAX_VALUE);
            throw new IllegalArgumentException(formattedError);
        }
        long lowerBoundIndex = xColumn.lowerBound(xValue, startIndex, (int) size);
        lowerBoundIndex -= startIndex;
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

    public DataSeries getSubset(double startXValue, double endXValue, int shoulder) {
        if (endXValue < startXValue) {
            String errorMessage = "Error during creating subset. Expected StartValue <= EndValue. StartValue = {0}, EndValue = {1}.";
            String formattedError = MessageFormat.format(errorMessage, startXValue, endXValue);
            throw new IllegalArgumentException(formattedError);
        }

        DataSeries subset = new DataSeries();
        subset.annotationColumn = annotationColumn;
        subset.xColumn = xColumn;
        subset.yColumns = yColumns;


        long fullSize = fullSize();
        if(fullSize == 0 || (startXValue <= xColumn.value(0) && endXValue >= xColumn.value(fullSize -1))) {
            return subset;
        }

        if((startXValue > xColumn.value(fullSize - 1) || endXValue < xColumn.value(0))) {
            subset.length = 0;
            return subset;
        }

        if (!(xColumn instanceof RegularColumn) && fullSize > Integer.MAX_VALUE) {
            String errorMessage = "Error during creating subset. Full size must be integer for no regular data sets. Full size = {0}, Integer.MAX_VALUE = {1}.";
            String formattedError = MessageFormat.format(errorMessage, fullSize, Integer.MAX_VALUE);
            throw new RuntimeException(formattedError);
        }

        if (isOrdered()) {
            subset.startIndex = xColumn.lowerBound(startXValue, 0, (int)fullSize);
            long subsetEndIndex = xColumn.upperBound(endXValue, 0, (int)fullSize);
            subset.startIndex -= shoulder;
            subsetEndIndex += shoulder;

            if (subset.startIndex < 0) {
                subset.startIndex = 0;
            }
            if (subsetEndIndex >= fullSize) {
                subsetEndIndex = fullSize - 1;
            }
            subset.length = subsetEndIndex - subset.startIndex + 1;
            if (subset.startIndex >= fullSize || subsetEndIndex < 0) {
                subset.length = 0;
            }
        }

        return subset;
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

    public void setXData(int[] data) {
        xColumn = new IntColumn(data);
    }

    public void setXData(float[] data) {
        xColumn = new FloatColumn(data);
    }

    public void setXData(List<? extends Number> data) {
        if (data.size() > 0 && data.get(0) instanceof Integer) {
            xColumn = new IntColumn((List<Integer>) data);
        } else {
            xColumn = new FloatColumn((List<Float>) data);
        }
    }

    public void addYData(int[] data) {
        yColumns.add(new IntColumn(data));
    }

    public void addYData(float[] data) {
        yColumns.add(new FloatColumn(data));
    }

    public void addYData(List<? extends Number> data) {
        if (data.size() > 0 && data.get(0) instanceof Integer) {
            yColumns.add(new IntColumn((List<Integer>) data));
        } else {
            yColumns.add(new FloatColumn((List<Float>) data));
        }
    }
}
