package org.ecnumc.voxelflow.enumeration;

import com.google.common.collect.ImmutableSet;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

import static org.ecnumc.voxelflow.enumeration.UserRole.*;

/**
 * @author liudongyu
 */
@Getter
@AllArgsConstructor
public enum RequirementStatus {
	REVIEWING("审核中", ImmutableSet.of(BUSINESS, DIAGNOSIS)) {
		@Override
		public RequirementStatus approved() {
			return COUNTERSIGNING;
		}

		@Override
		public RequirementStatus rejected() {
			return REJECTED;
		}
	},
	COUNTERSIGNING("三方会签中", ImmutableSet.of(PRODUCT, SECURITY, DEVELOPMENT, ART, MODEL, BUILDING)) {	//产品、信息安全、项目负责人
		@Override
		public RequirementStatus approved() {
			return REQUIREMENT_ANALYSIS;
		}

		@Override
		public RequirementStatus rejected() {
			return REJECTED;
		}
	},
	REQUIREMENT_ANALYSIS("需求分析中", ImmutableSet.of(PRODUCT)) {
		@Override
		public RequirementStatus approved() {
			return REQUIREMENT_REVIEWING;
		}

		@Override
		public RequirementStatus rejected() {
			return REJECTED;
		}
	},
	REQUIREMENT_REVIEWING("需求评审中", ImmutableSet.of(DEVELOPMENT, TEST, ART, MODEL, BUILDING)) {
		@Override
		public RequirementStatus approved() {
			return DESIGNING;
		}

		@Override
		public RequirementStatus rejected() {
			return REQUIREMENT_ANALYSIS;
		}
	},
	DESIGNING("设计中", ImmutableSet.of(ARCHITECTURE)) {
		@Override
		public RequirementStatus approved() {
			return SCHEDULING;
		}

		@Override
		public RequirementStatus rejected() {
			return REQUIREMENT_ANALYSIS;
		}
	},
	SCHEDULING("排期中", ImmutableSet.of(PRODUCT)) {
		@Override
		public RequirementStatus approved() {
			return DEVELOPING;
		}

		@Override
		public RequirementStatus rejected() {
			return REJECTED;
		}
	},
	DEVELOPING("开发中", ImmutableSet.of(DEVELOPMENT, ART, MODEL, BUILDING)) {
		@Override
		public RequirementStatus approved() {
			return TESTING;
		}

		@Override
		public RequirementStatus rejected() {
			return SCHEDULING;
		}
	},
	TESTING("测试中", ImmutableSet.of(TEST)) {
		@Override
		public RequirementStatus approved() {
			return CHECKING;
		}

		@Override
		public RequirementStatus rejected() {
			return DEVELOPING;
		}
	},
	CHECKING("验收中", ImmutableSet.of(BUSINESS)) {
		@Override
		public RequirementStatus approved() {
			return RELEASED;
		}

		@Override
		public RequirementStatus rejected() {
			return DEVELOPING;
		}
	},
	RELEASED("已发布", ImmutableSet.of()) {
		@Override
		public RequirementStatus approved() {
			return RELEASED;
		}

		@Override
		public RequirementStatus rejected() {
			return REJECTED;
		}
	},
	REJECTED("已打回", ImmutableSet.of()) {
		@Override
		public RequirementStatus approved() {
			return REJECTED;
		}

		@Override
		public RequirementStatus rejected() {
			return REJECTED;
		}
	},
	CANCELED("已取消", ImmutableSet.of()) {
		@Override
		public RequirementStatus approved() {
			return CANCELED;
		}

		@Override
		public RequirementStatus rejected() {
			return CANCELED;
		}
	};

	private final String name;
	private final Set<UserRole> operableRoles;

	/**
	 * 负责人完成该阶段
	 * @return 下一个阶段
	 */
	public abstract RequirementStatus approved();

	/**
	 * 负责人拒绝该阶段
	 * @return 拒绝后的阶段
	 */
	public abstract RequirementStatus rejected();

	/**
	 * 需求被取消
	 * @return 取消后的阶段
	 */
	public RequirementStatus canceled() {
		return CANCELED;
	}
}
