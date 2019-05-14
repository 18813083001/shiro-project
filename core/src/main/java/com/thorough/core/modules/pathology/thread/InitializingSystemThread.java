package com.thorough.core.modules.pathology.thread;

import com.thorough.core.modules.pathology.model.vo.KafkaMessage;
import com.thorough.core.modules.pathology.web.ImageController;
import com.thorough.library.constant.Constant;
import com.thorough.library.system.utils.CacheUtils;
import com.thorough.library.utils.FileUtils;
import com.thorough.library.utils.PropertyUtil;
import com.thorough.library.utils.StringUtils;
import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.serializer.StringDecoder;
import kafka.utils.VerifiableProperties;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class InitializingSystemThread implements ApplicationListener<ContextRefreshedEvent>,DisposableBean {
    protected Logger logger = LoggerFactory.getLogger(InitializingSystemThread.class);
    public final static String TOPIC = "TEST-TOPIC";
    public final static String kafkaCache = "kafkaCache";
//    ExecutorService threadPool = Executors.newFixedThreadPool(100);
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        new Thread(){
            public void run(){
                String zookeeperConnect = PropertyUtil.getProperty("pathology.zookeeper.connect");
                Properties props = new Properties();
                //zookeeper 配置
                props.put("zookeeper.connect", zookeeperConnect);

                //group 代表一个消费组
                props.put("group.id", "jd-group");

                //zk连接超时
                props.put("zookeeper.session.timeout.ms", "4000");
                props.put("zookeeper.sync.time.ms", "200");
                props.put("auto.commit.interval.ms", "1000");
                props.put("auto.offset.reset", "smallest");
                //序列化类
                props.put("serializer.class", "kafka.serializer.StringEncoder");

                ConsumerConfig config = new ConsumerConfig(props);

                ConsumerConnector consumer = Consumer.createJavaConsumerConnector(config);

                Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
                String topic = PropertyUtil.getProperty("pathology.kafka.consumer.topic");
                topicCountMap.put(topic, new Integer(1));

                StringDecoder keyDecoder = new StringDecoder(new VerifiableProperties());
                StringDecoder valueDecoder = new StringDecoder(new VerifiableProperties());

                Map<String, List<KafkaStream<String, String>>> consumerMap =
                        consumer.createMessageStreams(topicCountMap,keyDecoder,valueDecoder);
                KafkaStream<String, String> stream = consumerMap.get(topic).get(0);

                ConsumerIterator<String, String> it = stream.iterator();
                boolean hasNext = true;
                while (hasNext){
                    try {
                        hasNext = it.hasNext();
                        String progress = it.next().message();

                        //采用异步处理不安全，如果数据量非常大，threadPool会用OOM的风险
                        JSONObject jsonObject = new JSONObject(progress);
                        String jobId = jsonObject.getString("job_id");
                        if (StringUtils.isNotBlank(jobId)){
                            if ("start_send_test".equals(jobId)){
                                logger.info("test kafka start，kafka is running normally!");
                            }else {
                                Object result = null;
                                try {
                                    result = jsonObject.getJSONArray("result");
                                }catch (Exception e){
                                }
                                KafkaMessage existMessage = (KafkaMessage) CacheUtils.get(kafkaCache,jobId);
                                //不是第一次返回结果,也不是最终结果才计算中间进度。第一次返回结果 kafkaObject == null，最终结果result != null
                                if( existMessage != null && result == null){
                                    JSONObject existObject = new JSONObject(existMessage.getMessage());
                                    try {
                                        int alreadyFinished = existObject.getInt("task_finished_num");
                                        int task_finished_num = jsonObject.getInt("task_finished_num");
                                        int totalFinished = alreadyFinished + task_finished_num;
                                        jsonObject.put("task_finished_num",totalFinished);
                                    }catch (Exception e){
                                        logger.error("error occur when parse the task_finished_num and task_finished_num :", e);
                                    }
                                }
                                if (result != null){
                                    try {
                                        FileUtils.writToFile(progress,"kafkaMessage");
                                        FileUtils.writToFile(jsonObject,"jsonObjectToString");
                                    }catch (Exception e){
                                        logger.error("write predict result to file",e);
                                    }
                                }
                                KafkaMessage message = new KafkaMessage();
                                message.setMessage(jsonObject.toString());
                                CacheUtils.put(kafkaCache,jobId,message);
                            }
                        }else {
                            logger.info("job_id is null!");
                        }
                    }catch (Exception e){
                        logger.error("exception occurs when the runnable processes kafka message:", e);
                    }
                }
            }
        }.start();

    }

    @Override
    public void destroy() throws Exception {
//        threadPool.shutdown();
    }
}
