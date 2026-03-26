package org.ecnumc.voxelflow.enumeration;

import com.google.common.collect.ImmutableSet;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.ecnumc.voxelflow.util.IOperableStatus;

import java.util.Set;

import static org.ecnumc.voxelflow.enumeration.UserRole.*;

/**
 * 问题状态枚举，定义了问题从创建到发布的完整生命周期状态喵~
 *
 * @author liudongyu
 */
@Getter
@AllArgsConstructor
public enum IssueStatus implements IOperableStatus {
	/**
	 * 审核中状态，由业务、测试、诊断角色审核问题的有效性喵~
	 */
	REVIEWING("审核中", ImmutableSet.of(BUSINESS, TEST, DIAGNOSIS)) {
		@Override
		public IssueStatus approved() {
			return CONFIRMING;
		}

		@Override
		public IssueStatus rejected() {
			return REJECTED;
		}
	},
	/**
	 * 确认中状态，由开发、测试、诊断角色确认问题的处理方案喵~
	 */
	CONFIRMING("确认中", ImmutableSet.of(DEVELOPMENT, TEST, DIAGNOSIS)) {
		@Override
		public IssueStatus approved() {
			return HANDLING;
		}

		@Override
		public IssueStatus rejected() {
			return REJECTED;
		}
	},
	/**
	 * 处理中状态，由开发、美术、模型、建筑角色处理问题喵~
	 */
	HANDLING("处理中", ImmutableSet.of(DEVELOPMENT, ART, MODEL, BUILDING)) {
		@Override
		public IssueStatus approved() {
			return TESTING;
		}

		@Override
		public IssueStatus rejected() {
			return CONFIRMING;
		}
	},
	/**
	 * 测试中状态，由测试角色验证问题修复效果喵~
	 */
	TESTING("测试中", ImmutableSet.of(TEST)) {
		@Override
		public IssueStatus approved() {
			return CHECKING;
		}

		@Override
		public IssueStatus rejected() {
			return HANDLING;
		}
	},
	/**
	 * 验收中状态，由业务、诊断角色验收问题修复成果喵~
	 */
	CHECKING("验收中", ImmutableSet.of(BUSINESS, DIAGNOSIS)) {
		@Override
		public IssueStatus approved() {
			return RELEASED;
		}

		@Override
		public IssueStatus rejected() {
			return HANDLING;
		}
	},
	/**
	 * 已发布状态，问题修复已上线发布，流程结束喵~
	 */
	RELEASED("已发布", ImmutableSet.of()) {
		@Override
		public IssueStatus approved() {
			return RELEASED;
		}

		@Override
		public IssueStatus rejected() {
			return REJECTED;
		}
	},
	/**
	 * 已打回状态，问题在某个阶段被拒绝，需要重新处理或关闭喵~
	 */
	REJECTED("已打回", ImmutableSet.of()) {
		@Override
		public IssueStatus approved() {
			return REJECTED;
		}

		@Override
		public IssueStatus rejected() {
			return REJECTED;
		}
	},
	/**
	 * 已取消状态，问题被主动取消，不再处理喵~
	 */
	CANCELED("已取消", ImmutableSet.of()) {
		@Override
		public IssueStatus approved() {
			return CANCELED;
		}

		@Override
		public IssueStatus rejected() {
			return CANCELED;
		}
	};

	/**
	 * 状态名称
	 */
	private final String name;
	/**
	 * 可操作的角色
	 */
	private final Set<UserRole> operableRoles;

	/**
	 * 负责人完成该阶段喵~
	 * @return 下一个阶段
	 */
	public abstract IssueStatus approved();

	/**
	 * 负责人拒绝该阶段喵~
	 * @return 拒绝后的阶段
	 */
	public abstract IssueStatus rejected();

	/**
	 * 问题被取消喵~
	 * @return 取消后的阶段
	 */
	public IssueStatus canceled() {
		return CANCELED;
	}
}
