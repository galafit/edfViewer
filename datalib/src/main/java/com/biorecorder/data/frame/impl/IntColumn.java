package com.biorecorder.data.frame.impl;

import com.biorecorder.data.frame.*;
import com.biorecorder.data.list.IntArrayList;
import com.biorecorder.data.sequence.IntSequence;
import com.biorecorder.data.utils.PrimitiveUtils;
import com.biorecorder.data.sequence.SequenceUtils;

import java.util.List;

/**
 * Created by galafit on 15/1/19.
 */
public class IntColumn implements Column {
    protected final static int NAN = Integer.MAX_VALUE;
    protected final static DataType dataType = DataType.INT;
    protected IntSequence dataSequence;
    protected StatsCalculator stats = new StatsCalculator();

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
        if (value == NAN) {
            return Double.NaN;
        }
        return value;
    }

    @Override
    public String label(int index) {
        int value = dataSequence.get(index);
        if (value == NAN) {
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
                if (length < 0) {
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
    public void cache(boolean isLastChangeable) {
        int nLastExcluded = 0;
        if (isLastChangeable) {
            nLastExcluded = 1;
        }
        if (!(dataSequence instanceof IntCachingSequence)) {
            dataSequence = new IntCachingSequence(dataSequence, nLastExcluded);
        }
    }

    @Override
    public void disableCaching() {
        if (dataSequence instanceof IntCachingSequence) {
            dataSequence = ((IntCachingSequence) dataSequence).getInnerData();
        }
    }

    @Override
    public int bisect(double value, int from, int length) {
        return SequenceUtils.bisect(dataSequence, PrimitiveUtils.roundDoubleToInt(value), from, length);
    }


    @Override
    public Stats stats(int length, boolean isLastChangeable) {
        return stats.getStats(length, isLastChangeable);
    }

    @Override
    public int[] sort(int from, int length, boolean isParallel) {
        return SequenceUtils.sort(dataSequence, from, length, isParallel);
    }

    @Override
    public IntSequence group(double interval, IntWrapper length) {
        IntSequence groupIndexes = new IntSequence() {
            Group currentGroup = new Group(interval);

            IntArrayList groupIndexesList = new IntArrayList(length.getValue());

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
                int groupListSize = groupIndexesList.size();
                if (length.getValue() == 0 || (groupListSize > 0 && groupIndexesList.get(groupListSize - 1) == length.getValue())) {
                    return;
                }

                int from;
                if (groupListSize == 0) {
                    groupIndexesList.add(0);
                    from = 0;
                } else {
                    // delete last "closing" group
                    groupIndexesList.remove(groupListSize - 1);
                    from = groupIndexesList.get(groupListSize - 2);
                }
                currentGroup = currentGroup.groupByValue(dataSequence.get(from));

                for (int i = from + 1; i < length.getValue(); i++) {
                    int data = dataSequence.get(i);
                    if (!currentGroup.contains(data)) {
                        groupIndexesList.add(i);
                        currentGroup = currentGroup.nextGroup(); // often situation

                        if (!currentGroup.contains(data)) { // rare situation
                            currentGroup = currentGroup.groupByValue(data);
                        }
                    }
                }
                // add last "closing" groupByEqualIntervals
                groupIndexesList.add(length.getValue());
            }
        };
        return groupIndexes;
    }

    class Group {
        double interval;
        long currentIntervalNumber;
        int castedNextGroupStart;

        public Group(double interval) {
            this(interval, 0);
        }

        public Group(double interval, double value) {
            this.interval = interval;
            currentIntervalNumber =  (long)(value / interval) ;
            if(currentIntervalNumber * interval > value) {
                currentIntervalNumber--;
            }

            double nextGroupStart = interval * (currentIntervalNumber + 1);
            castedNextGroupStart = PrimitiveUtils.doubleToInt(nextGroupStart);
            if(castedNextGroupStart > nextGroupStart) {
                castedNextGroupStart--;
            }
        }

        public Group nextGroup() {
            Group ng = new Group(interval);
            ng.currentIntervalNumber = currentIntervalNumber + 1;
            return ng;
        }

        public Group groupByValue(double value) {
            return new Group(interval, value);
        }

        public boolean contains(int value) {
            // group function valid only for sorted (increased data)
            // we do not need to check that value >= interval * currentIntervalNumber
            if(value < castedNextGroupStart) {
                return true;
            }
            return false;
        }

    }


    /**
     * @return grouping function Object corresponding
     * to the given type of data (IntGroupingAvg, FloatGroupingMin and so on)
     */
    private IntAggFunction getAggFunction(Aggregation aggregation) {
        // Capitalize the first letter of dataType string
        String type = dataType.toString().substring(0, 1).toUpperCase() + dataType.toString().substring(1);
        String functionClassName = "com.biorecorder.data.frame.impl."+ type + aggregation.toString();
        try {
            return (IntAggFunction) (Class.forName(functionClassName)).newInstance();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Column aggregate(Aggregation aggregation, IntSequence groupIndexes) {
        IntSequence resultantSequence = new IntSequence() {
            private IntAggFunction aggFunction = getAggFunction(aggregation);
            private int lastIndex = -1;

            @Override
            public int size() {
                return groupIndexes.size() - 1;
            }

            @Override
            public int get(int index) {
                if (index != lastIndex) {
                    aggFunction.reset();
                    lastIndex = index;
                }
                int n = aggFunction.getN();
                int length = groupIndexes.get(index + 1) - groupIndexes.get(index) - n;
                int from = groupIndexes.get(index) + n;
                if (length > 0) {
                    aggFunction.add(dataSequence, from, length);
                }
                return aggFunction.getValue();
            }
        };
        return new IntColumn(resultantSequence);
    }

    @Override
    public Column aggregate(Aggregation aggregation, int points) {
        IntSequence groupIndexes = new IntSequence() {
            int size;

            @Override
            public int size() {
                int dataSize = dataSequence.size();
                if (dataSize % points == 0) {
                    size = dataSize / points + 1;
                } else {
                    size = dataSize / points + 2;
                }
                return size;
            }

            @Override
            public int get(int index) {
                if (index == size - 1) {
                    return dataSequence.size();
                } else {
                    return index * points;
                }
            }
        };
        return aggregate(aggregation, groupIndexes);
    }


    class StatsCalculator {
        protected int count;
        private int min;
        private int max;
        private boolean isIncreasing = true;
        private boolean isDecreasing = true;

        public StatsInt calculate(int from, int length) {
            int min1 = dataSequence.get(from);
            int max1 = min1;
            boolean isIncreasing1 = true;
            boolean isDecreasing1 = true;

            for (int i = 1; i < length; i++) {
                int data_i = dataSequence.get(i + from);
                min1 = Math.min(min1, data_i);
                max1 = Math.max(max1, data_i);
                if (isIncreasing1 || isDecreasing1) {
                    int diff = data_i - dataSequence.get(i + from - 1);
                    if (isDecreasing1 && diff > 0) {
                        isDecreasing1 = false;
                    }
                    if (isIncreasing1 && diff < 0) {
                        isIncreasing1 = false;
                    }
                }
            }

            return new StatsInt(min1, max1, isIncreasing1, isDecreasing1);
        }

        public StatsInt getStats(int length, boolean isLastChangeable) {
            if (length <= 0) {
                String errMsg = "Statistic can not be calculated if length <= 0: " + length;
                throw new IllegalStateException(errMsg);
            }
            int dataSize = dataSequence.size();
            int length1 = length;
            if (isLastChangeable && length == dataSize) {
                length1--;
            }

            if (length1 < count) {
                count = 0;
            }

            if (length1 > count) {
                StatsInt stats = calculate(count, length1 - count);
                if (count == 0) {
                    min = stats.getMin();
                    max = stats.getMax();
                    isIncreasing = stats.isIncreasing();
                    isDecreasing = stats.isDecreasing();
                } else {
                    min = Math.min(min, stats.getMin());
                    max = Math.max(max, stats.getMax());
                    int diff = dataSequence.get(count) - dataSequence.get(count - 1);
                    isIncreasing = isIncreasing && stats.isIncreasing() && diff >= 0;
                    isDecreasing = isDecreasing && stats.isDecreasing() && diff <= 0;
                }
                count = length1;
            }

            if (length1 < length) {
                if (length1 < 1) {
                    int data = dataSequence.get(length1);
                    return new StatsInt(data, data, true, true);

                } else {
                    int lastData = dataSequence.get(length1);
                    int diff = lastData - dataSequence.get(length1 - 1);
                    return new StatsInt(Math.min(min, lastData), Math.max(max, lastData), isIncreasing && diff >= 0, isDecreasing && diff <= 0);

                }
            } else {
                return new StatsInt(min, max, isIncreasing, isDecreasing);
            }

        }
    }

    class StatsInt implements Stats {
        private final int min;
        private final int max;
        private final boolean isIncreasing;
        private final boolean isDecreasing;

        public StatsInt(int min, int max, boolean isIncreasing, boolean isDecreasing) {
            this.min = min;
            this.max = max;
            this.isIncreasing = isIncreasing;
            this.isDecreasing = isDecreasing;
        }

        int getMin() {
            return min;
        }

        int getMax() {
            return max;
        }

        @Override
        public double min() {
            return min;
        }

        @Override
        public double max() {
            return max;
        }

        @Override
        public boolean isIncreasing() {
            return isIncreasing;
        }

        @Override
        public boolean isDecreasing() {
            return isDecreasing;
        }
    }
}
