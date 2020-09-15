package com.lxt.kafka.demo.listener;

import com.github.shyiko.mysql.binlog.event.EventData;
import com.lxt.kafka.demo.bo.BinlogData;
import com.lxt.kafka.demo.enums.OptionType;

/**
 * @author lixt90
 */
public class KafkaListenerImpl implements Listener {
    @Override
    public void register() {

    }

    @Override
    public BinlogData converter(EventData eventData, String dbname, String tbname, OptionType optionType) {
        return null;
    }

    @Override
    public void onEvent(BinlogData binlogData) {

    }
}
