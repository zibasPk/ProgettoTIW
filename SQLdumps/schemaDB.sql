CREATE DATABASE  IF NOT EXISTS `progettotiw` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `progettotiw`;
-- MySQL dump 10.13  Distrib 8.0.28, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: progettotiw
-- ------------------------------------------------------
-- Server version	8.0.28

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `album`
--

DROP TABLE IF EXISTS `album`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `album` (
  `ID` int NOT NULL AUTO_INCREMENT,
  `creation_date` date NOT NULL,
  `title` varchar(32) NOT NULL,
  `ownerID` int NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `userId_idx` (`ownerID`),
  CONSTRAINT `userId` FOREIGN KEY (`ownerID`) REFERENCES `user` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `album`
--

LOCK TABLES `album` WRITE;
/*!40000 ALTER TABLE `album` DISABLE KEYS */;
INSERT INTO `album` VALUES (1,'2021-04-20','paesaggi',1),(2,'2020-08-10','statue',3),(3,'1990-04-23','Picasso',2),(4,'1930-02-02','tour effeil 1',1),(5,'1930-03-02','tour effeil 2',1),(6,'1930-04-02','tour effeil 3',1);
/*!40000 ALTER TABLE `album` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `album_order`
--

DROP TABLE IF EXISTS `album_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `album_order` (
  `userID` int NOT NULL,
  `albumID` int NOT NULL,
  `position` int NOT NULL,
  PRIMARY KEY (`userID`,`albumID`),
  KEY `albumID_idx` (`albumID`),
  CONSTRAINT `albmId` FOREIGN KEY (`albumID`) REFERENCES `album` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `usrId` FOREIGN KEY (`userID`) REFERENCES `user` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `album_order`
--

LOCK TABLES `album_order` WRITE;
/*!40000 ALTER TABLE `album_order` DISABLE KEYS */;
/*!40000 ALTER TABLE `album_order` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `comment`
--

DROP TABLE IF EXISTS `comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comment` (
  `ID` int NOT NULL AUTO_INCREMENT,
  `userID` int NOT NULL,
  `imageID` int NOT NULL,
  `text` varchar(280) NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comment`
--

LOCK TABLES `comment` WRITE;
/*!40000 ALTER TABLE `comment` DISABLE KEYS */;
INSERT INTO `comment` VALUES (6,1,3,'so cool'),(7,1,3,'ciao'),(8,9,3,'ciao'),(9,9,3,'che bello'),(11,9,3,'fantastico'),(12,1,2,'wow so pretty'),(16,1,3,'adsajdn'),(17,1,3,'so cool'),(18,1,3,'so cool'),(19,1,3,'martone'),(20,1,3,'beta'),(21,1,3,'gamma'),(22,1,3,'ftugyhus'),(23,1,3,'milano'),(24,1,3,'milanoafbua'),(25,1,3,'mario'),(26,1,3,'mario'),(27,1,3,'oj'),(28,1,4,'nice family'),(29,1,4,'wow'),(30,1,3,'aewsredftgyh');
/*!40000 ALTER TABLE `comment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `image`
--

DROP TABLE IF EXISTS `image`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `image` (
  `ID` int NOT NULL AUTO_INCREMENT,
  `uploaderID` int NOT NULL,
  `title` varchar(32) NOT NULL,
  `path` varchar(260) NOT NULL,
  `date` date NOT NULL,
  `description` varchar(280) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `uploaderID_idx` (`uploaderID`),
  CONSTRAINT `uploaderID` FOREIGN KEY (`uploaderID`) REFERENCES `user` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `image`
--

LOCK TABLES `image` WRITE;
/*!40000 ALTER TABLE `image` DISABLE KEYS */;
INSERT INTO `image` VALUES (1,1,'Girasoli','/img/girasoli.jpg','2021-04-18','girasoli al tramonto'),(2,3,'Autoritratto di Picasso','/img/autopicasso.jpg','1980-04-18','Autoritratto del pittore Pablo Picasso'),(3,2,'facciata teatro la scala','/img/lascala.jpg','1980-04-18','Facciata del teatro la scala di sera'),(4,1,'foto famiglia 1','/img/famiglia.jpg','2022-05-11','una foto famiglia'),(5,1,'foto famiglia 2','/img/famiglia2.jpg','2022-05-11','una foto famiglia'),(6,1,'foto famiglia 3','/img/famiglia3.jpg','2022-05-11','una foto famiglia'),(7,1,'foto famiglia 4','/img/famiglia4.jpg','2022-05-11','una foto famiglia'),(8,1,'foto famiglia 5','/img/famiglia5.jpg','2022-05-11','una foto famiglia'),(9,1,'foto famiglia 6','/img/famiglia6.jpg','2022-05-11','una foto famiglia'),(10,1,'islanda','/img/islanda.jpg','2005-03-23','il paesaggio islandese primaverile'),(11,1,'umbria','/img/umbria.jpg','2013-04-11','il paesaggio umbro estivo'),(12,1,'dolomiti','/img/dolomiti.jpg','2019-04-11','le dolomiti d\'estate'),(13,1,'canarie','/img/canarie.jpg','2015-04-13','le fantastiche canarie'),(14,1,'canada','/img/canada.jpg','2018-03-23','un paesaggio molto freddo');
/*!40000 ALTER TABLE `image` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `image_to_album`
--

DROP TABLE IF EXISTS `image_to_album`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `image_to_album` (
  `imageID` int NOT NULL,
  `albumID` int NOT NULL,
  PRIMARY KEY (`imageID`,`albumID`),
  KEY `userID_idx1` (`albumID`),
  KEY `userID_iddx` (`albumID`),
  CONSTRAINT `albumID` FOREIGN KEY (`albumID`) REFERENCES `album` (`ID`),
  CONSTRAINT `imageID` FOREIGN KEY (`imageID`) REFERENCES `image` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `image_to_album`
--

LOCK TABLES `image_to_album` WRITE;
/*!40000 ALTER TABLE `image_to_album` DISABLE KEYS */;
INSERT INTO `image_to_album` VALUES (3,1),(4,1),(5,1),(6,1),(7,1),(8,1),(9,1),(10,1),(11,1),(12,1),(13,1),(14,1),(1,2),(2,3);
/*!40000 ALTER TABLE `image_to_album` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `ID` int NOT NULL AUTO_INCREMENT,
  `email` varchar(320) NOT NULL,
  `name` varchar(45) NOT NULL,
  `surname` varchar(45) NOT NULL,
  `password` varchar(45) NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `email_UNIQUE` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'milo.brontesi@gmail.com','milo','brontesi','admin'),(2,'pietro.fraternari@gmail.com','pietro','fraternari','admin'),(3,'andrea.colombo@gmail.com','andrea','colombo','admin'),(9,'federico.milani@mail.polimi.it','federico','milani','admin'),(12,'mambata1@mambata.it','mambata','sirda','mambata');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'progettotiw'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2022-06-29 13:15:15
