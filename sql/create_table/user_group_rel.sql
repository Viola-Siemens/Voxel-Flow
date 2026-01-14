DROP TABLE IF EXISTS `user_group_rel`;

CREATE TABLE `user_group_rel` (
    `id` bigint(20) unsigned NOT NULL auto_increment COMMENT '自增主键',
    `uid` varchar(45) NOT NULL COMMENT '用户 UID',
    `group_code` varchar(45) NOT NULL COMMENT '编码',
    `valid` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否可用',
    `created_by` varchar(45) NOT NULL DEFAULT 'SYSTEM' COMMENT '创建人',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_by` varchar(45) NOT NULL DEFAULT 'SYSTEM' COMMENT '更新人',
    `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_uid` (`uid`) USING BTREE,
    KEY `idx_group_code` (`group_code`) USING BTREE,
    KEY `idx_updated_at` (`updated_at`) USING BTREE
) ENGINE=InnoDB CHARSET=utf8mb4 COMMENT='部门/小组关系表';