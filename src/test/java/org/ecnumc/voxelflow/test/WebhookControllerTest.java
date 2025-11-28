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
class WebhookControllerTest {
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

	private static final User CORRECT_USER = new User();

	private static final JSONObject LEGAL_PAYLOAD = new JSONObject(ImmutableMap.of(
			"commits", ImmutableList.of(
					new JSONObject(ImmutableMap.of(
							"message", "fix(BUG-123): fix crash while respawning",
							"author", new JSONObject(ImmutableMap.of(
									"email", CORRECT_EMAIL
							))
					)),
					new JSONObject(ImmutableMap.of(
							"message", "feat(LDY-9527): 新增 logo 渲染",
							"author", new JSONObject(ImmutableMap.of(
									"email", CORRECT_EMAIL
							))
					))
			)
	));
	private static final JSONObject PARTIALLY_LEGAL_PAYLOAD = new JSONObject(ImmutableMap.of(
			"commits", ImmutableList.of(
					new JSONObject(ImmutableMap.of(
							"message", "fix(hi): fix crash while respawning",
							"author", new JSONObject(ImmutableMap.of(
									"email", CORRECT_EMAIL
							))
					)),
					new JSONObject(ImmutableMap.of(
							"message", "feat(LDY-9527):新增 logo 渲染",
							"author", new JSONObject(ImmutableMap.of(
									"email", CORRECT_EMAIL
							))
					))
			)
	));
	private static final JSONObject ILLEGAL_PAYLOAD = new JSONObject(ImmutableMap.of(
			"commits", ImmutableList.of(
					new JSONObject(ImmutableMap.of(
							"message", "fix(REQ-234: fix crash while respawning",
							"author", new JSONObject(ImmutableMap.of(
									"email", CORRECT_EMAIL
							))
					)),
					new JSONObject(ImmutableMap.of(
							"message", "feat:  新增 logo 渲染",
							"author", new JSONObject(ImmutableMap.of(
									"email", CORRECT_EMAIL
							))
					))
			)
	));

	@BeforeEach
	void setUp() {
		try {
			Field service = WebhookController.class.getDeclaredField("commitCommandService");
			service.setAccessible(true);
			service.set(this.webhookController, this.commitCommandService);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}

		when(this.userQueryRepository.getByEmail(CORRECT_EMAIL)).thenReturn(CORRECT_USER);
		when(this.issueRequest.getHeader("X-GitHub-Event")).thenReturn("issue");
		when(this.pushRequest.getHeader("X-GitHub-Event")).thenReturn("push");
	}

	@Test
	void testNonPush() {
		this.webhookController.webhook(wrapPayload(LEGAL_PAYLOAD), this.issueRequest);

		verify(this.commitCommandRepository, never()).add(any(), any(), any(), any());
		verify(this.userQueryRepository, never()).getByEmail(any());
	}

	@Test
	void testIllegalPush() {
		this.webhookController.webhook(wrapPayload(ILLEGAL_PAYLOAD), this.pushRequest);

		verify(this.commitCommandRepository, never()).add(any(), any(), any(), any());
		verify(this.userQueryRepository, times(2)).getByEmail(any());
	}

	@Test
	void testPartiallyLegalPush() {
		this.webhookController.webhook(wrapPayload(PARTIALLY_LEGAL_PAYLOAD), this.pushRequest);

		verify(this.commitCommandRepository, times(1)).add(any(), any(), any(), any());
		verify(this.userQueryRepository, times(2)).getByEmail(any());
	}

	@Test
	void testLegalPush() {
		this.webhookController.webhook(wrapPayload(LEGAL_PAYLOAD), this.pushRequest);

		verify(this.commitCommandRepository, times(2)).add(any(), any(), any(), any());
		verify(this.userQueryRepository, times(2)).getByEmail(any());
	}

	private static JSONObject wrapPayload(JSONObject payload) {
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
