package com.biorecorder.data.frame.impl;

import com.biorecorder.data.list.ShortArrayList;
import com.biorecorder.data.sequence.ShortSequence;

/**
 * Class is designed to store/cache a computed input data and to give quick access to them.
 * Input data could be a filter, function and so on
 */

 /**************************************
  * This file is automatically created.
  * DO NOT MODIFY IT!
  * Edit template file _E_CachingSequence.tmpl
  *************************************/
public class ShortCachingSequence implements ShortSequence {
    static final int REASONABLE_SIZE = 3000;
    private ShortSequence innerData;
    private ShortArrayList cachedData;


    public ShortCachingSequence(ShortSequence data) {
        this.innerData = data;
        int size = innerData.size();
        if(size < REASONABLE_SIZE) {
            cachedData = new ShortArrayList(size);
        } else {
            cachedData = new ShortArrayList();
        }
    }

    @Override
    public short get(int index) {
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

    public ShortSequence getInnerData() {
        return innerData;
    }
}
