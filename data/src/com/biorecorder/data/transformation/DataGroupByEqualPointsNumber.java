package com.biorecorder.data.transformation;


import com.biorecorder.data.frame.DataSeries;
import com.biorecorder.data.sequence.LongRegularSequence;

/**
 * Equal frequencies [equal height] grouping(binning)
 * Divides the data into groups so that
 * each group/bin has equal number of elements or data points.
 */
public class DataGroupByEqualPointsNumber extends DataGroup {
    private final long pointsNumber;

    public DataGroupByEqualPointsNumber(long pointsNumber) {
        this.pointsNumber = pointsNumber;
    }

    public long getPointsNumber() {
        return pointsNumber;
    }

    @Override
    public void setInputData(DataSeries inDataSeries1) {
        super.setInputData(inDataSeries1);
        groupStartIndexes = new RegularGroupStartIndexes(0, pointsNumber);
    }


    class RegularGroupStartIndexes extends LongRegularSequence implements GroupStartIndexes{
        public RegularGroupStartIndexes(long startValue, long dataInterval) {
            super(startValue, dataInterval);
        }

        @Override
        public void updateSize() {
            long inDataSize = inDataSeries.size();
            if(inDataSize % pointsNumber == 0) {
                size = inDataSize / pointsNumber + 1;
            } else {
                size = inDataSize / pointsNumber + 2;
            }
        }
    }
}