CREATE TABLE IF NOT EXISTS `cc_users`
(
  `id`          int(11)     NOT NULL COMMENT 'id' AUTO_INCREMENT,
  `pwd`         varchar(65) NOT NULL comment '密码',
  `username`    varchar(11) NOT NULL COMMENT '学号',
  `open_id`     varchar(40) NOT NULL default '' comment '用户的openID',
  `real_name`   varchar(20) NOT NULL COMMENT '姓名',
  `phone`       varchar(11) NOT NULL DEFAULT '' COMMENT '电话号码',
  `create_time` int(11)     NOT NULL DEFAULT '0' COMMENT '绑定手机时间',
  `school`      varchar(20) NOT NULL COMMENT '学院信息',
  `gender`      varchar(2)  NOT NULL COMMENT '性别',
  `points`      int(8)      NOT NULL DEFAULT '10' COMMENT '积分，初始为10',
  `admin_type`  int(4)      NOT NULL DEFAULT '0' COMMENT '管理员类型，0普通学生老师，1学生管理员，2老师管理员，3超级管理员',
  `admin_note`  varchar(30) NOT NULL DEFAULT '' COMMENT '备注',
  UNIQUE KEY (`username`),
  PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='用户表';
# ALTER table cc_users add UNIQUE KEY (`username`);

CREATE TABLE IF NOT EXISTS `cc_areas`
(
  `area_id`       int(4)      NOT NULL COMMENT '场地id' AUTO_INCREMENT,
  `area_name`     varchar(40) NOT NULL DEFAULT '' COMMENT '场地名称',
  `area_capacity` varchar(16) NOT NULL DEFAULT '' COMMENT '场地容量',
  PRIMARY KEY (`area_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='场地表';


CREATE TABLE IF NOT EXISTS `cc_area_usage`
(
  `id`               int(11)     NOT NULL COMMENT 'id' AUTO_INCREMENT,
  `area_id`          int(4)      NOT NULL COMMENT '场地id',
  `day`              varchar(8)  NOT NULL COMMENT '具体日期',
  `apply_id`         int(11)     NOT NULL comment '申请项id',
  `type`             TINYINT(2)  NOT NULL comment '类型 0:场地申请，1:活动申请',
  `start_time_index` TINYINT(2)  NOT NULL comment '开始时间下标',
  `end_time_index`   TINYINT(2)  NOT NULL comment '结束时间下标',
  `admin_user`       varchar(11) NOT NULL comment '审批人用户名',
  PRIMARY KEY (`id`),
  UNIQUE KEY (`area_id`, `day`, `start_time_index`),
  UNIQUE KEY (`apply_id`, `type`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='场地使用情况表';


CREATE TABLE IF NOT EXISTS `cc_application`
(
  `apply_id`      int(11)      NOT NULL COMMENT '申请id' AUTO_INCREMENT,
  `start_time`    DATETIME     NOT NULL COMMENT '开始时间',
  `end_time`      DATETIME     NOT NULL COMMENT '结束时间',
  `apply_user`    varchar(11)  NOT NULL DEFAULT '' COMMENT '申请人学号',
  `sponsor`       varchar(20)  NOT NULL DEFAULT '' COMMENT '主办方',
  `introduce`     varchar(200) NOT NULL DEFAULT '' COMMENT '申请原因介绍',
  `apply_area`    int(4)       NOT NULL COMMENT '申请区域id',
  `admin1_status` int(2)       NOT NULL default '0' COMMENT '学生管理员审批状态，0未审批，1通过，2驳回',
  `admin2_status` int(2)       NOT NULL default '0' COMMENT '教师管理员审批状态，0未审批，1通过，2驳回',
  `admin1_advice` varchar(200) NOT NULL DEFAULT '' COMMENT '学生管理员审批意见',
  `admin2_advice` varchar(200) NOT NULL DEFAULT '' COMMENT '教师管理员审批意见',
  `admin1_name`   varchar(20)  NOT NULL DEFAULT '' COMMENT '学生管理员姓名',
  `admin2_name`   varchar(20)  NOT NULL DEFAULT '' COMMENT '老师管理员姓名',
  `apply_status`  int(2)       NOT NULL default '0' COMMENT '0接受报名，1报名结束至活动结束，2活动结束,3活动破产',
  `create_time`   int(11)      NOT NULL DEFAULT '0' COMMENT '创建时间',
  PRIMARY KEY (`apply_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='场地申请表';


CREATE TABLE IF NOT EXISTS `cc_activity`
(
  `activity_id`     int(11)      NOT NULL COMMENT '申请id' AUTO_INCREMENT,
  `start_time`      DATETIME     NOT NULL COMMENT '开始时间',
  `end_time`        DATETIME     NOT NULL COMMENT '结束时间',
  `apply_user`      varchar(11)  NOT NULL DEFAULT '' COMMENT '申请人学号',
  `sponsor`         varchar(20)  NOT NULL DEFAULT '' COMMENT '主办方',
  `title`           varchar(30)  NOT NULL DEFAULT '' COMMENT '活动标题',
  `introduce`       varchar(200) NOT NULL DEFAULT '' COMMENT '活动介绍',
  `apply_area`      int(4)       NOT NULL COMMENT '申请区域id',
  `members_less`    int(6)       NOT NULL default '0' COMMENT '最低人数要求',
  `admin1_status`   int(2)       NOT NULL default '0' COMMENT '学生管理员审批状态，0未审批，1通过，2驳回',
  `admin2_status`   int(2)       NOT NULL default '0' COMMENT '教师管理员审批状态，0未审批，1通过，2驳回',
  `admin1_advice`   varchar(200) NOT NULL DEFAULT '' COMMENT '学生管理员审批意见',
  `admin2_advice`   varchar(200) NOT NULL DEFAULT '' COMMENT '教师管理员审批意见',
  `admin1_name`     varchar(20)  NOT NULL DEFAULT '' COMMENT '学生管理员姓名',
  `admin2_name`     varchar(20)  NOT NULL DEFAULT '' COMMENT '老师管理员姓名',
  `activity_status` int(2)       NOT NULL default '0' COMMENT '0接受报名，1报名结束至活动结束，2活动结束,3活动破产',
  `thumbnail`       varchar(40)  NOT NULL DEFAULT '' COMMENT '缩略图url',
  `full_image`      varchar(40)  NOT NULL DEFAULT '' COMMENT '原图url',
  `enroll_time`     DATETIME     NOT NULL COMMENT '报名截止时间',
  `create_time`     int(11)      NOT NULL DEFAULT '0' COMMENT '创建时间',
  PRIMARY KEY (`activity_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='活动申请表';


# CREATE TABLE IF NOT EXISTS `cc_area_feedback` (
#   `id` int(11) NOT NULL COMMENT 'id',
#   `area_id` int(4) NOT NULL COMMENT '场地id',
#   `username` varchar(11) NOT NULL COMMENT '学号',
#   `score` int(3) NOT NULL COMMENT '场地评分',
#   `content` varchar(400) NOT NULL DEFAULT '' COMMENT '反馈内容',
#   `create_time` int(11) NOT NULL DEFAULT '0' COMMENT '反馈时间',
#   PRIMARY KEY (`id`)
# ) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COMMENT='场地评价表' AUTO_INCREMENT=10000;

CREATE TABLE IF NOT EXISTS `cc_apply_feedback`
(
  `id`             int(11)      NOT NULL COMMENT 'id' AUTO_INCREMENT,
  `apply_id`       int(11)      NOT NULL COMMENT '场地申请id',
  `username`       varchar(11)  NOT NULL COMMENT '学号',
  `content_score`  int(3)       NOT NULL COMMENT '内容评价',
  `organize_score` int(3)       NOT NULL COMMENT '组织评价',
  `use_score`      int(3)       NOT NULL COMMENT '使用评价',
  `content`        varchar(400) NOT NULL DEFAULT '' COMMENT '反馈内容',
  `create_time`    int(11)      NOT NULL DEFAULT '0' COMMENT '反馈时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY (`apply_id`, `username`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='场地活动评价表';


CREATE TABLE IF NOT EXISTS `cc_activity_feedback`
(
  `id`             int(11)      NOT NULL COMMENT 'id' AUTO_INCREMENT,
  `activity_id`    int(11)      NOT NULL COMMENT '活动id',
  `username`       varchar(11)  NOT NULL COMMENT '学号',
  `content_score`  int(3)       NOT NULL COMMENT '内容评价',
  `organize_score` int(3)       NOT NULL COMMENT '组织评价',
  `use_score`      int(3)       NOT NULL COMMENT '使用评价',
  `content`        varchar(400) NOT NULL DEFAULT '' COMMENT '反馈内容',
  `create_time`    int(11)      NOT NULL DEFAULT '0' COMMENT '反馈时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY (`activity_id`, `username`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='活动评价表';

CREATE TABLE IF NOT EXISTS `cc_activity_user`
(
  `id`          int(11)     NOT NULL COMMENT 'id' AUTO_INCREMENT,
  `activity_id` int(11)     NOT NULL COMMENT '活动id',
  `username`    varchar(11) NOT NULL COMMENT '学号',
  `status`      int(3)      NOT NULL default '1' COMMENT '状态',
  PRIMARY KEY (`id`),
  UNIQUE KEY (`activity_id`, `username`)
) ENGINE = InnoDB
  DEFAULT charset utf8mb4 comment '用户活动表';

CREATE TABLE IF NOT EXISTS `cc_apply_user`
(
  `id`       int(11)     NOT NULL COMMENT 'id' AUTO_INCREMENT,
  `apply_id` int(11)     NOT NULL COMMENT '活动id',
  `username` varchar(11) NOT NULL COMMENT '学号',
  `status`   int(3)      NOT NULL default '1' COMMENT '状态,0未同意，1同意',
  PRIMARY KEY (`id`),
  UNIQUE KEY (`apply_id`, `username`)
) ENGINE = InnoDB
  DEFAULT charset utf8mb4 comment '用户场地表';

CREATE TABLE IF NOT EXISTS `cc_activity_complaint`
(
  `id`               int(11)      NOT NULL COMMENT 'id' AUTO_INCREMENT,
  `activity_id`      int(11)      NOT NULL COMMENT '活动id',
  `username`         varchar(11)  NOT NULL comment '学号',
  `complaint_type`   varchar(12)  NOT NULL comment '投诉类型',
  `complaint_reason` varchar(400) NOT NULL comment '原因',
  `feedback`         varchar(200) NOT NULL default '' comment '反馈',
  `status`           int(3)       NOT NULL default '0' comment '处理状态，0未处理，1预处理，2已处理',
  `create_time`      int(11)      NOT NULL DEFAULT '0' COMMENT '投诉时间',
  `admin_user`       VARCHAR(12)  NOT NULL DEFAULT '' COMMENT '审查人',
  primary key (`id`)
) ENGINE = InnoDB
  DEFAULT charset utf8mb4 comment '活动投诉表';


CREATE TABLE IF NOT EXISTS `cc_apply_complaint`
(
  `id`               int(11)      NOT NULL COMMENT 'id' AUTO_INCREMENT,
  `apply_id`         int(11)      NOT NULL COMMENT '活动id',
  `complaint_reason` varchar(400) NOT NULL default '' comment '原因',
  `status`           int(3)       NOT NULL default '0' comment '处理状态，0未处理，1预处理，2已处理',
  `score_change`     int(3)       NOT NULL default '0' comment '申请人分数变动',
  `ban_time`         varchar(6)   NOT NULL default 'no' comment '禁止使用时间:"一周"，"两周","1月"，"2月"，"1学期"，1学年，永久',
  `admin1_user`      VARCHAR(12)  NOT NULL DEFAULT '' COMMENT '学生管理员',
  `admin2_user`      VARCHAR(12)  NOT NULL DEFAULT '' COMMENT '老师管理员',
  `update_time`      int(11)      NOT NULL DEFAULT '0' COMMENT '处理时间',
  primary key (`id`),
  unique key (`apply_id`)
) ENGINE = InnoDB
  DEFAULT charset utf8mb4 comment '场地申请惩罚表';

CREATE TABLE IF NOT EXISTS `cc_activity_punishment`
(
  `id`               int(11)      NOT NULL COMMENT 'id' AUTO_INCREMENT,
  `activity_id`      int(11)      NOT NULL COMMENT '活动id',
  `complaint_reason` varchar(400) NOT NULL default '' comment '原因',
  `status`           int(3)       NOT NULL default '0' comment '处理状态，0未处理，1预处理，2已处理',
  `score_change`     int(3)       NOT NULL default '0' comment '申请人分数变动',
  `ban_time`         varchar(6)   NOT NULL default 'no' comment '禁止使用时间:"一周"，"两周","1月"，"2月"，"1学期"，1学年，永久',
  `admin1_user`      VARCHAR(12)  NOT NULL DEFAULT '' COMMENT '学生管理员',
  `admin2_user`      VARCHAR(12)  NOT NULL DEFAULT '' COMMENT '老师管理员',
  `update_time`      int(11)      NOT NULL DEFAULT '0' COMMENT '处理时间',
  primary key (`id`),
  unique key (`activiy_id`)
) ENGINE = InnoDB
  DEFAULT charset utf8mb4 comment '活动惩罚表';

CREATE TABLE IF NOT EXISTS `cc_activity_punishment`
(
  `id`               int(11)      NOT NULL AUTO_INCREMENT COMMENT 'id',
  `activity_id`      int(11)      NOT NULL COMMENT '活动id',
  `complaint_reason` varchar(400) NOT NULL DEFAULT '' COMMENT '原因',
  `status`           int(3)       NOT NULL DEFAULT '0' COMMENT '处理状态，0未处理，1预处理，2已处理',
  `score_change`     int(3)       NOT NULL DEFAULT '0' COMMENT '申请人分数变动',
  `ban_time`         varchar(6)   NOT NULL DEFAULT 'no' COMMENT '禁止使用时间:"一周"，"两周","1月"，"2月"，"1学期"，1学年，永久',
  `admin1_user`      varchar(12)  NOT NULL DEFAULT '' COMMENT '学生管理员',
  `admin2_user`      varchar(12)  NOT NULL DEFAULT '' COMMENT '老师管理员',
  `update_time`      int(11)      NOT NULL DEFAULT '0' COMMENT '处理时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `activity_id` (`activity_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 42
  DEFAULT CHARSET = utf8mb4 COMMENT ='活动惩罚表';

CREATE TABLE IF NOT EXISTS `cc_activity_punishment`
(
  `id`               int(11)      NOT NULL AUTO_INCREMENT COMMENT 'id',
  `apply_id`         int(11)      NOT NULL COMMENT '活动id',
  `complaint_reason` varchar(400) NOT NULL DEFAULT '' COMMENT '原因',
  `status`           int(3)       NOT NULL DEFAULT '0' COMMENT '处理状态，0未处理，1预处理，2已处理',
  `score_change`     int(3)       NOT NULL DEFAULT '0' COMMENT '申请人分数变动',
  `ban_time`         varchar(6)   NOT NULL DEFAULT 'no' COMMENT '禁止使用时间:"一周"，"两周","1月"，"2月"，"1学期"，1学年，永久',
  `admin1_user`      varchar(12)  NOT NULL DEFAULT '' COMMENT '学生管理员',
  `admin2_user`      varchar(12)  NOT NULL DEFAULT '' COMMENT '老师管理员',
  `update_time`      int(11)      NOT NULL DEFAULT '0' COMMENT '处理时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `apply_id` (`apply_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 33
  DEFAULT CHARSET = utf8mb4 COMMENT ='活动投诉表';



# CREATE TABLE IF NOT EXISTS `cc_apply_user` (
#   `id` int(11) NOT NULL COMMENT 'id' AUTO_INCREMENT,
#   `day` DATE NOT NULL COMMENT '记录日期',
#   `1001` varchar(32) NOT NULL default '00000000000000000000000000000000' COMMENT '10001场地使用情况',
#   `1002` varchar(32) NOT NULL default '00000000000000000000000000000000' COMMENT '10001场地使用情况',
#   `1003` varchar(32) NOT NULL default '00000000000000000000000000000000' COMMENT '10001场地使用情况',
#   `1004` varchar(32) NOT NULL default '00000000000000000000000000000000' COMMENT '10001场地使用情况',
#   primary key (`id`)
# )ENGINE=InnoDB DEFAULT charset utf8mb4 comment '场地使用表';
