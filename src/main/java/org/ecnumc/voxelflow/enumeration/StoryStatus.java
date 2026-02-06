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
 * 故事状态喵~
 * @author liudongyu
 */
@Getter
@AllArgsConstructor
public enum StoryStatus implements IOperableStatus {
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

	private final String name;
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
