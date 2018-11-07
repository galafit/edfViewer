package com.biorecorder.data.transformation;

import com.biorecorder.data.DataSeries;

/**
 * Created by galafit on 31/10/18.
 */
public interface DataTransform {
    public void setInputData(DataSeries inDataSeries);
    public DataSeries getTransformedData();
}
