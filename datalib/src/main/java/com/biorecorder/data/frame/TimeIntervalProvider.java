package com.biorecorder.data.frame;
import java.util.Calendar;

public class TimeIntervalProvider implements IntervalProvider {
    private Calendar calendar;
    private int weekField = Calendar.WEEK_OF_YEAR;
    private int[] calendarFields = {Calendar.MILLISECOND, Calendar.SECOND, Calendar.MINUTE,
            Calendar.HOUR_OF_DAY,  weekField, Calendar.DAY_OF_MONTH, Calendar.MONTH, Calendar.YEAR};
    private int calendarFieldIndex;

    private TimeUnit unit;
    private int unitMultiplier;

    public TimeIntervalProvider(com.biorecorder.data.frame.TimeInterval timeInterval) {
        this(timeInterval.getTimeUnit(), timeInterval.getUnitMultiplier());
    }

    public TimeIntervalProvider(TimeUnit unit, int unitMultiplier) {
        int[] allowedValues = unit.getAllowedMultiples();
        if(allowedValues != null && allowedValues.length > 0) {
            unitMultiplier = Math.min(unitMultiplier, allowedValues[allowedValues.length - 1]);
            for (int v : allowedValues) {
                if(unitMultiplier <= v) {
                    unitMultiplier = v;
                    break;
                }
            }
        }
        this.unitMultiplier = unitMultiplier;
        this.unit = unit;
        switch (unit) {
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

    private TimeInterval createInterval() {
        long intervalStart = calendar.getTimeInMillis();
        calendar.add(calendarFields[calendarFieldIndex], unitMultiplier);
        long nextIntervalStart = calendar.getTimeInMillis();
        calendar.add(calendarFields[calendarFieldIndex], -unitMultiplier);
        return new TimeInterval(intervalStart, nextIntervalStart);
    }

    public TimeUnit getTimeUnit() {
        return unit;
    }

    public int getUnitMultiplier() {
        return unitMultiplier;
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
            calendarFieldValue = (calendarFieldValue / unitMultiplier) * unitMultiplier;
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
        calendar.add(calendarFields[calendarFieldIndex], unitMultiplier);
        return createInterval();
    }

    @Override
    public Interval getPrevious() {
        calendar.add(calendarFields[calendarFieldIndex], -unitMultiplier);
        return createInterval();
    }

    class TimeInterval implements Interval {
        private final long intervalStart;
        private final long nextIntervalStart;

        public TimeInterval(long start, long nextIntervalStart) {
            this.intervalStart = start;
            this.nextIntervalStart = nextIntervalStart;
        }

        /*
         * As we will use methods contains only on INCREASING data
         * we do only one check (value < nextIntervalStart) instead of both
         */
        @Override
        public boolean contains(byte value) {
            // return value >= intervalStart && value < nextIntervalStart;
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
