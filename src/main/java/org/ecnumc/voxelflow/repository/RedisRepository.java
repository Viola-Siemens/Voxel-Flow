package org.ecnumc.voxelflow.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Nullable;
import java.util.concurrent.TimeUnit;

/**
 * 封装的调用 Redis 存储
 * @author liudongyu
 */
@Repository
@Slf4j
public class RedisRepository {
	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	/**
	 * 设置缓存，不会过期
	 * @param key	缓存 key
	 * @param value	缓存 value
	 */
	public void set(String key, String value) {
		this.redisTemplate.opsForValue().set(key, value);
	}

	/**
	 * 设置缓存，并设置超时时间
	 * @param key		缓存 key
	 * @param value		缓存 value
	 * @param timeout	超时时间，单位秒
	 */
	public void set(String key, String value, long timeout) {
		this.redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
	}

	/**
	 * 删除缓存
	 * @param key	缓存 key
	 */
	public void delete(String key) {
		this.redisTemplate.delete(key);
	}

	/**
	 * 获取缓存
	 * @param key	缓存 key
	 * @return 缓存 value
	 */
	@Nullable
	public String get(String key) {
		return this.redisTemplate.opsForValue().get(key);
	}
}
