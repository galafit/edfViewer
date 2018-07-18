package com.biorecorder.basechart.chart.config;

/**
 * Created by galafit on 9/7/18.
 */
public class DataProcessingConfig {
    private boolean isCropEnabled = true;
    private boolean isGroupingEnabled = true;

    private boolean isDataExpencive = true;

    // These 2 properties work together.
    // If grouping superposition enabled we can make
    // grouping of ALL points not only visible ones (full grouping) are grouped
    // and cache the result to use it as a base
    // for further quick groupings
    private boolean isGroupingSuperpositionEnabled = true;
    private int fullGroupingFactor = 64;

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

    public boolean isDataExpencive() {
        return isDataExpencive;
    }

    public void setDataExpencive(boolean dataExpencive) {
        isDataExpencive = dataExpencive;
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
