package org.ecnumc.voxelflow.lock;

import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 分布式锁喵~
 * @author liudongyu
 */
@Component
public interface DistributedLock {
	/**
	 * 尝试获取锁喵~
	 * @param key		锁键
	 * @param waitTime	等待时间
	 * @param leaseTime	锁过期时间
	 * @param unit		时间单位
	 * @return 获取锁成功返回 true，否则返回 false
	 * @throws InterruptedException 等待锁过程中被中断
	 */
	boolean tryLock(String key, long waitTime, long leaseTime, TimeUnit unit) throws InterruptedException;

	/**
	 * 获取锁喵~
	 * @param key		锁键
	 * @param leaseTime	锁过期时间
	 * @param unit		时间单位
	 */
	void lock(String key, long leaseTime, TimeUnit unit);

	/**
	 * 释放锁喵~
	 * @param key	锁键
	 */
	void unlock(String key);
}
