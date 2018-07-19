package com.biorecorder.basechart.data;

import java.text.MessageFormat;

/**
 * Class is designed to store/cache a computed input com.biorecorder.basechart.chart.data and to give quick access to them.
 * Input com.biorecorder.basechart.chart.data could be a filter, function and so on
 */

public class CachingIntSeries implements IntSeries {
    private IntSeries inputData;
    private IntArrayList cachedData;
    private boolean isCashingEnabled = true;


    public CachingIntSeries(IntSeries inputData) {
        this.inputData = inputData;
        cachedData = new IntArrayList((int)inputData.size());
        cacheData();
    }

    private void cacheData() {
        if(inputData.size() > Integer.MAX_VALUE) {
            String errorMessage = "Error during caching com.biorecorder.basechart.chart.data. Expected: inputData.size() is integer. inputData.size = {0}, Integer.MAX_VALUE = {1}.";
            String formattedError = MessageFormat.format(errorMessage, inputData.size(), Integer.MAX_VALUE);
            throw new RuntimeException(formattedError);

        }
        if (cachedData.size()  < inputData.size()) {
            for (int i = (int)cachedData.size(); i < inputData.size(); i++) {
                cachedData.add(inputData.get(i));
            }
        }
    }

    public void disableCashing() {
        isCashingEnabled = false;
        cachedData = null;
    }

    @Override
    public int get(long index) {
        if(isCashingEnabled) {
            return cachedData.get(index);
        }
        return inputData.get(index);
    }


    @Override
    public long size() {
        if(isCashingEnabled) {
            cacheData();
            return cachedData.size();
        }
        return inputData.size();
    }
}

