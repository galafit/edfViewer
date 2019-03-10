package com.biorecorder.data.frame;

import com.biorecorder.data.aggregation.AggregateFunction;
import com.biorecorder.data.aggregation.IntAggFunction;
import com.biorecorder.data.list.IntArrayList;
import com.biorecorder.data.sequence.IntSequence;
import com.biorecorder.data.sequence.PrimitiveUtils;
import com.biorecorder.data.sequence.SequenceUtils;

import java.util.List;

/**
 * Created by galafit on 15/1/19.
 */
public class IntColumn implements Column {
    protected final static int NAN = Integer.MAX_VALUE;
    protected final static DataType dataType = DataType.INT;
    protected IntSequence dataSequence;
    protected MinMaxAgg minMaxAgg = new MinMaxAgg();

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
        return dataType;
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
    public Column view(int[] order) {
        IntSequence subSequence = new IntSequence() {
            @Override
            public int size() {
                return order.length;
            }

            @Override
            public int get(int index) {
                return dataSequence.get(order[index]);
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
        if(! (dataSequence instanceof IntCachingSequence)) {
            dataSequence = new IntCachingSequence(dataSequence, nLastExcluded);
        }
    }

    @Override
    public void disableCaching() {
        if(dataSequence instanceof IntCachingSequence) {
            dataSequence = ((IntCachingSequence) dataSequence).getInnerData();
        }
    }

    @Override
    public int bisect(double value, int from, int length) {
        return SequenceUtils.bisect(dataSequence, PrimitiveUtils.doubleToInt(value), from,  length);
    }

    @Override
    public double min(int length) {
        recalculateAgg(length);
        return minMaxAgg.getMin();
    }

    @Override
    public double max(int length) {
        recalculateAgg(length);
        return minMaxAgg.getMax();
    }

    @Override
    public boolean isIncreasing(int length) {
        recalculateAgg(length);
        return minMaxAgg.isIncreasing();
    }

    @Override
    public boolean isDecreasing(int length) {
        recalculateAgg(length);
        return minMaxAgg.isDecreasing();
    }

    private void recalculateAgg(int length) {
        int n = minMaxAgg.getN();
        if(n < length) {
            minMaxAgg.add(dataSequence, n, length - n);
        }
    }

    @Override
    public int[] sort(int from, int length, boolean isParallel) {
        int[] orderedIndexes = new int[length];
        if(isDecreasing(length)) {
            for (int i = 0; i < length; i++) {
                orderedIndexes[i]  = length + from - 1 - i;
            }
            return orderedIndexes;
        }

        if(isIncreasing(length)) {
            for (int i = 0; i < length; i++) {
                orderedIndexes[i]  = i + from;
            }
            return orderedIndexes;
        }

        return SequenceUtils.sort(dataSequence, from, length, isParallel);
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

                int from;
                if(groupListSize == 0) {
                    groupIndexesList.add(0);
                    from = 0;
                } else {
                    // delete last "closing" group
                    groupIndexesList.remove(groupListSize - 1);
                    from = groupIndexesList.get(groupListSize - 2);
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
            private IntAggFunction aggFunction = (IntAggFunction) aggregateFunction.getFunctionImpl(dataType);;
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

    class MinMaxAgg {
        protected int count;
        private int min;
        private int max;
        private boolean isIncreasing = true;
        private boolean isDecreasing = true;

        public int add(IntSequence sequence, int from, int length) {
            if(count == 0) {
                min = sequence.get(from);
                max = min;
            }
            int till = from + length;
            for (int i = from + 1; i < till; i++) {
              min = Math.min(min, sequence.get(i));
              max = Math.max(max, sequence.get(i));
              if(isIncreasing || isDecreasing) {
                  int diff = sequence.get(i) - sequence.get(i - 1);
                  if(isDecreasing && diff > 0) {
                      isDecreasing = false;
                  }
                  if(isIncreasing && diff < 0) {
                      isIncreasing = false;
                  }
              }
            }
            count +=length;
            return count;
        }

        public int getMin() {
            checkIfEmpty();
            return min;
        }

        public int getMax() {
            checkIfEmpty();
            return max;
        }

        public boolean isIncreasing() {
            checkIfEmpty();
            return isIncreasing;
        }

        public boolean isDecreasing() {
            checkIfEmpty();
            return isDecreasing;
        }

        public int getN() {
            return count;
        }

        public void reset() {
            count = 0;
        }

        private void checkIfEmpty() {
            if(count == 0) {
                String errMsg = "No elements was added to group. Grouping function can not be calculated.";
                throw new IllegalStateException(errMsg);
            }
        }
    }
}
