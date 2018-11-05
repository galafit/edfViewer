package com.biorecorder.basechart.grouping;


/**
 * Created by galafit on 25/11/17.
 */
public enum GroupApproximation {
    AVERAGE("Average"),
    SUM("Sum"),
    LOW("Min"),
    HIGH("High"),
    OPEN("Open"),
    CLOSE("Close");


    private String functionName;

    GroupApproximation(String functionName) {
        this.functionName = functionName;
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
