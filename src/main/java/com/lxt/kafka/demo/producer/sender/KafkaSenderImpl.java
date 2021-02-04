package com.lxt.kafka.demo.producer.sender;

import com.alibaba.fastjson.JSON;
import com.lxt.kafka.demo.bo.BinlogData;
import com.lxt.kafka.demo.bo.KafkaData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import javax.annotation.Resource;

/**
 * @author lixt90
 */
@Slf4j
@Service("kafkaSender")
@SuppressWarnings({"all"})
public class KafkaSenderImpl implements Sender {

    private static final String MY_FIRST_TOPIC = "my-first-topic";

    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    /**
     * kafka生产者
     *
     * @param msg
     */
    @Override
    public void send(BinlogData binlogData) {
        try {
            KafkaData kafkaData = (KafkaData) binlogData;
            String topic = kafkaData.getDbname() + '.' + kafkaData.getTbname();
            String msg = JSON.toJSONString(kafkaData);
            ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, msg);
            SendResult<String, String> result = future.get();
            log.info("send msg:{}, topic:{}, partition:{}, key:{}", msg, result.getProducerRecord().topic(),
                    result.getProducerRecord().partition(), result.getProducerRecord().key());
        } catch (Exception e) {
            log.error("kafka send msg fail:{}", e.getMessage(), e);
        }
    }
}
