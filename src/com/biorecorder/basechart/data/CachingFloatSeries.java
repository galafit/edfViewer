package com.biorecorder.basechart.data;

import java.text.MessageFormat;

/**
 * Created by galafit on 25/11/17.
 */
public class CachingFloatSeries implements FloatSeries {
    private FloatSeries inputData;
    private FloatArrayList cachedData;
    private boolean isCashingEnabled = true;


    public CachingFloatSeries(FloatSeries inputData) {
        this.inputData = inputData;
        cachedData = new FloatArrayList((int)inputData.size());
        cacheData();
    }

    private void cacheData() {
        if(inputData.size() > Integer.MAX_VALUE) {
            String errorMessage = "Error during caching data. Expected: inputData.size() <= Integer.MAX_VALUE. inputData.size = {0}, Integer.MAX_VALUE = {1}.";
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
    public float get(long index) {
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
