package com.biorecorder.data.frame;

import com.biorecorder.data.sequence.IntSequence;
import com.biorecorder.data.sequence.SequenceUtils;
import com.biorecorder.data.sequence.StringSequence;


/**
 * Created by galafit on 17/4/19.
 */
public class StringColumn extends RegularColumn {

    private StringSequence stringSequence;


    public StringColumn(StringSequence data, int start) {
        super(start, 1);
        this.stringSequence = data;
    }

    public StringColumn(StringSequence data) {
        this(data, 0);
    }

    public StringColumn(String[] data, int start) {
        this(new StringSequence() {
            @Override
            public int size() {
                return data.length;
            }

            @Override
            public String get(int index) {
                return data[index];
            }
        }, start);
    }

    public StringColumn(String[] data) {
        this(data, 0);
    }


    @Override
    public int size() {
        return stringSequence.size();
    }

    @Override
    public double value(int index) {
        return Double.NaN;
    }

    @Override
    public String label(int index) {
        return stringSequence.get(index);
    }

    @Override
    public DataType dataType() {
        return DataType.STRING;
    }

    @Override
    public int[] sort(int from, int length, boolean isParallel) {
        return SequenceUtils.sort(stringSequence, from, length, isParallel);
    }

    @Override
    public Column slice(int from, int length) {
        String[] slicedData = new String[length];
        for (int i = 0; i < length; i++) {
            slicedData[i] = stringSequence.get(from + i);
        }
        return new StringColumn(slicedData, from);
    }

    @Override
    public Column view(int from, int length) {
        StringSequence subSequence = new StringSequence() {
            @Override
            public int size() {
                return length;
            }

            @Override
            public String get(int index) {
                return stringSequence.get(index + from);
            }
        };

        return new StringColumn(subSequence, from);
    }

    @Override
    public Column view(int[] order) {
        StringSequence subSequence = new StringSequence() {
            @Override
            public int size() {
                return order.length;
            }

            @Override
            public String get(int index) {
                return stringSequence.get(order[index]);
            }
        };
        return new StringColumn(subSequence);
    }


    @Override
    public void cache() {
        if (!(stringSequence instanceof StringCachingSequence)) {
            stringSequence = new StringCachingSequence(stringSequence);
        }
    }

    @Override
    public void disableCaching() {
        if (stringSequence instanceof StringCachingSequence) {
            stringSequence = ((StringCachingSequence) stringSequence).getInnerData();
        }
    }

    @Override
    public Column resample(Aggregation aggregation, IntSequence groupIndexes, boolean isDataAppendMode) {
        StringSequence resultantSequence = new StringSequence() {
            @Override
            public int size() {
                return groupsCount(groupIndexes, isDataAppendMode);
            }

            @Override
            public String get(int index) {
                return stringSequence.get(groupIndexes.get(index));
            }
        };
        return new StringColumn(resultantSequence);
    }

    @Override
    public Column resample(Aggregation aggregation, int points, IntWrapper length, boolean isDataAppendMode) {
        return resample(aggregation, groupIndexes(points, length), isDataAppendMode);
    }
}
