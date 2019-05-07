package com.biorecorder.data.frame.impl;

import com.biorecorder.data.list.LongArrayList;
import com.biorecorder.data.sequence.LongSequence;

/**
 * Class is designed to store/cache a computed input data and to give quick access to them.
 * Input data could be a filter, function and so on
 */

 /**************************************
  * This file is automatically created.
  * DO NOT MODIFY IT!
  * Edit template file _E_CachingSequence.tmpl
  *************************************/
class LongCachingSequence implements LongSequence {
    static final int REASONABLE_SIZE = 3000;
    private LongSequence innerData;
    private LongArrayList cachedData;


    public LongCachingSequence(LongSequence data) {
        this.innerData = data;
        int size = innerData.size();
        if(size < REASONABLE_SIZE) {
            cachedData = new LongArrayList(size);
        } else {
            cachedData = new LongArrayList();
        }
    }

    @Override
    public long get(int index) {
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

    public LongSequence getInnerData() {
        return innerData;
    }
}
