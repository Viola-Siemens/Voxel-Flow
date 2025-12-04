package org.ecnumc.voxelflow.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.Executors;

/**
 * 定时任务配置
 * @author liudongyu
 */
@Configuration
public class ScheduleConfig implements SchedulingConfigurer {
	/**
	 * 创建一个线程池，用于执行定时任务
	 * @param taskRegistrar	定时任务注册器
	 */
	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		taskRegistrar.setScheduler(Executors.newScheduledThreadPool(8));
	}
}
