INSERT INTO `users` (`id`, `first_name`, `last_name`, `email`, `username`, `password`, `join_date`) VALUES
                    (2, 'Frodo', 'Baggins', 'frodo@mail.com', 'frodo1', 'qwerty1', '2023-01-01 19:30:59'),
                    (3, 'Bruce', 'Wayne', 'batman@yahoo.com', 'batman', 'bat-man13', '2023-02-21 11:55:40'),
                    (4, 'Admin', 'Admin', 'admin@mail.com', 'admin', 'admin', '2023-01-01 00:00:00'),
                    (5, 'Spammer', 'Spammer', 'spam@mail.com', 'spammer', 'spammer', '2023-02-21 12:01:00'),
                    (6, 'John', 'Smith', 'johnsmith@mail.com', 'johnny', 'john123', '2023-01-21 12:04:17'),
                    (7, 'Kate', 'Beckinsale', 'katy@yahoo.com', 'katybeck', 'pass0000', '2023-02-06 12:05:29'),
                    (8, 'Vika', 'Mariot', 'viky@gmail.com', 'vikky', 'v123ky', '2023-01-16 12:07:20');

INSERT INTO `posts` (`id`, `title`, `content`, `likes`, `user_id`, `date_created`) VALUES
                    (1, 'Old times', 'Once upon a time there was a...', 13, 2, '2023-02-01 12:12:10'),
                    (2, 'Ice and Fire', 'They were destroying everything on their path...', 3, 2, '2023-02-04 12:12:59'),
                    (3, 'Self observation', 'This practice requires your full attention to details', 20, 4, '2023-01-01 12:14:25'),
                    (4, 'Batman quotes', 'The night is darkest just before the dawn', 50, 3, '2023-02-21 12:14:51'),
                    (5, 'Batman quotes', 'Your anger gives you great power', 25, 3, '2023-02-21 12:16:20'),
                    (6, 'Learn Java in no time', 'You can start from Oracle documentation, then take some Udemy courses, then start practicing... ', 5, 8, '2023-01-21 12:20:42'),
                    (7, 'Test title goes here', 'Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industrys standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book.', 99, 4, '2023-01-10 12:26:34');

INSERT INTO `comments` (`id`, `post_id`, `user_id`, `content`, `date_created`) VALUES
                        (1, 4, 7, 'Yeah, I liked this movies', '2023-02-15 12:22:12'),
                        (2, 5, 7, 'My favourite quote', '2023-02-18 12:23:00'),
                        (3, 6, 6, 'Easy to say, hard to do it', '2023-02-21 12:22:10'),
                        (4, 6, 8, 'Great guide, thank you', '2023-02-19 12:25:06'),
                        (5, 1, 4, 'Sound like a good story', '2023-02-21 12:25:58');

INSERT INTO `permissions` (`user_id`, `is_blocked`, `is_admin`) VALUES
                                                                    (2, 0, 0),
                                                                    (3, 0, 0),
                                                                    (4, 0, 1),
                                                                    (5, 1, 0),
                                                                    (6, 0, 0),
                                                                    (7, 0, 0),
                                                                    (8, 0, 0);

INSERT INTO `phones` (`user_id`, `phone_number`) VALUES
    (4, '+123-222-5555');