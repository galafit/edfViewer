package com.biorecorder.data.frame;
import java.util.Calendar;

public class TimeIntervalProvider implements IntervalProvider {
    private Calendar calendar;
    private int weekField = Calendar.WEEK_OF_YEAR;
    private int[] calendarFields = {Calendar.MILLISECOND, Calendar.SECOND, Calendar.MINUTE,
            Calendar.HOUR_OF_DAY,  weekField, Calendar.DAY_OF_MONTH, Calendar.MONTH, Calendar.YEAR};
    private int calendarFieldIndex;

    private TimeInterval timeInterval;
    

    public TimeIntervalProvider(TimeInterval timeInterval) {
        this.timeInterval = timeInterval;
        switch (timeInterval.getTimeUnit()) {
            case MILLISECOND:
                calendarFieldIndex = 0;
                break;

            case SECOND:
                calendarFieldIndex = 1;
                break;

            case MINUTE:
                calendarFieldIndex = 2;
                break;

            case HOUR:
                calendarFieldIndex = 3;
                break;

            case WEEK:
                calendarFieldIndex = 4;
                break;

            case DAY:
                calendarFieldIndex = 5;
                break;

            case MONTH:
                calendarFieldIndex = 6;
                break;

            case YEAR:
                calendarFieldIndex = 7;
                break;

        }
        calendar = Calendar.getInstance();
    }

    private LongInterval createInterval() {
        long intervalStart = calendar.getTimeInMillis();
        calendar.add(calendarFields[calendarFieldIndex], timeInterval.getTimeUnitMultiplier());
        long nextIntervalStart = calendar.getTimeInMillis();
        calendar.add(calendarFields[calendarFieldIndex], -timeInterval.getTimeUnitMultiplier());
        return new LongInterval(intervalStart, nextIntervalStart);
    }

    public TimeInterval getTimeInterval() {
        return timeInterval;
    }

    public long getCurrentIntervalStartMs() {
        return calendar.getTimeInMillis();
    }

    @Override
    public Interval getContaining(double value) {
        long longValue = (long) value;
        calendar.setTimeInMillis(longValue);
        int calendarField = calendarFields[calendarFieldIndex];
        if(calendarField == weekField) {
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        } else {
            int calendarFieldValue = calendar.get(calendarField);
            calendarFieldValue = (calendarFieldValue / timeInterval.getTimeUnitMultiplier()) * timeInterval.getTimeUnitMultiplier();
            calendar.set(calendarField, calendarFieldValue);
        }
        for (int i = 0; i < calendarFieldIndex; i++) {
            if(calendarFields[i] == weekField ) {
                // do nothing
            } else if(calendarFields[i] == Calendar.DAY_OF_MONTH) {
                calendar.set(Calendar.DAY_OF_MONTH, 1);
            } else {
                calendar.set(calendarFields[i], 0);
            }
        }
        return createInterval();
    }

    @Override
    public Interval getNext() {
        calendar.add(calendarFields[calendarFieldIndex], timeInterval.getTimeUnitMultiplier());
        return createInterval();
    }

    @Override
    public Interval getPrevious() {
        calendar.add(calendarFields[calendarFieldIndex], -timeInterval.getTimeUnitMultiplier());
        return createInterval();
    }

    class LongInterval implements Interval {
        private final long start;
        private final long nextIntervalStart;

        public LongInterval(long start, long nextIntervalStart) {
            this.start = start;
            this.nextIntervalStart = nextIntervalStart;
        }
        
        /*
        * As we will use methods contains only on INCREASING data
        * we do only one check (value < nextIntervalStart) instead of both
        */
        @Override
        public boolean contains(byte value) {
            // return value >= start && value < nextIntervalStart;
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
