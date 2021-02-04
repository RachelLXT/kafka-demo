package com.lxt.kafka.demo.producer.dao;

import com.lxt.kafka.demo.producer.po.BinlogTableRegistry;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author lixt90
 */
public interface BinlogTableRegistryMapper {

    @Select({"select * from binlog_table_registry where listener = #{listener}"})
    List<BinlogTableRegistry> selectByListener(@Param("listener") String listener);
}
