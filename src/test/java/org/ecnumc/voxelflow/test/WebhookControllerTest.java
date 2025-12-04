package org.ecnumc.voxelflow.test;

import org.ecnumc.voxelflow.controller.WebhookController;
import org.ecnumc.voxelflow.service.CommitCommandService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import javax.servlet.http.HttpServletRequest;

import static org.ecnumc.voxelflow.test.WebhookServiceTest.LEGAL_PAYLOAD;
import static org.ecnumc.voxelflow.test.WebhookServiceTest.wrapPayload;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

class WebhookControllerTest {
	@Mock
	private CommitCommandService commitCommandService;

	@InjectMocks
	private WebhookController webhookController;

	@Mock
	private HttpServletRequest issueRequest;

	@BeforeEach
	void setUp() {
		reset(this.commitCommandService, this.issueRequest);
		when(this.issueRequest.getHeader("X-GitHub-Event")).thenReturn("issue");
	}

	@Test
	void testNonPush() {
		this.webhookController.webhook(wrapPayload(LEGAL_PAYLOAD), this.issueRequest);

		verify(this.commitCommandService, never()).push(any());
	}
}
