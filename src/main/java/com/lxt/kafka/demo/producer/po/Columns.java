package com.lxt.kafka.demo.producer.po;

import lombok.Data;

/**
 * @author lixt90
 */
@Data
public class Columns {

    private String tableSchema;
    private String tableName;
    private String columnName;
    private Integer ordinalPosition;
}
