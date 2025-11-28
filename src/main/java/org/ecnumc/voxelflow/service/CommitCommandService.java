package org.ecnumc.voxelflow.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.ecnumc.voxelflow.enumeration.CommitType;
import org.ecnumc.voxelflow.po.User;
import org.ecnumc.voxelflow.repository.CommitCommandRepository;
import org.ecnumc.voxelflow.repository.UserQueryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class CommitCommandService {
	@Autowired
	private CommitCommandRepository commitCommandRepository;

	@Autowired
	private UserQueryRepository userQueryRepository;

	/**
	 * 推送代码，将提交的代码解析，并保存到数据库中。使用提交记录中的 email 字段获取用户信息，需提醒用户 git 配置邮箱与本平台注册邮箱需保持一致。
	 * @param payload	推送代码的参数
	 */
	public void push(JSONObject payload) {
		JSONArray commits = payload.getJSONArray("commits");
		Pattern pattern = Pattern.compile("^" + CommitType.getRegex() + "\\(([A-Z][A-Z0-9]+-\\d+)\\):[\\s]{0,2}(.*)$");
		commits.forEach(commit -> {
			JSONObject commitObject = (JSONObject)commit;
			String message = commitObject.getString("message");
			String authorEmail = commitObject.getJSONObject("author").getString("email");
			User user = this.userQueryRepository.getByEmail(authorEmail);
			if(user == null || user.getUid() == null) {
				return;
			}
			Matcher matcher = pattern.matcher(message);
			if(matcher.matches() && matcher.groupCount() == 3) {
				String type = matcher.group(0);
				String field = matcher.group(1);
				String msg = matcher.group(2);
				this.commitCommandRepository.add(type, field, msg, user.getUid());
			}
		});
	}
}
