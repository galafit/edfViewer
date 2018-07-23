package com.biorecorder.basechart.data;

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
        if(yColumns.size() > 0) {
           yColumns.remove(yColumns.size() - 1);
        }
    }
}
