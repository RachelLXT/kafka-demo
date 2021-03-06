package com.lxt.kafka.demo.dao;

import com.lxt.kafka.demo.consumer.dao.CmsBlogDumpMapper;
import com.lxt.kafka.demo.consumer.po.CmsBlogDump;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author lixt90
 */
@SpringBootTest
@RunWith(SpringRunner.class)
class CmsBlogDumpMapperTest {

    @Resource
    private CmsBlogDumpMapper cmsBlogDumpMapper;

    @Test
    @Transactional
    void insert() {
        CmsBlogDump cmsBlogDump = new CmsBlogDump();
        cmsBlogDump.setId(1);
        cmsBlogDump.setTitle("a");
        cmsBlogDump.setAuthor("b");
        cmsBlogDump.setContent("c");
        cmsBlogDump.setAddTime(new Date());
        cmsBlogDump.setUpdateTime(new Date());
        cmsBlogDump.setDeleted(0);

        cmsBlogDumpMapper.insert(cmsBlogDump);
    }
}