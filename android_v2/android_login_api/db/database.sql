CREATE DATABASE android_api; /** Creating Database **/
 
USE android_api; /** Selecting Database **/
 
CREATE TABLE chat_rooms (
  chat_room_id int(11) NOT NULL AUTO_INCREMENT,
  name varchar(100) NOT NULL,
  created_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (chat_room_id)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

INSERT INTO `chat_rooms` (`chat_room_id`, `name`, `created_at`) VALUES
(1, 'Room 1', '2018-02-02 06:57:40'),
(2, 'Room 2', '2016-02-02 06:57:40'),
(3, 'Room 3', '2016-02-02 06:57:40'),
(4, 'Room 4', '2016-02-02 06:57:40'); 

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
 
ALTER TABLE messages
  ADD KEY chat_room_id (chat_room_id),
  ADD KEY user_id (user_id),
  ADD KEY chat_room_id_2 (chat_room_id);
 
ALTER TABLE users
  ADD UNIQUE KEY email (email);
 
ALTER TABLE messages
  ADD CONSTRAINT messages_ibfk_3 FOREIGN KEY (chat_room_id) REFERENCES chat_rooms (chat_room_id),
  ADD CONSTRAINT messages_ibfk_2 FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE ON UPDATE CASCADE;

