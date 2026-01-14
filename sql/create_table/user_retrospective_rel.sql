DROP TABLE IF EXISTS `user_retrospective_rel`;

CREATE TABLE `user_retrospective_rel` (
    `id` bigint(20) unsigned NOT NULL auto_increment COMMENT '自增主键',
    `code` varchar(45) NOT NULL COMMENT '复盘单编号',
    `uid` varchar(45) NOT NULL COMMENT '用户 UUID',
    `description` TEXT NOT NULL COMMENT '修改描述，如同意/拒绝理由',
    `relation_type` varchar(45) NOT NULL COMMENT '关系类型',
    `old_status` varchar(45) NULL COMMENT '复盘单旧状态',
    `new_status` varchar(45) NULL COMMENT '复盘单新状态',
    `created_by` varchar(45) NOT NULL DEFAULT 'SYSTEM' COMMENT '创建人',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_by` varchar(45) NOT NULL DEFAULT 'SYSTEM' COMMENT '更新人',
    `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_code_uid` (`code`, `uid`) USING BTREE,
    KEY `idx_updated_at` (`updated_at`) USING BTREE
) ENGINE=InnoDB CHARSET=utf8mb4 COMMENT='复盘单修改记录';