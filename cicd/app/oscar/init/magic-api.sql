-- MagicApi接口信息表
create TABLE IF NOT EXISTS SYSDBA.MAGIC_API_RESOURCE (
  file_path   VARCHAR(512) NOT NULL,
  file_content text   NOT NULL,
  created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY (file_path)
);
comment on column SYSDBA.magic_api_resource.file_path is '接口路径';
comment on column SYSDBA.magic_api_resource.file_content is '接口内容' ;
comment on column SYSDBA.magic_api_resource.created_time is '创建时间' ;
comment on table SYSDBA.magic_api_resource is 'MagicApi接口信息表' ;
-- MagicApi备份表
create TABLE IF NOT EXISTS SYSDBA.magic_backup_record (
  id varchar(32) NOT NULL,
  create_date BIGINT NOT NULL,
  tag varchar(32) DEFAULT NULL,
  type varchar(32) DEFAULT NULL,
  name varchar(64) DEFAULT NULL,
  content blob,
  create_by varchar(64) DEFAULT NULL,
  PRIMARY KEY (id, create_date)
);
comment on column SYSDBA.magic_backup_record.create_date is '备份时间' ;
comment on column SYSDBA.magic_backup_record.tag is '标签' ;
comment on column SYSDBA.magic_backup_record.type is '类型' ;
comment on column SYSDBA.magic_backup_record.name is '原名称' ;
comment on column SYSDBA.magic_backup_record.content is '备份内容' ;
comment on column SYSDBA.magic_backup_record.create_by is '操作人' ;
comment on table SYSDBA.magic_backup_record is 'MagicApi备份表' ;
