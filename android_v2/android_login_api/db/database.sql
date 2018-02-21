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
  `event_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `start_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `end_at` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `location` varchar(300) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`event_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

INSERT INTO `events` (`event_id`, `name`, `start_at`, `end_at`, `location`, `created_at`) VALUES
(1, 'Keliling Papua', '2018-04-04 13:13:12', '0000-00-00 00:00:00', '', '2018-04-04 06:57:40'),
(2, 'Student Summit Bandung', '2018-04-14 13:13:13', '0000-00-00 00:00:00', '', '2018-04-04 06:57:40'),
(3, 'Nasi Padang Gratis', '2020-04-04 13:13:13', '0000-00-00 00:00:00', '', '2018-04-04 06:57:40'),
(8, 'Arctic Expedition', '2018-02-20 12:55:00', '2027-01-03 23:10:00', 'Arctic', '2018-02-20 12:56:24'),
(9, 'Invest in Indonesia', '2018-02-21 12:55:00', '2025-02-24 16:02:00', 'Indonesia', '2018-02-20 13:03:38'),
(10, 'Seattle Teathre', '2018-02-20 13:11:00', '2018-02-20 13:11:00', 'A', '2018-02-20 13:12:01');

 
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

