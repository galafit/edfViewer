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
        private DateFormat labelFormat;

        public void setTickInterval(double interval) {
            timeIntervalProvider = new TimeIntervalProvider(TimeInterval.getClosest(Math.round(interval), true));
            labelFormat = getDateFormat(timeIntervalProvider.getTimeInterval().getTimeUnit());
        }

        public void setTickIntervalCount(int tickIntervalCount) {
            if (tickIntervalCount < 1) {
                tickIntervalCount = 1;
            }
            double max = domain[domain.length - 1];
            double min = domain[0];
            long interval = Math.round((max - min) / tickIntervalCount);
            timeIntervalProvider = new TimeIntervalProvider(TimeInterval.getClosest(interval, true));
            labelFormat = getDateFormat(timeIntervalProvider.getTimeInterval().getTimeUnit());
        }

        @Override
        public void increaseTickInterval(int increaseFactor) {
            long intervalNew = timeIntervalProvider.getTimeInterval().toMilliseconds() * increaseFactor;
            setTickInterval(intervalNew);
        }

        @Override
        public Tick getNextTick() {
            timeIntervalProvider.getNext();
            return new Tick(timeIntervalProvider.getCurrentIntervalStartMs(), labelFormat.format(timeIntervalProvider.getCurrentIntervalStartMs()));
        }

        @Override
        public Tick getPreviousTick() {
            timeIntervalProvider.getPrevious();
            return new Tick(timeIntervalProvider.getCurrentIntervalStartMs(), labelFormat.format(timeIntervalProvider.getCurrentIntervalStartMs()));
        }

        @Override
        public Tick getUpperTick(double value) {
            timeIntervalProvider.getContaining(value);
            if (timeIntervalProvider.getCurrentIntervalStartMs() < value) {
                timeIntervalProvider.getNext();
            }
            return new Tick(timeIntervalProvider.getCurrentIntervalStartMs(), labelFormat.format(timeIntervalProvider.getCurrentIntervalStartMs()));
        }

        @Override
        public Tick getLowerTick(double value) {
            timeIntervalProvider.getContaining(value);
            return new Tick(timeIntervalProvider.getCurrentIntervalStartMs(), labelFormat.format(timeIntervalProvider.getCurrentIntervalStartMs()));
        }

        private DateFormat getDateFormat(TimeUnit unit) {
            switch (unit) {
                case MILLISECOND:
                    return new SimpleDateFormat("HH:mm:ss.SSS");
                case SECOND:
                    return new SimpleDateFormat("HH:mm:ss");
                case MINUTE:
                    return new SimpleDateFormat("HH:mm");
                case HOUR:
                    return new SimpleDateFormat("dd. MMM HH:mm");
                case DAY:
                    return new SimpleDateFormat("dd. MMM");
                case WEEK:
                case MONTH:
                    return new SimpleDateFormat("MMM'' yy");
                default:
                    return new SimpleDateFormat("yyyy");
            }
        }
    }
}
