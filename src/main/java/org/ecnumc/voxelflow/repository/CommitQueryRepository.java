package org.ecnumc.voxelflow.repository;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.ecnumc.voxelflow.mapper.CommitMapper;
import org.ecnumc.voxelflow.po.Commit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 提交记录查询 Repository
 * @author liudongyu
 */
@Repository
@Slf4j
@SuppressWarnings("java:S1192")
public class CommitQueryRepository {
	@Autowired
	private CommitMapper commitMapper;

	/**
	 * 获取所有提交记录
	 * @param pageNum	页数
	 * @param pageSize	页内最大元素数
	 * @return 所有提交记录
	 */
	public List<Commit> getAll(int pageNum, int pageSize) {
		return this.commitMapper.selectList(
				new QueryWrapper<Commit>()
						.orderByDesc("updated_at")
						.last("LIMIT " + pageSize + " OFFSET " + ((pageNum - 1) * pageSize))
		);
	}

	/**
	 * 获取所有提交记录的条数
	 * @return 提交记录的条数
	 */
	public int countAll() {
		return this.commitMapper.selectCount(null).intValue();
	}

	/**
	 * 获取指定故事/需求/需求编号的提交记录
	 * @param code		编号
	 * @param pageNum	页数
	 * @param pageSize	页内最大元素数
	 * @return 指定编号的所有提交记录
	 */
	public List<Commit> getByCode(String code, int pageNum, int pageSize) {
		return this.commitMapper.selectList(
				new QueryWrapper<Commit>()
						.eq("code", code)
						.orderByDesc("updated_at")
						.last("LIMIT " + pageSize + " OFFSET " + ((pageNum - 1) * pageSize))
		);
	}

	/**
	 * 获取指定故事/需求/需求编号的提交记录的条数
	 * @param code	编号
	 * @return 提交记录的条数
	 */
	public int countByCode(String code) {
		return this.commitMapper.selectCount(
				new QueryWrapper<Commit>()
						.eq("code", code)
		).intValue();
	}

	/**
	 * 获取指定故事/需求/需求编号和提交类型的提交记录
	 * @param code		编号
	 * @param type		提交类型
	 * @param pageNum	页数
	 * @param pageSize	页内最大元素数
	 * @return 指定编号和提交类型的所有提交记录
	 */
	public List<Commit> getByCodeAndType(String code, String type, int pageNum, int pageSize) {
		return this.commitMapper.selectList(
				new QueryWrapper<Commit>()
						.eq("code", code)
						.eq("commit_type", type)
						.orderByDesc("updated_at")
						.last("LIMIT " + pageSize + " OFFSET " + ((pageNum - 1) * pageSize))
		);
	}

	/**
	 * 获取指定故事/需求/需求编号和提交类型的提交记录的条数
	 * @param code	编号
	 * @param type	提交类型
	 * @return 提交记录的条数
	 */
	public int countByCodeAndType(String code, String type) {
		return this.commitMapper.selectCount(
				new QueryWrapper<Commit>()
						.eq("code", code)
						.eq("commit_type", type)
		).intValue();
	}

	/**
	 * 获取指定故事/需求/需求编号和用户的提交记录
	 * @param code		编号
	 * @param uid		用户 ID
	 * @param pageNum	页数
	 * @param pageSize	页内最大元素数
	 * @return 指定编号和用户的所有提交记录
	 */
	public List<Commit> getByCodeAndUid(String code, String uid, int pageNum, int pageSize) {
		return this.commitMapper.selectList(
				new QueryWrapper<Commit>()
						.eq("code", code)
						.eq("created_by", uid)
						.orderByDesc("updated_at")
						.last("LIMIT " + pageSize + " OFFSET " + ((pageNum - 1) * pageSize))
		);
	}

	/**
	 * 获取指定故事/需求/需求编号和用户的提交记录的条数
	 * @param code	编号
	 * @param uid	用户 ID
	 * @return 提交记录的条数
	 */
	public int countByCodeAndUid(String code, String uid) {
		return this.commitMapper.selectCount(
				new QueryWrapper<Commit>()
						.eq("code", code)
						.eq("created_by", uid)
		).intValue();
	}

	/**
	 * 获取指定故事/需求/需求编号、用户和提交类型的提交记录
	 * @param code		编号
	 * @param uid		用户 ID
	 * @param type		提交类型
	 * @param pageNum	页数
	 * @param pageSize	页内最大元素数
	 * @return 指定编号、用户和提交类型的所有提交记录
	 */
	public List<Commit> getByCodeAndUidAndType(String code, String uid, String type, int pageNum, int pageSize) {
		return this.commitMapper.selectList(
				new QueryWrapper<Commit>()
						.eq("code", code)
						.eq("created_by", uid)
						.eq("commit_type", type)
						.orderByDesc("updated_at")
						.last("LIMIT " + pageSize + " OFFSET " + ((pageNum - 1) * pageSize))
		);
	}

	/**
	 * 获取指定故事/需求/需求编号、用户和提交类型的提交记录的条数
	 * @param code	编号
	 * @param uid	用户 ID
	 * @param type	提交类型
	 * @return 提交记录的条数
	 */
	public int countByCodeAndUidAndType(String code, String uid, String type) {
		return this.commitMapper.selectCount(
				new QueryWrapper<Commit>()
						.eq("code", code)
						.eq("created_by", uid)
						.eq("commit_type", type)
		).intValue();
	}

	/**
	 * 获取指定用户的提交记录
	 * @param uid		用户 ID
	 * @param pageNum	页数
	 * @param pageSize	页内最大元素数
	 * @return 指定用户的所有提交记录
	 */
	public List<Commit> getByUid(String uid, int pageNum, int pageSize) {
		return this.commitMapper.selectList(
				new QueryWrapper<Commit>()
						.eq("created_by", uid)
						.orderByDesc("updated_at")
						.last("LIMIT " + pageSize + " OFFSET " + ((pageNum - 1) * pageSize))
		);
	}

	/**
	 * 获取指定用户的提交记录的条数
	 * @param uid	用户 ID
	 * @return 提交记录的条数
	 */
	public int countByUid(String uid) {
		return this.commitMapper.selectCount(
				new QueryWrapper<Commit>()
						.eq("created_by", uid)
		).intValue();
	}
}
