package com.lxt.kafka.demo.dao;

import com.lxt.kafka.demo.po.CmsBlogDump;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Update;

/**
 * kafka消费者客户端落数据用的mapper
 * @author lixt90
 */
public interface CmsBlogDumpMapper extends BaseMapper<CmsBlogDump> {


    @Override
    @Delete("delete from cms_blog_dump where id = #{id}")
    int deleteByPrimaryKey(Integer id);

    @Override
    @Insert({"insert into cms_blog_dump(id, title, author, content, add_time, update_time, deleted) ",
            "values(#{id}, #{title}, #{author}, #{content}, #{addTime}, #{updateTime}, #{deleted})"})
    int insert(CmsBlogDump record);

    @Override
    @Update({"update cms_blog_dump set title = #{title}, author = #{author}, ",
            "content = #{content}, add_time = #{addTime}, update_time = #{updateTime}, deleted = #{deleted} ",
            "where id = #{id}"})
    int updateByPrimaryKey(CmsBlogDump record);
}