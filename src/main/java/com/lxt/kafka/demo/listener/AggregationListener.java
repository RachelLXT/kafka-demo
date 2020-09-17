package com.lxt.kafka.demo.listener;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.github.shyiko.mysql.binlog.event.Event;
import com.github.shyiko.mysql.binlog.event.EventData;
import com.github.shyiko.mysql.binlog.event.EventType;
import com.github.shyiko.mysql.binlog.event.TableMapEventData;
import com.lxt.kafka.demo.bo.BinlogData;
import com.lxt.kafka.demo.dao.ColumnsMapper;
import com.lxt.kafka.demo.enums.OptionType;
import com.lxt.kafka.demo.enums.TableEnum;
import com.lxt.kafka.demo.po.Columns;
import com.lxt.kafka.demo.table.AbstractTableHolder;
import com.lxt.kafka.demo.table.Table;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * @author lixt90
 */
@Slf4j
@Component
public class AggregationListener implements BinaryLogClient.EventListener {

    private String concurrentDbname;
    private String concurrentTbname;

    private Map<String, Table> tableMap = new ConcurrentHashMap<>();
    private Map<String, List<Listener>> tableListenerMap = new ConcurrentHashMap<>();

    @Resource
    private ColumnsMapper columnsMapper;

    public void register(TableEnum tableEnum, AbstractTableHolder holder, Listener listener) {
        String tableMapKey = genTableMapKey(tableEnum.getDbname(), tableEnum.getTbname(), listener.getClass().getSimpleName());
        List<Columns> list = columnsMapper.selectByDbnameAndTbname(tableEnum.getDbname(), tableEnum.getTbname());
        if (CollectionUtils.isEmpty(list)) {
            log.error("register fail: tableMapKey={}", tableMapKey);
        }
        tableMap.put(tableMapKey, holder.toTable(list));
        List<Listener> listenerList = getOrCreate(genTableListenerMapKey(tableEnum.getDbname(), tableEnum.getTbname()), tableListenerMap, ArrayList::new);
        listenerList.add(listener);
        log.info("register success: tableMapKey={}", tableMapKey);
    }

    public List<Map<String, String>> toMap(List<Serializable[]> rows, String dbname, String tbname, Listener listener) {
        String tableMapKey = genTableMapKey(dbname, tbname, listener.getClass().getSimpleName());
        Table table = tableMap.get(tableMapKey);
        if (table == null) {
            log.error("table not exists:{}", tableMapKey);
            return Collections.emptyList();
        }

        List<Map<String, String>> list = new ArrayList<>();
        for (Serializable[] row : rows) {
            Map<String, String> map = new HashMap<>();
            for (int i = 0; i < row.length; i++) {
                String columnName = table.getColumnMap().get(i + 1);
                if (StringUtils.isEmpty(columnName)) {
                    continue;
                }
                if (row[i] instanceof byte[]) {
                    map.put(columnName, new String((byte[]) row[i]));
                } else {
                    map.put(columnName, row[i].toString());
                }
            }
            list.add(map);
        }
        return list;
    }

    @Override
    public void onEvent(Event event) {
        EventType eventType = event.getHeader().getEventType();

        EventData eventData = event.getData();

        if (eventType == EventType.TABLE_MAP) {
            TableMapEventData tableMapEventData = (TableMapEventData) eventData;
            concurrentDbname = tableMapEventData.getDatabase();
            concurrentTbname = tableMapEventData.getTable();
            log.debug("tableMapEventData--> dbname:{}, tbname:{}", concurrentDbname, concurrentTbname);
            return;
        }

        if (eventType != EventType.EXT_WRITE_ROWS && eventType != EventType.EXT_UPDATE_ROWS && eventType != EventType.EXT_DELETE_ROWS) {
            return;
        }

        List<Listener> listenerList = tableListenerMap.get(genTableListenerMapKey(concurrentDbname, concurrentTbname));
        if (CollectionUtils.isEmpty(listenerList)) {
            return;
        }
        OptionType optionType = OptionType.convert(eventType);
        listenerList.forEach(listener -> {
            BinlogData binlogData = listener.converter(event.getData(), concurrentDbname, concurrentTbname, optionType);
            if (binlogData != null) {
                listener.onEvent(binlogData);
            }
        });
    }

    private String genTableMapKey(String dbname, String tbname, String listener) {
        return dbname + "." + tbname + ":" + listener;
    }

    private String genTableListenerMapKey(String dbname, String tbname) {
        return dbname + "." + tbname;
    }

    private <K, V> V getOrCreate(K key, Map<K, V> map, Supplier<V> factory) {
        return map.computeIfAbsent(key, k -> factory.get());
    }
}
