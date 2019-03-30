package com.biorecorder.basechart;



/**
 * Created by galafit on 9/7/18.
 */
public class DataProcessingConfig {
    private boolean isCropEnabled = true;
    private int cropShoulder = 2; // number of additional points that we leave on every side during crop

    private boolean isGroupingEnabled = true;
    // NO REGROUPING if axis length change less then groupingStability
    int groupingStability = 20; // percents


    // In case of GroupType.AUTO regular DataSeries will be grouped by equal points number
    // and non-regular by equal intervals
    private GroupingType groupingType = GroupingType.AUTO;

    // if all data grouped and groupingIntervals = null then regrouping will be
    // done only if the "new best groping interval" bigger or less then the interval
    // of already grouped data in reGroupingStep times
    // If step <= 1 then already grouped data never will be used and new grouping will be done every time
    private int reGroupingStep = 2;

    // if defined (not null) group intervals will be taken only from that array,
    // otherwise every time "the best group interval" will be calculated automatically
    private double[] groupingIntervals = null;
    // if groupingIntervals are specified (not null) and isGroupingForced = true then grouping will be done in any case
    private boolean isGroupingForced = false;

    // group all data or only visible ones
    private boolean isGroupAll = false;

    public DataProcessingConfig() {
    }

    public DataProcessingConfig(boolean isCropEnabled, boolean isGroupingEnabled) {
        this.isCropEnabled = isCropEnabled;
        this.isGroupingEnabled = isGroupingEnabled;
    }

    public DataProcessingConfig(DataProcessingConfig config) {
        cropShoulder = config.cropShoulder;
        groupingStability = config.groupingStability;
        isCropEnabled = config.isCropEnabled;
        isGroupingEnabled = config.isGroupingEnabled;
        groupingType = config.groupingType;
        isGroupingForced = config.isGroupingForced;
        reGroupingStep = config.reGroupingStep;
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

    public int getReGroupingStep() {
        return reGroupingStep;
    }


    public void setReGroupingStep(int reGroupingStep) throws IllegalArgumentException {
        this.reGroupingStep = reGroupingStep;
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


    public boolean isGroupingEnabled() {
        return isGroupingEnabled;
    }

    public void setGroupingEnabled(boolean groupingEnabled) {
        isGroupingEnabled = groupingEnabled;
    }

}
