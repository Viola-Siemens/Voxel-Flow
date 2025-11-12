package org.ecnumc.voxelflow.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author liudongyu
 */
@Getter
@AllArgsConstructor
public enum StoryStatus {
	DRAFT("草稿") {
		@Override
		public StoryStatus approved() {
			return PROGRESSING;
		}

		@Override
		public StoryStatus rejected() {
			return REJECTED;
		}
	},
	PROGRESSING("进行中") {
		@Override
		public StoryStatus approved() {
			return FINISHED;
		}

		@Override
		public StoryStatus rejected() {
			return REJECTED;
		}
	},
	FINISHED("已完成") {
		@Override
		public StoryStatus approved() {
			return FINISHED;
		}

		@Override
		public StoryStatus rejected() {
			return REJECTED;
		}
	},
	REJECTED("已打回") {
		@Override
		public StoryStatus approved() {
			return REJECTED;
		}

		@Override
		public StoryStatus rejected() {
			return REJECTED;
		}
	},
	CANCELED("已取消") {
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

	/**
	 * 负责人完成该阶段
	 * @return 下一个阶段
	 */
	public abstract StoryStatus approved();

	/**
	 * 负责人拒绝该阶段
	 * @return 拒绝后的阶段
	 */
	public abstract StoryStatus rejected();

	/**
	 * 需求被取消
	 * @return 取消后的阶段
	 */
	public StoryStatus canceled() {
		return CANCELED;
	}
}
