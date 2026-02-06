package org.ecnumc.voxelflow.lock;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Redis 分布式锁喵~
 * @author liudongyu
 */
@Component
@Slf4j
public class RedisDistributedLock implements DistributedLock {
	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	/**
	 * 尝试获取分布式锁，支持等待超时和锁自动过期喵~
	 *
	 * @param key 锁的键名喵~
	 * @param waitTime 等待获取锁的最长时间喵~
	 * @param leaseTime 锁的自动过期时间喵~
	 * @param unit 时间单位喵~
	 * @return true 表示成功获取锁，false 表示获取失败喵~
	 * @throws InterruptedException 如果等待过程中线程被中断喵~
	 */
	@SuppressWarnings("BusyWait")
	@Override
	public boolean tryLock(String key, long waitTime, long leaseTime, TimeUnit unit) throws InterruptedException {
		long waitMillis = unit.toMillis(waitTime);
		long leaseMillis = unit.toMillis(leaseTime);
		long current = System.currentTimeMillis();
		long threadId = Thread.currentThread().getId();
		// 获取锁
		boolean isAcquire = this.tryAcquire(key, leaseMillis, threadId);
		if (isAcquire) {
			return true;
		}

		waitMillis -= System.currentTimeMillis() - current;
		// 等待时间用完，获取锁失败
		if (waitMillis <= 0) {
			return false;
		}
		// 获取锁
		while (true) {
			long currentTime = System.currentTimeMillis();
			Thread.sleep(1L);
			isAcquire = this.tryAcquire(key, leaseMillis, threadId);
			if (isAcquire) {
				return true;
			}

			waitMillis -= System.currentTimeMillis() - currentTime;
			if (waitMillis <= 0) {
				return false;
			}
		}
	}

	/**
	 * 获取分布式锁，会一直阻塞直到成功获取锁喵~
	 *
	 * @param key 锁的键名喵~
	 * @param leaseTime 锁的自动过期时间喵~
	 * @param unit 时间单位喵~
	 */
	@Override
	public void lock(String key, long leaseTime, TimeUnit unit) {
		long threadId = Thread.currentThread().getId();
		long leaseMillis = unit.toMillis(leaseTime);
		boolean isAcquire = this.tryAcquire(key, leaseMillis, threadId);
		while(!isAcquire) {
			try {
				Thread.sleep(1L);
			} catch (InterruptedException e) {
				log.error("Failed to sleep: ", e);
				Thread.currentThread().interrupt();
			}
			isAcquire = this.tryAcquire(key, leaseMillis, threadId);
		}
	}

	/**
	 * 释放分布式锁，只有持有锁的线程才能成功释放喵~
	 *
	 * @param key 锁的键名喵~
	 */
	@Override
	public void unlock(String key) {
		long threadId = Thread.currentThread().getId();
		String value = this.redisTemplate.opsForValue().getAndDelete(key);
		if(!Objects.equals(String.valueOf(threadId), value)) {
			log.warn("Failed to unlock, key: {}, threadId: {}, value: {}", key, threadId, value);
		}
	}

	/**
	 * 尝试获取锁的内部实现，使用 Redis 的 SETNX 命令实现喵~
	 *
	 * @param key 锁的键名喵~
	 * @param leaseMillis 锁的自动过期时间（毫秒）喵~
	 * @param threadId 当前线程 ID，用作锁的值喵~
	 * @return true 表示成功获取锁，false 表示获取失败喵~
	 */
	private boolean tryAcquire(String key, long leaseMillis, long threadId) {
		String value = String.valueOf(threadId);
		Boolean result = this.redisTemplate.opsForValue().setIfAbsent(key, value, Duration.ofMillis(leaseMillis));
		if(result == null) {
			return false;
		}
		return result;
	}
}
