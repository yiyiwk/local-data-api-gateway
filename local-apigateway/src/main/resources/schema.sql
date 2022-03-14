--用户表--
--varchar必须固定长度
CREATE TABLE api_user (
      id SMALLINT DEFAULT NULL,
      name varchar(20) DEFAULT NULL,         --用户名
      password varchar(500) DEFAULT NULL,      --密码
      PRIMARY KEY (id)
);
--数据源表--
--drop table api_datamart;
CREATE TABLE api_datamart (
      id  integer not null generated always as identity(start with 1,increment by 1),
      name varchar(50) DEFAULT NULL,           -- 名称
      type varchar(2) DEFAULT NULL,                -- 类型
      url  varchar(5000) DEFAULT NULL,           -- url
      username varchar(500) ,                    -- 用户名
      password varchar(500) ,                    -- 密码
      state char DEFAULT NULL,                  -- 状态
      PRIMARY KEY (id)
);
--drop table api_database;
--jdbc驱动数据表--
CREATE TABLE api_database (
      id  int not null ,
      type varchar(2) DEFAULT NULL,                  -- type
      name varchar(50) DEFAULT NULL,              --名称
      default_url varchar(100) default NULL,            -- url串
      PRIMARY KEY (id)
);
--CREATE TABLE api_database (
      --id  integer not null ,
      --type varchar(2) DEFAULT NULL,                  -- type
      --name varchar(50) DEFAULT NULL,              --名称
     -- default_url varchar(150) default NULL,            -- url串
      --file varchar(100) default NULL,      -- 文件名
      --class varchar(150) default NULL,      -- 驱动名
      --custom varchar(1)  default NULL,     --是否是自定义 1是 0不是
      --PRIMARY KEY (id)
--);
--api 接口表
CREATE TABLE api_interface (
      id  integer not null generated always as identity(start with 1,increment by 1),
      path varchar(50) DEFAULT NULL,        --路径
      name varchar(50) DEFAULT NULL,        --名称
      represent  varchar(1000) ,              --描述
      state  char DEFAULT NULL,             --状态
      PRIMARY KEY (id)
);

--数据项表--
CREATE TABLE api_dataitem (
      id  integer not null generated by default as identity(start with 1,increment by 1),
      nodename varchar(50) DEFAULT NULL,            --节点名
      name varchar(100) DEFAULT NULL,               --名称
      datamart_id integer DEFAULT NULL,             --数据源id
      interface_id integer DEFAULT NULL,            -- api接口id
      querysql varchar(10000) DEFAULT NULL,          --查询语句
      state char DEFAULT NULL,                      --状态
      PRIMARY KEY (id)
);

--数据处理器表--
--drop table api_handler;
CREATE TABLE api_handler (
      id  integer not null generated always as identity(start with 1,increment by 1),
      name varchar(100) DEFAULT NULL,           -- 处理器名称
      interface_id integer DEFAULT NULL,        -- api 接口id
      processor varchar(100) ,                   -- 处理器上传地址
      sort integer ,                            -- 排序
      state char DEFAULT NULL,                  -- 状态
      PRIMARY KEY (id)
);
--入参处理器表--
CREATE TABLE api_param_handler (
      id  integer not null generated always as identity(start with 1,increment by 1),
      name varchar(100) DEFAULT NULL,           -- 处理器名称
      interface_id integer DEFAULT NULL,        -- api 接口id
      processor varchar(100) ,                   -- 处理器上传地址
      sort integer ,                            -- 排序
      state char DEFAULT NULL,                  -- 状态
      PRIMARY KEY (id)
);

--用户登录token表，定时任务过过期策略--
CREATE TABLE api_user_token (
      id  integer not null generated always as identity(start with 1,increment by 1),
      token varchar(50) DEFAULT NULL,           -- token
      create_time date DEFAULT NULL,       -- 创建时间
      user_id integer  DEFAULT NULL,            -- 用户id
      PRIMARY KEY (id)
);

CREATE TABLE api_config (
      id SMALLINT DEFAULT NULL,
      vcode varchar(20) DEFAULT NULL,           -- 安全码
      domain_name  varchar(50) DEFAULT NULL,    --域名/ip设置
      ts SMALLINT DEFAULT NULL,                 -- 时间戳校验
      user_id integer  DEFAULT NULL,            -- 用户id
      PRIMARY KEY (id)
);

