package com.lxt.kafka.demo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author lixt90
 */
@RunWith(SpringRunner.class)
@SpringBootTest
class KafkaDemoTest {

    @Autowired
    private KafkaDemo kafkaDemo;

    @Test
    @Timeout(3600)
    void sendData() throws InterruptedException {
        for (int i = 0; i < 10000; i++) {
            Thread.sleep(15);
            kafkaDemo.send(i + "");
        }
    }

    @Test
    @Timeout(3600)
    void send() {
        kafkaDemo.send();
    }
}