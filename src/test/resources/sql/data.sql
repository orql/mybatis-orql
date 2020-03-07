insert into role (id, name) values (1, '系统管理员');
insert into role (id, name) values (2, '普通用户');

insert into user (name, email, create_time, role_id) values ('n1', 'e1@e.com', '2020-02-01 00:00:00',1 );
insert into user (name, email, create_time, role_id) values ('n2', 'e2@e.com', '2020-02-02 00:00:00', 2);
insert into user (name, email, create_time, role_id) values ('n3', 'e3@e.com', '2020-02-03 00:00:00', 2);
insert into user (name, email, create_time, role_id) values ('n4', 'e4@e.com', '2020-02-04 00:00:00', 2);
insert into user (name, email, create_time, role_id) values ('n5', 'e5@e.com', '2020-02-05 00:00:00', 2);