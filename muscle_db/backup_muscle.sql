-- MySQL dump 10.13  Distrib 5.5.49, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: muscle
-- ------------------------------------------------------
-- Server version	5.5.49-0ubuntu0.14.04.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `exercise`
--

DROP TABLE IF EXISTS `exercise`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `exercise` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `description` text,
  `image_path` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `exercise`
--

LOCK TABLES `exercise` WRITE;
/*!40000 ALTER TABLE `exercise` DISABLE KEYS */;
INSERT INTO `exercise` VALUES (1,'Chest press','Instructions:\\n 1. Sit upright with your back supported by the back pad.\\n 2. Hold the fixed handles at chest height; adjust the seat, if needed\\n 3. Press the handles forward until your arms are fully extended.\\n 4. Slowly return to the starting position.','chest_press.png'),(2,'Front Lat Pulldowns','Instructions:\\n 1. Sit facing away from machine with your knees locked under the roller pads.\\n 2. Grip the lat bar with your hands slightly wider than shoulder width, with your palms facing forward.\\n 3. Lean back slightly, and pull the bar down slowly until it touches your upper chest area.\\n 4. Slowly return to the starting position.','front_lat_pulldowns.png'),(3,'Leg extension','Instructions:\\n 1. Adjust the seat so that the pivot of the left extension/leg curl arms lines up with the pivot point of your knees.\\n 2. Adjust back pad angle, if needed\\n 3. Hook your legs over the upper roller pads and your feet under the lowest roller pads.\\n 4. Extend your legs forward, and hold your position momentarily.\\n 5. Slowly return to starting position.\\n','leg_extension.png'),(5,'Triceps pushdown','Instructions:\\n 1. Attach the easy curl bar to the high pulley.\\n 2. Stand facing towards the machine.\\n 3. Grip the bar with your hands 4 to 6 inch apart, palms facing forward.\\n 4. Bring the bar to chest height. Keep your arms tight against your body, and push the bar down slowly.\\n 5. Slowly return to the starting position.','triceps_pushdown.png'),(6,'Leg curl','Instructions:\\n 1. Stand facing the machine, and adjust the upper roller pads to hit just above your knees.\\n 2. Hook one foot under lower roller pad, and grip the press arm for balance.\\n 3. Curl your leg upward, and hold your position momentarily.\\n 4. Slowly return to the starting position','leg_curl.png'),(7,'Seated cable row','Instructions:\\n 1. Stand facing the machine, and adjust the upper roller pads to hit just above your knees.\\n 2. Hook one foot under lower roller pad, and grip the press arm for balance.\\n 3. Curl your leg upward, and hold your position momentarily.\\n 4. Slowly return to the starting position','seated_cable_row.png'),(8,'Front Lat Pullover','Instructions:\\n 1. Attach the chain to the high pulley, and then attach the straight bar to the end of the chain.\\n 2. Grab the straight bar with an overhand grip.\\n 3. Keep your arms straight and pull down.\\n 4. Return slowly.','front_lat_pullover.png'),(9,'Overhead Biceps Curl','Instructions:\\n 1. Attach the straight bar to the high pulley.\\n 2. Sit facing the machine and lock your knees under the knee hold-down pads.\\n 3. Grab the straight bar with an underhand grip.\\n 4. Concentrate on keeping your upper arm stationary while curling the straight bar behind your head.\\n 5. Return slowly','overhead_biceps_curl.png'),(10,'Triceps Extension From High Pulley','Instructions:\\n 1. Attach the straight bar to the high pulley.\\n 2. Sit in the seat leaning slightly forward and grab the straight bar with an overhand grip.\\n 3. Try to hold your upper arm locked into a horizontal position while extending your lower arm at the elbow.\\n 4. Return slowly.','triceps_extension_from_high_pulley.png'),(12,'Butterfly','Instructions:\\n 1. Sit on the machine with your back flat on the pad.\\n 2. Take hold of the handles. Tip: Your upper arms should be positioned parallel to the floor; adjust the machine accordingly. This will be your starting position.\\n 3. Push the handles together slowly as you squeeze your chest in the middle. Breathe out during this part of the motion and hold the contraction for a second.\\n 4. Return back to the starting position slowly as you inhale until your chest muscles are fully stretched.\\n 5. Repeat for the recommended amount of repetitions.','butterfly.png'),(13,'Back Lat Pulldowns','Instructions:\\n 1. Sit facing the machine with the thighs positioned under the pads, grasping the bar with a wide (wider than shoulder width) overhand grip.\\n 2. As you have both arms extended in front of you holding the bar at the chosen grip width, bring your torso and head forward (lean your torso slightly forward, keeping your neck and back straight).\\n 3. Think of an imaginary line from the center of the bar down to the back of your neck.\\n 4. Inhale and pull the bar down to the back of the neck, bringing the elbows alongside the body.\\n 5. Exhale at the end of the movement.\\n 6. Hold the contraction for a moment, then slowly return the bar all the way back to the starting position.','back_lat_pulldowns.png'),(14,'Inner Thigh','Instructions:\\n 1. Attach the padded cuff to the lower pulley.\\n 2. Stand sideways to the machine.\\n 3. Attach the cuff to the leg closest to the machine.\\n 4. Slowly scissor your leg across your body while keeping your body straight.\\n 5. Slowly return to the starting position.','inner_thigh.png'),(15,'Outer Thigh','Instructions:\\n 1. Attach the padded cuff to the lower pulley.\\n 2. Stand sideways to the machine.\\n 3. Attach the cuff to the leg furthest from the machine.\\n 4. While keeping your leg straight and positioned slightly in front of your body, lift your leg outward and upward as far as possible. Keep your body straight, and lead with your heel.\\n 5. Slowly return to the starting position.','outer_thigh.png'),(16,'Glute Kick','Instructions:\\n 1. Hook a leather ankle cuff to a low cable pulley and then attach the cuff to your ankle.\\n 2. Face the weight stack from a distance of about two feet, grasping the steel frame for support.\\n 3. While keeping your knees and hips bent slightly and your abs tight, contract your glutes to slowly \"kick\" the working leg back in a semicircular arc as high as it will comfortably go as you breathe out.\\n 4. Repeat for the recommended amount of repetitions.\\n 5. Switch legs and repeat the movement for the other side.','glute_kick.png');
/*!40000 ALTER TABLE `exercise` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `exercise_history`
--

DROP TABLE IF EXISTS `exercise_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `exercise_history` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `plan_id` int(11) DEFAULT NULL,
  `exercise_id` int(11) DEFAULT NULL,
  `datetime` datetime DEFAULT NULL,
  `weight` int(11) DEFAULT NULL,
  `number_of_repetitions` int(11) DEFAULT NULL,
  `average_intensity` double DEFAULT NULL,
  `average_rep_time` double DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `plan_id` (`plan_id`),
  KEY `exercise_id` (`exercise_id`),
  CONSTRAINT `exercise_history_ibfk_2` FOREIGN KEY (`exercise_id`) REFERENCES `exercise` (`id`),
  CONSTRAINT `exercise_history_ibfk_1` FOREIGN KEY (`plan_id`) REFERENCES `plan` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `exercise_history`
--

LOCK TABLES `exercise_history` WRITE;
/*!40000 ALTER TABLE `exercise_history` DISABLE KEYS */;
INSERT INTO `exercise_history` VALUES (1,1,1,'2017-04-19 23:10:28',20,5,1.5,0.7);
/*!40000 ALTER TABLE `exercise_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `exercise_muscle_group`
--

DROP TABLE IF EXISTS `exercise_muscle_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `exercise_muscle_group` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `exercise_id` int(11) DEFAULT NULL,
  `muscle_group_id` int(11) DEFAULT NULL,
  `primary_muscle` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`id`),
  KEY `exercise_id` (`exercise_id`),
  KEY `muscle_group_id` (`muscle_group_id`),
  CONSTRAINT `exercise_muscle_group_ibfk_2` FOREIGN KEY (`muscle_group_id`) REFERENCES `muscle_group` (`id`),
  CONSTRAINT `exercise_muscle_group_ibfk_1` FOREIGN KEY (`exercise_id`) REFERENCES `exercise` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `exercise_muscle_group`
--

LOCK TABLES `exercise_muscle_group` WRITE;
/*!40000 ALTER TABLE `exercise_muscle_group` DISABLE KEYS */;
INSERT INTO `exercise_muscle_group` VALUES (1,1,1,1),(2,2,2,1),(3,2,3,1),(4,3,5,1),(7,5,4,1),(8,6,5,1),(9,7,2,1),(10,8,2,1),(11,9,4,1),(12,10,4,1),(14,12,1,1),(15,13,2,1),(16,13,3,1),(17,14,5,1),(18,15,5,1),(19,16,5,1);
/*!40000 ALTER TABLE `exercise_muscle_group` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `exercise_muscle_type`
--

DROP TABLE IF EXISTS `exercise_muscle_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `exercise_muscle_type` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `exercise_id` int(11) DEFAULT NULL,
  `muscle_type_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `exercise_id` (`exercise_id`),
  KEY `muscle_type_id` (`muscle_type_id`),
  CONSTRAINT `exercise_muscle_type_ibfk_2` FOREIGN KEY (`muscle_type_id`) REFERENCES `muscle_type` (`id`),
  CONSTRAINT `exercise_muscle_type_ibfk_1` FOREIGN KEY (`exercise_id`) REFERENCES `exercise` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `exercise_muscle_type`
--

LOCK TABLES `exercise_muscle_type` WRITE;
/*!40000 ALTER TABLE `exercise_muscle_type` DISABLE KEYS */;
INSERT INTO `exercise_muscle_type` VALUES (1,1,1),(2,1,2),(3,1,3),(4,2,4),(5,2,5),(6,3,6),(8,5,3),(9,6,7),(10,7,8),(11,8,4),(12,8,8),(13,8,9),(14,8,5),(15,9,5),(16,9,10),(17,10,3),(18,10,10),(20,12,1),(21,13,4),(22,13,5),(23,14,6),(24,15,6),(25,16,12);
/*!40000 ALTER TABLE `exercise_muscle_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `exercises_in_plan`
--

DROP TABLE IF EXISTS `exercises_in_plan`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `exercises_in_plan` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `plan_id` int(11) DEFAULT NULL,
  `exercise_id` int(11) DEFAULT NULL,
  `number_of_sets` int(11) DEFAULT NULL,
  `number_of_reps` int(11) DEFAULT NULL,
  `weight` int(11) DEFAULT NULL,
  `subplan_number` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `plan_id` (`plan_id`),
  KEY `exercise_id` (`exercise_id`),
  KEY `day_of_week_id` (`subplan_number`),
  CONSTRAINT `exercises_in_plan_ibfk_1` FOREIGN KEY (`plan_id`) REFERENCES `plan` (`id`),
  CONSTRAINT `exercises_in_plan_ibfk_2` FOREIGN KEY (`exercise_id`) REFERENCES `exercise` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `exercises_in_plan`
--

LOCK TABLES `exercises_in_plan` WRITE;
/*!40000 ALTER TABLE `exercises_in_plan` DISABLE KEYS */;
INSERT INTO `exercises_in_plan` VALUES (1,1,1,3,10,50,1),(2,1,2,3,8,40,2),(3,1,3,3,10,55,3);
/*!40000 ALTER TABLE `exercises_in_plan` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `muscle_group`
--

DROP TABLE IF EXISTS `muscle_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `muscle_group` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `muscle_group`
--

LOCK TABLES `muscle_group` WRITE;
/*!40000 ALTER TABLE `muscle_group` DISABLE KEYS */;
INSERT INTO `muscle_group` VALUES (1,'Chest'),(2,'Back'),(3,'Abs'),(4,'Arms'),(5,'Legs');
/*!40000 ALTER TABLE `muscle_group` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `muscle_type`
--

DROP TABLE IF EXISTS `muscle_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `muscle_type` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `muscle_type`
--

LOCK TABLES `muscle_type` WRITE;
/*!40000 ALTER TABLE `muscle_type` DISABLE KEYS */;
INSERT INTO `muscle_type` VALUES (1,'Pectorals'),(2,'Deltroid'),(3,'Triceps'),(4,'Latissimus dorsi'),(5,'Biceps'),(6,'Quadriceps'),(7,'Harmstrings'),(8,'Rhomboideus'),(9,'Trapezius'),(10,'Brachioradials'),(11,'Rectus abdominus'),(12,'Gluteals');
/*!40000 ALTER TABLE `muscle_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `plan`
--

DROP TABLE IF EXISTS `plan`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `plan` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) DEFAULT NULL,
  `plan_name` varchar(255) DEFAULT NULL,
  `start_date` datetime DEFAULT NULL,
  `end_date` datetime DEFAULT NULL,
  `plan_type_id` int(11) DEFAULT NULL,
  `status` int(1) DEFAULT '1',
  PRIMARY KEY (`id`),
  KEY `status` (`status`),
  KEY `plan_type_id` (`plan_type_id`),
  CONSTRAINT `plan_ibfk_1` FOREIGN KEY (`plan_type_id`) REFERENCES `plan_type` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `plan`
--

LOCK TABLES `plan` WRITE;
/*!40000 ALTER TABLE `plan` DISABLE KEYS */;
INSERT INTO `plan` VALUES (1,NULL,'My plan','2017-04-19 22:59:55','2017-06-23 22:59:57',1,1);
/*!40000 ALTER TABLE `plan` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `plan_type`
--

DROP TABLE IF EXISTS `plan_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `plan_type` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `plan_type` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `plan_type`
--

LOCK TABLES `plan_type` WRITE;
/*!40000 ALTER TABLE `plan_type` DISABLE KEYS */;
INSERT INTO `plan_type` VALUES (1,'Get mass');
/*!40000 ALTER TABLE `plan_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `Email` varchar(255) NOT NULL DEFAULT '',
  `Name` varchar(128) NOT NULL,
  `Password` text NOT NULL,
  `Date_of_birth` date NOT NULL,
  `Gender` tinyint(1) NOT NULL,
  `Height` smallint(6) NOT NULL,
  `Weight` float(6,2) NOT NULL,
  `Profile_image` text,
  `status` int(1) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'adeus@ua.pt','adeus@ua.pt','$pbkdf2-sha256$200000$Zew9x3gPIYRQynkvBSDkXA$Q0msijSprAfabumWqDZSMIOHeoVhuxaEjhpuO5TLWdc','1992-03-29',1,170,70.00,NULL,1),(2,'david@ua.pt',' David','$pbkdf2-sha256$200000$f0.pdQ7B2BsDAGCsNaZUag$M/Cin0J4vLQfDr0fWXbQDNsk5QGW/7XIfTHI1/mGPMo','1996-09-27',1,170,75.00,'david.jpg',1),(3,'hdjd@ua.pt','hdjd@ua.pt','$pbkdf2-sha256$200000$gJAyRgiBkLL2nnNO6T3HuA$oDOzxirNuLIED8QBaLAPGrbwur1k9cJ4q/1BpsZKbCs','1992-03-29',1,170,70.00,NULL,1),(4,'hjdiijiirrydja@ua.pt','hjdiijiirrydja@ua.pt','$pbkdf2-sha256$200000$c47xvheC8D5njDEG4FzLmQ$EasKNAJ3is4Cd2kVMQOm4kcCMn7MfGkD0CXnw3Q1L8A','1992-03-29',1,170,70.00,NULL,1),(5,'la@ua.pt','la@ua.pt','$pbkdf2-sha256$200000$XKtVyvl/D8G4955TipHSGg$UEfYExjnfY2QdqkIL/j6N.Ofqp1MER45zZ5Dx76J/Jw','1992-03-29',0,170,70.00,NULL,1),(6,'obama@ua.pt','obama@ua.pt','$pbkdf2-sha256$200000$OUeo9R7jPIdQyrl3jhHCmA$xpyqDExZvZCqWNrvE1eCme.p1M.FAlFAh1IDjfUb4/M','1992-03-29',1,170,70.00,NULL,1),(7,'oi@ua.pt','oi@ua.pt','$pbkdf2-sha256$200000$EaJU6l2LUUop5TzH.N87Zw$1BdQ7Qv.Gf5cYfnVoyvxWpaakZc5enoSkQxvtRw15Q4','1992-03-29',1,170,70.00,NULL,1),(8,'ojdnsla1@ua.pt','ojdnsla1@ua.pt','$pbkdf2-sha256$200000$MQYAYGzNWUupdS6FEELIeQ$FoytuYTA.Ai7M1nspC4mMXYylTjkQj6ttA6Hfz5KEtA','1992-03-29',0,170,70.00,NULL,1),(9,'ola1@ua.pt','ola1@ua.pt','$pbkdf2-sha256$200000$/n8PYYwRYkzJ.V9rbQ2B0A$Dvkm7ApCocQhni1i9X/p8JJcBmip6xE8PADKZzt4MvM','1992-03-29',0,170,70.00,NULL,1),(10,'ola@ua.pt','John Though','$pbkdf2-sha256$200000$/N/7v1fKmbO2thZi7N17bw$knrHDUu.n6rhYm8MGEOZN4RQzEL67ohJRlMpsNGKUQ8','1996-06-25',0,33,80.50,'user_ola@ua.pt_profile_pic.jpeg',1),(11,'oldda@ua.pt','oldda@ua.pt','$pbkdf2-sha256$200000$d875fy8lpHTufW8NAWDMmQ$tmPGy1dTVx5Lg6D9xW3rphjEDVkzYLYsKRCfJGngImQ','1992-03-29',1,170,70.00,NULL,1),(12,'olggdhja@ua.pt','olggdhja@ua.pt','$pbkdf2-sha256$200000$ubf2/r83ZmwNYWytFcLY.w$TENpY9hxtIArIyNYs2u5NYeri0NcQw6rBJPXkPOcKHI','1992-03-29',1,170,70.00,NULL,1),(13,'olhfia@ua.pt','olhfia@ua.pt','$pbkdf2-sha256$200000$b82Z0zrnfG8N4VzLWesdIw$WOOEtD4rHl3QpEuufnkorQ1MVtgBlj/jBqmcqJVCe8U','1992-03-29',1,170,70.00,NULL,1),(14,'olra@ua.pt','olra@ua.pt','$pbkdf2-sha256$200000$cI7xPqd0rrV27p1TqjWmlA$c3otha5b1TAyrXCWScbHWmSnyhKJObR0l8ZuPRY/.YE','1992-03-29',1,170,70.00,NULL,1),(15,'orfla@ua.pt','orfla@ua.pt','$pbkdf2-sha256$200000$.38PwVhLyXlvbS3F2JtTyg$ndh.4zWMZBlqxe4eR6bHTkVEi3igbMjmBVwk2sfkHj8','1992-03-29',1,170,70.00,NULL,1),(16,'orla@ua.pt','orla@ua.pt','$pbkdf2-sha256$200000$T8nZOycE4Fwr5bw3hlAq5Q$T5L7G.4uRQ4tTAKwYTI.lf7yBf71ebTV2DBwVCM9veY','1992-03-29',1,170,70.00,NULL,1),(17,'sun@ua.pt','sun@ua.pt','$pbkdf2-sha256$200000$CeEcY6xVyhljDCFkrFUK4Q$Cq9dyqWZ5rssL.PAU7y2fNojK5YqXwynKl5ZcGNxLI0','1992-04-04',1,170,70.00,NULL,1),(18,'theVid@ua.pt',' David','$pbkdf2-sha256$200000$jXEuBYDQGqP0/v8fQ6iV8g$2OXfZrMAdeSliR4AztNs04fCnvviK7T9e5q/2TFQsiM','1996-09-27',1,170,75.00,'david.jpg',1),(19,'theVidAllMayThe@ua.pt',' David','$pbkdf2-sha256$200000$UUpJqVWKESKEkLJWSkkpRQ$Zs3H0RwvAyatDHkGMyyDH2j6iqntS/ugsX3q39ABa6A','0000-00-00',1,170,75.00,'david.jpg',1),(20,'tiago@ras.pt','tiago@ras.pt','$pbkdf2-sha256$200000$k/K.l7LW2vsfg1BKCSHknA$WQD5LecgU8YLJJasXjXzStx5i.BZBwjBPJLM1858HRI','1992-04-04',1,170,70.00,NULL,1),(21,'baf@ua.pt','baf@ua.pt','$pbkdf2-sha256$200000$jLG2FiKE8P5f632PMcaYMw$0mPbZbZqndh86Qtfm8unoulNBEQHJmHB/fA8bsjncVA','1992-04-23',1,170,70.00,NULL,NULL),(22,'jon@ua.pt','jon dough','$pbkdf2-sha256$200000$7l0LQYiRUooxRugdIwQAAA$ZrSRpcL1vrewELXSV8WPyKI8MU7h643diCrsO6YGyA8','1994-04-23',0,175,60.80,'user_jon@ua.pt_profile_pic.jpeg',NULL),(23,'hjjshs@ua.pt','udhsshs','$pbkdf2-sha256$200000$j/Heu5cSIqR0bg3BWMt5bw$Wm/AUABcP0PpLelB2yiq4GDMHcZtQI9zapiwa.tpFCE','1996-03-23',1,180,105.96,'user_hjjshs@ua.pt_profile_pic.jpeg',NULL),(24,'Gon@ua.pt','Gon@ua.pt','$pbkdf2-sha256$200000$9F6LUWpNSYkxBgBgDKHU.g$tCLvZqCBgXjI.rjQboNwwpStYiudLDoZdzPCcCC6nN8','1992-04-23',1,170,70.00,NULL,NULL),(25,'r2112@ua.pt','batatas','$pbkdf2-sha256$200000$/T9HqHXOWWttzbkXYsy5tw$2LdGQ2i3ymR3IJgXWJme11V1x348qMmdpfqSrebS9H8','1976-03-23',0,170,53.00,'user_r2112@ua.pt_profile_pic.jpeg',NULL),(26,'theVidAllMayTheassada@ua.pt',' David','$pbkdf2-sha256$200000$pJQSgrD2fm.NEWKsNQZACA$Nr5pm3JtZKgU5OjNj3SVByF2Q5JgSbr//EiJXU3Ld00','1996-09-27',1,170,75.00,NULL,1);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_weight_history`
--

DROP TABLE IF EXISTS `user_weight_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_weight_history` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) DEFAULT NULL,
  `weight` double DEFAULT NULL,
  `date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `user_weight_history_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_weight_history`
--

LOCK TABLES `user_weight_history` WRITE;
/*!40000 ALTER TABLE `user_weight_history` DISABLE KEYS */;
INSERT INTO `user_weight_history` VALUES (1,1,72,'2017-04-17 23:02:12'),(2,1,76,'2017-04-19 23:02:21');
/*!40000 ALTER TABLE `user_weight_history` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2017-04-25 18:28:29
