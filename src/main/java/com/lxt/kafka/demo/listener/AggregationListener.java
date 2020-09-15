package com.lxt.kafka.demo.listener;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.github.shyiko.mysql.binlog.event.Event;
import com.github.shyiko.mysql.binlog.event.EventData;
import com.github.shyiko.mysql.binlog.event.EventType;
import com.github.shyiko.mysql.binlog.event.TableMapEventData;
import com.lxt.kafka.demo.bo.BinlogData;
import com.lxt.kafka.demo.enums.OptionType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lixt90
 */
@Slf4j
@Component
public class AggregationListener implements BinaryLogClient.EventListener {

    private String concurrentDbname;
    private String concurrentTbname;

    private Map<String, Listener> tableListenerMap = new ConcurrentHashMap<>();

    public void register(String dbname, String tbname, Listener listener) {
        tableListenerMap.put(genKey(dbname, tbname), listener);
        log.info("register success: {}.{}", dbname, tbname);
    }

    private String genKey(String dbname, String tbname) {
        return dbname + "." + tbname;
    }

    @Override
    public void onEvent(Event event) {
        EventType eventType = event.getHeader().getEventType();

        EventData eventData = event.getData();

        if (eventType == EventType.TABLE_MAP) {
            TableMapEventData tableMapEventData = (TableMapEventData) eventData;
            concurrentDbname = tableMapEventData.getDatabase();
            concurrentTbname = tableMapEventData.getTable();
            log.info("tableMapEventData--> dbname:{}, tbname:{}", concurrentDbname, concurrentTbname);
            return;
        }

        if (eventType != EventType.EXT_WRITE_ROWS && eventType != EventType.EXT_UPDATE_ROWS && eventType != EventType.EXT_DELETE_ROWS) {
            return;
        }

        Listener listener = tableListenerMap.get(genKey(concurrentDbname, concurrentTbname));
        if (listener == null) {
            return;
        }
        BinlogData binlogData = listener.converter(event.getData(), concurrentDbname, concurrentTbname, OptionType.convert(eventType));
        if (binlogData != null) {
            listener.onEvent(binlogData);
        }
    }
}
