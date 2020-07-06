package com.cdcdata.presto.udf;

import com.facebook.presto.spi.block.BlockBuilder;
import com.facebook.presto.spi.function.*;
import com.facebook.presto.spi.type.StandardTypes;
import io.airlift.slice.Slice;
import io.airlift.slice.Slices;

import static com.facebook.presto.spi.type.VarcharType.VARCHAR;

@AggregationFunction("cdcdata_concat")
public class CdcdataAggregationUDF {
    //进来的数据和state 进行累加 排除空
    @InputFunction
    public static void input(StringValueState state, @SqlType(StandardTypes.VARCHAR) Slice value){
        //先从state中将数据取出来 然后 放入state返回
        state.setStringValue(Slices.utf8Slice(isNull(state.getStringValue()) + "|"
        + value.toStringUtf8())
        );
    }
    //多个节点的state进行累加
    @CombineFunction
    public static void combine(StringValueState state1,StringValueState state2){
        state1.setStringValue(Slices.utf8Slice(isNull(state1.getStringValue())+"|"
        +isNull(state2.getStringValue())
        ));
    }
    //汇总 然后通过builder输出state
    @OutputFunction(StandardTypes.VARCHAR)
    public static void output(StringValueState state, BlockBuilder builder){
        VARCHAR.writeSlice(builder,state.getStringValue());
    }

    private static String isNull(Slice slice){
        return slice == null ? "":slice.toStringUtf8();
    }
}
