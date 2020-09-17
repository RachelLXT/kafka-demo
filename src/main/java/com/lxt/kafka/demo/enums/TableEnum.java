package com.lxt.kafka.demo.enums;

/**
 * 数据源需要监听器的数据库与表名
 *
 * @author lixt90
 */
public enum TableEnum {

    /**
     *
     */
    CMS_BLOG_TABLE("demo", "cms_blog"),
    ;

    private final String dbname;
    private final String tbname;

    TableEnum(String dbname, String tbname) {
        this.dbname = dbname;
        this.tbname = tbname;
    }

    public String getDbname() {
        return dbname;
    }

    public String getTbname() {
        return tbname;
    }
}
