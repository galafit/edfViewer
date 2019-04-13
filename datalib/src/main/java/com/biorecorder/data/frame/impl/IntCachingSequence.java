package com.biorecorder.data.frame.impl;


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


    public IntCachingSequence(IntSequence data) {
        this.innerData = data;
        int size = innerData.size();
        if(size < REASONABLE_SIZE) {
            cachedData = new IntArrayList(size);
        } else {
            cachedData = new IntArrayList();
        }
    }

    @Override
    public int get(int index) {
        if(index >= cachedData.size()) {
            for (int i = cachedData.size(); i <= index; i++) {
                cachedData.add(innerData.get(i));
            }
        }
        return cachedData.get(index);
    }


    @Override
    public int size() {
        return innerData.size();
    }

    public IntSequence getInnerData() {
        return innerData;
    }
}
