DROP TABLE IF EXISTS `user_role_rel`;

CREATE TABLE `user_role_rel` (
    `id` bigint(20) unsigned NOT NULL auto_increment COMMENT '自增主键',
    `uid` varchar(45) NOT NULL COMMENT '用户 UUID',
    `role` varchar(45) NOT NULL COMMENT '用户角色',
    `created_by` varchar(45) NOT NULL DEFAULT 'SYSTEM' COMMENT '创建人',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_by` varchar(45) NOT NULL DEFAULT 'SYSTEM' COMMENT '更新人',
    `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_uid` (`uid`) USING BTREE,
    KEY `idx_role` (`role`) USING BTREE,
    KEY `idx_updated_at` (`updated_at`) USING BTREE
) ENGINE=InnoDB CHARSET=utf8mb4 COMMENT='用户角色表';

INSERT INTO `user_role_rel` (`uid`, `role`) VALUES
('00000000-0000-0000-0000-000000000000', 'BUSINESS');
