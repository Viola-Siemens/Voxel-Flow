package org.ecnumc.voxelflow.service;

import lombok.extern.slf4j.Slf4j;
import org.ecnumc.voxelflow.converter.CommitConverter;
import org.ecnumc.voxelflow.repository.CommitQueryRepository;
import org.ecnumc.voxelflow.resp.CommitResp;
import org.ecnumc.voxelflow.resp.PagedResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.util.stream.Collectors;

/**
 * 提交记录查询服务，提供代码提交记录的查询功能喵~
 *
 * @author liudongyu
 */
@Service
@Slf4j
public class CommitQueryService {
	@Autowired
	private CommitQueryRepository commitQueryRepository;

	@Autowired
	private CommitConverter commitConverter;

	/**
	 * 查询用户提交记录
	 * @param uid		提交人
	 * @param pageNum	页数
	 * @param pageSize	页内最大元素数
	 */
	public PagedResp<CommitResp> queryUser(@Nullable String uid, int pageNum, int pageSize) {
		if(uid == null) {
			return PagedResp.<CommitResp>builder()
					.pageNum(pageNum)
					.pageSize(pageSize)
					.total(this.commitQueryRepository.countAll())
					.list(this.commitQueryRepository.getAll(pageNum, pageSize).stream().map(this.commitConverter::convertToResp).collect(Collectors.toList()))
					.build();
		}
		return PagedResp.<CommitResp>builder()
				.pageNum(pageNum)
				.pageSize(pageSize)
				.total(this.commitQueryRepository.countByUid(uid))
				.list(this.commitQueryRepository.getByUid(uid, pageNum, pageSize).stream().map(this.commitConverter::convertToResp).collect(Collectors.toList()))
				.build();
	}

	/**
	 * 查询编号内容对应的提交记录
	 * @param code		故事/问题/需求编号
	 * @param type		提交类型
	 * @param uid		提交人
	 * @param pageNum	页数
	 * @param pageSize	页内最大元素数
	 */
	public PagedResp<CommitResp> queryCode(String code, @Nullable String type, @Nullable String uid, int pageNum, int pageSize) {
		if(type == null) {
			if(uid == null) {
				return PagedResp.<CommitResp>builder()
						.pageNum(pageNum)
						.pageSize(pageSize)
						.total(this.commitQueryRepository.countByCode(code))
						.list(this.commitQueryRepository.getByCode(code, pageNum, pageSize).stream().map(this.commitConverter::convertToResp).collect(Collectors.toList()))
						.build();
			}
			return PagedResp.<CommitResp>builder()
					.pageNum(pageNum)
					.pageSize(pageSize)
					.total(this.commitQueryRepository.countByCodeAndUid(code, uid))
					.list(this.commitQueryRepository.getByCodeAndUid(code, uid, pageNum, pageSize).stream().map(this.commitConverter::convertToResp).collect(Collectors.toList()))
					.build();
		}
		if(uid == null) {
			return PagedResp.<CommitResp>builder()
					.pageNum(pageNum)
					.pageSize(pageSize)
					.total(this.commitQueryRepository.countByCodeAndType(code, type))
					.list(this.commitQueryRepository.getByCodeAndType(code, type, pageNum, pageSize).stream().map(this.commitConverter::convertToResp).collect(Collectors.toList()))
					.build();
		}
		return PagedResp.<CommitResp>builder()
				.pageNum(pageNum)
				.pageSize(pageSize)
				.total(this.commitQueryRepository.countByCodeAndUidAndType(code, uid, type))
				.list(this.commitQueryRepository.getByCodeAndUidAndType(code, uid, type, pageNum, pageSize).stream().map(this.commitConverter::convertToResp).collect(Collectors.toList()))
				.build();
	}
}
