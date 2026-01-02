package org.ecnumc.voxelflow.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Objects;

/**
 * 用户验证 Repository，主要用于设置和校验 token。
 * @author liudongyu
 */
@Repository
@Slf4j
public class UserValidationRepository {
	@Autowired
	private RedisRepository redisRepository;

	/**
	 * 验证用户 TOKEN
	 * @param uid	用户 UID
	 * @param token	用户 TOKEN
	 * @return 验证结果
	 */
	public boolean validateToken(String uid, String token) {
		String redisToken = this.redisRepository.get(uid);
		return Objects.equals(redisToken, token);
	}

	/**
	 * 设置用户 TOKEN
	 * @param uid	用户 UID
	 * @param token	用户 TOKEN
	 */
	public void setToken(String uid, String token) {
		// TOKEN 半天过期
		this.redisRepository.set(uid, token, 43200L);
	}

	/**
	 * 删除用户 TOKEN
	 * @param uid	用户 UID
	 */
	public void deleteToken(String uid) {
		this.redisRepository.delete(uid);
	}
}
