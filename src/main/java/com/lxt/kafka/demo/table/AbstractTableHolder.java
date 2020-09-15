package com.lxt.kafka.demo.table;

import com.lxt.kafka.demo.po.Columns;

import java.util.List;

/**
 * @author lixt90
 */
public abstract class AbstractTableHolder {

    protected Table holder;

    public abstract Table toTable(List<Columns> list);
}
