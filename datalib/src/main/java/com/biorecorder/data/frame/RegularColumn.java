package com.biorecorder.data.frame;

import com.biorecorder.basechart.BRange;
import com.biorecorder.data.aggregation.AggregateFunction;
import com.biorecorder.data.sequence.DoubleSequence;
import com.biorecorder.data.sequence.IntSequence;

/**
 * Created by galafit on 20/1/19.
 */
public class RegularColumn implements Column {
    private double startValue;
    private double step;
    private int size;
    private boolean isInt;

    public RegularColumn(double startValue, double step) {
        this(startValue, step, Integer.MAX_VALUE);
    }

    public RegularColumn(double startValue, double step, int size) {
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
        return DataType.NUMBER;
    }

    @Override
    public Column slice(int from, int length) {
        return new RegularColumn(value(from), step, length);
    }

    @Override
    public Column view(int from, int length) {
        return slice(from, length);
    }

    @Override
    public void cache(int nLastExcluded) {
        // do nothing
    }

    @Override
    public void disableCaching() {
       // do nothing
    }

    @Override
    public int nearest(double value, int from, int length) {
        int index = (int) ((value - value(0)) / step);
        if(index < from) {
            return from - 1;
        } else if(index > from + length) {
            index = from + length + 1;
        }
        return index;
    }

    @Override
    public IntSequence group(double interval) {
        return null;
    }

    public Column aggregate(AggregateFunction aggregateFunction, int numberOfPoints) {
        switch (aggregateFunction) {
            case MIN:
            case FIRST: {
                double startNew = startValue;
                double stepNew = step * numberOfPoints;
                return new RegularColumn(startNew, stepNew);
            }
            case MAX:
            case LAST: {
                double startNew = startValue + step * numberOfPoints;
                double stepNew = step * numberOfPoints;
                return new RegularColumn(startNew, stepNew);
            }
            case COUNT: {
                double startNew = numberOfPoints;
                double stepNew = 0;
                return new RegularColumn(startNew, stepNew);
            }
            case SUM:{
                double startNew = sum(0, numberOfPoints);
                double stepNew = step * numberOfPoints * numberOfPoints;
                return new RegularColumn(startNew, stepNew);
            }
            case AVERAGE:{
                double startNew = avg(0, numberOfPoints);
                double stepNew = step * numberOfPoints;
                return new RegularColumn(startNew, stepNew);
            }
            default:
                return null;
        }
    }

    @Override
    public Column aggregate(AggregateFunction aggregateFunction, IntSequence groupIndexes) {
        switch (aggregateFunction) {
            case MIN:
            case FIRST: {
                DoubleSequence resultantSequence = new DoubleSequence() {
                    @Override
                    public int size() {
                        return groupIndexes.size() - 1;
                    }

                    @Override
                    public double get(int index) {
                        return value(groupIndexes.get(index));
                    }
                };
                return null;
            }
            case MAX:
            case LAST: {
                DoubleSequence resultantSequence = new DoubleSequence() {
                    @Override
                    public int size() {
                        return groupIndexes.size() - 1;
                    }

                    @Override
                    public double get(int index) {
                        return value(groupIndexes.get(index + 1) - 1);
                    }
                };
                return null;
            }
            case COUNT: {
                DoubleSequence resultantSequence = new DoubleSequence() {
                    @Override
                    public int size() {
                        return groupIndexes.size() - 1;
                    }

                    @Override
                    public double get(int index) {
                        return groupIndexes.get(index + 1) - groupIndexes.get(index);
                    }
                };
                return null;
            }
            case SUM: {
                DoubleSequence resultantSequence = new DoubleSequence() {
                    @Override
                    public int size() {
                        return groupIndexes.size() - 1;
                    }

                    @Override
                    public double get(int index) {
                        return sum(groupIndexes.get(index), groupIndexes.get(index + 1) - groupIndexes.get(index));
                    }
                };
                return null;
            }
            case AVERAGE: {
                DoubleSequence resultantSequence = new DoubleSequence() {
                    @Override
                    public int size() {
                        return groupIndexes.size() - 1;
                    }

                    @Override
                    public double get(int index) {
                        return avg(groupIndexes.get(index), groupIndexes.get(index + 1) - groupIndexes.get(index));
                    }
                };
                return null;
            }
            default:
                return null;
        }
    }

    private double sum(int from, int length) {
        return (value(from) + value(from + length - 1)) * length / 2;
    }

    private double avg(int from, int length) {
        return sum(from, length) / length;
    }

    @Override
    public BRange range(int from, int length) {
        return new BRange(value(from), value(from + length));
    }
}
