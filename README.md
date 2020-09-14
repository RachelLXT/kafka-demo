# kafka-demo

### kafka单机

* 启动  
zookeeper-server-start /usr/local/etc/kafka/zookeeper.properties & kafka-server-start /usr/local/etc/kafka/server.properties

* 创建主题  
`kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic my-first-topic`

* 所有topic  
`kafka-topics --list --zookeeper localhost:2181`

* topic详细信息  
`kafka-topics --describe --zookeeper localhost:2181 --topic my-first-topic`

* 创建生产者  
`kafka-console-producer --bootstrap-server localhost:9092 --topic my-first-topic`

* 创建消费者  
`kafka-console-consumer --bootstrap-server localhost:9092 --topic my-first-topic --from-beginning`

----

### kafka集群
复制配置文件server.properties  

|server|server1|server2|
|:----|:----|:----|
|broker.id=0|broker.id=1|broker.id=2|
|log.dirs=/usr/local/var/lib/kafka-logs|log.dirs=/usr/local/var/lib/kafka-logs-1|log.dirs=/usr/local/var/lib/kafka-logs-2|
|num.partitions=3|num.partitions=3|num.partitions=3|
|listeners=PLAINTEXT://:9092|listeners=PLAINTEXT://:9093|listeners=PLAINTEXT://:9094|

* 启动(先启动zookeeper)  
`kafka-server-start /usr/local/etc/kafka/server.properties & kafka-server-start /usr/local/etc/kafka/server1.properties & kafka-server-start /usr/local/etc/kafka/server2.properties`

* 创建主题  
`kafka-topics --create --zookeeper localhost:2181 --replication-factor 3 --partitions 3 --topic my-group-topic`

