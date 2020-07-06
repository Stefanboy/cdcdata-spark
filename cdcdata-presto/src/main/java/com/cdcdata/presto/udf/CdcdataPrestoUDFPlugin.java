package com.cdcdata.presto.udf;


import com.facebook.presto.jdbc.internal.guava.collect.ImmutableSet;
import com.facebook.presto.spi.Plugin;

import java.util.Set;

public class CdcdataPrestoUDFPlugin implements Plugin {

    @Override
    public Set<Class<?>> getFunctions() {
        return ImmutableSet.<Class<?>>builder()
                .add(PrefixUDF.class)//将自定义udf函数注册到plugin中
                .build();
    }
}
