package com.biorecorder.basechart.data;

import com.biorecorder.util.lists.LongArrayList;

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
            long size = inDataSeries.size();
            long from = 0;
            if(groupStartsList.size() > 0) {
                from = groupStartsList.get(groupStartsList.size() - 1);
            }


            if(size > from) {
                if(from > 0) {
                    // delete last "closing" group
                    groupStartsList.remove((int) groupStartsList.size() - 1);
                    from = groupStartsList.get(groupStartsList.size() - 1);
                }
                double intervalValue = interval.getIntervalAsNumber();

                double groupStart = (int)((inDataSeries.xColumn.value(from) / intervalValue)) * intervalValue;
                groupStart += intervalValue;
                for (long i = from;  i < size; i++) {
                    if (inDataSeries.xColumn.value(i) >= groupStart) {
                        groupStartsList.add(i);
                        groupStart += intervalValue; // often situation

                        if(inDataSeries.xColumn.value(i) > groupStart) { // rare situation
                            groupStart = (int)((inDataSeries.xColumn.value(from) / intervalValue)) * intervalValue;
                            groupStart += intervalValue;
                        }
                    }
                }
                // add last "closing" group
                groupStartsList.add(size);
            }
        }
    }
}
