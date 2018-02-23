CREATE DATABASE android_api; /** Creating Database **/
 
USE android_api; /** Selecting Database **/
 
CREATE TABLE chat_rooms (
  chat_room_id int(11) NOT NULL AUTO_INCREMENT,
  name varchar(100) NOT NULL,
  encrypted_password varchar(80) NOT NULL,
  salt varchar(10) NOT NULL,
  created_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (chat_room_id)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE chat_room_by_user (
  chat_room_id int(11) NOT NULL,
  user_id int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

INSERT INTO `chat_rooms` (`chat_room_id`, `name`, `encrypted_password`, `salt`, `created_at`) VALUES
(1, 'Room 1', 'test', '', '2018-02-02 06:57:40'),
(2, 'Room 2', 'test', '', '2016-02-02 06:57:40'),
(3, 'Room 3', 'test', '', '2016-02-02 06:57:40'),
(4, 'Room 4', 'test', '', '2016-02-02 06:57:40'); 

CREATE TABLE messages (
  message_id int(11) NOT NULL AUTO_INCREMENT,
  chat_room_id int(11) NOT NULL,
  user_id int(11) NOT NULL,
  message text NOT NULL,
  created_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (message_id)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
 
CREATE TABLE users(
  user_id int(11) AUTO_INCREMENT,
  gcm_registration_id text NOT NULL,
  name varchar(50) NOT NULL,
  email varchar(100) NOT NULL,
  encrypted_password varchar(80) NOT NULL,
  salt varchar(10) NOT NULL,
  created_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


CREATE TABLE `events` (
  `event_id` int(11) NOT NULL,
  `name` varchar(100) NOT NULL,
  `description` text NOT NULL,
  `start_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `end_at` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `location` text NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

INSERT INTO `events` (`event_id`, `name`, `description`, `start_at`, `end_at`, `location`, `created_at`) VALUES
(1, 'Keliling Papua', 'Mengelilingi Papua 100x Hingga Menguasai Freeport beserta seluruh properties miliknya.', '2018-02-23 07:29:06', '2018-02-21 10:25:13', '-1.0616390466778358,97.7134494110942', '2018-04-04 06:57:40'),
(2, 'Vacation with Intern', 'Ayo kita pergi ke mana aja boleh, yang penting kalian bahagia :D', '2018-02-23 07:30:37', '2018-02-28 13:21:00', '-1,100', '2018-04-04 06:57:40'),
(3, 'Nasi Padang Gratis', 'Makan bareng-bareng di dunia dan diakhirat hingga kenyang tanpa ada rasa lapar lagi selamalamalamalamalamanyahhh~', '2019-04-04 13:13:13', '2030-08-12 17:00:00', '20.112,9.0101', '2018-04-04 06:57:40'),
(4, 'Makan Orang', 'Ayo kita makan makan orang di festival cannibal bersama CEO mikrosopt USSR :O', '2018-02-23 08:00:00', '2018-05-22 17:00:00', '18.902375205984804,-96.36404044926165', '2018-02-23 07:41:24');

 
ALTER TABLE messages
  ADD KEY chat_room_id (chat_room_id),
  ADD KEY user_id (user_id),
  ADD KEY chat_room_id_2 (chat_room_id);
 
ALTER TABLE users
  ADD UNIQUE KEY email (email);
 
ALTER TABLE messages
  ADD CONSTRAINT messages_ibfk_3 FOREIGN KEY (chat_room_id) REFERENCES chat_rooms (chat_room_id),
  ADD CONSTRAINT messages_ibfk_2 FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE chat_room_by_user
  ADD CONSTRAINT chatbyuser_ibfk_3 FOREIGN KEY (chat_room_id) REFERENCES chat_rooms (chat_room_id),
  ADD CONSTRAINT chatbyuser_ibfk_2 FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `events`
  ADD PRIMARY KEY (`event_id`);

ALTER TABLE `events`
  MODIFY `event_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;
COMMIT;