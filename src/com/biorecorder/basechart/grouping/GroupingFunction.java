package com.biorecorder.basechart.grouping;

/**
 * Created by galafit on 25/11/17.
 */
public enum GroupingFunction {
    AVG(1, "Avg"),
    MIN(1, "Min"),
    MAX(1, "Max"),
    FIRST(1, "First"),
    MIN_MAX(2, "MinMax");

    private int dimension;
    private String functionName;

    GroupingFunction(int dimension, String functionName) {
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
        String functionClassName = "com.biorecorder.basechart.grouping."+type + "Grouping" + functionName;
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
