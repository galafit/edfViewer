package com.biorecorder.data;


import com.biorecorder.data.series.StringSeries;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by galafit on 28/9/17.
 */
class StringColumn {
    protected String name;
    protected final StringSeries series;

    public StringColumn(StringSeries series) {
        this.series = series;
    }

    public long size() {
        return series.size();
    }

    public StringColumn subColumn(long fromIndex, long length) {
        StringSeries subSeries = new StringSeries() {
            @Override
            public long size() {
                return length;
            }

            @Override
            public String get(long index) {
                return get(index - fromIndex);
            }
        };
        return new StringColumn(subSeries);
    }


    public void add(String element) throws UnsupportedOperationException {
        if(series instanceof ArrayList) {
            ((ArrayList) series).add(element);
        }
        throw  new UnsupportedOperationException("Elements can be added to the column only if that column wraps ArrayList");
    }


    public void add(String[] elements) throws UnsupportedOperationException {
        if(series instanceof ArrayList) {
            ((ArrayList) series).addAll(Arrays.asList(elements));
        }
        throw  new UnsupportedOperationException("Elements can be added to the column only if that column wraps ArrayList");
    }

    public void remove(int index) {
        if(series instanceof ArrayList) {
            ((ArrayList) series).remove(index);
        }
        throw  new UnsupportedOperationException("Element can be removed from the column only if that column wraps ArrayList");
    }



    public String getString(long index) {
        return series.get(index);
    }

    public StringColumn copy() {
        return new StringColumn(series);
    }

    public StringColumn cache() {
        long size = size();
        if (size > Integer.MAX_VALUE) {
            String errorMessage = "Column can not be cached if its size > Integer.MAX_VALUE. Size = " + size;
            throw new IllegalArgumentException(errorMessage);
        }
        final ArrayList<String> stringList = new ArrayList<String>((int)size);
        for (int i = 0; i < size; i++) {
            stringList.add(series.get(i));
        }
        return new StringColumn(new StringSeries() {
            @Override
            public long size() {
                return stringList.size();
            }

            @Override
            public String get(long index) {
                return stringList.get((int)index);
            }
        });
    }

}
