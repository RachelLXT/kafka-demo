-- MySQL dump 10.13  Distrib 8.0.19, for macos10.15 (x86_64)
--
-- Host: localhost    Database: demo
-- ------------------------------------------------------
-- Server version	8.0.19

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `binlog_table_registry`
--

DROP TABLE IF EXISTS `binlog_table_registry`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `binlog_table_registry` (
  `id` int NOT NULL AUTO_INCREMENT,
  `table_schema` varchar(64) NOT NULL,
  `table_name` varchar(64) NOT NULL,
  `column_name` varchar(64) NOT NULL,
  `listener` varchar(128) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `index_listener` (`listener`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `binlog_table_registry`
--

LOCK TABLES `binlog_table_registry` WRITE;
/*!40000 ALTER TABLE `binlog_table_registry` DISABLE KEYS */;
INSERT INTO `binlog_table_registry` VALUES (1,'demo','cms_blog','add_time','com.lxt.kafka.demo.producer.listener.KafkaListenerImpl'),(2,'demo','cms_blog','author','com.lxt.kafka.demo.producer.listener.KafkaListenerImpl'),(3,'demo','cms_blog','content','com.lxt.kafka.demo.producer.listener.KafkaListenerImpl'),(4,'demo','cms_blog','deleted','com.lxt.kafka.demo.producer.listener.KafkaListenerImpl'),(5,'demo','cms_blog','id','com.lxt.kafka.demo.producer.listener.KafkaListenerImpl'),(6,'demo','cms_blog','title','com.lxt.kafka.demo.producer.listener.KafkaListenerImpl'),(7,'demo','cms_blog','update_time','com.lxt.kafka.demo.producer.listener.KafkaListenerImpl'),(8,'demo','cms_blog','add_time','com.lxt.kafka.demo.producer.listener.AlarmListenerImpl'),(9,'demo','cms_blog','author','com.lxt.kafka.demo.producer.listener.AlarmListenerImpl'),(10,'demo','cms_blog','content','com.lxt.kafka.demo.producer.listener.AlarmListenerImpl'),(11,'demo','cms_blog','deleted','com.lxt.kafka.demo.producer.listener.AlarmListenerImpl'),(12,'demo','cms_blog','id','com.lxt.kafka.demo.producer.listener.AlarmListenerImpl'),(13,'demo','cms_blog','title','com.lxt.kafka.demo.producer.listener.AlarmListenerImpl'),(14,'demo','cms_blog','update_time','com.lxt.kafka.demo.producer.listener.AlarmListenerImpl'),(15,'demo','user','AGE','com.lxt.kafka.demo.producer.listener.KafkaListenerImpl'),(16,'demo','user','CREATE_DATE','com.lxt.kafka.demo.producer.listener.KafkaListenerImpl'),(17,'demo','user','GENDER','com.lxt.kafka.demo.producer.listener.KafkaListenerImpl'),(18,'demo','user','ID','com.lxt.kafka.demo.producer.listener.KafkaListenerImpl'),(19,'demo','user','NAME','com.lxt.kafka.demo.producer.listener.KafkaListenerImpl'),(20,'demo','user','PASSWORD','com.lxt.kafka.demo.producer.listener.KafkaListenerImpl'),(21,'demo','user','SERIAL_NUMBER','com.lxt.kafka.demo.producer.listener.KafkaListenerImpl');
/*!40000 ALTER TABLE `binlog_table_registry` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2021-02-04 12:56:48
