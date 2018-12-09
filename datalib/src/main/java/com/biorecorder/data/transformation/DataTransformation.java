package com.biorecorder.data.transformation;

import com.biorecorder.data.frame.DataSeries;

/**
 * Created by galafit on 31/10/18.
 */
public interface DataTransformation {
    public void setInputData(DataSeries inDataSeries);
    public DataSeries getTransformedData();
}
