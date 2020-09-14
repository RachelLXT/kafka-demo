package com.lxt.kafka.demo;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author lixt90
 */
@RunWith(SpringRunner.class)
@SpringBootTest
class KafkaConfigTest {

    @Autowired
    private KafkaConfig kafkaConfig;

    @Test
    void sendData() {
        for (int i = 0; i < 10; i++) {
            kafkaConfig.send(i + "");
        }
//        kafkaConfig.send("hello, david!");
//        kafkaConfig.send();
    }

    @Test
    void send() {
        kafkaConfig.send();
    }
}