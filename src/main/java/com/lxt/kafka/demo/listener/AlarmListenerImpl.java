package com.lxt.kafka.demo.listener;

import com.github.shyiko.mysql.binlog.event.DeleteRowsEventData;
import com.github.shyiko.mysql.binlog.event.EventData;
import com.lxt.kafka.demo.bo.AlarmData;
import com.lxt.kafka.demo.bo.BinlogData;
import com.lxt.kafka.demo.enums.OptionType;
import com.lxt.kafka.demo.enums.TableEnum;
import com.lxt.kafka.demo.table.CmsBlogTable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * @author lixt90
 */
@Slf4j
@Component
public class AlarmListenerImpl implements Listener {


    @Autowired
    private AggregationListener aggregationListener;
    @Resource
    private CmsBlogTable cmsBlogTable;

    @Override
    @PostConstruct
    public void register() {
        aggregationListener.register(TableEnum.CMS_BLOG_TABLE, cmsBlogTable, this);
    }

    @Override
    public BinlogData converter(EventData eventData, String dbname, String tbname, OptionType optionType) {
        if (optionType == OptionType.DELETE) {
            AlarmData alarmData = new AlarmData();
            alarmData.setDbname(dbname);
            alarmData.setTbname(tbname);
            alarmData.setOptionType(optionType);
            DeleteRowsEventData deleteRowsEventData = (DeleteRowsEventData) eventData;
            alarmData.setBefore(aggregationListener.toMap(deleteRowsEventData.getRows(), dbname, tbname, this));
            return alarmData;
        }
        return null;
    }

    @Override
    public void onEvent(BinlogData binlogData) {
        log.error("alarming!!! {}", binlogData);
    }
}
