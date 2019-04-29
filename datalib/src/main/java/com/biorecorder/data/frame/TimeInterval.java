package com.biorecorder.data.frame;

import java.util.Calendar;


public class TimeInterval implements Interval {
    Calendar calendar;
    int weekField = Calendar.WEEK_OF_YEAR;
    int[] calendarFields = {Calendar.MILLISECOND, Calendar.SECOND, Calendar.MINUTE,
            Calendar.HOUR_OF_DAY,  weekField, Calendar.DAY_OF_MONTH, Calendar.MONTH, Calendar.YEAR};
    int calendarFieldIndex;
    TimeUnit unit;
    int unitMultiplier;
    long intervalStart;
    long nextIntervalStart;
    // if isDataIncreasing only one check will be done in contains(value)
    boolean isDataIncreasing;

    public TimeInterval(TimeUnit unit, int unitMultiplier,  boolean isDataIncreasing) {
        this(unit, unitMultiplier, 0, isDataIncreasing);
    }

    public TimeInterval(TimeUnit unit, int unitMultiplier, double value, boolean isDataIncreasing) {
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

        this.isDataIncreasing = isDataIncreasing;
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
        goContaining(value);
    }

    private void updateStarts() {
        intervalStart = calendar.getTimeInMillis();
        calendar.add(calendarFields[calendarFieldIndex], unitMultiplier);
        nextIntervalStart = calendar.getTimeInMillis();
        calendar.add(calendarFields[calendarFieldIndex], -unitMultiplier);
    }

    @Override
    public void goContaining(double value) {
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
        updateStarts();
    }

    @Override
    public void goNext() {
        calendar.add(calendarFields[calendarFieldIndex], unitMultiplier);
        updateStarts();
    }

    @Override
    public void goPrevious() {
        calendar.add(calendarFields[calendarFieldIndex], -unitMultiplier);
        updateStarts();
    }

    public boolean contains(long value) {
        if(isDataIncreasing) {
            return value < nextIntervalStart;
        }
        return value >= intervalStart && value < nextIntervalStart;
    }

    @Override
    public String toString() {
        return calendar.getTime().toString();
    }

}
