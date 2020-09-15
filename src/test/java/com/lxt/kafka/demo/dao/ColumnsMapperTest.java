package com.lxt.kafka.demo.dao;

import com.lxt.kafka.demo.po.Columns;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author lixt90
 */
@SpringBootTest
@RunWith(SpringRunner.class)
class ColumnsMapperTest {

    @Resource
    private ColumnsMapper columnsMapper;

    @Test
    void selectByDbnameAndTbname() {
        List<Columns> columnsList = columnsMapper.selectByDbnameAndTbname("demo", "user");
        columnsList.forEach(System.out::println);
    }
}