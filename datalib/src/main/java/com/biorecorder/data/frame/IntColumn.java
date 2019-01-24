package com.biorecorder.data.frame;

import com.biorecorder.basechart.BRange;
import com.biorecorder.data.aggregation.AggregateFunction;
import com.biorecorder.data.aggregation.IntAggFunction;
import com.biorecorder.data.list.IntArrayList;
import com.biorecorder.data.sequence.IntSequence;
import com.biorecorder.data.sequence.SequenceUtils;

import java.util.List;

/**
 * Created by galafit on 15/1/19.
 */
public class IntColumn implements Column {
    protected final static int NAN = Integer.MAX_VALUE;
    protected IntSequence dataSequence;

    public IntColumn(IntSequence data) {
        this.dataSequence = data;
    }

    public IntColumn(int[] data) {
        this(new IntSequence() {
            @Override
            public int size() {
                return data.length;
            }

            @Override
            public int get(int index) {
                return data[index];
            }
        });
    }

    public IntColumn(List<Integer> data) {
        this(new IntSequence() {
            @Override
            public int size() {
                return data.size();
            }

            @Override
            public int get(int index) {
                return data.get(index);
            }
        });
    }

    @Override
    public int size() {
        return dataSequence.size();
    }

    @Override
    public double value(int index) {
        int value = dataSequence.get(index);
        if(value == NAN) {
            return Double.NaN;
        }
        return value;
    }

    @Override
    public String label(int index) {
        int value = dataSequence.get(index);
        if(value == NAN) {
            return null;
        }
        return Integer.toString(value);
    }

    @Override
    public DataType dataType() {
        return DataType.NUMBER;
    }

    @Override
    public Column view(int from, int length) {
        IntSequence subSequence = new IntSequence() {
            @Override
            public int size() {
                if(length < 0) {
                    return dataSequence.size() - from;
                }
                return length;
            }

            @Override
            public int get(int index) {
                return dataSequence.get(index + from);
            }
        };
        return new IntColumn(subSequence);
    }

    @Override
    public Column slice(int from, int length) {
        int[] slicedData = new int[length];
        for (int i = 0; i < length; i++) {
            slicedData[i] = dataSequence.get(from + i);
        }
        return new IntColumn(slicedData);
    }

    @Override
    public void cache(int nLastExcluded) {
        if(! (dataSequence instanceof CachingIntSequence)) {
            dataSequence = new CachingIntSequence(dataSequence, nLastExcluded);
        }
    }

    @Override
    public void disableCaching() {
        if(dataSequence instanceof CachingIntSequence) {
            dataSequence = ((CachingIntSequence) dataSequence).getInnerData();
        }
    }


    @Override
    public int nearest(double value, int from, int length) {
        return SequenceUtils.binarySearch(dataSequence, (int)Math.round(value), from,  length);

    }

    @Override
    public BRange range(int from1, int length1) {
        int from = from1;
        int length = length1;
        int dataSize = dataSequence.size();

        if(dataSize == 0 || length <= 0 || from >= dataSize){
            return null;
        }
        if(from < 0) {
            from = 0;
        }
        if(length + from > dataSize) {
            length = dataSize - from;
        }

        // invoke data.get(i) can be expensive in the case data is grouped data
        int dataItem = dataSequence.get(from);
        int min = dataItem;
        int max = dataItem;
        int till = from + length;
        for (int i = from + 1; i < till ; i++) {
            dataItem = dataSequence.get(i);
            min = Math.min(min, dataItem);
            max = Math.max(max, dataItem);
        }

        return new BRange(min, max);
    }


    @Override
    public IntSequence group(double interval) {
        int sequenceSize = dataSequence.size();

        IntSequence groupIndexes = new IntSequence() {
            int intervalValue = (int) interval;
            IntArrayList groupIndexesList = new IntArrayList(sequenceSize);
            @Override
            public int size() {
                update();
                return groupIndexesList.size();
            }

            @Override
            public int get(int index) {
                return groupIndexesList.get(index);
            }

            private void update() {
                int dataSize =  dataSequence.size();
                int groupListSize =  groupIndexesList.size();
                if(dataSize == 0 || (groupListSize > 0 && groupIndexesList.get(groupListSize - 1) == dataSize)) {
                    return;
                }

                if(groupListSize == 0) {
                    groupIndexesList.add(0);
                } else {
                    // delete last "closing" group
                    groupIndexesList.remove(groupListSize - 1);
                }

                int from = 0;
                if(groupListSize > 0) {
                   from = groupIndexesList.get(groupListSize - 1);
                }
                int groupValue = ((dataSequence.get(from) / intervalValue)) * intervalValue;
                groupValue += intervalValue;
                for (int i = from + 1;  i < dataSize; i++) {
                    int data = dataSequence.get(i);
                    if (dataSequence.get(i) >= groupValue) {
                        groupIndexesList.add(i);
                        groupValue += intervalValue; // often situation

                        if(data > groupValue) { // rare situation
                            groupValue = ((dataSequence.get(i) / intervalValue)) * intervalValue;
                            groupValue += intervalValue;
                        }
                    }
                }
                // add last "closing" groupByEqualIntervals
                groupIndexesList.add(dataSize);
            }
        };
        return groupIndexes;
    }

    @Override
    public Column aggregate(AggregateFunction aggregateFunction, IntSequence groupIndexes) {
        IntSequence resultantSequence = new IntSequence() {
            private IntAggFunction aggFunction = (IntAggFunction) aggregateFunction.getFunctionImpl("int");;
            private int lastIndex = -1;

            @Override
            public int size() {
                return groupIndexes.size() - 1;
            }

            @Override
            public int get(int index) {
                if(index != lastIndex) {
                    aggFunction.reset();
                    lastIndex = index;
                }
                int n = aggFunction.getN();
                int length = groupIndexes.get(index + 1) - groupIndexes.get(index) - n;
                int from = groupIndexes.get(index) + n;
                if(length > 0) {
                    aggFunction.add(dataSequence, from, length);
                }
                return aggFunction.getValue();
            }
        };
        return new IntColumn(resultantSequence);
    }
}
