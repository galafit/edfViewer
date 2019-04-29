package com.biorecorder.data.frame;

import java.util.Calendar;

/**
 * Created by galafit on 28/4/19.
 */
public class HourInterval implements Interval {
    Calendar calendar;
    int count;
    long start;
    long nextStart;

    public HourInterval(int count) {
        this.count = count;
        calendar = Calendar.getInstance();
        goContaining(0);
    }

    private void updateStarts() {
        start = calendar.getTimeInMillis();
        nextStart = start + 1000 * 60 * 60;
    }

    @Override
    public void goContaining(double value) {
        long longValue = (long) value;
        calendar.setTimeInMillis(longValue);
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        hours = (hours / count) * count;
        calendar.set(Calendar.HOUR, hours);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        updateStarts();
    }

    @Override
    public void goNext() {
        calendar.add(Calendar.HOUR, count);
        updateStarts();
    }

    @Override
    public void goPrevious() {
        calendar.add(Calendar.HOUR, -count);
        updateStarts();
    }

    public boolean contains(double value) {
        long longValue = (long) value;
        if(longValue >= start && longValue < nextStart) {
            return true;
        }
        return false;
    }
}
