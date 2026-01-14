DROP TABLE IF EXISTS `story`;

CREATE TABLE `story` (
    `id` bigint(20) unsigned NOT NULL auto_increment COMMENT '自增主键',
    `code` varchar(45) NOT NULL COMMENT '故事编号',
    `req_code` varchar(45) NOT NULL COMMENT '对应的需求编号',
    `title` varchar(250) NOT NULL COMMENT '故事标题',
    `description` TEXT NOT NULL COMMENT '故事描述',
    `status` varchar(45) NOT NULL DEFAULT 'DRAFT' COMMENT '故事状态',
    `priority` tinyint(2) NOT NULL DEFAULT 2 COMMENT '故事优先级',
    `created_by` varchar(45) NOT NULL DEFAULT 'SYSTEM' COMMENT '创建人',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_by` varchar(45) NOT NULL DEFAULT 'SYSTEM' COMMENT '更新人',
    `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`) USING BTREE,
    KEY `idx_updated_at` (`updated_at`) USING BTREE
) ENGINE=InnoDB CHARSET=utf8mb4 COMMENT='故事表';
