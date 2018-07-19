package com.biorecorder.basechart.data;

import com.biorecorder.basechart.config.DataConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by galafit on 2/7/18.
 */
public class Data {
    List<NumberColumn> numberColumns = new ArrayList<>();
    List<StringColumn> stringColumns = new ArrayList<>();

    public DataSeries getDataSeries(DataConfig dataConfig) {
        DataSeries dataSeries = new DataSeries();
        if(dataConfig.getXColumnIndex() < 0) {
            dataSeries.setXData(dataConfig.getXStartValue(), dataConfig.getXInterval());
        } else {
            dataSeries.setXData(numberColumns.get(dataConfig.getXColumnIndex()));
        }
        for (int i = 0; i < dataConfig.YColumnsCount(); i++) {
            dataSeries.addYData(numberColumns.get(dataConfig.getYColumnIndex(i)));
        }
        return dataSeries;
    }

    public void addNumberColumn(NumberColumn numberColumn) {
        numberColumns.add(numberColumn);
    }
}
