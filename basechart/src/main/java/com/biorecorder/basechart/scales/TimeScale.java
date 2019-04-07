package com.biorecorder.basechart.scales;

import com.biorecorder.basechart.axis.LabelPrefixAndSuffix;
import com.biorecorder.data.utils.NormalizedNumber;

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
    public TickProvider getTickProviderByIntervalCount(int tickIntervalCount, LabelPrefixAndSuffix labelFormatInfo) {
        TimeTickProvider provider = new TimeTickProvider();
        provider.setTickIntervalCount(tickIntervalCount);
        return provider;
    }

    @Override
    public  TickProvider getTickProviderByInterval(double tickInterval, LabelPrefixAndSuffix labelFormatInfo) {
        TimeTickProvider provider = new TimeTickProvider();
        provider.setTickInterval(tickInterval);
        return provider;
    }

    @Override
    public String formatDomainValue(double value) {
        if (numberFormatter == null) {
            float rangePointsCount = (int)Math.abs(range[range.length - 1] - range[0]) + 1;
            double domainLength = domain[domain.length - 1] - domain[0];
            long pointInterval = (long)(domainLength / rangePointsCount);
            numberFormatter = getDateFormat(pointInterval);
        }
        return numberFormatter.format(value);
    }

    private DateFormat getDateFormat(long Interval) {
        String DATE_FORMAT_MSEC = "HH:mm:ss.SSS";
        String DATE_FORMAT_SEC = "HH:mm:ss";
        String DATE_FORMAT_MIN = "HH:mm";
        String DATE_FORMAT_HOUR = "HH";
        DateFormat format = new SimpleDateFormat(DATE_FORMAT_MSEC);
        if (Interval >= SECOND) {
            format = new SimpleDateFormat(DATE_FORMAT_SEC);
        }
        if (Interval >= MINUTE) {
            format = new SimpleDateFormat(DATE_FORMAT_MIN);
        }
        if (Interval >= HOUR) {
            format = new SimpleDateFormat(DATE_FORMAT_HOUR);
        }
        return format;
    }


    class TimeTickProvider implements TickProvider {
        private long tickInterval = 1;
        private DateFormat dateFormat;
        private Tick lastTick;

        public void setTickInterval(double tickInterval) {
            this.tickInterval = (long) tickInterval;
            dateFormat = getDateFormat(this.tickInterval);
        }

        public void setTickIntervalCount(int tickIntervalCount) {
            tickInterval = getRoundTickInterval(tickIntervalCount);
            dateFormat = getDateFormat(tickInterval);
        }

        @Override
        public void increaseTickInterval(int increaseFactor) {
            tickInterval *= increaseFactor;
            dateFormat = getDateFormat(tickInterval);
        }

        @Override
        public Tick getNextTick() {
            if (lastTick == null) {
                long min = domain[0];
                lastTick = getLowerTick(min);
            } else {
                long tickValue = lastTick.getTickValue().getDigits() + tickInterval;
                lastTick = new Tick(new NormalizedNumber(tickValue, 0), dateFormat.format(tickValue));
            }
            return lastTick;
        }

        @Override
        public Tick getPreviousTick() {
            if (lastTick == null) {
                long min = domain[0];
                lastTick = getLowerTick(min);
            } else {
                long tickValue = lastTick.getTickValue().getDigits() - tickInterval;
                lastTick = new Tick(new NormalizedNumber(tickValue, 0), dateFormat.format(tickValue));
            }
            return lastTick;
        }

        @Override
        public Tick getUpperTick(double value) {
            long tickValue = (long) (Math.ceil(value / tickInterval) * tickInterval);
            lastTick = new Tick(new NormalizedNumber(tickValue, 0), dateFormat.format(tickValue));
            return lastTick;
        }

        @Override
        public Tick getLowerTick(double value) {
            long tickValue = (long) (Math.floor(value / tickInterval) * tickInterval);
            lastTick = new Tick(new NormalizedNumber(tickValue, 0), dateFormat.format(tickValue));
            return lastTick;
        }


        /**
         * Choose the closest tickInterval
         * from TIME_INTERVALS
         */
        private long getRoundTickInterval(int tickIntervalCount) {
            if (tickIntervalCount < 1) {
                String errMsg = MessageFormat.format("Invalid tick interval count: {0}. Expected >= 2", tickIntervalCount);
                throw new IllegalArgumentException(errMsg);
            }
            long max =  domain[domain.length - 1];
            long min =  domain[0];
            long interval = (max - min) / tickIntervalCount;
            if(interval <= 0) {
                interval = 1;
            }
            for (int i = 0; i < TIME_INTERVALS.length - 1; i++) {
                if(interval >= TIME_INTERVALS[i] && interval <= TIME_INTERVALS[i + 1]) {
                    if(interval - TIME_INTERVALS[i] <= TIME_INTERVALS[i + 1] - interval) {
                        interval = TIME_INTERVALS[i];
                    } else {
                        interval = TIME_INTERVALS[i + 1];
                    }
                }
            }
            if(interval > TIME_INTERVALS[TIME_INTERVALS.length - 1]) {
                interval = TIME_INTERVALS[TIME_INTERVALS.length - 1];
            }
            return interval;
        }
    }
}
