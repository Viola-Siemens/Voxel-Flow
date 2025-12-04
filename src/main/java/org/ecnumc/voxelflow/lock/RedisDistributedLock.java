package org.ecnumc.voxelflow.lock;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Redis 分布式锁
 * @author liudongyu
 */
@Component
@Slf4j
public class RedisDistributedLock implements DistributedLock {
	@Autowired
	private RedisTemplate<String, String> redisTemplate;

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

	@Override
	public void unlock(String key) {
		long threadId = Thread.currentThread().getId();
		String value = this.redisTemplate.opsForValue().getAndDelete(key);
		if(!Objects.equals(String.valueOf(threadId), value)) {
			log.warn("Failed to unlock, key: {}, threadId: {}, value: {}", key, threadId, value);
		}
	}

	private boolean tryAcquire(String key, long leaseMillis, long threadId) {
		String value = String.valueOf(threadId);
		Boolean result = this.redisTemplate.opsForValue().setIfAbsent(key, value, Duration.ofMillis(leaseMillis));
		if(result == null) {
			return false;
		}
		return result;
	}
}
