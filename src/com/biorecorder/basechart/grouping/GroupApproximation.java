package com.biorecorder.basechart.grouping;


/**
 * Created by galafit on 25/11/17.
 */
public enum GroupApproximation {
    AVERAGE(1, "Average"),
    SUM(1, "Sum"),
    LOW(1, "Min"),
    HIGH(1, "High"),
    OPEN(1, "Open"),
    CLOSE(1, "Close"),
    LOW_HIGH(2, "LowHigh"),
    OHLC(4, "Ohlc"); //open, high, low and close values within the grouped data

    private int dimension;
    private String functionName;

    GroupApproximation(int dimension, String functionName) {
        this.dimension = dimension;
        this.functionName = functionName;
    }

    public int getDimension() {
        return dimension;
    }


    /**
     *
     * @param dataType: short, int, long, float, double
     * @return grouping function Object corresponding
     * to the given type of data (IntGroupingAvg, FloatGroupingMin and so on)
     */
    public Object getGroupingFunction(String dataType) {
        // Capitalize the first letter of dataType string
        String type = dataType.substring(0, 1).toUpperCase() + dataType.substring(1);
        String functionClassName = "com.biorecorder.basechart.grouping."+type + "Group" + functionName;
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
