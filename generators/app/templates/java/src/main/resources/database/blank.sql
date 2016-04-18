USE blank;

DROP TABLE IF EXISTS `tb_demo`;
CREATE TABLE `tb_demo` (
  `id` int(11) NOT NULL auto_increment COMMENT '自增ID',
  `name` varchar(128) NULL default '' COMMENT 'name',
  `created_on` timestamp NULL COMMENT '创建时间',
  `modified_on` timestamp NULL COMMENT '更新时间',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB COMMENT='Demo表';