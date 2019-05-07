package com.biorecorder.data.frame;

import com.biorecorder.data.frame.impl.LongColumn;
import com.biorecorder.data.sequence.IntSequence;
import com.biorecorder.data.sequence.SequenceUtils;
import com.biorecorder.data.sequence.StringSequence;
import com.biorecorder.data.utils.PrimitiveUtils;

/**
 * Created by galafit on 26/4/19.
 */
public class StringColumn implements Column {
    LongColumn intColumn;
    private StringSequence labelSequence;

    public StringColumn(LongColumn intColumn, StringSequence labelSequence) {
        this.intColumn = intColumn;
        this.labelSequence = labelSequence;
    }

    public StringColumn(StringSequence labelSequence) {
        this.labelSequence = labelSequence;
        intColumn = new LongRegularColumn(0, 1);
    }

    public StringSequence getLabels() {
        return labelSequence;
    }

    @Override
    public int size() {
        return Math.min(intColumn.size(), labelSequence.size());
    }

    @Override
    public double value(int index) {
        return intColumn.value(index);
    }

    @Override
    public String label(int index) {
        int labelIndex = PrimitiveUtils.long2int(intColumn.longValue(index));
        if(labelIndex >= 0 && labelIndex < labelSequence.size()) {
            return labelSequence.get(labelIndex);
        }
        return intColumn.label(index);
    }

    @Override
    public DataType dataType() {
        return DataType.String;
    }

    @Override
    public int[] sort(int from, int length, boolean isParallel) {
        return SequenceUtils.sort(labelSequence, from, length, isParallel);
    }

    @Override
    public Column slice(int from, int length) {
        return new StringColumn((LongColumn) intColumn.slice(from, length), labelSequence);
    }

    @Override
    public Column view(int from, int length) {
        return new StringColumn((LongColumn) intColumn.view(from, length), labelSequence);
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
                return labelSequence.get(order[index]);
            }
        };
        return new StringColumn(subSequence);
    }


    @Override
    public int bisect(double value, int from, int length) {
        return intColumn.bisect(value, from, length);
    }

    @Override
    public Stats stats(int length) {
        return intColumn.stats(length);
    }

    @Override
    public void cache() {
        intColumn.cache();
    }

    @Override
    public void disableCaching() {
       intColumn.disableCaching();
    }

    @Override
    public IntSequence group(double interval, IntWrapper length) {
        return intColumn.group(interval, length);
    }

    @Override
    public IntSequence group(TimeInterval timeInterval, IntWrapper length) {
        return intColumn.group(timeInterval, length);
    }


    @Override
    public Column resample(Aggregation aggregation, IntSequence groupIndexes, boolean isDataAppendMode) {
        return new StringColumn((LongColumn) intColumn.resample(aggregation, groupIndexes, isDataAppendMode), labelSequence);
    }

    @Override
    public Column resample(Aggregation aggregation, int points, IntWrapper length, boolean isDataAppendMode) {
        return new StringColumn((LongColumn) intColumn.resample(aggregation, points, length, isDataAppendMode), labelSequence);
    }
}
