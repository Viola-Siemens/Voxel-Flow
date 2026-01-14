DROP TABLE IF EXISTS `group`;

CREATE TABLE `group` (
    `id` bigint(20) unsigned NOT NULL auto_increment COMMENT '自增主键',
    `code` varchar(45) NOT NULL COMMENT '编码',
    `name` varchar(45) NOT NULL COMMENT '名称',
    `leader` varchar(45) NULL COMMENT '主管/组长 uid',
    `created_by` varchar(45) NOT NULL DEFAULT 'SYSTEM' COMMENT '创建人',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_by` varchar(45) NOT NULL DEFAULT 'SYSTEM' COMMENT '更新人',
    `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`) USING BTREE,
    KEY `idx_updated_at` (`updated_at`) USING BTREE
) ENGINE=InnoDB CHARSET=utf8mb4 COMMENT='部门/小组';