package com.lxt.kafka.demo.sender;

import com.alibaba.fastjson.JSON;
import com.lxt.kafka.demo.bo.KafkaData;
import com.lxt.kafka.demo.dao.CmsBlogDumpMapper;
import com.lxt.kafka.demo.po.CmsBlogDump;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
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
public class KafkaSenderImpl implements Sender {

    private static final String MY_FIRST_TOPIC = "my-first-topic";
    private static final String GROUP = "Dump";

    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;
    @Resource
    private CmsBlogDumpMapper cmsBlogDumpMapper;

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

    @KafkaListener(topics = {MY_FIRST_TOPIC}, groupId = GROUP)
    public void consume(ConsumerRecord<?, ?> record) {
        if (record == null) {
            return;
        }
        KafkaData kafkaData = JSON.parseObject(record.value().toString(), KafkaData.class);
        switch (kafkaData.getOptionType()) {
            case DELETE:
                kafkaData.getBefore().forEach(map -> {
                    String id = map.get("id");
                    cmsBlogDumpMapper.deleteByPrimaryKey(Integer.valueOf(id));
                });
                break;

            case INSERT:
                kafkaData.getAfter().forEach(map -> {
                    CmsBlogDump cmsBlogDump = JSON.parseObject(JSON.toJSONString(map), CmsBlogDump.class);
                    cmsBlogDumpMapper.insert(cmsBlogDump);
                });
                break;
            case UPDATE:
                kafkaData.getAfter().forEach(map -> {
                    CmsBlogDump cmsBlogDump = JSON.parseObject(JSON.toJSONString(map), CmsBlogDump.class);
                    cmsBlogDumpMapper.updateByPrimaryKey(cmsBlogDump);
                });
                break;
            default:
                break;
        }
        log.info("Consumer-Group-{} consume kafka data:{}", GROUP, kafkaData);
    }

}
