package com.lxt.kafka.demo.config;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.github.shyiko.mysql.binlog.event.DeleteRowsEventData;
import com.github.shyiko.mysql.binlog.event.EventData;
import com.github.shyiko.mysql.binlog.event.UpdateRowsEventData;
import com.github.shyiko.mysql.binlog.event.WriteRowsEventData;
import com.lxt.kafka.demo.listener.AggregationListener;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * @author lixt90
 */
@Slf4j
@Data
@Configuration
@ConfigurationProperties(prefix = "binlog")
public class BinlogConfig {

    private String host;
    private Integer port;
    private String username;
    private String password;
    private String filename;
    private Long position;
    private Long heartbeatInterval;


    @Autowired
    private AggregationListener aggregationListener;

    /**
     * 更新:UpdateRowsEventData{tableId=111, includedColumnsBeforeUpdate={0, 1, 2, 3, 4, 5, 6}, includedColumns={0, 1, 2, 3, 4, 5, 6}, rows=[
     * {before=[61, test3, 16, 1, 2, 123, Wed Sep 02 00:31:42 CST 2020], after=[61, test3, 16, 2, 2, 123, Wed Sep 02 00:31:42 CST 2020]}
     * ]}
     * <p>
     * 插入:WriteRowsEventData{tableId=111, includedColumns={0, 1, 2, 3, 4, 5, 6}, rows=[
     * [61, test3, 16, 1, 2, 123, Wed Sep 02 00:31:42 CST 2020]
     * ]}
     * <p>
     * 删除:DeleteRowsEventData{tableId=111, includedColumns={0, 1, 2, 3, 4, 5, 6}, rows=[
     * [61, test3, 16, 2, 2, 123, Wed Sep 02 00:31:42 CST 2020]
     * ]}
     *
     * @return
     */
    @Bean(destroyMethod = "disconnect")
    public BinaryLogClient binaryLogClient() {
        BinaryLogClient binaryLogClient = new BinaryLogClient(host, port, username, password);
        // filename=null:从最新的位置开始监听; filename="":从最老的记录开始监听.
//        binaryLogClient.setBinlogFilename(StringUtils.isEmpty(filename) ? null : filename);
        // Any value less than 4 gets automatically adjusted to 4 on connect. 没有指定binlog文件时，position设置无效
//        binaryLogClient.setBinlogPosition(position);
        binaryLogClient.setHeartbeatInterval(heartbeatInterval);

        binaryLogClient.registerEventListener(event -> {
            EventData eventData = event.getData();
            if (eventData instanceof UpdateRowsEventData) {
                log.info("更新:{}", eventData.toString());
            } else if (eventData instanceof WriteRowsEventData) {
                log.info("插入:{}", eventData.toString());
            } else if (eventData instanceof DeleteRowsEventData) {
                log.info("删除:{}", eventData.toString());
            }
        });

        binaryLogClient.registerEventListener(aggregationListener);

        new Thread(() -> {
            try {
                // Note that this method blocks until disconnected.
                binaryLogClient.connect();
            } catch (IOException e) {
                log.error("binlog connect fail:{}", e.getMessage(), e);
            }
        }).start();

        return binaryLogClient;
    }
}
