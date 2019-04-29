package com.biorecorder.data.frame;

import com.biorecorder.data.utils.PrimitiveUtils;

import java.util.Calendar;

/**
 * TODO: add isIncreasing and isDecreasing sequence to make only one check in contains(value)
 */
public class TimeInterval implements Interval {
    Calendar calendar;
    int weekField = Calendar.WEEK_OF_YEAR;
    int[] calendarFields = {Calendar.MILLISECOND, Calendar.SECOND, Calendar.MINUTE,
            Calendar.HOUR_OF_DAY,  weekField, Calendar.DAY_OF_MONTH, Calendar.MONTH, Calendar.YEAR};
    int calendarFieldIndex;
    Unit unit;
    int unitCount;
    long intervalStart;
    long nextIntervalStart;

    public TimeInterval(int unitCount, Unit unit, double value) {
        int[] allowedValues = unit.getAllowedMultiples();
        if(allowedValues != null && allowedValues.length > 0) {
            unitCount = Math.min(unitCount, allowedValues[allowedValues.length - 1]);
            for (int v : allowedValues) {
                if(unitCount <= v) {
                    unitCount = v;
                    break;
                }
            }
        }

        this.unitCount = unitCount;
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
        calendar.add(calendarFields[calendarFieldIndex], unitCount);
        nextIntervalStart = calendar.getTimeInMillis();
        calendar.add(calendarFields[calendarFieldIndex], -unitCount);
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
            calendarFieldValue = (calendarFieldValue / unitCount) * unitCount;
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
        calendar.add(calendarFields[calendarFieldIndex], unitCount);
        updateStarts();
    }

    @Override
    public void goPrevious() {
        calendar.add(calendarFields[calendarFieldIndex], -unitCount);
        updateStarts();
    }

    public boolean contains(long value) {
        if(value >= intervalStart && value < nextIntervalStart) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return calendar.getTime().toString();
    }

}
