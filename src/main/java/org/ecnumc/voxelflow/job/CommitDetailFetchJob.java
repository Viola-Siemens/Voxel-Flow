package org.ecnumc.voxelflow.job;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.ecnumc.voxelflow.lock.RedisDistributedLock;
import org.ecnumc.voxelflow.mapper.CommitMapper;
import org.ecnumc.voxelflow.po.Commit;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.select.Evaluator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 获取提交详情，定时请求喵~
 * @author liudongyu
 */
@Component
@EnableScheduling
@Slf4j
public class CommitDetailFetchJob {
	@Autowired
	private CommitMapper commitMapper;

	@Autowired
	private RedisDistributedLock redisDistributedLock;

	private static final String LOCK_KEY = "commit_detail_fetch";

	/**
	 * 每 10 分钟请求一次，失败则下次重新请求喵~
	 */
	@Scheduled(cron = "0 0/10 * * * ?")
	public void compute() {
		boolean lock = false;
		try {
			lock = this.redisDistributedLock.tryLock(LOCK_KEY, 5L, 45L, TimeUnit.SECONDS);
			if(lock) {
				List<Commit> commits = this.commitMapper.selectList(new QueryWrapper<Commit>().isNotNull("range"));
				commits.forEach(commit -> {
					if (commit.getRepoUrl() == null || commit.getCommitUrl() == null) {
						return;
					}
					try {
						Document doc = Jsoup.connect(commit.getCommitUrl()).get();
						Elements elements = doc.select(new Evaluator.AttributeWithValue("data-target", "react-app.embeddedData"));
						for (Element element: elements) {
							JSONObject json = JSON.parseObject(element.text()).getJSONObject("payload").getJSONObject("headerInfo");

							commit.setCommitUrl(null);
							commit.setFile(json.getInteger("filesChanged"));
							commit.setLine(json.getInteger("additions") + json.getInteger("deletions"));
							this.commitMapper.updateById(commit);
						}
					} catch (Exception e) {
						log.error("Failed to fetch commit detail for " + commit.getCommitId() + ":", e);
					}
				});
			}
		} catch (InterruptedException e) {
			log.error("Failed to get commit detail lock: ", e);
			Thread.currentThread().interrupt();
		} finally {
			if (lock) {
				this.redisDistributedLock.unlock(LOCK_KEY);
			}
		}
	}
}
