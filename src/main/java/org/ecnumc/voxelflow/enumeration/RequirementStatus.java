package org.ecnumc.voxelflow.enumeration;

import com.google.common.collect.ImmutableSet;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.ecnumc.voxelflow.util.IOperableStatus;

import java.util.Set;

import static org.ecnumc.voxelflow.enumeration.UserRole.*;

/**
 * 需求状态枚举，定义了需求从创建到发布的完整生命周期状态喵~
 *
 * @author liudongyu
 */
@Getter
@AllArgsConstructor
public enum RequirementStatus implements IOperableStatus {
	/**
	 * 审核中状态，由业务、诊断角色审核需求的合理性和必要性喵~
	 */
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
	/**
	 * 三方会签中状态，由产品、信息安全、项目负责人（开发/美术/模型/建筑）进行会签，需所有人批准才能通过喵~
	 */
	COUNTERSIGNING("三方会签中", ImmutableSet.of(PRODUCT, SECURITY, DEVELOPMENT, ART, MODEL, BUILDING)) {	//产品、信息安全、项目负责人
		@Override
		public RequirementStatus approved() {
			return REQUIREMENT_ANALYSIS;
		}

		@Override
		public RequirementStatus rejected() {
			return REJECTED;
		}

		@Override
		public boolean waitingForAllApprovals() {
			return true;
		}
	},
	/**
	 * 需求分析中状态，由产品角色进行详细的需求分析和方案设计喵~
	 */
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
	/**
	 * 需求评审中状态，由开发、架构、测试、美术、模型、建筑角色评审需求方案，需所有人批准才能通过喵~
	 */
	REQUIREMENT_REVIEWING("需求评审中", ImmutableSet.of(DEVELOPMENT, ARCHITECTURE, TEST, ART, MODEL, BUILDING)) {
		@Override
		public RequirementStatus approved() {
			return DESIGNING;
		}

		@Override
		public RequirementStatus rejected() {
			return REQUIREMENT_ANALYSIS;
		}

		@Override
		public boolean waitingForAllApprovals() {
			return true;
		}
	},
	/**
	 * 设计中状态，由架构角色进行技术方案设计和架构规划喵~
	 */
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
	/**
	 * 排期中状态，由产品角色进行开发任务拆分和时间排期喵~
	 */
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
	/**
	 * 开发中状态，由开发、美术、模型、建筑角色进行功能开发和资源制作喵~
	 */
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
	/**
	 * 测试中状态，由测试角色进行功能测试和质量验证喵~
	 */
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
	/**
	 * 验收中状态，由业务、产品角色验收需求实现效果喵~
	 */
	CHECKING("验收中", ImmutableSet.of(BUSINESS, PRODUCT)) {
		@Override
		public RequirementStatus approved() {
			return RELEASED;
		}

		@Override
		public RequirementStatus rejected() {
			return DEVELOPING;
		}
	},
	/**
	 * 已发布状态，需求已上线发布，流程结束喵~
	 */
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
	/**
	 * 已打回状态，需求在某个阶段被拒绝，需要重新审核或关闭喵~
	 */
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
	/**
	 * 已取消状态，需求被主动取消，不再继续执行喵~
	 */
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
	 * 负责人完成该阶段喵~
	 * @return 下一个阶段
	 */
	public abstract RequirementStatus approved();

	/**
	 * 负责人拒绝该阶段喵~
	 * @return 拒绝后的阶段
	 */
	public abstract RequirementStatus rejected();

	/**
	 * 需求被取消喵~
	 * @return 取消后的阶段
	 */
	public RequirementStatus canceled() {
		return CANCELED;
	}

	/**
	 * 是否应等待所有负责人完成喵~
	 * @return true 表示等待所有负责人完成，false 则任意一位完成即可
	 */
	public boolean waitingForAllApprovals() {
		return false;
	}
}
