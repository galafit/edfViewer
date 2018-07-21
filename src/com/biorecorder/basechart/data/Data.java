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

    public DataConfig addDataSerie(DataSeries dataSeries) {
        DataConfig dataConfig = new DataConfig();
        if(dataSeries.getXColumn() instanceof RegularColumn) {
            RegularColumn xColumn = (RegularColumn) dataSeries.getXColumn();
            dataConfig.setXStartAndInterval(xColumn.getStartValue(), xColumn.getDataInterval());
        } else {
            numberColumns.add(dataSeries.getXColumn());
            dataConfig.setXColumnIndex(numberColumns.size() - 1);
        }
        for (int i = 0; i < dataSeries.YColumnsCount(); i++) {
            numberColumns.add(dataSeries.getYColumn(i));
            dataConfig.addYColumn(numberColumns.size() - 1);
        }
        return dataConfig;
    }
}
