package com.biorecorder.data.frame;

/**
 * Created by galafit on 13/1/19.
 */
public enum DataType {
    SHORT("short", true),
    INT("int", true),
    LONG("long", true),
    FLOAT("float", true),
    DOUBLE("double", true),
    STRING("String",false),
    BOOLEAN("boolean",false);

    private boolean isNumber;
    private String label;

    DataType(String label, boolean isNumber) {
        this.label = label;
        this.isNumber = isNumber;
    }

    public boolean isNumber() {
        return isNumber;
    }


    @Override
    public String toString() {
        return label;
    }
}
