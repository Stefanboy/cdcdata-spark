package com.cdcdata.presto.udf;

import com.facebook.presto.spi.function.ScalarFunction;
import com.facebook.presto.spi.function.SqlType;
import com.facebook.presto.spi.type.StandardTypes;
import io.airlift.slice.Slice;
import io.airlift.slice.Slices;

public class PrefixUDF {
    @ScalarFunction("cdcdata_prefix")//函数名
    //@Deprecated
    @SqlType(StandardTypes.VARCHAR)//返回值类型
    //下面是参数的类型
    public static Slice prefix(@SqlType(StandardTypes.VARCHAR) Slice input){

        return Slices.utf8Slice("cddata_prefix_"+input.toStringUtf8());
    }
}
