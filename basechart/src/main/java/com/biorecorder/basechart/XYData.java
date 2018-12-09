package com.biorecorder.basechart;

import com.biorecorder.data.frame.DataSeries;
import com.biorecorder.data.sequence.IntSequence;

/**
 * Created by galafit on 23/7/18.
 */
public class XYData extends DataSeries {
    public void setYData(IntSequence data) {
        removeYData();
        addYData(data);
    }

    public void setYData(int[] data) {
        removeYData();
        addYData(data);
    }

    private void removeYData() {
        if(yCount() > 0) {
            removeYData(yCount() - 1);
        }
    }
}
