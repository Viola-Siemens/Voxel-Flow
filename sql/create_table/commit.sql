DROP TABLE IF EXISTS `commit`;

CREATE TABLE `commit` (
    `id` bigint(20) unsigned NOT NULL auto_increment COMMENT '自增主键',
    `commit_id` varchar(40) NOT NULL COMMENT '提交ID',
    `repo_url` varchar(2048) NOT NULL COMMENT '仓库地址',
    `commit_type` varchar(45) NOT NULL COMMENT '提交类型',
    `code` varchar(45) NOT NULL COMMENT '故事/问题/需求编号',
    `message` varchar(2048) NOT NULL COMMENT '提交信息',
    `commit_url` varchar(2048) NULL COMMENT '提交地址',
    `file` int(10) NOT NULL DEFAULT 0 COMMENT '修改文件数',
    `line` int(10) NOT NULL DEFAULT 0 COMMENT '修改行数',
    `created_by` varchar(45) NOT NULL DEFAULT 'SYSTEM' COMMENT '创建人',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_by` varchar(45) NOT NULL DEFAULT 'SYSTEM' COMMENT '更新人',
    `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_commit_id` (`commit_id`) USING BTREE,
    KEY `idx_updated_at` (`updated_at`) USING BTREE
) ENGINE=InnoDB CHARSET=utf8mb4 COMMENT='GitHub 提交记录';