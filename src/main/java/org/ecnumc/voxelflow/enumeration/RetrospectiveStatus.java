package org.ecnumc.voxelflow.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 复盘状态枚举，定义了复盘从就绪到完成的完整生命周期状态喵~
 *
 * @author liudongyu
 */
@Getter
@AllArgsConstructor
public enum RetrospectiveStatus {
	/**
	 * 就绪状态，复盘已创建，等待所有参与人确认后开始处理喵~
	 */
	READY("就绪") {
		@Override
		public boolean waitingForAllApprovals() {
			return true;
		}

		@Override
		public RetrospectiveStatus next() {
			return HANDLING;
		}
	},
	/**
	 * 处理中状态，复盘正在进行，参与人编写总结和反思喵~
	 */
	HANDLING("处理中") {
		@Override
		public RetrospectiveStatus next() {
			return FINISHED;
		}
	},
	/**
	 * 已完成状态，复盘已完成，流程结束喵~
	 */
	FINISHED("已完成") {
		@Override
		public RetrospectiveStatus next() {
			return FINISHED;
		}
	},
	/**
	 * 已取消状态，复盘被主动取消，不再继续执行喵~
	 */
	CANCELED("已取消") {
		@Override
		public RetrospectiveStatus next() {
			return CANCELED;
		}
	};

	/**
	 * 状态名称喵~
	 */
	private final String name;

	/**
	 * 是否应等待所有负责人完成喵~
	 * @return true 表示等待所有负责人完成，false 则任意一位完成即可
	 */
	public boolean waitingForAllApprovals() {
		return false;
	}

	/**
	 * 获取下一个状态喵~
	 * @return 下一个状态
	 */
	public abstract RetrospectiveStatus next();
}
