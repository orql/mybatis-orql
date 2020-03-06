drop table if exists user;

create table user (
  id bigint(20) primary key auto_increment,
  name varchar(8) not null
);