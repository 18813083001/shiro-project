package com.thorough.core.modules.pathology.thread;


import com.thorough.library.utils.PropertyUtil;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class InitkafkaProducerThread implements ApplicationListener<ContextRefreshedEvent> {

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        new Thread(){
            public void run(){
                String param = "{}";
                //kafka
                Properties props = new Properties();
                //此处配置的是kafka的端口
                String brokerList = PropertyUtil.getProperty("pathology.kafka.broker.list");
                props.put("metadata.broker.list", brokerList);

                //配置value的序列化类
                props.put("serializer.class", "kafka.serializer.StringEncoder");
                //配置key的序列化类
                props.put("key.serializer.class", "kafka.serializer.StringEncoder");

                //request.required.acks
                //0, which means that the producer never waits for an acknowledgement from the broker (the same behavior as 0.7). This option provides the lowest latency but the weakest durability guarantees (some data will be lost when a server fails).
                //1, which means that the producer gets an acknowledgement after the leader replica has received the data. This option provides better durability as the client waits until the server acknowledges the request as successful (only messages that were written to the now-dead leader but not yet replicated will be lost).
                //-1, which means that the producer gets an acknowledgement after all in-sync replicas have received the data. This option provides the best durability, we guarantee that no messages will be lost as long as at least one in sync replica remains.
                props.put("request.required.acks","-1");
                Producer<String, String> producer = new Producer<String, String>(new ProducerConfig(props));
                String topic = PropertyUtil.getProperty("pathology.kafka.consumer.topic");
                producer.send(new KeyedMessage<String, String>(topic, "mkey" ,param));
            }
        }.start();

//        new Thread(){
//            public void run(){
//                RoleDiseaseDao roleDiseaseDao = event.getApplicationContext().getBean(RoleDiseaseDao.class);
//                List<String> list = roleDiseaseDao.getChildIdsByParentId("0");
//
//            }
//        }.start();
    }
}
