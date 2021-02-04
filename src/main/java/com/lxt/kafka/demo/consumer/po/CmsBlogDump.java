package com.lxt.kafka.demo.consumer.po;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * @author lixt90
 */
@Data
public class CmsBlogDump implements Serializable {
    private Integer id;

    /**
     * 博客标题
     */
    private String title;

    /**
     * 博客作者
     */
    private String author;

    /**
     * 博客内容
     */
    private String content;

    /**
     * 创建时间
     */
    @JSONField(format = "EEE MMM dd HH:mm:ss zzz yyyy")
    private Date addTime;

    /**
     * 更新时间
     */
    @JSONField(format = "EEE MMM dd HH:mm:ss zzz yyyy")
    private Date updateTime;

    /**
     * 逻辑删除
     */
    private int deleted;

    private static final long serialVersionUID = 1L;
}