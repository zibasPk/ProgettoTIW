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
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comment`
--

LOCK TABLES `comment` WRITE;
/*!40000 ALTER TABLE `comment` DISABLE KEYS */;
INSERT INTO `comment` VALUES (1,1,3,'i like it!');
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
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `image`
--

LOCK TABLES `image` WRITE;
/*!40000 ALTER TABLE `image` DISABLE KEYS */;
INSERT INTO `image` VALUES (1,1,'Girasoli','/img/girasoli.jpg','2021-04-18','girasoli al tramonto'),(2,3,'Autoritratto di Picasso','/img/autopicasso.jpg','1980-04-18','Autoritratto del pittore Pablo Picasso'),(3,2,'facciata teatro la scala','/img/lascala.jpg','1980-04-18','Facciata del teatro la scala di sera'),(4,1,'foto famiglia 1','/img/famiglia.jpg','2022-05-11','una foto famiglia'),(5,1,'foto famiglia 2','/img/famiglia2.jpg','2022-05-11','una foto famiglia'),(6,1,'foto famiglia 3','/img/famiglia3.jpg','2022-05-11','una foto famiglia'),(7,1,'foto famiglia 4','/img/famiglia4.jpg','2022-05-11','una foto famiglia'),(8,1,'foto famiglia 5','/img/famiglia5.jpg','2022-05-11','una foto famiglia'),(9,1,'foto famiglia 6','/img/famiglia6.jpg','2022-05-11','una foto famiglia');
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
INSERT INTO `image_to_album` VALUES (3,1),(4,1),(5,1),(6,1),(7,1),(8,1),(9,1),(1,2),(2,3);
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
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'ginomicade@gmail.com','gino','malamanno','nesmonemane'),(2,'ginomicade@gmail.com','ginaio','malamasdanno','nesmonemane'),(3,'capoAndrea@gmail.com','andrea','colombo','zione'),(4,'capoMilo@gmail.com','milo','brontesi','nazione'),(5,'matteo.montesi@gmail.com','matteo','montesi','maledetto'),(6,'machioni@gmail.com','marchio','machioni@gmail.com','marchioni'),(7,'jino@gmail.com','jino','jino@gmail.com','jino'),(8,'ginan','givanni','ginan','ginon'),(9,'a','a','a','a');
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

-- Dump completed on 2022-05-11 13:26:34
