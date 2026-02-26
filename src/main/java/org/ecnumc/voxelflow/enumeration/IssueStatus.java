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

	private final String name;
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
