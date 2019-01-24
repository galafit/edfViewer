package com.biorecorder.data.frame;

/**
 * Created by galafit on 13/1/19.
 */
public enum DataType {
    NUMBER(true),
    STRING(false),
    BOOLEAN(false);

    private boolean isNumber;

    DataType(boolean isNumber) {
        this.isNumber = isNumber;
    }

    public boolean isNumber() {
        return isNumber;
    }
}
