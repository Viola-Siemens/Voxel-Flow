DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
    `id` bigint(20) unsigned NOT NULL auto_increment COMMENT '自增主键',
    `uid` varchar(45) NOT NULL COMMENT '用户 UUID',
    `username` varchar(45) NOT NULL COMMENT '用户名',
    `password` varchar(45) NOT NULL COMMENT '密码',
    `email` varchar(45) NOT NULL COMMENT '邮箱',
    `email_verified` varchar(45) NOT NULL DEFAULT 'UNVERIFIED' COMMENT '是否已验证邮箱',
    `user_status` varchar(45) NOT NULL DEFAULT 'ACTIVE' COMMENT '用户状态',
    `created_by` varchar(45) NULL COMMENT '创建人',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_by` varchar(45) NULL COMMENT '更新人',
    `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_uid` (`uid`) USING BTREE,
    UNIQUE KEY `uk_username` (`username`) USING BTREE,
    KEY `idx_updated_at` (`updated_at`) USING BTREE
) ENGINE=InnoDB CHARSET=utf8mb4 COMMENT='用户表';

INSERT INTO `user` (`uid`, `username`, `password`, `email`, `email_verified`) VALUES
('00000000-0000-0000-0000-000000000000', 'dummy', 'dummy_password', '10185101162@stu.ecnu.edu.cn', 'VERIFIED');
