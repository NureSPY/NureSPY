--
-- Скрипт сгенерирован Devart dbForge Studio for MySQL, Версия 7.4.201.0
-- Домашняя страница продукта: http://www.devart.com/ru/dbforge/mysql/studio
-- Дата скрипта: 02.06.2018 22:43:55
-- Версия сервера: 5.7.19-log
-- Версия клиента: 4.1
--

-- 
-- Отключение внешних ключей
-- 
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;

-- 
-- Установить режим SQL (SQL mode)
-- 
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;

-- 
-- Установка кодировки, с использованием которой клиент будет посылать запросы на сервер
--
SET NAMES 'utf8';

--
-- Установка базы данных по умолчанию
--
USE nurespy;

--
-- Удалить таблицу `chat_user`
--
DROP TABLE IF EXISTS chat_user;

--
-- Удалить таблицу `message`
--
DROP TABLE IF EXISTS message;

--
-- Удалить таблицу `chat`
--
DROP TABLE IF EXISTS chat;

--
-- Удалить таблицу `event`
--
DROP TABLE IF EXISTS event;

--
-- Удалить таблицу `user`
--
DROP TABLE IF EXISTS user;

--
-- Установка базы данных по умолчанию
--
USE nurespy;

--
-- Создать таблицу `user`
--
CREATE TABLE user (
  id int(11) NOT NULL AUTO_INCREMENT,
  fullname varchar(100) NOT NULL,
  mail varchar(35) NOT NULL,
  login varchar(30) NOT NULL,
  password varchar(30) NOT NULL,
  phone varchar(20) NOT NULL,
  status enum ('student', 'teacher') NOT NULL,
  `group` varchar(20) NOT NULL,
  stay_in tinyint(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (id)
)
ENGINE = INNODB,
CHARACTER SET utf8,
COLLATE utf8_general_ci;

--
-- Создать таблицу `event`
--
CREATE TABLE event (
  id int(11) NOT NULL AUTO_INCREMENT,
  name varchar(200) NOT NULL,
  user_id int(11) NOT NULL,
  datetime varchar(19) NOT NULL DEFAULT '00:00:00 0000.00.00',
  location varchar(200) NOT NULL,
  duration int(11) NOT NULL,
  discription text NOT NULL,
  PRIMARY KEY (id)
)
ENGINE = INNODB,
CHARACTER SET utf8,
COLLATE utf8_general_ci;

--
-- Создать внешний ключ
--
ALTER TABLE event
ADD CONSTRAINT E_User_id FOREIGN KEY (user_id)
REFERENCES user (id) ON DELETE NO ACTION;

--
-- Создать таблицу `chat`
--
CREATE TABLE chat (
  id int(11) NOT NULL AUTO_INCREMENT,
  name varchar(50) NOT NULL,
  PRIMARY KEY (id)
)
ENGINE = INNODB,
CHARACTER SET utf8,
COLLATE utf8_general_ci;

--
-- Создать таблицу `message`
--
CREATE TABLE message (
  id int(11) NOT NULL AUTO_INCREMENT,
  text text NOT NULL,
  user_id int(11) NOT NULL,
  chat_id int(11) NOT NULL,
  datetime varchar(19) NOT NULL DEFAULT '00:00:00 0000.00.00',
  PRIMARY KEY (id)
)
ENGINE = INNODB,
CHARACTER SET utf8,
COLLATE utf8_general_ci;

--
-- Создать внешний ключ
--
ALTER TABLE message
ADD CONSTRAINT M_Chat_id FOREIGN KEY (chat_id)
REFERENCES chat (id) ON DELETE NO ACTION;

--
-- Создать внешний ключ
--
ALTER TABLE message
ADD CONSTRAINT M_User_id FOREIGN KEY (user_id)
REFERENCES user (id) ON DELETE NO ACTION;

--
-- Создать таблицу `chat_user`
--
CREATE TABLE chat_user (
  chat_id int(11) NOT NULL,
  user_id int(11) NOT NULL,
  access enum ('write and read', 'read only') NOT NULL
)
ENGINE = INNODB,
CHARACTER SET utf8,
COLLATE utf8_general_ci;

--
-- Создать внешний ключ
--
ALTER TABLE chat_user
ADD CONSTRAINT CU_Chat_id FOREIGN KEY (chat_id)
REFERENCES chat (id) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Создать внешний ключ
--
ALTER TABLE chat_user
ADD CONSTRAINT CU_User_id FOREIGN KEY (user_id)
REFERENCES user (id) ON DELETE CASCADE ON UPDATE CASCADE;

-- 
-- Вывод данных для таблицы user
--
-- Таблица nurespy.user не содержит данных

-- 
-- Вывод данных для таблицы chat
--
-- Таблица nurespy.chat не содержит данных

-- 
-- Вывод данных для таблицы message
--
-- Таблица nurespy.message не содержит данных

-- 
-- Вывод данных для таблицы event
--
-- Таблица nurespy.event не содержит данных

-- 
-- Вывод данных для таблицы chat_user
--
-- Таблица nurespy.chat_user не содержит данных

-- 
-- Восстановить предыдущий режим SQL (SQL mode)
-- 
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;

-- 
-- Включение внешних ключей
-- 
/*!40014 SET FOREIGN_KEY_CHECKS = @OLD_FOREIGN_KEY_CHECKS */;