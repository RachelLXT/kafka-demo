package com.lxt.kafka.demo.table;

import lombok.Data;

import java.util.Map;

/**
 * @author lixt90
 */
@Data
public class Table {

    private String dbname;
    private String tbname;
    /**
     * <columnIndex, columnName>
     */
    private Map<Integer, String> columnMap;
}
