package com.biorecorder.data.frame.impl;

import com.biorecorder.data.list.FloatArrayList;
import com.biorecorder.data.sequence.FloatSequence;

/**
 * Class is designed to store/cache a computed input data and to give quick access to them.
 * Input data could be a filter, function and so on
 */

 /**************************************
  * This file is automatically created.
  * DO NOT MODIFY IT!
  * Edit template file _E_CachingSequence.tmpl
  *************************************/
class FloatCachingSequence implements FloatSequence {
    static final int REASONABLE_SIZE = 3000;
    private FloatSequence innerData;
    private FloatArrayList cachedData;


    public FloatCachingSequence(FloatSequence data) {
        this.innerData = data;
        int size = innerData.size();
        if(size < REASONABLE_SIZE) {
            cachedData = new FloatArrayList(size);
        } else {
            cachedData = new FloatArrayList();
        }
    }

    @Override
    public float get(int index) {
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

    public FloatSequence getInnerData() {
        return innerData;
    }
}
