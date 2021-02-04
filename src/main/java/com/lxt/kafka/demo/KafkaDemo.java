package com.lxt.kafka.demo;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;

import javax.annotation.Resource;
import java.util.Optional;

/**
 * @author lixt90
 */
@Configuration
@Slf4j
public class KafkaDemo {

    private static final String MY_FIRST_TOPIC = "my-first-topic";
    private static final String MY_GROUP_TOPIC = "my-group-topic";

    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    public void send(String data) {
        try {
            ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(MY_FIRST_TOPIC, data);
            SendResult<String, String> result = future.get();
            log.info("send msg:{}, topic:{}, partition:{}, key:{}", data, result.getProducerRecord().topic(),
                    result.getProducerRecord().partition(), result.getProducerRecord().key());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void send() {
        for (int i = 0; i < 10000; i++) {
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
            kafkaTemplate.send(MY_GROUP_TOPIC, i % 3, "sendKey", "message" + i);
        }
    }


    @KafkaListener(topics = {MY_FIRST_TOPIC}, groupId = "group1")
    public void consume1(ConsumerRecord<?, ?> record) {
        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
        if (kafkaMessage.isPresent()) {
            Object message = kafkaMessage.get();
            log.info("1.consume msg:{}, topic:{}, partition:{}, key:{}", message.toString(), record.topic(), record.partition(), record.key());
        }
    }

    @KafkaListener(topics = {MY_GROUP_TOPIC}, groupId = "group2")
    public void consume2(ConsumerRecord<?, ?> record) {
        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
        if (kafkaMessage.isPresent()) {
            Object message = kafkaMessage.get();
            log.info("2.consume msg:{}, topic:{}, partition:{}, key:{}", message.toString(), record.topic(), record.partition(), record.key());
        }
    }
}
