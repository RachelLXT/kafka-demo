package com.lxt.kafka.demo.producer.dao;

import com.lxt.kafka.demo.producer.po.Columns;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author lixt90
 */
public interface ColumnsMapper {


    @Select({"select table_schema, table_name, column_name, ordinal_position from information_schema.columns where table_schema = #{dbname} and table_name = #{tbname}"})
    List<Columns> selectByDbnameAndTbname(@Param("dbname") String dbname, @Param("tbname") String tbname);
}
