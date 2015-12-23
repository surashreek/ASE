# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table COURSES (
  course_id                 integer auto_increment not null,
  name                      varchar(255),
  description               varchar(255),
  constraint pk_COURSES primary key (course_id))
;

create table RATING (
  SESSION_ID                integer auto_increment not null,
  rating                    integer,
  user_comment              varchar(255),
  constraint pk_RATING primary key (SESSION_ID))
;

create table YODA_SESSION (
  session_id                integer auto_increment not null,
  tutor_id                  integer,
  tutee_id                  integer,
  scheduled_time            datetime(6),
  location                  varchar(255),
  status                    varchar(255),
  email_sent                tinyint(1) default 0,
  course_id                 integer,
  course_name               varchar(255),
  rating                    integer,
  user_comment              varchar(255),
  constraint pk_YODA_SESSION primary key (session_id))
;

create table TUTORS (
  user_id                   integer,
  course_id                 integer,
  description               varchar(255),
  constraint pk_TUTORS primary key (user_id, course_id))
;

create table YODA_USER (
  USER_ID                   integer auto_increment not null,
  USER_NAME                 varchar(255),
  PASSWORD                  varchar(255),
  FIRST_NAME                varchar(255),
  LAST_NAME                 varchar(255),
  DESCRIPTION               varchar(255),
  CREATION_DATE             datetime(6),
  IS_ACTIVE                 integer,
  HASH                      integer,
  NUM_FLAGS                 integer,
  SESSION_QUOTA             integer,
  constraint pk_YODA_USER primary key (USER_ID))
;




# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table COURSES;

drop table RATING;

drop table YODA_SESSION;

drop table TUTORS;

drop table YODA_USER;

SET FOREIGN_KEY_CHECKS=1;

