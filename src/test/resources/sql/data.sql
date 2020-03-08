insert into role (id, name) values (1, '系统管理员');
insert into role (id, name) values (2, '普通用户');

insert into user (id, name, email, create_time, role_id) values (1, 'n1', 'e1@e.com', '2020-02-01 00:00:00', 1);
insert into user (id, name, email, create_time, role_id) values (2, 'n2', 'e2@e.com', '2020-02-02 00:00:00', 2);
insert into user (id, name, email, create_time, role_id) values (3, 'n3', 'e3@e.com', '2020-02-03 00:00:00', 2);
insert into user (id, name, email, create_time, role_id) values (4, 'n4', 'e4@e.com', '2020-02-04 00:00:00', 2);
insert into user (id, name, email, create_time, role_id) values (5, 'n5', 'e5@e.com', '2020-02-05 00:00:00', 2);

insert into user_info (birthday, user_id) values ('1990-01-01 00:00:00', 1);
insert into user_info (birthday, user_id) values ('1990-01-02 00:00:00', 2);