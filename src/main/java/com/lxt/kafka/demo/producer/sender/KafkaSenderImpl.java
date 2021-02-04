package com.lxt.kafka.demo.producer.sender;

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
    private static final String GROUP = "Dump";

    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    /**
     * kafka生产者
     *
     * @param msg
     */
    @Override
    public void send(String msg) {
        try {
            ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(MY_FIRST_TOPIC, msg);
            SendResult<String, String> result = future.get();
            log.info("send msg:{}, topic:{}, partition:{}, key:{}", msg, result.getProducerRecord().topic(),
                    result.getProducerRecord().partition(), result.getProducerRecord().key());
        } catch (Exception e) {
            log.error("kafka send msg fail:{}", e.getMessage(), e);
        }
    }
}
