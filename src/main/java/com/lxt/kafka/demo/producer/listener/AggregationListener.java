package com.lxt.kafka.demo.producer.listener;


import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.github.shyiko.mysql.binlog.event.Event;
import com.github.shyiko.mysql.binlog.event.EventData;
import com.github.shyiko.mysql.binlog.event.EventType;
import com.github.shyiko.mysql.binlog.event.TableMapEventData;
import com.lxt.kafka.demo.bo.BinlogData;
import com.lxt.kafka.demo.producer.dao.BinlogTableRegistryMapper;
import com.lxt.kafka.demo.producer.dao.ColumnsMapper;
import com.lxt.kafka.demo.producer.enums.OptionType;
import com.lxt.kafka.demo.producer.po.BinlogTableRegistry;
import com.lxt.kafka.demo.producer.po.Columns;
import com.lxt.kafka.demo.producer.po.Table;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * 聚合监听器
 *
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
    @Resource
    private BinlogTableRegistryMapper tableRegistryMapper;

    /**
     * 监听器注册
     *
     * @param listener 监听器
     */
    public void register(Listener listener) {
        String listenerKey = listener.getClass().getName();
        List<BinlogTableRegistry> registries = tableRegistryMapper.selectByListener(listenerKey);
        if (CollectionUtils.isEmpty(registries)) {
            log.error("no data for listener:[{}] in table:binlog_table_registry", listener.getClass().getName());
        }

        // <dbname.tableName, customizeFields>
        Map<String, Set<String>> map = new HashMap<>(8);
        for (BinlogTableRegistry registry : registries) {
            String schemaAndTableKey = registry.getTableSchema() + "." + registry.getTableName();
            Set<String> customizeFields = getOrCreate(schemaAndTableKey, map, HashSet::new);
            customizeFields.add(registry.getColumnName());
        }

        for (Map.Entry<String, Set<String>> customizeFieldsEntry : map.entrySet()) {
            String[] schemaAndTable = customizeFieldsEntry.getKey().split("\\.");
            Table table = Table.builder()
                    .dbname(schemaAndTable[0])
                    .tbname(schemaAndTable[1])
                    .build();
            List<Columns> totalColumns = columnsMapper.selectByDbnameAndTbname(table.getDbname(), table.getTbname());
            String tableMapKey = genTableMapKey(table.getDbname(), table.getTbname(), listenerKey);
            if (CollectionUtils.isEmpty(totalColumns)) {
                log.error("register fail: tableMapKey={}", tableMapKey);
                continue;
            }
            table.customizeRegisterFields(totalColumns, customizeFieldsEntry.getValue());
            tableMap.put(tableMapKey, table);
            List<Listener> listenerList = getOrCreate(genTableListenerMapKey(table.getDbname(), table.getTbname()), tableListenerMap, ArrayList::new);
            listenerList.add(listener);
            log.info("register success: tableMapKey={}", tableMapKey);
        }
    }

    /**
     * binlog消息<columnIndex, columnValue>转换成<columnName, columnValue>
     *
     * @param rows     binlog消息
     * @param dbname
     * @param tbname
     * @param listener
     * @return List<map < columnName, columnValue>>
     */
    public List<Map<String, String>> toMap(List<Serializable[]> rows, String dbname, String tbname, Listener listener) {
        String tableMapKey = genTableMapKey(dbname, tbname, listener.getClass().getName());
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
        return dbname + '.' + tbname + ':' + listener;
    }

    private String genTableListenerMapKey(String dbname, String tbname) {
        return dbname + '.' + tbname;
    }

    private <K, V> V getOrCreate(K key, Map<K, V> map, Supplier<V> factory) {
        return map.computeIfAbsent(key, k -> factory.get());
    }
}
