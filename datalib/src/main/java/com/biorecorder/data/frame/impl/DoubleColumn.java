package com.biorecorder.data.frame.impl;

import com.biorecorder.data.frame.*;
import com.biorecorder.data.frame.Interval;
import com.biorecorder.data.list.IntArrayList;
import com.biorecorder.data.sequence.IntSequence;
import com.biorecorder.data.sequence.DoubleSequence;
import com.biorecorder.data.utils.PrimitiveUtils;
import com.biorecorder.data.sequence.SequenceUtils;


/**************************************
 * This file is automatically created.
 * DO NOT MODIFY IT!
 * Edit template file _E_Column.tmpl
 *************************************/

public class DoubleColumn implements Column {
    private DoubleSequence dataSequence;
    private StatsDouble stats;

    public DoubleColumn(DoubleSequence data) {
        this.dataSequence = data;
    }

    public double doubleValue(int index) {
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
        return Double.toString(dataSequence.get(index));
    }

    @Override
    public DataType dataType() {
        return DataType.Double;
    }

    @Override
    public Column view(int from, int length) {
        DoubleSequence subSequence = new DoubleSequence() {
            @Override
            public int size() {
                return length;
            }

            @Override
            public double get(int index) {
                return dataSequence.get(index + from);
            }
        };
        return new DoubleColumn(subSequence);
    }

    @Override
    public Column view(int[] order) {
        DoubleSequence subSequence = new DoubleSequence() {
            @Override
            public int size() {
                return order.length;
            }

            @Override
            public double get(int index) {
                return dataSequence.get(order[index]);
            }
        };
        return new DoubleColumn(subSequence);
    }


  @Override
    public Column slice(int from, int length) {
        double[] slicedData = new double[length];
        for (int i = 0; i < length; i++) {
            slicedData[i] = dataSequence.get(from + i);
        }
        return new DoubleColumn(new DoubleSequence() {
            @Override
            public int size() {
                return slicedData.length;
            }

            @Override
            public double get(int index) {
                return slicedData[index];
            }
        });
    }


    @Override
    public void cache() {
        if (!(dataSequence instanceof DoubleCachingSequence)) {
            dataSequence = new DoubleCachingSequence(dataSequence);
        }
    }

    @Override
    public void disableCaching() {
        if (dataSequence instanceof DoubleCachingSequence) {
            dataSequence = ((DoubleCachingSequence) dataSequence).getInnerData();
        }
    }

    @Override
    public int bisect(double value, int from, int length) {
        return SequenceUtils.bisect(dataSequence, PrimitiveUtils.roundDouble2double(value), from, length);
    }

    @Override
    public int[] sort(int from, int length, boolean isParallel) {
        return SequenceUtils.sort(dataSequence, from, length, isParallel);
    }


    @Override
    public IntSequence group(double interval, IntWrapper length) {
        return group(new DoubleIntervalProvider(PrimitiveUtils.roundDouble2double(interval)), length);
    }

    @Override
    public IntSequence group(TimeInterval timeInterval, IntWrapper length) {
        return group(new TimeIntervalProvider(timeInterval), length);
    }

    private IntSequence group(IntervalProvider intervalProvider, IntWrapper length) {
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
                if (l == 0 || (groupListSize > 0 && groupIndexesList.get(groupListSize - 1) == length.getValue())) {
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

                Interval currentGroupInterval = intervalProvider.getContaining(dataSequence.get(from));
                boolean isCurrentGroupEmpty = false;
                for (int i = from + 1; i < l; i++) {
                    double data = dataSequence.get(i);
                    if (!currentGroupInterval.contains(data)) {
                        if(!isCurrentGroupEmpty) {
                            groupIndexesList.add(i);
                            currentGroupInterval = intervalProvider.getNext();
                            isCurrentGroupEmpty = true;
                        } else {
                            currentGroupInterval = intervalProvider.getContaining(data);
                            isCurrentGroupEmpty = false;

                        }
                    } else {
                        isCurrentGroupEmpty = false;
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
    private DoubleAggFunction getAggFunction(Aggregation aggregation) {
        // Capitalize the first letter of dataType string
        String type = dataType().toString().substring(0, 1).toUpperCase() + dataType().toString().substring(1);
        String functionClassName = "com.biorecorder.data.frame.impl." + type + aggregation.toString();
        try {
            return (DoubleAggFunction) (Class.forName(functionClassName)).newInstance();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    protected int groupsCount(IntSequence groupIndexes, boolean isDataAppendMode) {
        int groupsCount = groupIndexes.size() - 1;
        if (isDataAppendMode && groupsCount > 0) {
            groupsCount--;
        }
        return groupsCount;
    }

    @Override
    public Column resample(Aggregation aggregation, IntSequence groupIndexes, boolean isDataAppendMode) {
        DoubleSequence resultantSequence = new DoubleSequence() {
            private DoubleAggFunction aggFunction = getAggFunction(aggregation);
            private int lastIndex = -1;

            @Override
            public int size() {
                return groupsCount(groupIndexes, isDataAppendMode);
            }

            @Override
            public double get(int index) {
                if (index != lastIndex) {
                    aggFunction = getAggFunction(aggregation);
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
        return new DoubleColumn(resultantSequence);
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
                if (length.getValue() % points == 0) {
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


    private StatsDouble calculateStats(int from, int length) {
        double min1 = dataSequence.get(from);
        double max1 = min1;
        boolean isIncreasing1 = true;
        boolean isDecreasing1 = true;

        for (int i = 1; i < length; i++) {
            double data_i = dataSequence.get(i + from);
            min1 = (double)Math.min(min1, data_i);
            max1 = (double)Math.max(max1, data_i);
            if (isIncreasing1 || isDecreasing1) {
                double diff = (double)(data_i - dataSequence.get(i + from - 1));
                if (isDecreasing1 && diff > 0) {
                    isDecreasing1 = false;
                }
                if (isIncreasing1 && diff < 0) {
                    isIncreasing1 = false;
                }
            }
        }

        return new StatsDouble(length, min1, max1, isIncreasing1, isDecreasing1);
    }

    @Override
    public Stats stats(int length) {
        if (length <= 0) {
            String errMsg = "Data size = "+ length + ". Statistic can be calculated only if size > 0";
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
            StatsDouble statsAdditional = calculateStats(stats.count(), length - stats.count());
            double min = (double)Math.min(stats.getMin(), statsAdditional.getMin());
            double max = (double)Math.max(stats.getMax(), statsAdditional.getMax());
            double diff = (double)(dataSequence.get(stats.count) - dataSequence.get(stats.count() - 1));
            boolean isIncreasing = stats.isIncreasing() && statsAdditional.isIncreasing() && diff >= 0;
            boolean isDecreasing = stats.isDecreasing() && statsAdditional.isDecreasing() && diff <= 0;
            stats = new StatsDouble(length, min, max, isIncreasing, isDecreasing);
        }
        return stats;
    }

    class StatsDouble implements Stats {
        private int count;
        private final double min;
        private final double max;
        private final boolean isIncreasing;
        private final boolean isDecreasing;

        public StatsDouble(int count, double min, double max, boolean isIncreasing, boolean isDecreasing) {
            this.count = count;
            this.min = min;
            this.max = max;
            this.isIncreasing = isIncreasing;
            this.isDecreasing = isDecreasing;
        }

        double getMin() {
            return min;
        }

        double getMax() {
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

    class DoubleIntervalProvider implements IntervalProvider {
        double interval;
        double currentIntervalStart;

        public DoubleIntervalProvider(double interval) {
            this.interval = interval;
            currentIntervalStart = 0;
        }

        @Override
        public Interval getContaining(double value) {
            double castedValue = PrimitiveUtils.roundDouble2double(value);
            currentIntervalStart = (double) (PrimitiveUtils.round(castedValue / interval) * interval);
            if (currentIntervalStart > value) {
                currentIntervalStart -= interval;
            }
            return new DoubleInterval(currentIntervalStart, (double)(currentIntervalStart + interval));

        }

        @Override
        public Interval getNext() {
            currentIntervalStart += interval;
            return new DoubleInterval(currentIntervalStart, (double)(currentIntervalStart + interval));

        }

        @Override
        public Interval getPrevious() {
            currentIntervalStart -= interval;
            return new DoubleInterval(currentIntervalStart, (double)(currentIntervalStart + interval));
        }
    }

    class DoubleInterval implements Interval {
        private final double start;
        private final double nextIntervalStart;

        public DoubleInterval(double start, double nextIntervalStart) {
            this.start = start;
            this.nextIntervalStart = nextIntervalStart;
        }

        //As we will use methods contains only on INCREASING data
        //we do only one check (value < nextIntervalStart) instead of both
        @Override
        public boolean contains(byte value) {
            // return value >= currentIntervalStart && value < nextIntervalStart;
            return value < nextIntervalStart;
        }

        @Override
        public boolean contains(short value) {
            return value < nextIntervalStart;
        }

        @Override
        public boolean contains(int value) {
            return value < nextIntervalStart;
        }

        @Override
        public boolean contains(long value) {
            return value < nextIntervalStart;
        }

        @Override
        public boolean contains(float value) {
            return value < nextIntervalStart;
        }

        @Override
        public boolean contains(double value) {
            return value < nextIntervalStart;
        }

    }
}
