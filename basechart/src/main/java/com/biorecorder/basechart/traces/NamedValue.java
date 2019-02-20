package com.biorecorder.basechart.traces;

public class NamedValue {
    private final String valueName;
    private final double value;
    private final String valueLabel;

    public NamedValue(String valueName, double value, String valueLabel) {
        this.valueName = valueName;
        this.value = value;
        this.valueLabel = valueLabel;
    }

    public String getValueName() {
        return valueName;
    }

    public double getValue() {
        return value;
    }

    public String getValueLabel() {
        return valueLabel;
    }
}
