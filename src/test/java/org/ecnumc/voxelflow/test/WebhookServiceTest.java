package org.ecnumc.voxelflow.test;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.ecnumc.voxelflow.Application;
import org.ecnumc.voxelflow.controller.WebhookController;
import org.ecnumc.voxelflow.po.User;
import org.ecnumc.voxelflow.repository.CommitCommandRepository;
import org.ecnumc.voxelflow.repository.UserQueryRepository;
import org.ecnumc.voxelflow.service.CommitCommandService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.util.Date;

import static org.mockito.Mockito.*;

@SpringBootTest(classes = Application.class)
class WebhookServiceTest {
	@Mock
	private CommitCommandRepository commitCommandRepository;

	@Mock
	private UserQueryRepository userQueryRepository;

	@InjectMocks
	private CommitCommandService commitCommandService;

	@Mock
	private HttpServletRequest pushRequest;

	@Mock
	private HttpServletRequest issueRequest;

	@InjectMocks
	private WebhookController webhookController;

	private static final String CORRECT_EMAIL = "test@test.com";

	private static final String REPO_HTML_URL = "https://github.com/ecnumc/voxelflow";

	private static final User CORRECT_USER = new User();

	static final JSONObject LEGAL_PAYLOAD = new JSONObject(ImmutableMap.of(
			"repository", new JSONObject(ImmutableMap.of(
					"html_url", REPO_HTML_URL
			)),
			"commits", ImmutableList.of(
					new JSONObject(ImmutableMap.of(
							"id", "0000000000000000000000000000000000000000",
							"message", "fix(BUG-123): fix crash while respawning",
							"committer", new JSONObject(ImmutableMap.of(
									"email", CORRECT_EMAIL
							)),
							"url", "https://github.com/ecnumc/voxelflow/commit/0000000000000000000000000000000000000000"
					)),
					new JSONObject(ImmutableMap.of(
							"id", "0000000000000000000000000000000000000001",
							"message", "feat(LDY-9527): 新增 logo 渲染",
							"committer", new JSONObject(ImmutableMap.of(
									"email", CORRECT_EMAIL
							)),
							"url", "https://github.com/ecnumc/voxelflow/commit/0000000000000000000000000000000000000001"
					))
			)
	));
	private static final JSONObject PARTIALLY_LEGAL_PAYLOAD = new JSONObject(ImmutableMap.of(
			"repository", new JSONObject(ImmutableMap.of(
					"html_url", REPO_HTML_URL
			)),
			"commits", ImmutableList.of(
					new JSONObject(ImmutableMap.of(
							"id", "0000000000000000000000000000000000000002",
							"message", "fix(hi): fix crash while respawning",
							"committer", new JSONObject(ImmutableMap.of(
									"email", CORRECT_EMAIL
							)),
							"url", "https://github.com/ecnumc/voxelflow/commit/0000000000000000000000000000000000000002"
					)),
					new JSONObject(ImmutableMap.of(
							"id", "0000000000000000000000000000000000000003",
							"message", "feat(LDY-9527):新增 logo 渲染",
							"committer", new JSONObject(ImmutableMap.of(
									"email", CORRECT_EMAIL
							)),
							"url", "https://github.com/ecnumc/voxelflow/commit/0000000000000000000000000000000000000003"
					))
			)
	));
	private static final JSONObject ILLEGAL_PAYLOAD = new JSONObject(ImmutableMap.of(
			"repository", new JSONObject(ImmutableMap.of(
					"html_url", REPO_HTML_URL
			)),
			"commits", ImmutableList.of(
					new JSONObject(ImmutableMap.of(
							"id", "0000000000000000000000000000000000000004",
							"message", "fix(REQ-234: fix crash while respawning",
							"committer", new JSONObject(ImmutableMap.of(
									"email", CORRECT_EMAIL
							)),
							"url", "https://github.com/ecnumc/voxelflow/commit/0000000000000000000000000000000000000004"
					)),
					new JSONObject(ImmutableMap.of(
							"id", "0000000000000000000000000000000000000005",
							"message", "feat:  新增 logo 渲染",
							"committer", new JSONObject(ImmutableMap.of(
									"email", CORRECT_EMAIL
							)),
							"url", "https://github.com/ecnumc/voxelflow/commit/0000000000000000000000000000000000000005"
					))
			)
	));

	@BeforeEach
	void setUp() {
		reset(this.commitCommandRepository, this.userQueryRepository, this.pushRequest, this.issueRequest);
		try {
			Field service = WebhookController.class.getDeclaredField("commitCommandService");
			service.setAccessible(true);
			service.set(this.webhookController, this.commitCommandService);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}

		when(this.userQueryRepository.getByEmail(CORRECT_EMAIL)).thenReturn(CORRECT_USER);
		when(this.pushRequest.getHeader("X-GitHub-Event")).thenReturn("push");
		when(this.issueRequest.getHeader("X-GitHub-Event")).thenReturn("issue");
	}

	@Test
	void testIllegalPush() {
		this.webhookController.webhook(wrapPayload(ILLEGAL_PAYLOAD), this.pushRequest);

		verify(this.commitCommandRepository, never()).add(any(), any(), any(), any(), any(), any(), any());
		verify(this.userQueryRepository, times(2)).getByEmail(any());
	}

	@Test
	void testPartiallyLegalPush() {
		this.webhookController.webhook(wrapPayload(PARTIALLY_LEGAL_PAYLOAD), this.pushRequest);

		verify(this.commitCommandRepository, times(1)).add(any(), any(), any(), any(), any(), any(), any());
		verify(this.userQueryRepository, times(2)).getByEmail(any());
	}

	@Test
	void testLegalPush() {
		this.webhookController.webhook(wrapPayload(LEGAL_PAYLOAD), this.pushRequest);

		verify(this.commitCommandRepository, times(2)).add(any(), any(), any(), any(), any(), any(), any());
		verify(this.userQueryRepository, times(2)).getByEmail(any());
	}

	static JSONObject wrapPayload(JSONObject payload) {
		return new JSONObject(ImmutableMap.of(
				"payload", payload
		));
	}

	static {
		CORRECT_USER.setId(1L);
		CORRECT_USER.setUid("00000000-0000-0000-0000-000000000000");
		CORRECT_USER.setUsername("test");
		CORRECT_USER.setPassword("test");
		CORRECT_USER.setEmail(CORRECT_EMAIL);
		CORRECT_USER.setEmailVerified("VERIFIED");
		CORRECT_USER.setUserStatus("ACTIVE");
		CORRECT_USER.setCreatedBy("SYSTEM");
		CORRECT_USER.setCreatedAt(new Date());
		CORRECT_USER.setUpdatedBy("SYSTEM");
		CORRECT_USER.setUpdatedAt(new Date());
	}
}
