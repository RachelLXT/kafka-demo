package com.lxt.kafka.demo.consumer.receiver;

import com.alibaba.fastjson.JSON;
import com.lxt.kafka.demo.bo.KafkaData;
import com.lxt.kafka.demo.consumer.dao.BaseMapper;
import com.lxt.kafka.demo.consumer.dao.MapperHolder;
import com.lxt.kafka.demo.consumer.enums.MapperEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * @author lixt90
 */
@Slf4j
@Component
public class KafkaReceiver {


    /**
     * kafka消费者
     *
     * @param record
     */
    @KafkaListener(topics = {"demo.cms_blog"}, groupId = "Dump")
    public void consume(ConsumerRecord<?, ?> record) {
        if (record == null) {
            return;
        }
        KafkaData kafkaData = JSON.parseObject(record.value().toString(), KafkaData.class);
        MapperEnum mapperEnum = MapperEnum.find(kafkaData.getDbname(), kafkaData.getTbname());
        if (mapperEnum == null) {
            return;
        }
        BaseMapper<Object> baseMapper = MapperHolder.find(mapperEnum.getMapper());

        switch (kafkaData.getOptionType()) {
            case DELETE:
                kafkaData.getData().forEach(map -> {
                    String id = map.get(mapperEnum.getPrimaryKey());
                    baseMapper.deleteByPrimaryKey(Integer.valueOf(id));
                });
                break;

            case INSERT:
                kafkaData.getData().forEach(map -> {
                    Class<?> clazz = mapperEnum.getPo();
                    baseMapper.insert(JSON.parseObject(JSON.toJSONString(map), clazz));
                });
                break;
            case UPDATE:
                kafkaData.getData().forEach(map -> {
                    Class<?> clazz = mapperEnum.getPo();
                    baseMapper.updateByPrimaryKey(JSON.parseObject(JSON.toJSONString(map), clazz));
                });
                break;
            default:
                break;
        }
        log.info("Topic=[demo.cms_blog], GroupId=[Dump], consume kafka data:{}", kafkaData);
    }
}
