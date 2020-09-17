package com.lxt.kafka.demo.sender;

import com.alibaba.fastjson.JSON;
import com.lxt.kafka.demo.bo.KafkaData;
import com.lxt.kafka.demo.dao.BaseMapper;
import com.lxt.kafka.demo.dao.MapperHolder;
import com.lxt.kafka.demo.enums.MapperEnum;
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

    /**
     * kafka消费者
     *
     * @param record
     */
    @KafkaListener(topics = {MY_FIRST_TOPIC}, groupId = GROUP)
    public void consume(ConsumerRecord<?, ?> record) {
        if (record == null) {
            return;
        }
        KafkaData kafkaData = JSON.parseObject(record.value().toString(), KafkaData.class);
        MapperEnum mapperEnum = MapperEnum.find(kafkaData.getDbname(), kafkaData.getTbname());
        if (mapperEnum == null) {
            return;
        }
        BaseMapper baseMapper = MapperHolder.find(mapperEnum.getMapper());

        switch (kafkaData.getOptionType()) {
            case DELETE:
                kafkaData.getBefore().forEach(map -> {
                    String id = map.get(mapperEnum.getPrimaryKey());
                    baseMapper.deleteByPrimaryKey(Integer.valueOf(id));
                });
                break;

            case INSERT:
                kafkaData.getAfter().forEach(map -> {
                    Class<?> clazz = mapperEnum.getPo();
                    baseMapper.insert(JSON.parseObject(JSON.toJSONString(map), clazz));
                });
                break;
            case UPDATE:
                kafkaData.getAfter().forEach(map -> {
                    Class<?> clazz = mapperEnum.getPo();
                    baseMapper.updateByPrimaryKey(JSON.parseObject(JSON.toJSONString(map), clazz));
                });
                break;
            default:
                break;
        }
        log.info("Consumer-Group-{} consume kafka data:{}", GROUP, kafkaData);
    }

}
