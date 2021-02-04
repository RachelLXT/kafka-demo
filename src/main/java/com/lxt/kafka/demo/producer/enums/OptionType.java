package com.lxt.kafka.demo.producer.enums;

import com.github.shyiko.mysql.binlog.event.EventType;

/**
 * @author lixt90
 */
public enum OptionType {

    /**
     * 数据库操作类型
     */
    INSERT,
    UPDATE,
    DELETE,
    OTHER,
    ;


    public static OptionType convert(EventType eventType) {

        switch (eventType) {
            case EXT_WRITE_ROWS:
                return INSERT;
            case EXT_UPDATE_ROWS:
                return UPDATE;
            case EXT_DELETE_ROWS:
                return DELETE;
            default:
                return OTHER;
        }
    }


}
