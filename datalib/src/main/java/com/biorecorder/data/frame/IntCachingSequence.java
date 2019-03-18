package com.biorecorder.data.frame;


import com.biorecorder.data.list.IntArrayList;
import com.biorecorder.data.sequence.IntSequence;


/**
 * Class is designed to store/cache a computed input data and to give quick access to them.
 * Input data could be a filter, function and so on
 */

public class IntCachingSequence implements IntSequence {
    static final int REASONABLE_SIZE = 3000;
    private IntSequence innerData;
    private IntArrayList cachedData;
    private int nLastExcluded;
    private int size;


    public IntCachingSequence(IntSequence data, int nLastExcluded) {
        this.innerData = data;
        this.nLastExcluded = nLastExcluded;
        size = innerData.size();
        if(size < REASONABLE_SIZE) {
            cachedData = new IntArrayList(size - nLastExcluded);
        } else {
            cachedData = new IntArrayList();
        }


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
        // cache data
        if(index < size - nLastExcluded) {
            for (int i = cachedData.size(); i <= index; i++) {
                cachedData.add(innerData.get(i));
            }
        }

        if(index < cachedData.size()) {
            return cachedData.get(index);
        } else {
            return innerData.get(index);
        }
    }


    @Override
    public int size() {
        size = innerData.size();
        return size;
    }

    public IntSequence getInnerData() {
        return innerData;
    }
}
