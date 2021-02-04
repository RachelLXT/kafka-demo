package com.lxt.kafka.demo.producer.po;

import lombok.Data;

import java.io.Serializable;

/**
 * @author lixt90
 */
@Data
public class BinlogTableRegistry implements Serializable {
    private static final long serialVersionUID = 3930884556154036865L;

    private Integer id;
    private String tableSchema;
    private String tableName;
    private String columnName;
    private String listener;
}
