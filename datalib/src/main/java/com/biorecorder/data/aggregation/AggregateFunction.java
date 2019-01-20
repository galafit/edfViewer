package com.biorecorder.data.aggregation;


/**
 * Created by galafit on 25/11/17.
 */
public enum AggregateFunction {
    AVERAGE("Average"),
    SUM("Sum"),
    COUNT("Count"),
    MIN("Min"),
    MAX("Max"),
    FIRST("First"),
    LAST("Last");

    private String functionName;

    AggregateFunction(String functionName) {
        this.functionName = functionName;
    }

    /**
     *
     * @param dataType: short, int, long, float, double
     * @return grouping function Object corresponding
     * to the given type of data (IntGroupingAvg, FloatGroupingMin and so on)
     */
    public Object getFunctionImpl(String dataType) {
        // Capitalize the first letter of dataType string
        String type = dataType.substring(0, 1).toUpperCase() + dataType.substring(1);
        String functionClassName = "com.biorecorder.data.aggregation.impl."+type + functionName;
        try {
            return  (Class.forName(functionClassName)).newInstance();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return functionName;
    }
}
