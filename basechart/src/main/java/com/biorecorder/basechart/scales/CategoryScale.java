package com.biorecorder.basechart.scales;

import com.biorecorder.basechart.axis.LabelPrefixAndSuffix;
import com.biorecorder.basechart.utils.NormalizedNumber;
import com.biorecorder.data.sequence.StringSequence;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

/**
 * Created by galafit on 17/4/19.
 */
public class CategoryScale extends LinearScale {
    private StringSequence labels;

    public CategoryScale() {
    }

    public CategoryScale(StringSequence labels) {
        this.labels = labels;
    }

    public CategoryScale(List<String> labels1) {
        this.labels = new StringSequence() {
            @Override
            public int size() {
                return labels1.size();
            }

            @Override
            public String get(int index) {
                return labels1.get(index);
            }
        };
    }

    public StringSequence getLabels() {
        return labels;
    }

    public void setLabels(StringSequence labels) {
        this.labels = labels;
    }

    @Override
    public Scale copy() {
        CategoryScale copyScale = new CategoryScale(labels);
        copyScale.setDomain(getDomain());
        copyScale.setRange(getRange());
        return copyScale;
    }


    @Override
    public String formatDomainValue(double value) {
        long longValue = Math.round(value);
        int labelIndex = (int)longValue;
        if(labels != null &&  value == labelIndex && labelIndex >= 0 && labelIndex < labels.size()) {
            return labels.get(labelIndex);
        }
        return super.formatDomainValue(value);
    }

    @Override
    public TickProvider getTickProviderByIntervalCount(int tickIntervalCount, LabelPrefixAndSuffix formatInfo) {
        CategoryTickProvider tickProvider =  new CategoryTickProvider();
        tickProvider.setTickIntervalCount(tickIntervalCount);
        return tickProvider;
    }

    @Override
    public TickProvider getTickProviderByInterval(double tickInterval, LabelPrefixAndSuffix formatInfo) {
        CategoryTickProvider tickProvider =  new CategoryTickProvider();
        tickProvider.setTickInterval(tickInterval);
        return tickProvider;
    }


    class CategoryTickProvider implements TickProvider {
        long currentValue;
        int  step = 1;

        public void setTickInterval(double tickInterval) {
            step = Math.max(1, (int)tickInterval);
        }

        public void setTickIntervalCount(double tickIntervalCount) {
            if (tickIntervalCount < 1) {
                String errMsg = MessageFormat.format("Invalid tick interval count: {0}. Expected >= 2", tickIntervalCount);
                throw new IllegalArgumentException(errMsg);
            }
            double max = domain[domain.length - 1];
            double min = domain[0];
            double interval = (max - min) / tickIntervalCount;
            setTickInterval(interval);
        }


        @Override
        public Tick getUpperTick(double value) {
            currentValue = (long) value;
            currentValue = (currentValue / step) * step;
            if(currentValue < value) {
                currentValue += step;
            }
            return new Tick(currentValue, formatDomainValue(currentValue));
        }

        @Override
        public Tick getLowerTick(double value) {
            currentValue = (long) value;
            currentValue = (currentValue / step) * step;
            return new Tick(currentValue, formatDomainValue(currentValue));
        }

        @Override
        public Tick getNextTick() {
            currentValue += step;
            return new Tick(currentValue, formatDomainValue(currentValue));
        }

        @Override
        public Tick getPreviousTick() {
            currentValue -= step;
            return new Tick(currentValue, formatDomainValue(currentValue));
        }

        @Override
        public void increaseTickInterval(int increaseFactor) {
           step *= increaseFactor;
        }
    }

}
