package com.lxt.kafka.demo.producer.listener;

import com.github.shyiko.mysql.binlog.event.DeleteRowsEventData;
import com.github.shyiko.mysql.binlog.event.EventData;
import com.github.shyiko.mysql.binlog.event.UpdateRowsEventData;
import com.github.shyiko.mysql.binlog.event.WriteRowsEventData;
import com.lxt.kafka.demo.bo.BinlogData;
import com.lxt.kafka.demo.bo.KafkaData;
import com.lxt.kafka.demo.producer.enums.OptionType;
import com.lxt.kafka.demo.producer.sender.Sender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * kafka消息监听器，监听cms_blog表，发送所有cms_blog表INSERT/UPDATE/DELETE日志
 *
 * @author lixt90
 */
@Slf4j
@Component
public class KafkaListenerImpl implements Listener {

    @Autowired
    private AggregationListener aggregationListener;
    @Autowired
    @Qualifier("kafkaSender")
    private Sender kafkaSender;

    @Override
    @PostConstruct
    public void register() {
        aggregationListener.register(this);
    }

    @Override
    public BinlogData converter(EventData eventData, String dbname, String tbname, OptionType optionType) {
        List<Map<String, String>> data;

        switch (optionType) {
            case UPDATE:
                UpdateRowsEventData updateRowsEventData = (UpdateRowsEventData) eventData;
                data = aggregationListener.toMap(updateRowsEventData.getRows().stream().map(Map.Entry::getValue).collect(Collectors.toList()), dbname, tbname, this);
                break;
            case INSERT:
                WriteRowsEventData writeRowsEventData = (WriteRowsEventData) eventData;
                data = aggregationListener.toMap(writeRowsEventData.getRows(), dbname, tbname, this);
                break;
            case DELETE:
                DeleteRowsEventData deleteRowsEventData = (DeleteRowsEventData) eventData;
                data = aggregationListener.toMap(deleteRowsEventData.getRows(), dbname, tbname, this);
                break;
            default:
                return null;
        }

        if (CollectionUtils.isEmpty(data)) {
            return null;
        }

        KafkaData kafkaData = new KafkaData();
        kafkaData.setDbname(dbname);
        kafkaData.setTbname(tbname);
        kafkaData.setOptionType(optionType);
        kafkaData.setData(data);

        return kafkaData;
    }


    @Override
    public void onEvent(BinlogData binlogData) {
        log.debug("kafkaListener-->{}", binlogData);
        kafkaSender.send(binlogData);
    }
}
