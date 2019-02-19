package com.biorecorder.basechart.traces;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by galafit on 17/2/19.
 */
public class PointInfo {
    private double xValue;
    private String xLabel;
    List<NamedValues> values = new ArrayList<>(1);

    public PointInfo(double xValue, String xLabel) {
        this.xValue = xValue;
        this.xLabel = xLabel;
    }

    public void addValue(String valueName, double value, String valueLabel) {
        values.add(new NamedValues(valueName, value, valueLabel));
    }

    public double getXValue() {
        return xValue;
    }

    public String getXLabel() {
        return xLabel;
    }

    public int valueCount() {
        return values.size();
    }

    public String getValueName(int valueNumber) {
        return values.get(valueNumber).valueName;
    }

    public double getValue(int valueNumber) {
        return values.get(valueNumber).value;
    }

    public String getValueLabel(int valueNumber) {
        return values.get(valueNumber).valueLabel;
    }


    class NamedValues {
        final String valueName;
        final double value;
        final String valueLabel;

        public NamedValues(String valueName, double value, String valueLabel) {
            this.valueName = valueName;
            this.value = value;
            this.valueLabel = valueLabel;
        }
    }
}
