package com.biorecorder.data.frame;


import com.biorecorder.data.list.IntArrayList;
import com.biorecorder.data.sequence.IntSequence;


/**
 * Class is designed to store/cache a computed input data and to give quick access to them.
 * Input data could be a filter, function and so on
 */

public class IntCachingSequence implements IntSequence {
    private IntSequence innerData;
    private IntArrayList cachedData;
    private int nLastExcluded;


    public IntCachingSequence(IntSequence data, int nLastExcluded) {
        this.innerData = data;
        this.nLastExcluded = nLastExcluded;
        cachedData = new IntArrayList(innerData.size() - nLastExcluded);
        cacheData();
    }

    private void cacheData() {
        long dataSize = innerData.size();
        dataSize -= nLastExcluded;

        if (cachedData.size()  < dataSize) {
            for (int i = cachedData.size(); i < dataSize; i++) {
                cachedData.add(innerData.get(i));
            }
        }
    }

    @Override
    public int get(int index) {
        if(index < cachedData.size()) {
            return cachedData.get(index);
        }
        cacheData();
        if(index < cachedData.size()) {
            return cachedData.get(index);
        } else {
            return innerData.get(index);
        }
    }


    @Override
    public int size() {
        return innerData.size();
    }

    public IntSequence getInnerData() {
        return innerData;
    }
}
