package org.ecnumc.voxelflow.enumeration;

import com.google.common.collect.ImmutableSet;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.ecnumc.voxelflow.util.IOperableStatus;

import java.util.Set;

import static org.ecnumc.voxelflow.enumeration.UserRole.*;
import static org.ecnumc.voxelflow.enumeration.UserRole.ART;
import static org.ecnumc.voxelflow.enumeration.UserRole.BUILDING;
import static org.ecnumc.voxelflow.enumeration.UserRole.MODEL;

/**
 * 故事状态枚举，定义了故事从草稿到完成的完整生命周期状态喵~
 *
 * @author liudongyu
 */
@Getter
@AllArgsConstructor
public enum StoryStatus implements IOperableStatus {
	/**
	 * 草稿状态，故事已创建，由开发、架构、美术、模型、建筑角色编写和完善喵~
	 */
	DRAFT("草稿", ImmutableSet.of(DEVELOPMENT, ARCHITECTURE, ART, MODEL, BUILDING)) {
		@Override
		public StoryStatus approved() {
			return PROGRESSING;
		}

		@Override
		public StoryStatus rejected() {
			return REJECTED;
		}
	},
	/**
	 * 进行中状态，故事开始开发，由开发、美术、模型、建筑角色实现功能喵~
	 */
	PROGRESSING("进行中", ImmutableSet.of(DEVELOPMENT, ART, MODEL, BUILDING)) {
		@Override
		public StoryStatus approved() {
			return TESTING;
		}

		@Override
		public StoryStatus rejected() {
			return REJECTED;
		}
	},
	/**
	 * 测试中状态，故事开发完成，由测试角色进行功能测试和质量验证喵~
	 */
	TESTING("测试中", ImmutableSet.of(TEST)) {
		@Override
		public StoryStatus approved() {
			return FINISHED;
		}

		@Override
		public StoryStatus rejected() {
			return PROGRESSING;
		}
	},
	/**
	 * 已完成状态，故事已测试通过，流程结束喵~
	 */
	FINISHED("已完成", ImmutableSet.of()) {
		@Override
		public StoryStatus approved() {
			return FINISHED;
		}

		@Override
		public StoryStatus rejected() {
			return REJECTED;
		}
	},
	/**
	 * 已打回状态，故事在某个阶段被拒绝，需要重新处理或关闭喵~
	 */
	REJECTED("已打回", ImmutableSet.of()) {
		@Override
		public StoryStatus approved() {
			return REJECTED;
		}

		@Override
		public StoryStatus rejected() {
			return REJECTED;
		}
	},
	/**
	 * 已取消状态，故事被主动取消，不再继续执行喵~
	 */
	CANCELED("已取消", ImmutableSet.of()) {
		@Override
		public StoryStatus approved() {
			return CANCELED;
		}

		@Override
		public StoryStatus rejected() {
			return CANCELED;
		}
	};

	/**
	 * 状态名称喵~
	 */
	private final String name;
	/**
	 * 可操作的角色喵~
	 */
	private final Set<UserRole> operableRoles;

	/**
	 * 负责人完成该阶段喵~
	 * @return 下一个阶段
	 */
	public abstract StoryStatus approved();

	/**
	 * 负责人拒绝该阶段喵~
	 * @return 拒绝后的阶段
	 */
	public abstract StoryStatus rejected();

	/**
	 * 需求被取消喵~
	 * @return 取消后的阶段
	 */
	public StoryStatus canceled() {
		return CANCELED;
	}
}
