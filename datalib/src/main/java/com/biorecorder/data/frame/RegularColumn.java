package com.biorecorder.data.frame;


import com.biorecorder.data.frame.impl.IntColumn;
import com.biorecorder.data.sequence.IntSequence;

/**
 * Created by galafit on 20/1/19.
 */
public class RegularColumn extends IntColumn {
    private double startValue;
    private double step;
    private int size;
    private boolean isInt;

    public RegularColumn(double startValue, double step) {
        this(startValue, step, Integer.MAX_VALUE);
    }

    public RegularColumn(double startValue, double step, int size) {
        super(new IntSequence() {
            @Override
            public int size() {
                return size;
            }

            @Override
            public int get(int index) {
                return (int)(startValue + step * index);
            }
        });
        this.startValue = startValue;
        this.step = step;
        this.size = size;
        if((long) startValue == startValue && (long) step == step) {
            isInt = true;
        }
    }

    public double getStartValue() {
        return startValue;
    }

    public double getStep() {
        return step;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public double value(int index) {
        return startValue + step * index;
    }

    @Override
    public String label(int index) {
        if(isInt) {
            return Long.toString((long)value(index));
        } else {
           return Double.toString(value(index));
        }
    }

    @Override
    public DataType dataType() {
        return DataType.DOUBLE;
    }

    @Override
    public int bisect(double value, int from, int length) {
        int index = (int) ((value - value(0)) / step);
        if(value(index) != value) { //to maintain sorted order
            index++;
        }
        if(index < from) {
            return from;
        } else if(index > from + length) {
            index = from + length;
        }
        return index;
    }

    @Override
    public Stats stats(int length) {
        return new Stats() {
            @Override
            public double min() {
                return value(0);
            }

            @Override
            public double max() {
                return value(length - 1);
            }

            @Override
            public boolean isIncreasing() {
                return true;
            }

            @Override
            public boolean isDecreasing() {
                return false;
            }
        };
    }

    @Override
    public int[] sort(int from, int length, boolean isParallel) {
        int[] orderedIndexes = new int[length];
        for (int i = 0; i < length; i++) {
            orderedIndexes[i] = i + from;
        }
        return orderedIndexes;
    }

    @Override
    public Column slice(int from, int length) {
        return new RegularColumn(value(from), step, length);
    }

    @Override
    public Column view(int from, int length) {
        return new RegularColumn(value(from), step, length);
    }

    @Override
    public Column aggregate(Aggregation aggregation, int points, IntWrapper length, boolean isDataAppendMode) throws IllegalArgumentException {
        int sizeNew = size/points;
        switch (aggregation) {
            case MIN:
            case FIRST: {
                double startNew = startValue;
                double stepNew = step * points;
                return new RegularColumn(startNew, stepNew, sizeNew);
            }
            case MAX:
            case LAST: {
                double startNew = startValue + step * points;
                double stepNew = step * points;
                return new RegularColumn(startNew, stepNew, sizeNew);
            }
            case COUNT: {
                double startNew = points;
                double stepNew = 0;
                return new RegularColumn(startNew, stepNew, sizeNew);
            }
            case SUM:{
                double startNew = sum(0, points);
                double stepNew = step * points * points;
                return new RegularColumn(startNew, stepNew, sizeNew);
            }
            case AVERAGE:{
                double startNew = avg(0, points);
                double stepNew = step * points;
                return new RegularColumn(startNew, stepNew, sizeNew);
            }
            default:
                String errMsg = "Unsupported Aggregate function: "+ aggregation;
                throw new IllegalArgumentException(errMsg);
        }
    }

    @Override
    public Column aggregate(Aggregation aggregateFunction, IntSequence groupIndexes, boolean isDataAppendMode) throws IllegalArgumentException {
        switch (aggregateFunction) {
            case MIN:
            case FIRST: {
                IntSequence resultantSequence = new IntSequence() {
                    @Override
                    public int size() {
                       return groupsCount(groupIndexes, isDataAppendMode);
                    }

                    @Override
                    public int get(int index) {
                        return (int)value(groupIndexes.get(index));
                    }
                };
                return new IntColumn(resultantSequence);
            }
            case MAX:
            case LAST: {
                IntSequence resultantSequence = new IntSequence() {
                    @Override
                    public int size() {
                        return groupsCount(groupIndexes, isDataAppendMode);
                    }

                    @Override
                    public int get(int index) {
                        return (int)value(groupIndexes.get(index + 1) - 1);
                    }
                };
                return new IntColumn(resultantSequence);
            }
            case COUNT: {
                IntSequence resultantSequence = new IntSequence() {
                    @Override
                    public int size() {
                        return groupsCount(groupIndexes, isDataAppendMode);
                    }

                    @Override
                    public int get(int index) {
                        return groupIndexes.get(index + 1) - groupIndexes.get(index);
                    }
                };
                return new IntColumn(resultantSequence);
            }
            case SUM: {
                IntSequence resultantSequence = new IntSequence() {
                    @Override
                    public int size() {
                        return groupsCount(groupIndexes, isDataAppendMode);
                    }

                    @Override
                    public int get(int index) {
                        return (int)sum(groupIndexes.get(index), groupIndexes.get(index + 1) - groupIndexes.get(index));
                    }
                };
                return new IntColumn(resultantSequence);
            }
            case AVERAGE: {
                IntSequence resultantSequence = new IntSequence() {
                    @Override
                    public int size() {
                        return groupsCount(groupIndexes, isDataAppendMode);
                    }

                    @Override
                    public int get(int index) {
                        return (int)avg(groupIndexes.get(index), groupIndexes.get(index + 1) - groupIndexes.get(index));
                    }
                };
                return new IntColumn(resultantSequence);
            }
            default:
                String errMsg = "Unsupported Aggregate function: "+aggregateFunction;
                throw new IllegalArgumentException(errMsg);
        }
    }

    private double sum(int from, int length) {
        return (value(from) + value(from + length - 1)) * length / 2;
    }

    private double avg(int from, int length) {
        return sum(from, length) / length;
    }

}
