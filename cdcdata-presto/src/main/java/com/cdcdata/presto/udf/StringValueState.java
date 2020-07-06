package com.cdcdata.presto.udf;

import com.facebook.presto.spi.function.AccumulatorState;
import io.airlift.slice.Slice;

public interface StringValueState extends AccumulatorState {

    //取值
    Slice getStringValue();
    //设值
    void setStringValue(Slice value);
}
