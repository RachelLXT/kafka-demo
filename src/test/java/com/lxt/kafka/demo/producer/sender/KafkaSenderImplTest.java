package com.lxt.kafka.demo.producer.sender;

import com.lxt.kafka.demo.bo.KafkaData;
import com.lxt.kafka.demo.producer.enums.OptionType;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lixt90
 */
@SpringBootTest
@RunWith(SpringRunner.class)
class KafkaSenderImplTest {

    @Resource
    private Sender kafkaSender;

    @Test
    void send() {
        Map<String, String> map = new HashMap<>();
        map.put("author", "Tommy");
        map.put("add_time", "2020-08-10 12:33:18");
        map.put("update_time", "Tue Jan 01 08:00:00 CST 2019");
        KafkaData kafkaData = new KafkaData();
        kafkaData.setDbname("demo");
        kafkaData.setTbname("test");
        kafkaData.setOptionType(OptionType.INSERT);
        kafkaData.setData(Collections.singletonList(map));

        kafkaSender.send(kafkaData);
    }
}