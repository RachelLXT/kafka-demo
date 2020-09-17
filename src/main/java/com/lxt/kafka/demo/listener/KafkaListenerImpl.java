package com.lxt.kafka.demo.listener;

import com.alibaba.fastjson.JSON;
import com.github.shyiko.mysql.binlog.event.DeleteRowsEventData;
import com.github.shyiko.mysql.binlog.event.EventData;
import com.github.shyiko.mysql.binlog.event.UpdateRowsEventData;
import com.github.shyiko.mysql.binlog.event.WriteRowsEventData;
import com.lxt.kafka.demo.bo.BinlogData;
import com.lxt.kafka.demo.bo.KafkaData;
import com.lxt.kafka.demo.enums.OptionType;
import com.lxt.kafka.demo.enums.TableEnum;
import com.lxt.kafka.demo.sender.Sender;
import com.lxt.kafka.demo.table.CmsBlogTable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
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

    @Resource
    private CmsBlogTable cmsBlogTable;
    @Autowired
    private AggregationListener aggregationListener;
    @Autowired
    @Qualifier("kafkaSender")
    private Sender kafkaSender;

    @Override
    @PostConstruct
    public void register() {
        aggregationListener.register(TableEnum.CMS_BLOG_TABLE, cmsBlogTable, this);
    }

    @Override
    public BinlogData converter(EventData eventData, String dbname, String tbname, OptionType optionType) {
        List<Map<String, String>> before = null;
        List<Map<String, String>> after = null;

        switch (optionType) {
            case UPDATE:
                UpdateRowsEventData updateRowsEventData = (UpdateRowsEventData) eventData;
                after = aggregationListener.toMap(updateRowsEventData.getRows().stream().map(Map.Entry::getValue).collect(Collectors.toList()), dbname, tbname, this);
                break;
            case INSERT:
                WriteRowsEventData writeRowsEventData = (WriteRowsEventData) eventData;
                after = aggregationListener.toMap(writeRowsEventData.getRows(), dbname, tbname, this);
                break;
            case DELETE:
                DeleteRowsEventData deleteRowsEventData = (DeleteRowsEventData) eventData;
                before = aggregationListener.toMap(deleteRowsEventData.getRows(), dbname, tbname, this);
                break;
            default:
                return null;
        }

        if (CollectionUtils.isEmpty(before) && CollectionUtils.isEmpty(after)) {
            return null;
        }

        KafkaData kafkaData = new KafkaData();
        kafkaData.setDbname(dbname);
        kafkaData.setTbname(tbname);
        kafkaData.setOptionType(optionType);
        kafkaData.setBefore(before);
        kafkaData.setAfter(after);

        return kafkaData;
    }


    @Override
    public void onEvent(BinlogData binlogData) {
        log.debug("kafkaListener-->{}", binlogData);
        kafkaSender.send(JSON.toJSONString(binlogData));
    }
}
