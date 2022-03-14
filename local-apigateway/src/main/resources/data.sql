insert into api_user(id,name,password) values(1,'dsadmin',null);
--主键自增的表，插入数据的时候，不能加主键
insert into api_config(id,vcode,domain_name,ts,user_id) values(1,'hsdA437Se2p3hjdh',null,1,1);
--Init数据库驱动表
--delete from api_database;
delete from api_user_token;
--修改数据库
alter table api_database add column file varchar(100) default NULL;  -- 文件名
alter table api_database add column class varchar(150) default NULL;  -- 驱动名
alter table api_database add column custom varchar(1) default NULL;  --是否是自定义 1是 0不是
alter table api_database modify column default_url varchar(150);
