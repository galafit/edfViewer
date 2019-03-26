package com.biorecorder.basechart;



/**
 * Created by galafit on 9/7/18.
 */
public class DataProcessingConfig {
    private int cropShoulder = 2; // number of additional points that we leave on every side during crop
    int lengthChangeMax = 20; // percents


    private boolean isCropEnabled = true;
    private boolean isGroupEnabled = true;

    // In case of GroupType.AUTO regular DataSeries will be grouped by equal points number
    // and non-regular by equal intervals
    private GroupingType groupingType = GroupingType.AUTO;

    private int groupingStep = 2;


    // if defined (not null) group intervals will be taken only from that array,
    // otherwise every time "the best interval" will be calculated automatically
    private double[] groupIntervals = null;
    // if groupIntervals are specified (not null) and isGroupingForced = true then grouping will be done in any case
    private boolean isGroupingForced = false;

    public DataProcessingConfig() {
    }

    public DataProcessingConfig(boolean isCropEnabled, boolean isGroupEnabled) {
        this.isCropEnabled = isCropEnabled;
        this.isGroupEnabled = isGroupEnabled;
    }

    public DataProcessingConfig(DataProcessingConfig config) {
        cropShoulder = config.cropShoulder;
        lengthChangeMax = config.lengthChangeMax;
        isCropEnabled = config.isCropEnabled;
        isGroupEnabled = config.isGroupEnabled;
        groupingType = config.groupingType;
        isGroupingForced = config.isGroupingForced;
        groupingStep = config.groupingStep;
        if(config.groupIntervals != null) {
            groupIntervals = new double[config.groupIntervals.length];
            for (int i = 0; i < groupIntervals.length; i++) {
                groupIntervals[i] = config.groupIntervals[i];
            }
        }
    }

    public int getGroupingStep() {
        return groupingStep;
    }

    public void setGroupingStep(int groupingStep) {
        this.groupingStep = groupingStep;
    }

    public boolean isGroupingForced() {
        return isGroupingForced;
    }

    public void setGroupingForced(boolean groupingForced) {
        isGroupingForced = groupingForced;
    }

    public int getCropShoulder() {
        return cropShoulder;
    }

    public void setCropShoulder(int cropShoulder) {
        this.cropShoulder = cropShoulder;
    }

    public int getLengthChangeMax() {
        return lengthChangeMax;
    }

    public void setLengthChangeMax(int lengthChangeMax) {
        this.lengthChangeMax = lengthChangeMax;
    }

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

}
