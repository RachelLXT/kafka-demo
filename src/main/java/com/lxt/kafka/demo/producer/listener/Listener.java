package com.lxt.kafka.demo.producer.listener;

import com.github.shyiko.mysql.binlog.event.EventData;
import com.lxt.kafka.demo.bo.BinlogData;
import com.lxt.kafka.demo.producer.enums.OptionType;

/**
 * @author lixt90
 */
public interface Listener {

    /**
     * 注册需要监听的table和对应的监听事件listener
     */
    void register();

    /**
     * data转换器
     * @param eventData
     * @param dbname
     * @param tbname
     * @param optionType
     * @return
     */
    BinlogData converter(EventData eventData, String dbname, String tbname, OptionType optionType);


    /**
     * 监听处理逻辑
     * @param binlogData
     */
    void onEvent(BinlogData binlogData);
}
