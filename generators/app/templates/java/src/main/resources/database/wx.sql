USE wx;

DROP TABLE IF EXISTS `tb_wx_user`;
CREATE TABLE `tb_wx_user` (
  `id` int(11) NOT NULL auto_increment COMMENT '自增ID',
  `openid` varchar(128) NULL default '' COMMENT '微信OpenID',
  `md5_openid` varchar(128) NULL default '' COMMENT 'MD5加密后的微信OpenID',
  `nickname` varchar(128) NULL default '' COMMENT '微信名称',
  `sex` varchar(4) NULL default '' COMMENT '性别',
  `province` varchar(32) NULL default '' COMMENT '省份',
  `city` varchar(32) NULL default '' COMMENT '城市',
  `country` varchar(32) NULL default '' COMMENT '国家',
  `headimgurl` varchar(512) NULL default '' COMMENT '头像',
  `created_on` timestamp NULL COMMENT '创建时间',
  `modified_on` timestamp NULL COMMENT '更新时间',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB COMMENT='微信用户信息表';