package com.biorecorder.data.frame.impl;

import com.biorecorder.data.list.DoubleArrayList;
import com.biorecorder.data.sequence.DoubleSequence;

/**
 * Class is designed to store/cache a computed input data and to give quick access to them.
 * Input data could be a filter, function and so on
 */

 /**************************************
  * This file is automatically created.
  * DO NOT MODIFY IT!
  * Edit template file _E_CachingSequence.tmpl
  *************************************/
class DoubleCachingSequence implements DoubleSequence {
    static final int REASONABLE_SIZE = 3000;
    private DoubleSequence innerData;
    private DoubleArrayList cachedData;


    public DoubleCachingSequence(DoubleSequence data) {
        this.innerData = data;
        int size = innerData.size();
        if(size < REASONABLE_SIZE) {
            cachedData = new DoubleArrayList(size);
        } else {
            cachedData = new DoubleArrayList();
        }
    }

    @Override
    public double get(int index) {
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

    public DoubleSequence getInnerData() {
        return innerData;
    }
}
