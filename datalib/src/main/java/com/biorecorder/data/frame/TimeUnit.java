package com.biorecorder.data.frame;


/**
 * Created by galafit on 28/4/19.
 */
public enum TimeUnit {
    MILLISECOND(1, 2, 5, 10, 20, 50, 100, 200, 500), // dividers of 1000
    SECOND(1, 2, 5, 10, 30), // dividers of 60
    MINUTE(1, 2, 5, 10, 30), // dividers of 60
    HOUR(1, 2, 6, 12), // dividers of 24
    DAY(1),
    WEEK(1),
    MONTH(1, 3, 6), // dividers of 12
    YEAR;

    int[] allowedMultiples;

    TimeUnit(int... allowedMultiples) {
        this.allowedMultiples = allowedMultiples;
    }

    public int[] getAllowedMultiples() {
        return allowedMultiples;
    }
}
