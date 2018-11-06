package com.biorecorder.basechart;

import com.biorecorder.basechart.data.GroupInterval;
import com.biorecorder.basechart.data.IntervalUnit;

/**
 * Created by galafit on 9/7/18.
 */
public class DataProcessingConfig {
    public final GroupInterval[] TIME_GROUP_INTERVALS = {
            new GroupInterval(1, IntervalUnit.MILLISECOND),
            new GroupInterval(2, IntervalUnit.MILLISECOND),
            new GroupInterval(5, IntervalUnit.MILLISECOND),
            new GroupInterval(10, IntervalUnit.MILLISECOND),
            new GroupInterval(25, IntervalUnit.MILLISECOND),
            new GroupInterval(50, IntervalUnit.MILLISECOND),
            new GroupInterval(100, IntervalUnit.MILLISECOND),
            new GroupInterval(250, IntervalUnit.MILLISECOND),
            new GroupInterval(500, IntervalUnit.MILLISECOND),

            new GroupInterval(1, IntervalUnit.SECOND),
            new GroupInterval(2, IntervalUnit.SECOND),
            new GroupInterval(5, IntervalUnit.SECOND),
            new GroupInterval(10, IntervalUnit.SECOND),
            new GroupInterval(15, IntervalUnit.SECOND),
            new GroupInterval(30, IntervalUnit.SECOND),

            new GroupInterval(1, IntervalUnit.MINUTE),
            new GroupInterval(2, IntervalUnit.MINUTE),
            new GroupInterval(5, IntervalUnit.MINUTE),
            new GroupInterval(10, IntervalUnit.MINUTE),
            new GroupInterval(15, IntervalUnit.MINUTE),
            new GroupInterval(30, IntervalUnit.MINUTE),

            new GroupInterval(1, IntervalUnit.HOUR),
            new GroupInterval(2, IntervalUnit.HOUR),
            new GroupInterval(6, IntervalUnit.HOUR),
            new GroupInterval(12, IntervalUnit.HOUR),

            new GroupInterval(1, IntervalUnit.DAY),

            new GroupInterval(1, IntervalUnit.WEEK)
            // at the moment not used
            /*
            new GroupInterval(1, IntervalUnit.MONTH),
            new GroupInterval(3, IntervalUnit.MONTH),
            new GroupInterval(6, IntervalUnit.MONTH),

            new GroupInterval(1, IntervalUnit.YEAR)*/
    };


    private boolean isCropEnabled = true;
    private boolean isGroupEnabled = true;

    // In case of GroupType.AUTO regular DataSeries will be grouped by equal points number
    // and non-regular by equal intervals
    private GroupType groupType = GroupType.AUTO;

    public GroupInterval[] getGroupIntervals() {
        return groupIntervals;
    }

    public void setGroupIntervals(GroupInterval[] groupIntervals) {
        this.groupIntervals = groupIntervals;
    }

    // If grouping superposition enabled we can make
    // further grouping on the base of previously grouped points
    // (in the case of grouped by equal points number and full grouping)
    private boolean isGroupSuperpositionEnabled = true;

    // whether group ALL points or only visible ones (cropped to x axis min-max range)
    // private boolean isGroupAll = false;
    // (at the moment not used. we make full grouping in preview when isCropEnabled = false)

    private int groupStep = 2;

    // if defined (not null) group intervals will be taken only from that array,
    // otherwise every time "the best interval" will be calculated automatically
    private GroupInterval[] groupIntervals = null;

    public GroupType getGroupType() {
        return groupType;
    }

    public void setGroupType(GroupType groupType) {
        this.groupType = groupType;
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
