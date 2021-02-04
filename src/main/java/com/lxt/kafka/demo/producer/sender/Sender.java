package com.lxt.kafka.demo.producer.sender;

import com.lxt.kafka.demo.bo.BinlogData;

/**
 * @author lixt90
 */
public interface Sender {
    void send(BinlogData binlogData);
}
