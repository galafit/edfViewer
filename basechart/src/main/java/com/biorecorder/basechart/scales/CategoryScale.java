package com.biorecorder.basechart.scales;

import com.biorecorder.basechart.axis.LabelPrefixAndSuffix;
import com.biorecorder.basechart.utils.NormalizedNumber;
import com.biorecorder.data.sequence.StringSequence;

import java.util.Arrays;
import java.util.List;

/**
 * Created by galafit on 17/4/19.
 */
public class CategoryScale extends LinearScale {
    private StringSequence labels;
    private long startValue = 0;



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
        int labelIndex = (int)(longValue - startValue);
        if(labels != null &&  labelIndex >= 0 && labelIndex < labels.size()) {
            return labels.get(labelIndex);
        }
        return String.valueOf(longValue);
    }

    @Override
    public TickProvider getTickProviderByIntervalCount(int tickIntervalCount, LabelPrefixAndSuffix formatInfo) {
        return new CategoryTickProvider();
    }

    @Override
    public TickProvider getTickProviderByInterval(double tickInterval, LabelPrefixAndSuffix formatInfo) {
        return new CategoryTickProvider();
    }


    class CategoryTickProvider implements TickProvider {
        long currentValue;
        int  step = 1;


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
