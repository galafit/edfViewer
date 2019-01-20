package com.biorecorder.basechart;



/**
 * Created by galafit on 9/7/18.
 */
public class DataProcessingConfig {

    private boolean isCropEnabled = true;
    private boolean isGroupEnabled = true;

    // In case of GroupType.AUTO regular DataSeries will be grouped by equal points number
    // and non-regular by equal intervals
    private GroupingType groupingType = GroupingType.AUTO;


    private int groupStep = 2;

    // if defined (not null) group intervals will be taken only from that array,
    // otherwise every time "the best interval" will be calculated automatically
    private double[] groupIntervals = null;

    public double[] getGroupIntervals() {
        return groupIntervals;
    }

    public void setGroupIntervals(double[] groupIntervals) {
        this.groupIntervals = groupIntervals;
    }

    public GroupingType getGroupingType() {
        return groupingType;
    }

    public void setGroupingType(GroupingType groupingType) {
        this.groupingType = groupingType;
    }

    public boolean isCropEnabled() {
        return isCropEnabled;
    }

    public void setCropEnabled(boolean cropEnabled) {
        isCropEnabled = cropEnabled;
    }


    public boolean isGroupEnabled() {
        return isGroupEnabled;
    }

    public void setGroupEnabled(boolean groupEnabled) {
        isGroupEnabled = groupEnabled;
    }

    public int getGroupStep() {
        return groupStep;
    }

    public void setGroupStep(int groupStep) {
        this.groupStep = groupStep;
    }
}
