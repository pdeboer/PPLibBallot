# --- !Ups

CREATE TABLE batch (
  id                         BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  allowed_answers_per_turker INT                NOT NULL
)
  ENGINE = InnoDB
  CHARSET = utf8;

CREATE TABLE question (
  id          BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  html        VARCHAR(1000)      NOT NULL,
  output_code INT                NOT NULL,
  batch_id    BIGINT             NOT NULL UNIQUE,
  create_time DATETIME           NOT NULL,
  uuid        VARCHAR(255)       NOT NULL,
  FOREIGN KEY (batch_id) REFERENCES batch (id)
)
  ENGINE = InnoDB
  CHARSET = utf8;

CREATE TABLE user (
  id                   BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  turker_id            VARCHAR(255)       NOT NULL,
  first_seen_date_time DATETIME           NOT NULL
)
  ENGINE = InnoDB
  CHARSET = utf8;

CREATE TABLE answer (
  id          BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  question_id BIGINT             NOT NULL,
  user_id     BIGINT             NOT NULL,
  time        DATETIME           NOT NULL,
  answer_json VARCHAR(255)       NOT NULL,
  FOREIGN KEY (question_id) REFERENCES question (id),
  FOREIGN KEY (user_id) REFERENCES user (id)
)
  ENGINE = InnoDB
  CHARSET = utf8;

INSERT INTO batch(allowed_answers_per_turker) VALUES(1);

INSERT INTO question (id, html, output_code, batch_id, create_time, uuid) VALUES	(1, '<h1>Identify the relation</h1><div>In the field of statistics, one generally uses statistical methods (such as ANOVA) to compare groups of data and derive findings.\nThese Statistical methods in general require some prerequisites to be satisfied before being applied to data.<br> <img src=\"https://placeholdit.imgix.net/~text?txtsize=60&txt=%5Bimg%5D&w=400&h=300\"></div><h3>In the text above, is there any kind of relationship between the prerequisite and the method?</h3><form action=\"/storeAnswer\" method=\"post\"><input type=\"submit\" name=\"answer\" value=\"Yes\" style=\"width:10%;\"><input type=\"submit\" name=\"answer\" value=\"No\" style=\"width:10%;\"></form>', 123, 1, '2015-07-03 00:00:00', '48ebcba6-23c2-11e5-bde4-45920f92afb9');

# --- !Downs

DROP TABLE batch;
DROP TABLE question;
DROP TABLE user;
DROP TABLE answer;