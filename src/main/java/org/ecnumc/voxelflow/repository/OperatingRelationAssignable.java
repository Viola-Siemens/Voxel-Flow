package org.ecnumc.voxelflow.repository;

import org.ecnumc.voxelflow.enumeration.RelationType;

import java.util.List;

/**
 * 操作关系修改接口
 * @author liudongyu
 * @param <S> 状态类型
 */
public interface OperatingRelationAssignable<S> {
	/**
	 * 更新关系
	 * @param code			编码
	 * @param oldStatus		旧的状态
	 * @param description	修改描述，如同意/拒绝理由
	 * @param relationType	修改类型
	 * @param updatedBy		更新人
	 */
	void updateRelation(String code, S oldStatus, String description, RelationType relationType, String updatedBy);

	/**
	 * 跳过剩余的修改关系
	 * @param code		编码
	 * @param oldStatus	旧的状态
	 * @param updatedBy	更新人
	 */
	void skipRemainingRelations(String code, S oldStatus, String updatedBy);

	/**
	 * 委派下一位责任人处理
	 * @param code		编码
	 * @param status	新的状态
	 * @param operator	下一位责任人
	 * @param updatedBy	更新人
	 */
	void assignOperator(String code, S status, String operator, String updatedBy);

	/**
	 * 撤销下一位责任人委派
	 * @param code		编码
	 * @param status	新的状态
	 * @param operator	下一位责任人
	 * @param updatedBy	更新人
	 */
	boolean unassignOperator(String code, S status, String operator, String updatedBy);

	/**
	 * 委派多位责任人处理
	 * @param code		编码
	 * @param status	新的状态
	 * @param operators	责任人们
	 * @param updatedBy	更新人
	 */
	void assignOperators(String code, S status, List<String> operators, String updatedBy);
}
