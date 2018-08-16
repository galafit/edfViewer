package com.biorecorder.basechart.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by galafit on 16/8/18.
 */
public class Data {
    List<DataSeries> dataSeriesList = new ArrayList<>();

    public void addSeries(DataSeries dataSeries) {
        dataSeriesList.add(dataSeries);
    }

    public DataSeries getSeries(int seriesNumber) {
        return dataSeriesList.get(seriesNumber);
    }
}
