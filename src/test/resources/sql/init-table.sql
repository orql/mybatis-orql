drop table if exists user;
drop table if exists role;
drop table if exists user_info;

create table role (
  id int primary key auto_increment,
  name varchar(8)
);

create table user (
  id bigint(20) primary key auto_increment,
  name varchar(8) not null,
  email varchar(32),
  create_time datetime not null,
  role_id int
);

create table user_info (
  id bigint(20) primary key auto_increment,
  birthday datetime,
  user_id bigint(20) not null
);