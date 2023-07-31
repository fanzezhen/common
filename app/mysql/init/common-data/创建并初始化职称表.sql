/*==============================================================*/
/* Table: "professional_title"                                  */
/*==============================================================*/
create table "professional_title"
(
    "id"                 NUMBER               not null,
    "name"               VARCHAR2(16 CHAR)    not null,
    "category"           VARCHAR2(16 CHAR)    not null,
    constraint PK_PROFESSIONAL_TITLE primary key ("id")
);

comment on table "professional_title" is
    '职称表';

comment on column "professional_title"."id" is
    '主键';

comment on column "professional_title"."name" is
    '名称';

comment on column "professional_title"."category" is
    '类别';


create sequence s_professional_title
--     minvalue 1  --最小值
    nomaxvalue  --不设置最大值(由机器决定)，或 根据表字段的值范围设置 maxvalue
--     maxvalue 999  -- 最大值
    start with 101   --从1开始计数，数值可变
    increment by 1  --每次加1，数值可变
    nocycle  --一直累加，不循环；cycle：达到最大值后，将从头开始累加
    nocache;  --不建缓冲区。   如果建立cache那么系统将自动读取cache值个seq，这样会加快运行速度；如果在单机中使用cache，或者oracle死了，那么下次读取的seq值将不连贯，所以不建议使用cache。


insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '研究员', '科学研究人员');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '副研究员', '科学研究人员');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '助理研究员', '科学研究人员');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '研究实习员', '科学研究人员');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '高级实验师', '实验人员');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '实验师', '实验人员');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '助理实验师', '实验人员');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '实验员', '实验人员');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '教授级高级工程师', '工程技术人员');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '研究员级高级工程师', '工程技术人员');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '高级工程师', '工程技术人员');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '工程师', '工程技术人员');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '助理工程师', '工程技术人员');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '技术员', '工程技术人员');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '高级经济师', '经济专业人员');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '经济师', '经济专业人员');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '助理经济师', '经济专业人员');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '经济员', '经济专业人员');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '研究馆员', '图书、文博、档案、资料人员');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '副研究馆员', '图书、文博、档案、资料人员');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '馆员', '图书、文博、档案、资料人员');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '助理馆员', '图书、文博、档案、资料人员');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '管理员', '图书、文博、档案、资料人员');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '高级会计师', '会计人员');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '会计师', '会计人员');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '助理会计师', '会计人员');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '会计员', '会计人员');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '高级审计师', '审计专业人员');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '审计师', '审计专业人员');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '助理审计师', '审计专业人员');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '审计员', '审计专业人员');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '高级统计师', '统计人员');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '统计师', '统计人员');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '助理统计师', '统计人员');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '统计员', '统计人员');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '编审', '出版专业人员（编审）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '副编审', '出版专业人员（编审）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '编辑', '出版专业人员（编审）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '助理编辑', '出版专业人员（编审）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '技术编辑', '出版专业人员（技术编辑）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '助理技术设计员', '出版专业人员（技术编辑）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '技术设计员', '出版专业人员（技术编辑）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '一级校对', '出版专业人员（校对）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '二级校对', '出版专业人员（校对）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '三级校对', '出版专业人员（校对）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '译审', '翻译人员');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '副译审', '翻译人员');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '翻译', '翻译人员');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '助理翻译', '翻译人员');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '教授（思政）', '思想政治工作人员');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '高级政工师', '思想政治工作人员');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '政工师', '思想政治工作人员');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '助理政工师', '思想政治工作人员');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '政工员', '思想政治工作人员');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '高级记者', '新闻专业人员（记者）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '主任记者', '新闻专业人员（记者）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '记者', '新闻专业人员（记者）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '助理记者', '新闻专业人员（记者）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '高级编辑', '新闻专业人员（编辑）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '主任编辑', '新闻专业人员（编辑）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '编辑', '新闻专业人员（编辑）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '助理编辑', '新闻专业人员（编辑）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '高级农艺师', '农业技术人员（农艺）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '农艺师', '农业技术人员（农艺）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '助理农艺师', '农业技术人员（农艺）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '农业技术员', '农业技术人员（农艺）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '高级兽医师', '农业技术人员（兽医）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '兽医师', '农业技术人员（兽医）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '助理兽医师', '农业技术人员（兽医）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '兽医技术员', '农业技术人员（兽医）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '高级畜牧师', '农业技术人员（畜牧）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '畜牧师', '农业技术人员（畜牧）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '助理畜牧师', '农业技术人员（畜牧）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '畜牧技术员', '农业技术人员（畜牧）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '教授', '高等学校教师');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '副教授', '高等学校教师');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '讲师', '高等学校教师');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '助教', '高等学校教师');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '高级讲师', '中等专业学校教师');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '讲师', '中等专业学校教师');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '助理讲师', '中等专业学校教师');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '教员', '中等专业学校教师');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '高级讲师', '技工学校教师（讲师）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '讲师', '技工学校教师（讲师）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '助理讲师', '技工学校教师（讲师）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '教员', '技工学校教师（讲师）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '高级实习指导教师', '技工学校教师（实习指导）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '一级实习指导教师', '技工学校教师（实习指导）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '二级实习指导教师', '技工学校教师（实习指导）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '三级实习指导教师', '技工学校教师（实习指导）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '中学高级教师', '中学教师');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '中学一级教师', '中学教师');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '中学二级教师', '中学教师');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '中学三级教师', '中学教师');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '小学高级教师', '小学教师');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '小学一级教师', '小学教师');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '小学二级教师', '小学教师');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '小学三级教师', '小学教师');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '播音指导', '播音员');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '主任播音员', '播音员');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '一级播音员', '播音员');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '二级播音员', '播音员');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '三级播音员', '播音员');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '主任医师', '卫生技术人员（医疗）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '副主任医师', '卫生技术人员（医疗）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '主治医师', '卫生技术人员（医疗）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '医师', '卫生技术人员（医疗）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '医士', '卫生技术人员（医疗）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '主任药师', '卫生技术人员（药剂）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '副主任药师', '卫生技术人员（药剂）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '主管药师', '卫生技术人员（药剂）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '药师', '卫生技术人员（药剂）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '药士', '卫生技术人员（药剂）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '主任护师', '卫生技术人员（护理）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '副主任护师', '卫生技术人员（护理）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '主管护师', '卫生技术人员（护理）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '护师', '卫生技术人员（护理）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '护士', '卫生技术人员（护理）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '主任技师', '卫生技术人员（技师）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '副主任技师', '卫生技术人员（技师）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '主管技师', '卫生技术人员（技师）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '技师', '卫生技术人员（技师）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '技士', '卫生技术人员（技师）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '主任法医师', '法医专业人员');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '副主任法医师', '法医专业人员');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '法医师', '法医专业人员');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '法医士', '法医专业人员');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '高级工艺美术师', '工艺美术人员');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '工艺美术师', '工艺美术人员');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '助理工艺美术师', '工艺美术人员');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '工艺美术员', '工艺美术人员');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '一级演奏员', '艺术人员（演奏员）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '二级演奏员', '艺术人员（演奏员）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '三级演奏员', '艺术人员（演奏员）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '四级演奏员', '艺术人员（演奏员）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '一级编剧', '艺术人员（编剧）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '二级编剧', '艺术人员（编剧）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '三级编剧', '艺术人员（编剧）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '四级编剧', '艺术人员（编剧）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '一级导演', '艺术人员（导演）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '二级导演', '艺术人员（导演）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '三级导演', '艺术人员（导演）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '四级导演', '艺术人员（导演）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '一级指挥', '艺术人员（指挥）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '二级指挥', '艺术人员（指挥）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '三级指挥', '艺术人员（指挥）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '四级指挥', '艺术人员（指挥）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '一级作曲', '艺术人员（作曲）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '二级作曲', '艺术人员（作曲）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '三级作曲', '艺术人员（作曲）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '四级作曲', '艺术人员（作曲）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '一级美术师', '艺术人员（美术）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '二级美术师', '艺术人员（美术）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '三级美术师', '艺术人员（美术）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '美术员', '艺术人员（美术）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '一级舞美设计师', '艺术人员（舞美设计）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '二级舞美设计师', '艺术人员（舞美设计）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '三级舞美设计师', '艺术人员（舞美设计）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '舞美设计员', '艺术人员（舞美设计）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '主任舞台技师', '艺术人员（舞台设计）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '舞台技师', '艺术人员（舞台设计）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '舞台技术员', '艺术人员（舞台设计）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '主教练', '体育教练');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '教练', '体育教练');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '助理教练', '体育教练');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '高级关务监督', '海关专业人员');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '关务监督', '海关专业人员');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '助理关务监督', '海关专业人员');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '监督员', '海关专业人员');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '一级律师', '律师');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '二级律师', '律师');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '三级律师', '律师');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '四级律师', '律师');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '律师助理', '律师');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '一级公证员', '公证员');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '二级公证员', '公证员');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '三级公证员', '公证员');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '四级公证员', '公证员');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '公证员助理', '公证员');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '高级船长', '船舶技术人员（驾驶）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '船长（大副）', '船舶技术人员（驾驶）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '二副', '船舶技术人员（驾驶）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '三副', '船舶技术人员（驾驶）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '高级轮机长', '船舶技术人员（轮机）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '轮机长（大管轮）', '船舶技术人员（轮机）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '二管轮', '船舶技术人员（轮机）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '三管轮', '船舶技术人员（轮机）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '高级电机员', '船舶技术人员（电机）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '通用电机员（一等电机员）', '船舶技术人员（电机）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '二等电机员', '船舶技术人员（电机）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '高级报务员', '船舶技术人员（报务）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '通用报务员（一等报务员）', '船舶技术人员（报务）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '二等报务员', '船舶技术人员（报务）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '限用报务员', '船舶技术人员（报务）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '高级引航员', '船舶技术人员（引航）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '一、二级引航员', '船舶技术人员（引航）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '三、四级引航员', '船舶技术人员（引航）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '一级飞行员', '民用航空飞行技术人员（驾驶）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '二级飞行员', '民用航空飞行技术人员（驾驶）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '三级飞行员', '民用航空飞行技术人员（驾驶）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '四级飞行员', '民用航空飞行技术人员（驾驶）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '一级领航员', '民用航空飞行技术人员（领航）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '二级领航员', '民用航空飞行技术人员（领航）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '三级领航员', '民用航空飞行技术人员（领航）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '四级领航员', '民用航空飞行技术人员（领航）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '一级飞行通信员', '民用航空飞行技术人员（通信）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '二级飞行通信员', '民用航空飞行技术人员（通信）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '三级飞行通信员', '民用航空飞行技术人员（通信）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '四级飞行通信员', '民用航空飞行技术人员（通信）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '一级飞行机械员', '民用航空飞行技术人员（机械）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '二级飞行机械员', '民用航空飞行技术人员（机械）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '三级飞行机械员', '民用航空飞行技术人员（机械）');
insert into "professional_title" ("id", "name", "category") VALUES (s_professional_title.nextval, '四级飞行机械员', '民用航空飞行技术人员（机械）');
