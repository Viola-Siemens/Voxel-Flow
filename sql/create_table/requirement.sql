DROP TABLE IF EXISTS `requirement`;

CREATE TABLE `requirement` (
    `id` bigint(20) unsigned NOT NULL auto_increment COMMENT '自增主键',
    `code` varchar(45) NOT NULL COMMENT '需求编号',
    `title` varchar(45) NOT NULL COMMENT '需求标题',
    `description` varchar(45) NOT NULL DEFAULT '' COMMENT '需求描述',
    `status` varchar(45) NOT NULL DEFAULT 'REVIEWING' COMMENT '需求状态',
    `priority` tinyint(2) NOT NULL DEFAULT 2 COMMENT '需求优先级',
    `requirement_type` varchar(45) NOT NULL COMMENT '需求类型',
    `created_by` varchar(45) NULL COMMENT '创建人',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_by` varchar(45) NULL COMMENT '更新人',
    `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`) USING BTREE,
    KEY `idx_updated_at` (`updated_at`) USING BTREE
) ENGINE=InnoDB CHARSET=utf8mb4 COMMENT='需求表';

INSERT INTO `requirement` (`code`, `title`, `description`, `status`, `priority`, `requirement_type`) VALUES
('REQ-1', '【VoxelFlow】项目管理平台搭建需求', '需求描述 1', '待确认', 0, 'EFFICIENCY');
