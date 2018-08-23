package com.biorecorder.basechart.scales;

import com.biorecorder.basechart.axis.TickFormatInfo;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;

/**
 * Created by galafit on 7/1/18.
 */
public class TimeScale implements Scale {
    int MSECOND = 1; //milliseconds
    int MSECONDS_2 = 2;
    int MSECONDS_5 = 5;
    int MSECONDS_10 = 10;
    int MSECONDS_20 = 20;
    int MSECONDS_50 = 50;
    int MSECONDS_100 = 100;
    int MSECONDS_200 = 200;
    int MSECONDS_500 = 500;
    int SECOND = 1000;
    int SECONDS_2 = 2 * SECOND;
    int SECONDS_5 = 5 * SECOND;
    int SECONDS_10 = 10 * SECOND;
    int SECONDS_30 = 30 * SECOND;
    int MINUTE = 60 * SECOND;
    int MINUTES_2 = 2 * MINUTE;
    int MINUTES_5 = 5 * MINUTE;
    int MINUTES_10 = 10 * MINUTE;
    int MINUTES_30 = 30 * MINUTE;
    int HOUR = 60 * MINUTE;
    int HOURS_2 = 2 * HOUR;
    int HOURS_5 = 5 * HOUR;

    int[] TIME_INTERVALS = {MSECOND, MSECONDS_2, MSECONDS_5, MSECONDS_10, MSECONDS_20, MSECONDS_50,
            MSECONDS_100, MSECONDS_200, MSECONDS_500, SECOND, SECONDS_2, SECONDS_5, SECONDS_10,
            SECONDS_30, MINUTE, MINUTES_2, MINUTES_5, MINUTES_10, MINUTES_30, HOUR, HOURS_2, HOURS_5};

    private long domain[] = {0, 1};
    private double range[] = {0, 1};
    private DateFormat numberFormatter;

    @Override
    public Scale copy() {
        TimeScale copyScale = new TimeScale();
        copyScale.domain = Arrays.copyOf(domain, domain.length);
        copyScale.range = Arrays.copyOf(range, range.length);
        return copyScale;
    }

    @Override
    public void setDomain(double... domain) {
        this.domain = new long[domain.length];
        for (int i = 0; i < domain.length; i++) {
            this.domain[i] = (long) domain[i];
        }
        numberFormatter = null;
    }

    @Override
    public void setRange(double... range) {
        this.range = Arrays.copyOf(range, range.length);
        numberFormatter = null;
    }

    @Override
    public double[] getDomain() {
        double domainCopy[] = new double[domain.length];
        for (int i = 0; i < domain.length; i++) {
           domainCopy[i] = domain[i];
        }
        return domainCopy;
    }

    @Override
    public double[] getRange() {
        return Arrays.copyOf(range, range.length);
    }

    @Override
    public double scale(double value) {
        return (float)(range[0] + (value - domain[0]) * (range[range.length - 1] - range[0]) / (domain[domain.length - 1] - domain[0]));
    }

    @Override
    public double invert(double value) {
        return domain[0] + (value - range[0]) * (domain[domain.length - 1] - domain[0]) / (range[range.length - 1] - range[0]);
    }

    @Override
    public TickProvider getTickProviderByCount(int tickCount, TickFormatInfo labelFormatInfo) {
        TimeTickProvider provider = new TimeTickProvider();
        provider.setTickCount(tickCount);
        return provider;
    }

    @Override
    public  TickProvider getTickProviderByStep(double tickStep,  TickFormatInfo labelFormatInfo) {
        TimeTickProvider provider = new TimeTickProvider();
        provider.setTickStep(tickStep);
        return provider;
    }

    @Override
    public String formatDomainValue(double value) {
        if (numberFormatter == null) {
            float rangePointsCount = (int)Math.abs(range[range.length - 1] - range[0]) + 1;
            double domainLength = domain[domain.length - 1] - domain[0];
            long pointStep = (long)(domainLength / rangePointsCount);
            numberFormatter = getDateFormat(pointStep);
        }
        return numberFormatter.format(value);
    }

    private DateFormat getDateFormat(long step) {
        String DATE_FORMAT_MSEC = "HH:mm:ss.SSS";
        String DATE_FORMAT_SEC = "HH:mm:ss";
        String DATE_FORMAT_MIN = "HH:mm";
        String DATE_FORMAT_HOUR = "HH";
        DateFormat format = new SimpleDateFormat(DATE_FORMAT_MSEC);
        if (step >= SECOND) {
            format = new SimpleDateFormat(DATE_FORMAT_SEC);
        }
        if (step >= MINUTE) {
            format = new SimpleDateFormat(DATE_FORMAT_MIN);
        }
        if (step >= HOUR) {
            format = new SimpleDateFormat(DATE_FORMAT_HOUR);
        }
        return format;
    }


    class TimeTickProvider implements TickProvider {
        private long tickStep = 1;
        private DateFormat dateFormat;
        private Tick lastTick;

        public void setTickStep(double tickStep) {
            this.tickStep = (long) tickStep;
            dateFormat = getDateFormat(this.tickStep);
        }

        public void setTickCount(int tickCount) {
            tickStep = getRoundTickStep(tickCount);
            dateFormat = getDateFormat(tickStep);
        }

        @Override
        public void increaseTickStep(int increaseFactor) {
            tickStep *= increaseFactor;
            dateFormat = getDateFormat(tickStep);
        }

        @Override
        public Tick getNextTick() {
            if (lastTick == null) {
                long min = domain[0];
                lastTick = getLowerTick(min);
            } else {
                double tickValue = lastTick.getValue() + tickStep;
                lastTick = new Tick(tickValue, dateFormat.format(tickValue));
            }
            return lastTick;
        }

        @Override
        public Tick getPreviousTick() {
            if (lastTick == null) {
                double min = domain[0];
                lastTick = getLowerTick(min);
            } else {
                double tickValue = lastTick.getValue() - tickStep;
                lastTick = new Tick(tickValue, dateFormat.format(tickValue));
            }
            return lastTick;
        }

        @Override
        public Tick getUpperTick(double value) {
            double tickValue = Math.ceil(value / tickStep) * tickStep;
            lastTick = new Tick(tickValue, dateFormat.format(tickValue));
            return lastTick;
        }

        @Override
        public Tick getLowerTick(double value) {
            double tickValue = Math.floor(value / tickStep) * tickStep;
            lastTick = new Tick(tickValue, dateFormat.format(tickValue));
            return lastTick;
        }


        /**
         * On the com.biorecorder.basechart.chart of ticks amount choose the closest tickStep
         * from TIME_INTERVALS
         */
        private long getRoundTickStep(int tickCount) {
            if (tickCount <= 1) {
                String errMsg = MessageFormat.format("Invalid ticks tickCount: {0}. Expected >= 2", tickCount);
                throw new IllegalArgumentException(errMsg);
            }
            long max = (long) domain[domain.length - 1];
            long min = (long) domain[0];
            long step = (max - min) / (tickCount - 1);
            for (int i = 0; i < TIME_INTERVALS.length - 1; i++) {
                if(step >= TIME_INTERVALS[i] && step <= TIME_INTERVALS[i + 1]) {
                    if(step - TIME_INTERVALS[i] <= TIME_INTERVALS[i + 1] - step) {
                        step = TIME_INTERVALS[i];
                    } else {
                        step = TIME_INTERVALS[i + 1];
                    }
                }
            }
            if(step > TIME_INTERVALS[TIME_INTERVALS.length - 1]) {
                step = TIME_INTERVALS[TIME_INTERVALS.length - 1];
            }
            return step;
        }
    }
}
