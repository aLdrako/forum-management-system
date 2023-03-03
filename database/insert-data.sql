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

-- Dumping data for table forum_system.comments: ~12 rows (approximately)
/*!40000 ALTER TABLE `comments` DISABLE KEYS */;
INSERT INTO `comments` (`id`, `post_id`, `user_id`, `content`, `date_created`) VALUES
    (1, 1, 1, 'I\'ve been practicing mindfulness for a few weeks now and I already feel more relaxed and focused.', '2023-02-11 12:43:57'),
    (2, 1, 6, 'I\'ve tried a few different meditation apps, but the mindfulness exercises in this book really resonated with me.', '2023-02-12 08:44:53'),
    (3, 2, 6, 'Virtual reality gaming is so much more fun than regular gaming. It\'s like you\'re actually in the game!', '2023-02-13 14:54:25'),
    (4, 3, 8, 'I used to be intimidated by cooking, but after taking a cooking class, I feel much more confident in the kitchen.', '2023-02-13 16:11:21'),
    (5, 3, 7, 'Learning to cook has been such a rewarding experience. I love being able to create delicious meals for myself and my family.', '2023-02-14 12:46:56'),
    (6, 4, 9, 'I took an astronomy course in college and it really opened my eyes to the beauty and complexity of the universe.', '2023-02-16 12:47:16'),
    (7, 3, 8, 'I\'ve been following this cooking blog for a few months now and I\'ve learned so many new techniques and recipes.', '2023-02-17 09:50:11'),
    (8, 8, 10, 'It\'s amazing how much we can learn from the art, literature, and philosophy of ancient civilizations. Their ideas still resonate with us today.', '2023-02-17 12:49:04'),
    (9, 8, 9, 'Learning about ancient civilizations has given me a greater appreciation for the history of our world and the people who came before us.', '2023-02-18 12:49:46'),
    (10, 7, 10, 'I\'ve been following this motivational speaker and his messages about positive thinking have really resonated with me.', '2023-02-19 12:52:06'),
    (12, 11, 13, 'Testing testing testing comment', '2023-02-20 00:00:00'),
    (13, 12, 14, 'Another testing comment goes here', '2023-02-20 00:00:00');
/*!40000 ALTER TABLE `comments` ENABLE KEYS */;

-- Dumping data for table forum_system.likes: ~6 rows (approximately)
/*!40000 ALTER TABLE `likes` DISABLE KEYS */;
INSERT INTO `likes` (`user_id`, `post_id`) VALUES
                                               (1, 1),
                                               (1, 2),
                                               (6, 5),
                                               (7, 5),
                                               (10, 10),
                                               (14, 12);
/*!40000 ALTER TABLE `likes` ENABLE KEYS */;

-- Dumping data for table forum_system.permissions: ~15 rows (approximately)
/*!40000 ALTER TABLE `permissions` DISABLE KEYS */;
INSERT INTO `permissions` (`user_id`, `is_deleted`, `is_blocked`, `is_admin`) VALUES
                                                                                  (1, 0, 0, 1),
                                                                                  (2, 0, 0, 0),
                                                                                  (3, 0, 0, 0),
                                                                                  (4, 0, 0, 0),
                                                                                  (5, 0, 0, 0),
                                                                                  (6, 0, 0, 0),
                                                                                  (7, 0, 0, 0),
                                                                                  (8, 0, 0, 0),
                                                                                  (9, 0, 0, 0),
                                                                                  (10, 0, 0, 0),
                                                                                  (11, 0, 1, 0),
                                                                                  (12, 0, 1, 1),
                                                                                  (13, 1, 0, 0),
                                                                                  (14, 0, 0, 1),
                                                                                  (15, 1, 1, 1);
/*!40000 ALTER TABLE `permissions` ENABLE KEYS */;

-- Dumping data for table forum_system.phones: ~1 rows (approximately)
/*!40000 ALTER TABLE `phones` DISABLE KEYS */;
INSERT INTO `phones` (`user_id`, `phone_number`) VALUES
    (1, '347-173-8817');
/*!40000 ALTER TABLE `phones` ENABLE KEYS */;

-- Dumping data for table forum_system.photos: ~0 rows (approximately)
/*!40000 ALTER TABLE `photos` DISABLE KEYS */;
/*!40000 ALTER TABLE `photos` ENABLE KEYS */;

-- Dumping data for table forum_system.posts: ~12 rows (approximately)
/*!40000 ALTER TABLE `posts` DISABLE KEYS */;
INSERT INTO `posts` (`id`, `title`, `content`, `user_id`, `date_created`) VALUES
    (1, 'The Art of Mindfulness', 'Mindfulness is the practice of being present in the moment and fully engaged in what you are doing. By cultivating mindfulness, you can reduce stress, improve your focus, and increase your overall well-being.', 1, '2023-01-01 12:00:00'),
    (2, 'Exploring the World of Virtual Reality', 'Virtual reality is a technology that allows users to experience immersive environments that look and feel like the real world. From gaming to education to therapy, virtual reality is being used in a wide variety of applications.', 6, '2023-02-10 10:00:00'),
    (3, 'Mastering the Basics of Cooking', 'Cooking is a fundamental life skill that can bring joy and nourishment to yourself and others. By learning the basics of cooking, you can expand your culinary repertoire and impress your friends and family.', 6, '2023-02-10 15:30:00'),
    (4, 'Discovering the Wonders of the Solar System', 'The solar system is home to a fascinating array of planets, moons, asteroids, and comets. By exploring the wonders of the solar system, you can gain a deeper appreciation for our place in the universe.', 7, '2023-02-11 10:00:30'),
    (5, 'Building a Successful Online Business', 'The internet has opened up countless opportunities for entrepreneurs to start and grow their own businesses. By learning how to build a successful online business, you can create a source of income and freedom for yourself.', 8, '2023-02-11 15:35:00'),
    (6, 'Navigating the Challenges of Parenthood', 'Parenthood is a rewarding and challenging experience that requires patience, compassion, and resilience. By learning how to navigate the challenges of parenthood, you can raise happy, healthy children and create a loving family', 8, '2023-02-15 13:40:30'),
    (7, 'The Power of Positive Thinking', 'Positive thinking is a mindset that focuses on seeing the good in every situation and believing in your own abilities. By harnessing the power of positive thinking, you can overcome obstacles, achieve your goals, and live a happier life.\n"Exploring the History of Ancient Civilizations', 9, '2023-02-15 20:25:35'),
    (8, 'Exploring the History of Ancient Civilizations', 'Ancient civilizations like Egypt, Greece, and Rome have left behind a rich legacy of art, architecture, literature, and philosophy. By exploring the history of ancient civilizations, you can gain a deeper understanding of the human experience and the roots of our modern culture.', 8, '2023-02-16 12:36:58'),
    (9, 'Achieving Peak Physical Fitness', 'Physical fitness is a key component of a healthy and fulfilling life. By setting fitness goals and following a disciplined training regimen, you can achieve peak physical fitness and enjoy the many benefits that come with it.', 10, '2023-02-17 12:05:25'),
    (10, 'Unlocking the Secrets of the Human Mind', 'The human mind is a complex and mysterious thing that has fascinated philosophers, scientists, and artists for centuries. By unlocking the secrets of the human mind, you can gain insight into your own thoughts and emotions, as well as those of others.', 8, '2023-02-18 12:25:39'),
    (11, 'Some title posted by deleted user', 'Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry\'s standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book.', 13, '2023-02-20 12:40:25'),
    (12, 'Another random title', 'Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium.', 14, '2023-02-23 12:58:01');
/*!40000 ALTER TABLE `posts` ENABLE KEYS */;

-- Dumping data for table forum_system.posts_tags: ~6 rows (approximately)
/*!40000 ALTER TABLE `posts_tags` DISABLE KEYS */;
INSERT INTO `posts_tags` (`post_id`, `tag_id`) VALUES
                                                   (1, 1),
                                                   (2, 4),
                                                   (4, 1),
                                                   (6, 1),
                                                   (6, 3),
                                                   (12, 2);
/*!40000 ALTER TABLE `posts_tags` ENABLE KEYS */;

-- Dumping data for table forum_system.tags: ~5 rows (approximately)
/*!40000 ALTER TABLE `tags` DISABLE KEYS */;
INSERT INTO `tags` (`id`, `name`) VALUES
                                      (2, 'concept'),
                                      (5, 'focus'),
                                      (3, 'global'),
                                      (1, 'knowledge'),
                                      (4, 'product');
/*!40000 ALTER TABLE `tags` ENABLE KEYS */;

-- Dumping data for table forum_system.users: ~15 rows (approximately)
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` (`id`, `first_name`, `last_name`, `email`, `username`, `password`, `join_date`) VALUES
    (1, 'Admin', 'Admin', 'admin@mail.com', 'admin', 'admin', '2023-01-01 00:00:00'),
    (2, 'Anonymous', 'Anonymous', 'anonymous@mail.com', 'anonymous', 'anonymous', '2023-01-01 00:00:00'),
    (3, 'Reserved1', 'Reserved1', 'reserved1@mail.com', 'reserved1', 'reserved1', '2023-01-01 00:00:00'),
    (4, 'Reserved2', 'Reserved2', 'reserved2@mail.com', 'reserved2', 'reserved2', '2023-01-01 00:00:00'),
    (5, 'Reserved3', 'Reserved3', 'reserved3@mail.com', 'reserved3', 'reserved3', '2023-01-01 00:00:00'),
    (6, 'Alexandra', 'Silcock', 'asilcock0@dailynews.com', 'asilcock0', '5JHqhCU9kBa', '2023-02-01 00:00:00'),
    (7, 'Margot', 'Rashleigh', 'mrashleigh1@rambler.ru', 'mrashleigh1', 'CclF2M7XY', '2023-02-02 00:00:00'),
    (8, 'Brian', 'Mussalli', 'bmussallij@purevolume.com', 'bmussallij', 'X0faOXpF', '2023-02-03 00:00:00'),
    (9, 'Orion', 'Wahner', 'owahnert@tinyurl.com', 'owahnert', 'qXO84K7', '2023-02-04 00:00:00'),
    (10, 'Reed', 'Flynn', 'rflynn12@php.net', 'rflynn12', 'GyDuVGTRVpq', '2023-02-05 00:00:00'),
    (11, 'Simple', 'Simple', 'simple@mail.com', 'simple', 'simple', '2023-02-10 00:00:00'),
    (12, 'Test', 'Test', 'test@gmail.com', 'test', 'test', '2023-02-10 00:00:00'),
    (13, 'Deleted', 'Deleted', 'deleted@mail.com', 'deleted', 'deleted', '2023-02-20 00:00:00'),
    (14, 'Tester1', 'Tester1', 'tester1@mail.com', 'tester1', 'tester1', '2023-02-20 00:00:00'),
    (15, 'All', 'Set', 'allset@mail.com', 'allset', 'setpass', '2023-02-20 00:00:00');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
