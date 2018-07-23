package com.biorecorder.basechart.data;

import com.biorecorder.basechart.Range;

/**
 * Created by galafit on 1/11/17.
 */
public class RegularColumn extends NumberColumn {
    private SeriesViewer series;

    public RegularColumn(double startValue, double dataInterval) {
        series = new SeriesViewer(new RegularSeries(startValue, dataInterval));
    }

    public RegularColumn() {
        this(0, 1);
    }

    public double getDataInterval() {
        return series.getDataInterval();
    }

    public double getStartValue() {
        return series.getStartValue();
    }

    @Override
    public long size() {
        return series.size();
    }

    @Override
    public double value(long index) {
        return series.get(index);
    }

    @Override
    public Range extremes(long length) {
        if(series.size() > 0) {
            return new Range(series.get(0), series.get(series.size() - 1));
        }
        return null;
    }

    @Override
    public long upperBound(double value, long length) {
       return series.upperBound(value);
    }

    @Override
    public long lowerBound(double value, long length) {
        return series.lowerBound(value);
    }

    @Override
    public void setCachingEnabled(boolean isCachingEnabled) {
        // do nothing
    }

    @Override
    public void clearCache() {
        // do nothing
    }

    @Override
    public boolean isCachingEnabled() {
        return false;
    }


    @Override
    public NumberColumn[] group(LongSeries groupIndexes) {
        return new NumberColumn[0];
    }

    @Override
    public void getGroupIndexes(LongArrayList buffer, double groupInterval, long from, long length) {
    }

    @Override
    public void setViewRange(long from, long length) {
        series.setViewRange(from, length);
    }

    @Override
    public NumberColumn copy() {
        return new RegularColumn(series.getStartValue(), series.getDataInterval());
    }

    class RegularSeries implements DoubleSeries {
        private double startValue;
        private double dataInterval;

        public RegularSeries(double startValue, double dataInterval) {
            this.startValue = startValue;
            this.dataInterval = dataInterval;
        }

        public double getStartValue() {
            return startValue;
        }

        public double getDataInterval() {
            return dataInterval;
        }

        @Override
        public long size() {
            return Long.MAX_VALUE;
        }

        @Override
        public double get(long index) {
            return startValue + dataInterval * index;
        }

        public long upperBound(double value) {
            long lowerBoundIndex = lowerBound(value);
            if(value == get(lowerBoundIndex)) {
                return lowerBoundIndex;
            }
            return lowerBoundIndex + 1;
        }

        public long lowerBound(double value) {
            long lowerBoundIndex = (long) ((value - startValue) / dataInterval);
            if(lowerBoundIndex < 0) {
                lowerBoundIndex = 0;
            }
            return lowerBoundIndex;
        }
    }

    class SeriesViewer implements DoubleSeriesRangeViewer {
        private RegularSeries series;
        private long startIndex = 0;
        private long length = -1;


        public SeriesViewer(RegularSeries series) {
            this.series = series;
        }

        @Override
        public void setViewRange(long startIndex, long length) {
            this.startIndex = startIndex;
            this.length = length;
        }

        @Override
        public long size() {
            if(length < 0) {
                return series.size();
            }
            return length;
        }

        @Override
        public double get(long index) {
            return series.get(index + startIndex);
        }

        public double getStartValue() {
            return get(0);
        }

        public double getDataInterval() {
            return series.getDataInterval();
        }

        public long upperBound(double value) {
           return series.upperBound(value) - startIndex;
        }


        public long lowerBound(double value) {
            return series.lowerBound(value) - startIndex;
        }
    }


}
