DROP TABLE IF EXISTS `issue`;

CREATE TABLE `issue` (
    `id` bigint(20) unsigned NOT NULL auto_increment COMMENT '自增主键',
    `code` varchar(45) NOT NULL COMMENT '问题编号',
    `title` varchar(45) NOT NULL COMMENT '问题标题',
    `description` varchar(45) NOT NULL DEFAULT '' COMMENT '问题描述',
    `status` varchar(45) NOT NULL DEFAULT 'REVIEWING' COMMENT '问题状态',
    `priority` tinyint(2) NOT NULL DEFAULT 2 COMMENT '问题优先级',
    `created_by` varchar(45) NULL COMMENT '创建人',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_by` varchar(45) NULL COMMENT '更新人',
    `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`) USING BTREE,
    KEY `idx_updated_at` (`updated_at`) USING BTREE
) ENGINE=InnoDB CHARSET=utf8mb4 COMMENT='问题表';
