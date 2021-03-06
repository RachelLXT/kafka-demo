package com.lxt.kafka.demo.bo;

import com.lxt.kafka.demo.producer.enums.OptionType;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author lixt90
 */
@Data
public class AlarmData implements BinlogData {

    private String dbname;
    private String tbname;
    private OptionType optionType;

    private List<Map<String, String>> before;
}
