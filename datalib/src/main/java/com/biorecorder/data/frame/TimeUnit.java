package com.biorecorder.data.frame;


/**
 * Created by galafit on 28/4/19.
 */
public enum TimeUnit {
    MILLISECOND(1),
    SECOND(1000),
    MINUTE(1000 * 60),
    HOUR(1000 * 60 * 60),
    DAY(1000 * 60 * 60 * 24),
    WEEK(1000 * 60 * 60 * 24 * 7),
    MONTH(1000 * 60 * 60 * 24 * 7 * 30),
    YEAR(1000 * 60 * 60 * 24 * 7 * 30 * 365);


    long milliseconds;

    TimeUnit(int milliseconds) {
        this.milliseconds = milliseconds;
    }

    public long toMilliseconds() {
        return milliseconds;
    }
}
