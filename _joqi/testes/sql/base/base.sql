/*
SQLyog Community Edition- MySQL GUI v7.02 
MySQL - 5.0.67-community-nt : Database - joqi
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;

/*Table structure for table `pessoas` */

DROP TABLE IF EXISTS `pessoas`;

CREATE TABLE `pessoas` (
  `cd_pessoa` int(11) default NULL,
  `nm_pessoa` varchar(255) default NULL,
  `vl_idade` int(11) default NULL,
  `nr_cpf` varchar(255) default NULL,
  `cd_pai` int(11) default NULL,
  `cd_mae` int(11) default NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `pessoas` */

LOCK TABLES `pessoas` WRITE;

insert  into `pessoas`(`cd_pessoa`,`nm_pessoa`,`vl_idade`,`nr_cpf`,`cd_pai`,`cd_mae`) values (1,'Douglas',22,'111.111.111-11',4,3),(2,'Nathalia',18,'222.222.222-22',4,3),(3,'Renata',49,'333.333.333-33',7,8),(4,'Carlos',50,'444.444.444-44',5,6),(5,'Assis',75,'555.555.555-55',0,0),(6,'Juracy',75,'666.666.666-66',0,0),(7,'Luna',75,'777.777.777-77',0,0),(8,'Neide',75,'777.777.777-77',0,0),(9,'Ricardo',40,'888.888.888-88',7,8),(10,'Rosana',45,'999.999.999-99',7,8),(11,'Tania',52,'AAA.AAA.AAA-AA',5,6),(12,'Luiz',45,'BBB.BBB.BBB-BB',5,6),(13,'Luiz Jr.',14,'CCC.CCC.CCC-CC',12,15),(14,'Carol',20,'DDD.DDD.DDD-DDD',12,15),(15,'Virginia',57,'DDD.DDD.DDD-DDD',0,0),(16,'Andressa',21,'EEE.EEE.EEE-EE',17,10),(17,'Joao',45,'FFF.FFF.FFF-FF',0,0),(18,'Nicole',13,'GGG.GGG.GGG-GG',17,10),(19,'Felipe',16,'HHH.HHH.HHH-HH',9,21),(20,'Vitor',13,'III.III.III-III',9,21),(21,'Eliane',40,'JJJ.JJJ.JJJ-JJ',0,0),(22,'Vanessa',30,NULL,12,15),(23,'Kaue',11,NULL,0,22),(24,'Junior',29,NULL,12,15),(25,'Elder',22,NULL,27,28),(26,'Deniele',24,NULL,27,28),(27,'Zica',50,NULL,0,0),(28,'Denise',45,NULL,0,0),(29,'Gabrielle',21,NULL,31,32),(30,'Guilherme',16,NULL,31,32),(31,'Cacaio',48,NULL,0,35),(32,'Katia',45,NULL,34,33),(33,'Marilene',70,NULL,0,0),(34,'Paulo',70,NULL,0,0),(35,'Dona Neide',70,NULL,0,0);

UNLOCK TABLES;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;