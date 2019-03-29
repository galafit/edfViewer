package com.biorecorder.basechart;



/**
 * Created by galafit on 9/7/18.
 */
public class DataProcessingConfig {
    private int cropShoulder = 2; // number of additional points that we leave on every side during crop
    // NO REGROUPING if axis length change less then groupingStability
    int groupingStability = 20; // percents


    private boolean isCropEnabled = true;
    private boolean isGroupEnabled = true;

    // In case of GroupType.AUTO regular DataSeries will be grouped by equal points number
    // and non-regular by equal intervals
    private GroupingType groupingType = GroupingType.AUTO;

    private int groupingStep = 2;

    // if defined (not null) group intervals will be taken only from that array,
    // otherwise every time "the best interval" will be calculated automatically
    private double[] groupingIntervals = null;
    // if groupingIntervals are specified (not null) and isGroupingForced = true then grouping will be done in any case
    private boolean isGroupingForced = false;

    private boolean isGroupAll = false;

    public DataProcessingConfig() {
    }

    public DataProcessingConfig(boolean isCropEnabled, boolean isGroupEnabled) {
        this.isCropEnabled = isCropEnabled;
        this.isGroupEnabled = isGroupEnabled;
    }

    public DataProcessingConfig(DataProcessingConfig config) {
        cropShoulder = config.cropShoulder;
        groupingStability = config.groupingStability;
        isCropEnabled = config.isCropEnabled;
        isGroupEnabled = config.isGroupEnabled;
        groupingType = config.groupingType;
        isGroupingForced = config.isGroupingForced;
        groupingStep = config.groupingStep;
        isGroupAll = config.isGroupAll;
        if(config.groupingIntervals != null) {
            groupingIntervals = new double[config.groupingIntervals.length];
            for (int i = 0; i < groupingIntervals.length; i++) {
                groupingIntervals[i] = config.groupingIntervals[i];
            }
        }
    }

    public boolean isGroupAll() {
        return isGroupAll;
    }

    public void setGroupAll(boolean groupAll) {
        isGroupAll = groupAll;
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

    public int getGroupingStability() {
        return groupingStability;
    }

    public void setGroupingStability(int groupingStability) {
        this.groupingStability = groupingStability;
    }

    public double[] getGroupingIntervals() {
        return groupingIntervals;
    }

    public void setGroupingIntervals(double[] groupingIntervals) {
        this.groupingIntervals = groupingIntervals;
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
