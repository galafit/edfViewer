package com.biorecorder.basechart.scales;

import com.biorecorder.data.frame.TimeInterval;
import com.biorecorder.basechart.axis.LabelPrefixAndSuffix;
import com.biorecorder.data.frame.TimeIntervalProvider;
import com.biorecorder.data.frame.TimeUnit;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by galafit on 7/1/18.
 */
public class TimeScale extends LinearScale {
    private DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss.SSS dd-MMM-yyyy ");

    @Override
    public Scale copy() {
        TimeScale copyScale = new TimeScale();
        copyScale.setDomain(getDomain());
        copyScale.setRange(getRange());
        return copyScale;
    }


    @Override
    public TickProvider getTickProviderByIntervalCount(int tickIntervalCount, LabelPrefixAndSuffix labelFormatInfo) {
        TimeTickProvider provider = new TimeTickProvider();
        provider.setTickIntervalCount(tickIntervalCount);
        return provider;
    }

    @Override
    public TickProvider getTickProviderByInterval(double tickInterval, LabelPrefixAndSuffix labelFormatInfo) {
        TimeTickProvider provider = new TimeTickProvider();
        provider.setTickInterval(tickInterval);
        return provider;
    }

    @Override
    public String formatDomainValue(double value) {
        return timeFormat.format((long) value);
    }



    class TimeTickProvider implements TickProvider {
        private TimeIntervalProvider timeIntervalProvider;
        private DateFormatter labelFormat;

        public void setTickInterval(double interval) {
            timeIntervalProvider = new TimeIntervalProvider(TimeInterval.getClosest(Math.round(interval), true));
            labelFormat = new DateFormatter(timeIntervalProvider.getTimeInterval().getTimeUnit());
        }

        public void setTickIntervalCount(int tickIntervalCount) {
            if (tickIntervalCount < 1) {
                tickIntervalCount = 1;
            }
            double max = domain[domain.length - 1];
            double min = domain[0];
            long interval = Math.round((max - min) / tickIntervalCount);
            timeIntervalProvider = new TimeIntervalProvider(TimeInterval.getClosest(interval, true));
            labelFormat = new DateFormatter(timeIntervalProvider.getTimeInterval().getTimeUnit());
        }

        @Override
        public void increaseTickInterval(int increaseFactor) {
            long intervalNew = timeIntervalProvider.getTimeInterval().toMilliseconds() * increaseFactor;
            setTickInterval(intervalNew);
        }

        @Override
        public Tick getNextTick() {
            timeIntervalProvider.getNext();
            return currentTick();
        }

        @Override
        public Tick getPreviousTick() {
            timeIntervalProvider.getPrevious();
            return currentTick();
        }

        @Override
        public Tick getUpperTick(double value) {
            timeIntervalProvider.getContaining(value);
            if (timeIntervalProvider.getCurrentIntervalStartMs() < value) {
                timeIntervalProvider.getNext();
            }
            return currentTick();
        }

        @Override
        public Tick getLowerTick(double value) {
            timeIntervalProvider.getContaining(value);
            return currentTick();
        }

        private Tick currentTick() {
            return new Tick(timeIntervalProvider.getCurrentIntervalStartMs(), labelFormat.format(timeIntervalProvider.getCurrentIntervalStartMs(), timeIntervalProvider.getCurrentIntervalStartHours()));
        }

        class DateFormatter {
            private final SimpleDateFormat primaryFormat;
            private final SimpleDateFormat secondaryFormat;

            public DateFormatter(TimeUnit timeUnit) {
                switch (timeUnit) {
                    case MILLISECOND:
                        primaryFormat = new SimpleDateFormat("HH:mm:ss.SSS");
                        secondaryFormat = primaryFormat;
                        break;
                    case SECOND:
                        primaryFormat = new SimpleDateFormat("HH:mm:ss");
                        secondaryFormat = primaryFormat;
                        break;
                    case MINUTE:
                        primaryFormat = new SimpleDateFormat("HH:mm");
                        secondaryFormat = primaryFormat;
                        break;
                    case HOUR:
                        primaryFormat = new SimpleDateFormat("HH:mm");
                        secondaryFormat = new SimpleDateFormat("dd. MMM");
                        break;
                    case DAY:
                        primaryFormat =  new SimpleDateFormat("dd. MMM");
                        secondaryFormat = primaryFormat;
                        break;
                    case WEEK:
                    case MONTH:
                        primaryFormat =  new SimpleDateFormat("MMM'' yy");
                        secondaryFormat = primaryFormat;
                        break;
                    default:
                        primaryFormat =  new SimpleDateFormat("yyyy");
                        secondaryFormat = primaryFormat;
                }

            }

            public String format(long ms, long hour) {
                if(hour == 0) {
                    return secondaryFormat.format(ms);
                } else {
                    return primaryFormat.format(ms);
                }
            }
        }
    }
}
