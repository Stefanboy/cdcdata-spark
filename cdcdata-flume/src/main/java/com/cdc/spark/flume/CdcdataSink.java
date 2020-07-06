package com.cdc.spark.flume;

import org.apache.flume.*;
import org.apache.flume.conf.Configurable;
import org.apache.flume.sink.AbstractSink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 自定义Sink
 *
 */
public class CdcdataSink extends AbstractSink implements Configurable {
    private static final Logger log = LoggerFactory.getLogger(CdcdataSink.class);
    private String prefix;
    private String suffix;

    //从channel中获取数据 并发送到目的地
    @Override
    public Status process() throws EventDeliveryException {
        Status status = null;

        Channel channel = getChannel();//获取channel
        Transaction transaction = channel.getTransaction();//获取事物
        transaction.begin();//开启事务

        try {
            Event event;

            while (true){//一直等待
                event = channel.take();
                if(event != null){//拿到不为空的event才往下执行
                    break;
                }
            }

            //加工数据
            String body = new String(event.getBody());

            log.info(prefix+body+suffix);

            transaction.commit();
            status = Status.READY;
        }catch (Exception e){
            transaction.rollback();
            status = Status.BACKOFF;

        }finally {
            transaction.close();
        }

        return status;
    }

    @Override
    public void configure(Context context) {
        this.prefix = context.getString("prefix","cdcdata");
        this.suffix = context.getString("suffix");
    }
}
