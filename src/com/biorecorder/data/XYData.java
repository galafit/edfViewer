package com.biorecorder.data;

import com.biorecorder.data.series.IntSeries;

/**
 * Created by galafit on 23/7/18.
 */
public class XYData extends DataSeries {
    public void setYData(IntSeries data) {
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
