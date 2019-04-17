package com.biorecorder.data.frame;

import com.biorecorder.data.sequence.StringSequence;

import java.util.ArrayList;

/**
 * Class is designed to store/cache a computed input data and to give quick access to them.
 * Input data could be a filter, function and so on
 */
public class StringCachingSequence implements StringSequence{
    static final int REASONABLE_SIZE = 3000;
    private StringSequence innerData;
    private ArrayList<String> cachedData;


    public StringCachingSequence(StringSequence data) {
        this.innerData = data;
        int size = innerData.size();
        if(size < REASONABLE_SIZE) {
            cachedData = new ArrayList(size);
        } else {
            cachedData = new ArrayList();
        }
    }

    @Override
    public String get(int index) {
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

    public StringSequence getInnerData() {
        return innerData;
    }

}
