package com.lxt.kafka.demo.consumer.dao;


/**
 * @author lixt90
 */
public interface BaseMapper<T> {

    int deleteByPrimaryKey(Integer id);

    int insert(T record);

    int updateByPrimaryKey(T record);
}
