package org.ecnumc.voxelflow.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 复盘状态喵~
 * @author liudongyu
 */
@Getter
@AllArgsConstructor
public enum RetrospectiveStatus {
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
	HANDLING("处理中") {
		@Override
		public RetrospectiveStatus next() {
			return FINISHED;
		}
	},
	FINISHED("已完成") {
		@Override
		public RetrospectiveStatus next() {
			return FINISHED;
		}
	},
	CANCELED("已取消") {
		@Override
		public RetrospectiveStatus next() {
			return CANCELED;
		}
	};

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
