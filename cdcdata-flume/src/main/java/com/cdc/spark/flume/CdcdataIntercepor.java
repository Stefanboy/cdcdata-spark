package com.cdc.spark.flume;

import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.interceptor.Interceptor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CdcdataIntercepor implements Interceptor {

    //定义集合存放events
    private List<Event> events;
    @Override
    public void initialize() {
        events = new ArrayList<>();
    }

    @Override
    public Event intercept(Event event) {
        Map<String, String> headers = event.getHeaders();
        String body = new String(event.getBody());
        if(body.contains("gifshow")){
            headers.put("type","gifshow");
        }else {
            headers.put("type","other");
        }
        return event;
    }

    @Override
    public List<Event> intercept(List<Event> list) {
        events.clear();//先把上一批次的数据清空
        for(Event event:list){
            events.add(intercept(event));
        }
        return events;
    }

    @Override
    public void close() {

    }

    //可以参考源码 HostInterceptor 看看人家是怎么实现的
    public static class Builder implements Interceptor.Builder{

        @Override
        public Interceptor build() {
            return new CdcdataIntercepor();
        }

        @Override
        public void configure(Context context) {

        }
    }
}
