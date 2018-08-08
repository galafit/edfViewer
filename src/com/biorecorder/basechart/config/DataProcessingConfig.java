package com.biorecorder.basechart.config;

/**
 * Created by galafit on 9/7/18.
 */
public class DataProcessingConfig {
    private boolean isCropEnabled = true;
    private boolean isGroupingEnabled = true;

    // if X_min or X_max is not specified data will be cropped
    // to occupy only available screen space/width in best way (without grouping)
    private boolean  cropToAvailableSpaceEnabled = false;

    private boolean isDataExpensive = false;

    // These 2 properties work together.
    // If grouping superposition enabled we can make
    // grouping of ALL points not only visible ones (full grouping) are grouped
    // and copy the result to use it as a base
    // for further quick groupings
    private boolean isGroupingSuperpositionEnabled = true;
    private int fullGroupingFactor = 64;

    public boolean isCropEnabled() {
        return isCropEnabled;
    }

    public void setCropEnabled(boolean cropEnabled) {
        isCropEnabled = cropEnabled;
    }


    public boolean isCropToAvailableSpaceEnabled() {
        return cropToAvailableSpaceEnabled;
    }

    public void setCropToAvailableSpaceEnabled(boolean cropToAvailableSpaceEnabled) {
        this.cropToAvailableSpaceEnabled = cropToAvailableSpaceEnabled;
    }

    public boolean isGroupingEnabled() {
        return isGroupingEnabled;
    }

    public void setGroupingEnabled(boolean groupingEnabled) {
        isGroupingEnabled = groupingEnabled;
    }

    public boolean isDataExpensive() {
        return isDataExpensive;
    }

    public void setDataExpensive(boolean dataExpensive) {
        isDataExpensive = dataExpensive;
    }

    public boolean isGroupingSuperpositionEnabled() {
        return isGroupingSuperpositionEnabled;
    }

    public void setGroupingSuperpositionEnabled(boolean groupingSuperpositionEnabled) {
        isGroupingSuperpositionEnabled = groupingSuperpositionEnabled;
    }

    public int getFullGroupingFactor() {
        return fullGroupingFactor;
    }

    public void setFullGroupingFactor(int fullGroupingFactor) {
        this.fullGroupingFactor = fullGroupingFactor;
    }
}
