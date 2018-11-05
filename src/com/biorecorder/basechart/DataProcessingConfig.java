package com.biorecorder.basechart;

/**
 * Created by galafit on 9/7/18.
 */
public class DataProcessingConfig {
    private boolean isCropEnabled = true;
    private boolean isGroupEnabled = true;

    // In case of GroupType.AUTO regular DataSeries will be grouped by equal points number
    // and non-regular by equal intervals
    private GroupType groupType = GroupType.AUTO;


    // If grouping superposition enabled we can make
    // further grouping on the base of previously grouped points
    // (in the case of grouped by equal points number and full grouping)
    private boolean isGroupSuperpositionEnabled = true;

    // whether group ALL points or only visible ones (cropped to x axis min-max range)
    // private boolean isGroupAll = false;
    // (at the moment not used. we make full grouping in preview when isCropEnabled = false)

    private int groupStep = 2;


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


    public boolean isGroupSuperpositionEnabled() {
        return isGroupSuperpositionEnabled;
    }

    public void setGroupSuperpositionEnabled(boolean groupSuperpositionEnabled) {
        isGroupSuperpositionEnabled = groupSuperpositionEnabled;
    }

    public int getGroupStep() {
        return groupStep;
    }

    public void setGroupStep(int groupStep) {
        this.groupStep = groupStep;
    }
}
