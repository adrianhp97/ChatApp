CREATE DATABASE android_api; /** Creating Database **/
 
USE android_api; /** Selecting Database **/
 
CREATE TABLE users(
   id int(11) AUTO_INCREMENT,
   unique_id varchar(23) NOT NULL UNIQUE,
   name varchar(50) NOT NULL,
   email varchar(100) NOT NULL UNIQUE,
   encrypted_password varchar(80) NOT NULL,
   salt varchar(10) NOT NULL,
   created_at datetime,
   updated_at datetime NULL,
   PRIMARY KEY (ID)
); /** Creating Users Table **/