package com.lxt.kafka.demo.enums;

import com.lxt.kafka.demo.dao.CmsBlogDumpMapper;
import com.lxt.kafka.demo.po.CmsBlogDump;

/**
 * kafka消费者客户端mapper映射关系
 *
 * @author lixt90
 */
public enum MapperEnum {
    /**
     *
     */
    CMS_BLOG_DUMP_TABLE("demo", "cms_blog", "id", CmsBlogDumpMapper.class, CmsBlogDump.class),
    ;

    /**
     * dbname: 数据源dbname
     * tbname: 数据源tbname
     * primaryKey: 主键列名
     * mapper: 落数据用的mapper类
     * po: 与mapper对应的实体类
     */
    private final String dbname;
    private final String tbname;
    private final String primaryKey;
    private final Class<?> mapper;
    private final Class<?> po;

    MapperEnum(String dbname, String tbname, String primaryKey, Class<?> mapper, Class<?> po) {
        this.dbname = dbname;
        this.tbname = tbname;
        this.primaryKey = primaryKey;
        this.mapper = mapper;
        this.po = po;
    }


    public static MapperEnum find(String dbname, String tbname) {
        for (MapperEnum e : MapperEnum.values()) {
            if (e.getDbname().equals(dbname) && e.getTbname().equals(tbname)) {
                return e;
            }
        }
        return null;
    }

    public String getDbname() {
        return dbname;
    }

    public String getTbname() {
        return tbname;
    }

    public String getPrimaryKey() {
        return primaryKey;
    }

    public Class<?> getMapper() {
        return mapper;
    }

    public Class<?> getPo() {
        return po;
    }
}
