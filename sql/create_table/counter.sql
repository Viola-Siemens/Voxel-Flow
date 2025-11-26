DROP TABLE IF EXISTS `counter`;

CREATE TABLE `counter` (
    `id` bigint(20) unsigned NOT NULL auto_increment COMMENT '自增主键',
    `code` varchar(45) NOT NULL COMMENT '编号 key',
    `cnt` int(10) NOT NULL COMMENT '当前最新 code 的编号数字',
    `created_by` varchar(45) NOT NULL DEFAULT 'SYSTEM' COMMENT '创建人',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_by` varchar(45) NOT NULL DEFAULT 'SYSTEM' COMMENT '更新人',
    `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`) USING BTREE,
    KEY `idx_updated_at` (`updated_at`) USING BTREE
) ENGINE=InnoDB CHARSET=utf8mb4 COMMENT='用户表';
