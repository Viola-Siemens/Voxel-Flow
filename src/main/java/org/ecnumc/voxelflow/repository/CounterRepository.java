package org.ecnumc.voxelflow.repository;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.ecnumc.voxelflow.mapper.CounterMapper;
import org.ecnumc.voxelflow.po.Counter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * 计数器 Repository
 * @author liudongyu
 */
@Repository
@Slf4j
public class CounterRepository {
	@Autowired
	private CounterMapper counterMapper;

	/**
	 * 自增，并获取计数器值
	 * @param code	计数器编码
	 * @param uid	操作用户 ID
	 * @return 计数器值
	 */
	public int increaseAndGet(String code, String uid) {
		Counter counter = this.counterMapper.selectOne(new QueryWrapper<Counter>().eq("code", code));
		int cnt;
		if(counter == null) {
			cnt = 1;
			counter = new Counter(1);
			counter.setCode(code);
			counter.setCreatedBy(uid);
			this.counterMapper.insert(counter);
		} else {
			cnt = counter.getCnt() + 1;
			this.counterMapper.update(new UpdateWrapper<Counter>().eq("code", code).set("cnt", cnt));
		}
		return cnt;
	}
}
