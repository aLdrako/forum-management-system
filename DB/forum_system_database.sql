-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server version:               10.11.1-MariaDB - mariadb.org binary distribution
-- Server OS:                    Win64
-- HeidiSQL Version:             11.3.0.6295
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Dumping database structure for forum_system
CREATE DATABASE IF NOT EXISTS `forum_system` /*!40100 DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci */;
USE `forum_system`;

-- Dumping structure for table forum_system.comments
CREATE TABLE IF NOT EXISTS `comments` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `post_id` bigint(20) NOT NULL,
    `user_id` bigint(20) NOT NULL,
    `content` text DEFAULT NULL,
    `date_created` datetime NOT NULL,
    PRIMARY KEY (`id`),
    KEY `comments_posts_fk` (`post_id`),
    KEY `comments_users_fk` (`user_id`),
    CONSTRAINT `comments_posts_fk` FOREIGN KEY (`post_id`) REFERENCES `posts` (`id`),
    CONSTRAINT `comments_users_fk` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
    ) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

-- Dumping data for table forum_system.comments: ~5 rows (approximately)
/*!40000 ALTER TABLE `comments` DISABLE KEYS */;
INSERT INTO `comments` (`id`, `post_id`, `user_id`, `content`, `date_created`) VALUES
                        (1, 4, 7, 'Yeah, I liked this movies', '2023-02-15 12:22:12'),
                        (2, 5, 7, 'My favourite quote', '2023-02-18 12:23:00'),
                        (3, 6, 6, 'Easy to say, hard to do it', '2023-02-21 12:22:10'),
                        (4, 6, 8, 'Great guide, thank you', '2023-02-19 12:25:06'),
                        (5, 1, 4, 'Sound like a good story', '2023-02-21 12:25:58');
/*!40000 ALTER TABLE `comments` ENABLE KEYS */;

-- Dumping structure for table forum_system.permissions
CREATE TABLE IF NOT EXISTS `permissions` (
    `user_id` bigint(20) NOT NULL,
    `is_blocked` tinyint(1) NOT NULL DEFAULT 0,
    `is_admin` tinyint(1) NOT NULL DEFAULT 0,
    PRIMARY KEY (`user_id`),
    CONSTRAINT `features_users_fk` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

-- Dumping data for table forum_system.permissions: ~7 rows (approximately)
/*!40000 ALTER TABLE `permissions` DISABLE KEYS */;
INSERT INTO `permissions` (`user_id`, `is_blocked`, `is_admin`) VALUES
                                                                        (2, 0, 0),
                                                                        (3, 0, 0),
                                                                        (4, 0, 1),
                                                                        (5, 1, 0),
                                                                        (6, 0, 0),
                                                                        (7, 0, 0),
                                                                        (8, 0, 0);
/*!40000 ALTER TABLE `permissions` ENABLE KEYS */;

-- Dumping structure for table forum_system.phones
CREATE TABLE IF NOT EXISTS `phones` (
    `user_id` bigint(20) NOT NULL,
    `phone_number` varchar(16) DEFAULT NULL,
    PRIMARY KEY (`user_id`),
    CONSTRAINT `phones_users_fk` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

-- Dumping data for table forum_system.phones: ~1 rows (approximately)
/*!40000 ALTER TABLE `phones` DISABLE KEYS */;
INSERT INTO `phones` (`user_id`, `phone_number`) VALUES
                                                        (4, '+123-222-5555');
/*!40000 ALTER TABLE `phones` ENABLE KEYS */;

-- Dumping structure for table forum_system.photos
CREATE TABLE IF NOT EXISTS `photos` (
    `user_id` bigint(20) NOT NULL,
    `photo` blob DEFAULT NULL,
    PRIMARY KEY (`user_id`),
    CONSTRAINT `photos_users_fk` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

-- Dumping data for table forum_system.photos: ~0 rows (approximately)
/*!40000 ALTER TABLE `photos` DISABLE KEYS */;
/*!40000 ALTER TABLE `photos` ENABLE KEYS */;

-- Dumping structure for table forum_system.posts
CREATE TABLE IF NOT EXISTS `posts` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `title` varchar(100) NOT NULL,
    `content` text DEFAULT NULL,
    `likes` int(11) DEFAULT NULL,
    `user_id` bigint(20) NOT NULL,
    `date_created` datetime NOT NULL,
    PRIMARY KEY (`id`),
    KEY `posts_users_fk` (`user_id`),
    CONSTRAINT `posts_users_fk` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
    ) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

-- Dumping data for table forum_system.posts: ~7 rows (approximately)
/*!40000 ALTER TABLE `posts` DISABLE KEYS */;
INSERT INTO `posts` (`id`, `title`, `content`, `likes`, `user_id`, `date_created`) VALUES
                        (1, 'Old times', 'Once upon a time there was a...', 13, 2, '2023-02-01 12:12:10'),
                        (2, 'Ice and Fire', 'They were destroying everything on their path...', 3, 2, '2023-02-04 12:12:59'),
                        (3, 'Self observation', 'This practice requires your full attention to details', 20, 4, '2023-01-01 12:14:25'),
                        (4, 'Batman quotes', 'The night is darkest just before the dawn', 50, 3, '2023-02-21 12:14:51'),
                        (5, 'Batman quotes', 'Your anger gives you great power', 25, 3, '2023-02-21 12:16:20'),
                        (6, 'Learn Java in no time', 'You can start from Oracle documentation, then take some Udemy courses, then start practicing... ', 5, 8, '2023-01-21 12:20:42'),
                        (7, 'Test title goes here', 'Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industrys standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book.', 99, 4, '2023-01-10 12:26:34');
/*!40000 ALTER TABLE `posts` ENABLE KEYS */;

-- Dumping structure for table forum_system.posts_tags
CREATE TABLE IF NOT EXISTS `posts_tags` (
    `post_id` bigint(20) NOT NULL,
    `tag_id` bigint(20) NOT NULL,
    PRIMARY KEY (`post_id`,`tag_id`),
    KEY `posts_tags_tags_fk` (`tag_id`),
    CONSTRAINT `posts_tags_posts_fk` FOREIGN KEY (`post_id`) REFERENCES `posts` (`id`),
    CONSTRAINT `posts_tags_tags_fk` FOREIGN KEY (`tag_id`) REFERENCES `tags` (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

-- Dumping data for table forum_system.posts_tags: ~0 rows (approximately)
/*!40000 ALTER TABLE `posts_tags` DISABLE KEYS */;
/*!40000 ALTER TABLE `posts_tags` ENABLE KEYS */;

-- Dumping structure for table forum_system.tags
CREATE TABLE IF NOT EXISTS `tags` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `name` varchar(16) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `tags_pk` (`name`)
    ) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

-- Dumping data for table forum_system.tags: ~0 rows (approximately)
/*!40000 ALTER TABLE `tags` DISABLE KEYS */;
/*!40000 ALTER TABLE `tags` ENABLE KEYS */;

-- Dumping structure for table forum_system.users
CREATE TABLE IF NOT EXISTS `users` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `first_name` varchar(32) NOT NULL,
    `last_name` varchar(32) NOT NULL,
    `email` varchar(50) NOT NULL,
    `username` varchar(50) NOT NULL,
    `password` varchar(50) NOT NULL,
    `join_date` datetime NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `users_pk2` (`email`)
    ) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=latin1 COLLATE=latin1_swedish_ci;

-- Dumping data for table forum_system.users: ~7 rows (approximately)
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` (`id`, `first_name`, `last_name`, `email`, `username`, `password`, `join_date`) VALUES
                        (2, 'Frodo', 'Baggins', 'frodo@mail.com', 'frodo1', 'qwerty1', '2023-01-01 19:30:59'),
                        (3, 'Bruce', 'Wayne', 'batman@yahoo.com', 'batman', 'bat-man13', '2023-02-21 11:55:40'),
                        (4, 'Admin', 'Admin', 'admin@mail.com', 'admin', 'admin', '2023-01-01 00:00:00'),
                        (5, 'Spammer', 'Spammer', 'spam@mail.com', 'spammer', 'spammer', '2023-02-21 12:01:00'),
                        (6, 'John', 'Smith', 'johnsmith@mail.com', 'johnny', 'john123', '2023-01-21 12:04:17'),
                        (7, 'Kate', 'Beckinsale', 'katy@yahoo.com', 'katybeck', 'pass0000', '2023-02-06 12:05:29'),
                        (8, 'Vika', 'Mariot', 'viky@gmail.com', 'vikky', 'v123ky', '2023-01-16 12:07:20');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
