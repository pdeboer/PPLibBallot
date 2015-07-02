# --- !Ups

CREATE TABLE batch (
  id                         BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  allowed_answers_per_turker INT                NOT NULL
)
  ENGINE = InnoDB
  CHARSET = utf8;

CREATE TABLE question (
  id          BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  html        VARCHAR(255)       NOT NULL,
  output_code INT                NOT NULL,
  batch_id    BIGINT             NOT NULL UNIQUE,
  create_time DATETIME           NOT NULL,
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

# --- !Downs

DROP TABLE batch;
DROP TABLE question;
DROP TABLE user;
DROP TABLE answer;