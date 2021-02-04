package com.lxt.kafka.demo.producer.po;

import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author lixt90
 */
@Builder
@Data
public class Table {

    private String dbname;
    private String tbname;
    /**
     * <columnIndex, columnName>
     */
    private Map<Integer, String> columnMap;

    public void customizeRegisterFields(List<Columns> totalColumns, Set<String> customizeFields) {
        columnMap = new HashMap<>(customizeFields.size());
        for (Columns column : totalColumns) {
            if (customizeFields.contains(column.getColumnName())) {
                columnMap.put(column.getOrdinalPosition(), column.getColumnName());
            }
        }
    }
}
