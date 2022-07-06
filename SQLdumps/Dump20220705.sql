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
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `album`
--

LOCK TABLES `album` WRITE;
/*!40000 ALTER TABLE `album` DISABLE KEYS */;
INSERT INTO `album` VALUES (10,'2022-07-05','Test Album 1 ',16),(11,'2022-07-05','Test album 2',16),(12,'2022-07-05','Test album 3',16),(13,'2022-07-05','Test Album 3',17);
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
  PRIMARY KEY (`ID`),
  KEY `userID_idx` (`userID`),
  KEY `imageID_idx` (`imageID`),
  CONSTRAINT `albmId2` FOREIGN KEY (`imageID`) REFERENCES `image` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `usrId2` FOREIGN KEY (`userID`) REFERENCES `user` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comment`
--

LOCK TABLES `comment` WRITE;
/*!40000 ALTER TABLE `comment` DISABLE KEYS */;
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
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `image`
--

LOCK TABLES `image` WRITE;
/*!40000 ALTER TABLE `image` DISABLE KEYS */;
INSERT INTO `image` VALUES (1,16,'ritratto di pablo picasso','/img/autopicasso.jpg','2021-03-20','autoritratto del pittore Pablo Picasso'),(2,16,'foto del Canada','/img/canada.jpg','2002-01-22','foto del paesaggio dello stato del Canada'),(3,16,'foto delle Canarie','/img/canarie.jpg','2010-12-31','foto del paesaggio delle Canarie'),(4,16,'foto delle Dolomiti','/img/dolomiti.jpg','2013-12-31','foto del paesaggio delle Dolimiti'),(5,16,'foto di un gatto','/img/gatto.jpg','2016-12-31','foto di un cucciolo di gatto'),(6,16,'foto di un campo di girasoli','/img/girasoli.jpg','2016-12-31','foto di un bellissimo campo di girasoli'),(7,16,'paesaggio islandese','/img/islanda.jpg','2018-08-10','immagine del paesaggio islandese'),(8,16,'teatro la scala','/img/lascala.jpg','2008-08-10','immagine del famoso teatro milanese'),(9,16,'tigre libera','/img/tigre.jpg','2000-08-10','foto di un esemplare di tigre'),(10,16,'logo di linux','/img/linuxlogo.jpg','1999-10-01','il pinguino più famoso di tutti'),(11,16,'foto della luna','/img/luna.jpg','2003-10-01','luna fotografata con telescopio digitale'),(12,16,'paesaggio urbano','/img/stradapalazzi.jpg','2016-12-31','paesaggio urbano americano'),(13,16,'paesaggio umbro','/img/umbria.jpg','2016-12-31','paesaggio umbro in estate'),(14,16,'tempio antico','/img/tempio.jpg','1998-12-31','tempio antico sperduto'),(15,17,'capodanno','/img/scintilla.jpg','2014-12-31','foto capodanno 2014'),(16,17,'cubo astratto','/img/cubo.jpg','2014-12-31','opera di arte moderna'),(17,17,'campi coltivati','/img/campi.jpg','2014-12-31','foto di campi scattata da un aereo'),(18,17,'campeggio sotto le stelle','/img/tenda.jpg','2014-12-31','foto di una tenda sotto il cielo stellato'),(19,17,'dune buggy','/img/quad.jpg','2020-10-21','ultimo modello del veicolo fuoristrada'),(20,17,'il mondo moderno','/img/semaforo.jpg','2020-10-21','foto esposta al MOMA'),(21,17,'fusione tra arte e natura','/img/statuatesta.jpg','2020-10-21','gabbiano che si poggia su una statua'),(22,17,'estate','/img/spiaggia.jpg','2021-08-10','spiaggia del sud italia'),(23,17,'cupola vista da lontano','/img/cupola.jpg','2021-08-10','cupola che spicca tra i palazzi'),(24,17,'foglie con rugiada','/img/rugiada.jpg','2019-08-10','foglie al mattino con la rugiada');
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
  KEY `userID_iddx` (`albumID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `image_to_album`
--

LOCK TABLES `image_to_album` WRITE;
/*!40000 ALTER TABLE `image_to_album` DISABLE KEYS */;
INSERT INTO `image_to_album` VALUES (1,10),(2,10),(3,10),(4,10),(5,10),(6,10),(7,10),(8,10),(9,10),(10,10),(11,10),(12,11),(13,11),(14,11);
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
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (16,'test@gmail.com','utente','base','admin'),(17,'administrator@gmail.com','admin','prof','admin');
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

-- Dump completed on 2022-07-05 18:11:10
