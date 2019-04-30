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
    private IntSequence dataSequence;
    private StatsInt stats;

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

    public int intValue(int index) {
        return dataSequence.get(index);
    }


    @Override
    public int size() {
        return dataSequence.size();
    }

    @Override
    public double value(int index) {
        return dataSequence.get(index);
    }

    @Override
    public String label(int index) {
        return Integer.toString(dataSequence.get(index));
    }

    @Override
    public DataType dataType() {
        return DataType.INT;
    }

    @Override
    public Column view(int from, int length) {
        IntSequence subSequence = new IntSequence() {
            @Override
            public int size() {
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
    public void cache() {
        if (!(dataSequence instanceof IntCachingSequence)) {
            dataSequence = new IntCachingSequence(dataSequence);
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
        return SequenceUtils.bisect(dataSequence, PrimitiveUtils.roundDouble2int(value), from, length);
    }

    @Override
    public int[] sort(int from, int length, boolean isParallel) {
        return SequenceUtils.sort(dataSequence, from, length, isParallel);
    }


    @Override
    public IntSequence group(double interval, IntWrapper length) {
        return group(new IntIntervalExt(PrimitiveUtils.roundDouble2int(interval), true), length);
    }

    @Override
    public IntSequence group(TimeUnit unit, int unitMultiplier, IntWrapper length) {
        return group(new TimeIntervalExt(unit, unitMultiplier, true), length);
    }

    private IntSequence group(IntervalExt currentGroup, IntWrapper length) {
        IntSequence groupIndexes = new IntSequence() {
            IntArrayList groupIndexesList = new IntArrayList();

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
                int l = length.getValue();
                if ( l == 0 || (groupListSize > 0 && groupIndexesList.get(groupListSize - 1) == length.getValue())) {
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
                if(from == 0) {
                    currentGroup.goContaining(dataSequence.get(from));
                }

                for (int i = from + 1; i < l; i++) {
                    int data = dataSequence.get(i);
                    if (!currentGroup.containsInt(data)) {
                        groupIndexesList.add(i);
                        currentGroup.goNext();
                        if(!currentGroup.containsInt(data)) {
                            currentGroup.goContaining(data);
                        }
                    }
                }
                // add last "closing" groupByEqualIntervals
                groupIndexesList.add(l);
            }
        };
         return groupIndexes;
    }

    /**
     * @return grouping function Object corresponding
     * to the given type of data (IntGroupingAvg, FloatGroupingMin and so on)
     */
    private IntAggFunction getAggFunction(Aggregation aggregation) {
        // Capitalize the first letter of dataType string
        String type = dataType().toString().substring(0, 1).toUpperCase() + dataType().toString().substring(1);
        String functionClassName = "com.biorecorder.data.frame.impl." + type + aggregation.toString();
        try {
            return (IntAggFunction) (Class.forName(functionClassName)).newInstance();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    protected int groupsCount(IntSequence groupIndexes, boolean isDataAppendMode) {
        int groupsCount = groupIndexes.size() - 1;
        if(isDataAppendMode && groupsCount > 0) {
            groupsCount--;
        }
        return groupsCount;
    }

    @Override
    public Column resample(Aggregation aggregation, IntSequence groupIndexes, boolean isDataAppendMode) {
        IntSequence resultantSequence = new IntSequence() {
            private IntAggFunction aggFunction = getAggFunction(aggregation);
            private int lastIndex = -1;

            @Override
            public int size() {
                return groupsCount(groupIndexes, isDataAppendMode);
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
    public Column resample(Aggregation aggregation, int points, IntWrapper length, boolean isDataAppendMode) {
        return resample(aggregation, groupIndexes(points, length), isDataAppendMode);
    }

    protected IntSequence groupIndexes(int points, IntWrapper length) {
        return new IntSequence() {
            int size;

            @Override
            public int size() {
                if(length.getValue() % points == 0) {
                    size = length.getValue() / points + 1;
                } else {
                    size = length.getValue() / points + 2;
                }
                return size;
            }

            @Override
            public int get(int index) {
                if (index == size - 1) {
                    return length.getValue();
                } else {
                    return index * points;
                }
            }
        };
    }


    private StatsInt calculateStats(int from, int length) {
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

        return new StatsInt(length, min1, max1, isIncreasing1, isDecreasing1);
    }

    @Override
    public Stats stats(int length) {
        if (length <= 0) {
            String errMsg = "Statistic can not be calculated if length <= 0: " + length;
            throw new IllegalStateException(errMsg);
        }
        if (length <= 2) {
            return calculateStats(0, length);
        }

        if (stats != null && length < stats.count()) {
            stats = null;
        }
        if (stats == null) {
            stats = calculateStats(0, length);
        }

        if (length > stats.count()) {
            StatsInt statsAdditional = calculateStats(stats.count(), length - stats.count());
            int min = Math.min(stats.getMin(), statsAdditional.getMin());
            int max = Math.max(stats.getMax(), statsAdditional.getMax());
            int diff = dataSequence.get(stats.count) - dataSequence.get(stats.count() - 1);
            boolean isIncreasing = stats.isIncreasing() && statsAdditional.isIncreasing() && diff >= 0;
            boolean isDecreasing = stats.isDecreasing() && statsAdditional.isDecreasing() && diff <= 0;
            stats = new StatsInt(length, min, max, isIncreasing, isDecreasing);
        }
        return stats;
    }

    class StatsInt implements Stats {
        private int count;
        private final int min;
        private final int max;
        private final boolean isIncreasing;
        private final boolean isDecreasing;

        public StatsInt(int count, int min, int max, boolean isIncreasing, boolean isDecreasing) {
            this.count = count;
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

        int count() {
            return count;
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

    interface IntervalExt extends Interval {
        boolean containsInt(int value);
    }

    class TimeIntervalExt extends TimeInterval implements IntervalExt {
        public TimeIntervalExt(TimeUnit unit,int unitMultiplier,  boolean isDataIncreasing) {
            super(unit, unitMultiplier, isDataIncreasing);
        }

        @Override
        public boolean containsInt(int value) {
            return contains((long)value);
        }
    }


    class IntIntervalExt implements IntervalExt {
        int interval;
        int intervalStart;
        int nextIntervalStart;
        boolean isDataIncreasing;

        public IntIntervalExt(int interval,  boolean isDataIncreasing) {
            this.interval = interval;
            this.isDataIncreasing = isDataIncreasing;
            intervalStart = 0;
            nextIntervalStart = intervalStart + interval;
        }


        @Override
        public void goContaining(double value) {
            int castedValue = PrimitiveUtils.roundDouble2int(value);
            intervalStart = PrimitiveUtils.round(castedValue / interval) * interval;
            if (intervalStart > value) {
                intervalStart -= interval;
            }
            nextIntervalStart = intervalStart + interval;
        }

        @Override
        public void goNext() {
            intervalStart = nextIntervalStart;
            nextIntervalStart += interval;
        }

        @Override
        public void goPrevious() {
            nextIntervalStart = intervalStart;
            intervalStart -= interval;

        }

        public boolean containsInt(int value) {
            if(isDataIncreasing) {
                return value < nextIntervalStart;
            }
            return value >= intervalStart && value < nextIntervalStart;
        }
    }
}
