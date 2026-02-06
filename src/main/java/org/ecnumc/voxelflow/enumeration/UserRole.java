package org.ecnumc.voxelflow.enumeration;

/**
 * 用户角色枚举，定义了系统中所有可用的用户角色类型喵~
 *
 * @author liudongyu
 */
public enum UserRole {
	/**
	 * 业务方，负责提供业务需求、项目验收
	 */
	BUSINESS,
	/**
	 * 产品，负责分析需求和撰写产品需求文档
	 */
	PRODUCT,
	/**
	 * 安全，负责审核项目是否符合安全要求
	 */
	SECURITY,
	/**
	 * 架构，负责架构设计，如系统架构、数据库架构、网络架构等
	 */
	ARCHITECTURE,
	/**
	 * 研发，负责技术开发，如模组工程师、数据包工程师、整合包工程师等
	 */
	DEVELOPMENT,
	/**
	 * 测试，负责设计测试样例、测试项目
	 */
	TEST,
	/**
	 * 运维，负责项目运维，如服务器、数据库、网络
	 */
	OPERATION,
	/**
	 * 美术
	 */
	ART,
	/**
	 * 模型
	 */
	MODEL,
	/**
	 * 建筑
	 */
	BUILDING,
	/**
	 * 诊断，负责游戏崩溃日志诊断和分析
	 */
	DIAGNOSIS,
	/**
	 * 超级管理员
	 */
	SUPER_ADMIN
}
