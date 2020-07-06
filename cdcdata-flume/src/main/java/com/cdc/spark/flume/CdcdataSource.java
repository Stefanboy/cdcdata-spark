package com.cdc.spark.flume;

import org.apache.flume.Context;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.PollableSource;
import org.apache.flume.conf.Configurable;
import org.apache.flume.event.EventBuilder;
import org.apache.flume.event.SimpleEvent;
import org.apache.flume.source.AbstractSource;

/**
 * 自定义flume的Source
 * 参考官网http://flume.apache.org/releases/content/1.9.0/FlumeDeveloperGuide.html#source
 * 比着样例写
 * 打包 传到服务器上
 *
 * 然后找到flume的.conf的脚本
 * 使用最简单的 netcat memory logger的即可
 *
 */
public class CdcdataSource extends AbstractSource implements Configurable, PollableSource {
    private String prefix;
    private String suffix;

    //处理Event
    @Override
    public Status process() throws EventDeliveryException {
        Status status = null;
        try {

            //自己模拟数据
            for (int i=1;i<100;i++){
                SimpleEvent event = new SimpleEvent();
                //把数据设置到event的body中去
                event.setBody((prefix+i+suffix).getBytes());
                //发送
                getChannelProcessor().processEvent(event);
            }
            status = Status.READY;
        } catch (Exception e) {
            status = Status.BACKOFF;
            e.printStackTrace();

        }

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //返回状态的结果
        return status;
    }

    @Override
    public long getBackOffSleepIncrement() {
        return 0;
    }

    @Override
    public long getMaxBackOffSleepInterval() {
        return 0;
    }

    //获取Agent中传入的信息
    @Override
    public void configure(Context context) {
        this.prefix = context.getString("prefix","cdcdata");
        this.suffix = context.getString("suffix");

    }
}
