package com.lxt.kafka.demo.listener;

import com.github.shyiko.mysql.binlog.event.DeleteRowsEventData;
import com.github.shyiko.mysql.binlog.event.EventData;
import com.lxt.kafka.demo.bo.AlarmData;
import com.lxt.kafka.demo.bo.BinlogData;
import com.lxt.kafka.demo.table.Table;
import com.lxt.kafka.demo.table.AbstractTableHolder;
import com.lxt.kafka.demo.table.CmsBlogTable;
import com.lxt.kafka.demo.dao.ColumnsMapper;
import com.lxt.kafka.demo.enums.OptionType;
import com.lxt.kafka.demo.enums.TableEnum;
import com.lxt.kafka.demo.po.Columns;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lixt90
 */
@Slf4j
@Component
public class AlarmListenerImpl implements Listener {

    private Map<String, Table> tableMap = new ConcurrentHashMap<>();

    @Resource
    private ColumnsMapper columnsMapper;
    @Autowired
    private AggregationListener aggregationListener;
    @Resource
    private CmsBlogTable cmsBlogTable;

    @Override
    @PostConstruct
    public void register() {
        registerTable(TableEnum.CMS_BLOG_TABLE, cmsBlogTable);
    }

    private void registerTable(TableEnum tableEnum, AbstractTableHolder holder) {
        List<Columns> list = columnsMapper.selectByDbnameAndTbname(tableEnum.getDbname(), tableEnum.getTbname());
        if (CollectionUtils.isEmpty(list)) {
            log.error("register fail:{}.{}", tableEnum.getDbname(), tableEnum.getTbname());
        }
        tableMap.put(tableEnum.getTbname(), holder.toTable(list));
        aggregationListener.register(tableEnum.getDbname(), tableEnum.getTbname(), this);
    }

    @Override
    public BinlogData converter(EventData eventData, String dbname, String tbname, OptionType optionType) {
        if (optionType == OptionType.DELETE) {
            AlarmData alarmData = new AlarmData();
            alarmData.setDbname(dbname);
            alarmData.setTbname(tbname);
            alarmData.setOptionType(optionType);
            DeleteRowsEventData deleteRowsEventData = (DeleteRowsEventData) eventData;
            alarmData.setBefore(tpMap(deleteRowsEventData.getRows(), tbname));
            return alarmData;
        }
        return null;
    }

    private List<Map<String, String>> tpMap(List<Serializable[]> rows, String tbname) {
        List<Map<String, String>> list = new ArrayList<>();

        Table table = tableMap.get(tbname);
        for (Serializable[] row : rows) {
            Map<String, String> map = new HashMap<>();
            for (int i = 0; i < row.length; i++) {
                String columnName = table.getColumnMap().get(i+1);
                if (StringUtils.isEmpty(columnName)) {
                    continue;
                }
                map.put(columnName, row[i].toString());
            }
            list.add(map);
        }
        return list;
    }

    @Override
    public void onEvent(BinlogData binlogData) {
        log.error("alarming!!! {}", binlogData);
    }
}
