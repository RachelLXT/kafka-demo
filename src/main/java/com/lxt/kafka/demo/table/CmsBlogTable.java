package com.lxt.kafka.demo.table;

import com.lxt.kafka.demo.po.Columns;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lixt90
 */
@Component
public class CmsBlogTable extends AbstractTableHolder {

    private static final String ID = "id";
    private static final String TITLE = "title";
    private static final String AUTHOR = "author";
    private static final String CONTENT = "content";
    private static final String ADD_TIME = "add_time";
    private static final String UPDATE_TIME = "update_time";
    private static final String DELETED = "deleted";

    @Override
    public Table toTable(List<Columns> list) {
        if (holder != null) {
            return holder;
        }

        Map<Integer, String> columnMap = new HashMap<>(7);
        holder = new Table();
        holder.setDbname(list.get(0).getTableSchema());
        holder.setTbname(list.get(0).getTableName());
        holder.setColumnMap(columnMap);

        list.forEach(column -> {
            switch (column.getColumnName()) {
                case ID:
                    columnMap.put(column.getOrdinalPosition(), ID);
                    break;
                case TITLE:
                    columnMap.put(column.getOrdinalPosition(), TITLE);
                    break;
                case AUTHOR:
                    columnMap.put(column.getOrdinalPosition(), AUTHOR);
                    break;
                case CONTENT:
                    columnMap.put(column.getOrdinalPosition(), CONTENT);
                    break;
                case ADD_TIME:
                    columnMap.put(column.getOrdinalPosition(), ADD_TIME);
                    break;
                case UPDATE_TIME:
                    columnMap.put(column.getOrdinalPosition(), UPDATE_TIME);
                    break;
                case DELETED:
                    columnMap.put(column.getOrdinalPosition(), DELETED);
                    break;
                default:
                    break;
            }
        });

        return holder;
    }
}
