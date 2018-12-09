package com.biorecorder.data.transformation;

import com.biorecorder.data.list.LongArrayList;
import com.biorecorder.data.frame.DataSeries;

/**
 * Equal intervals [equal width] grouping(binning)
 * Divides the data into groups so that
 * each group/bin has the same interval between starting and end values
 */
public class DataGroupByEqualIntervals extends DataGroup {
    private final GroupInterval interval;

    public DataGroupByEqualIntervals(GroupInterval interval) {
        this.interval = interval;
    }

    public GroupInterval getInterval() {
        return interval;
    }

    @Override
    public void setInputData(DataSeries inDataSeries) {
        super.setInputData(inDataSeries);
        groupStartIndexes = new IrregularGroupStartIndexes();
    }

    class IrregularGroupStartIndexes implements GroupStartIndexes {
        LongArrayList groupStartsList = new LongArrayList();
        @Override
        public long get(long index) {
            return groupStartsList.get(index);
        }

        @Override
        public long size() {
            return groupStartsList.size();
        }

        @Override
        public void updateSize() {
            if(inDataSeries.size() == 0 || (groupStartsList.size() > 0 && groupStartsList.get(groupStartsList.size() - 1) == inDataSeries.size())) {
                return;
            }

            if(groupStartsList.size() == 0) {
                groupStartsList.add(0);
            } else {
                // delete last "closing" group
                groupStartsList.remove((int) groupStartsList.size() - 1);
            }

            long from = groupStartsList.get(groupStartsList.size() - 1);
            double intervalValue = interval.getIntervalAsNumber();

            double groupStart = (int)((inDataSeries.getXValue(from) / intervalValue)) * intervalValue;
            groupStart += intervalValue;
            for (long i = from + 1;  i < inDataSeries.size(); i++) {
                if (inDataSeries.getXValue(i) >= groupStart) {
                    groupStartsList.add(i);
                    groupStart += intervalValue; // often situation

                    if(inDataSeries.getXValue(i) > groupStart) { // rare situation
                        groupStart = (int)((inDataSeries.getXValue(i) / intervalValue)) * intervalValue;
                        groupStart += intervalValue;
                    }
                }
            }
            // add last "closing" group
            groupStartsList.add(inDataSeries.size());
        }
    }
}
