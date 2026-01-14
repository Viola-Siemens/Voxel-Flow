package org.ecnumc.voxelflow.test;

import org.ecnumc.voxelflow.Application;
import org.ecnumc.voxelflow.converter.RetrospectiveConverter;
import org.ecnumc.voxelflow.enumeration.ClientErrorCode;
import org.ecnumc.voxelflow.enumeration.RetrospectiveStatus;
import org.ecnumc.voxelflow.po.Retrospective;
import org.ecnumc.voxelflow.repository.RetrospectiveCommandRepository;
import org.ecnumc.voxelflow.repository.RetrospectiveQueryRepository;
import org.ecnumc.voxelflow.repository.UserQueryRepository;
import org.ecnumc.voxelflow.resp.RetrospectiveResp;
import org.ecnumc.voxelflow.service.RetrospectiveService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = Application.class)
class RetrospectiveServiceTest {
	@Mock
	private RetrospectiveCommandRepository retrospectiveCommandRepository;

	@Mock
	private RetrospectiveQueryRepository retrospectiveQueryRepository;

	@Mock
	private UserQueryRepository userQueryRepository;

	@Mock
	private RetrospectiveConverter retrospectiveConverter;

	@InjectMocks
	private RetrospectiveService retrospectiveService;

	private static final String TEST_CODE = "RTS-001";
	private static final String TEST_TITLE = "测试复盘";
	private static final String TEST_DESCRIPTION = "这是一个简介，你没有必要把它读完，因为它真的没什么用。";
	private static final String TEST_UID = "00000000-0000-0000-0000-000000000001";
	private static final String TEST_NEXT_OPERATOR = "00000000-0000-0000-0000-000000000002";

	private Retrospective createTestRetrospective() {
		Retrospective retrospective = new Retrospective();
		retrospective.setId(1L);
		retrospective.setCode(TEST_CODE);
		retrospective.setTitle(TEST_TITLE);
		retrospective.setDescription(TEST_DESCRIPTION);
		retrospective.setStatus(RetrospectiveStatus.HANDLING.name());
		retrospective.setCreatedBy(TEST_UID);
		retrospective.setCreatedAt(new Date());
		retrospective.setUpdatedBy(TEST_UID);
		retrospective.setUpdatedAt(new Date());
		return retrospective;
	}

	private RetrospectiveResp createTestRetrospectiveResp() {
		return RetrospectiveResp.builder()
				.code(TEST_CODE)
				.title(TEST_TITLE)
				.description(TEST_DESCRIPTION)
				.status(RetrospectiveStatus.HANDLING.name())
				.createdBy(TEST_UID)
				.createdAt(new Date())
				.updatedBy(TEST_UID)
				.updatedAt(new Date())
				.build();
	}

	@BeforeEach
	void setUp() {
		reset(this.retrospectiveCommandRepository, this.retrospectiveQueryRepository,
				this.userQueryRepository, this.retrospectiveConverter);
	}

	/**
	 * 测试创建复盘成功的场景
	 */
	@Test
	void testCreateRetrospectiveSuccess() {
		Retrospective retrospective = this.createTestRetrospective();
		RetrospectiveResp retrospectiveResp = this.createTestRetrospectiveResp();

		when(this.retrospectiveCommandRepository.createRetrospective(TEST_TITLE, TEST_DESCRIPTION, TEST_UID))
				.thenReturn(retrospective);
		when(this.retrospectiveConverter.convertToResp(retrospective)).thenReturn(retrospectiveResp);

		RetrospectiveResp result = this.retrospectiveService
				.createRetrospective(TEST_TITLE, TEST_DESCRIPTION, TEST_UID);

		assertNotNull(result);
		assertEquals(TEST_CODE, result.getCode());
		assertEquals(TEST_TITLE, result.getTitle());
		verify(this.retrospectiveCommandRepository, times(1))
				.createRetrospective(TEST_TITLE, TEST_DESCRIPTION, TEST_UID);
		verify(this.retrospectiveConverter, times(1)).convertToResp(retrospective);
	}

	/**
	 * 测试查询复盘成功的场景
	 */
	@Test
	void testQueryRetrospectiveSuccess() {
		Retrospective retrospective = this.createTestRetrospective();
		RetrospectiveResp retrospectiveResp = this.createTestRetrospectiveResp();

		when(this.retrospectiveQueryRepository.getRetrospectiveByCode(TEST_CODE)).thenReturn(retrospective);
		when(this.retrospectiveConverter.convertToResp(retrospective)).thenReturn(retrospectiveResp);

		RetrospectiveResp result = this.retrospectiveService.queryByCode(TEST_CODE);

		assertNotNull(result);
		assertEquals(TEST_CODE, result.getCode());
		verify(this.retrospectiveQueryRepository, times(1)).getRetrospectiveByCode(TEST_CODE);
		verify(this.retrospectiveConverter, times(1)).convertToResp(retrospective);
	}

	/**
	 * 测试查询复盘失败的场景，复盘不存在
	 */
	@Test
	void testQueryRetrospectiveFailNotFound() {
		when(this.retrospectiveQueryRepository.getRetrospectiveByCode(TEST_CODE)).thenReturn(null);

		RetrospectiveResp result = this.retrospectiveService.queryByCode(TEST_CODE);

		assertNull(result);
		verify(this.retrospectiveQueryRepository, times(1)).getRetrospectiveByCode(TEST_CODE);
		verify(this.retrospectiveConverter, never()).convertToResp(any());
	}

	/**
	 * 测试更新复盘成功的场景
	 */
	@Test
	void testUpdateRetrospectiveSuccess() {
		Retrospective retrospective = this.createTestRetrospective();

		when(this.retrospectiveQueryRepository.getRetrospectiveByCode(TEST_CODE)).thenReturn(retrospective);

		String newTitle = "Updated Title";
		String newDescription = "Updated Description";
		ClientErrorCode result = this.retrospectiveService
				.updateRetrospective(TEST_CODE, newTitle, newDescription, TEST_UID);

		assertNull(result);
		verify(this.retrospectiveQueryRepository, times(1)).getRetrospectiveByCode(TEST_CODE);
		verify(this.retrospectiveCommandRepository, times(1))
				.updateRetrospective(TEST_CODE, newTitle, newDescription, TEST_UID);
	}

	/**
	 * 测试更新复盘失败的场景，复盘不存在
	 */
	@Test
	void testUpdateRetrospectiveFailNotFound() {
		when(this.retrospectiveQueryRepository.getRetrospectiveByCode(TEST_CODE)).thenReturn(null);

		ClientErrorCode result = this.retrospectiveService
				.updateRetrospective(TEST_CODE, "New Title", "New Description", TEST_UID);

		assertEquals(ClientErrorCode.ERROR_1450, result);
		verify(this.retrospectiveQueryRepository, times(1)).getRetrospectiveByCode(TEST_CODE);
		verify(this.retrospectiveCommandRepository, never()).updateRetrospective(any(), any(), any(), any());
	}

	/**
	 * 测试更新复盘失败的场景，复盘状态不允许修改
	 */
	@Test
	void testUpdateRetrospectiveFailNotModifiable() {
		Retrospective retrospective = this.createTestRetrospective();
		retrospective.setStatus(RetrospectiveStatus.FINISHED.name());
		when(this.retrospectiveQueryRepository.getRetrospectiveByCode(TEST_CODE)).thenReturn(retrospective);

		ClientErrorCode result = this.retrospectiveService.updateRetrospective(TEST_CODE, "New Title", "New Description", TEST_UID);

		assertEquals(ClientErrorCode.ERROR_1452, result);
		verify(this.retrospectiveQueryRepository, times(1)).getRetrospectiveByCode(TEST_CODE);
		verify(this.retrospectiveCommandRepository, never()).updateRetrospective(any(), any(), any(), any());
	}

	/**
	 * 测试批准复盘成功的场景
	 */
	@Test
	void testApproveRetrospectiveSuccess() {
		Retrospective retrospective = this.createTestRetrospective();
		List<String> nextOperators = Collections.singletonList(TEST_NEXT_OPERATOR);

		when(this.retrospectiveQueryRepository.getRetrospectiveByCode(TEST_CODE)).thenReturn(retrospective);
		when(this.retrospectiveQueryRepository.getPendingRelationCount(TEST_CODE, RetrospectiveStatus.HANDLING)).thenReturn(0);

		ClientErrorCode result = this.retrospectiveService.approve(TEST_CODE, nextOperators, "Approved", TEST_UID);

		assertNull(result);
		verify(this.retrospectiveQueryRepository, times(1)).getRetrospectiveByCode(TEST_CODE);
		verify(this.retrospectiveCommandRepository, times(1)).updateStatus(TEST_CODE, RetrospectiveStatus.HANDLING, RetrospectiveStatus.FINISHED, TEST_UID);
	}

	/**
	 * 测试批准复盘失败的场景，复盘不存在
	 */
	@Test
	void testApproveRetrospectiveFailNotFound() {
		when(this.retrospectiveQueryRepository.getRetrospectiveByCode(TEST_CODE)).thenReturn(null);

		List<String> nextOperators = Collections.singletonList(TEST_NEXT_OPERATOR);
		ClientErrorCode result = this.retrospectiveService.approve(TEST_CODE, nextOperators, "Approved", TEST_UID);

		assertEquals(ClientErrorCode.ERROR_1450, result);
		verify(this.retrospectiveQueryRepository, times(1)).getRetrospectiveByCode(TEST_CODE);
		verify(this.retrospectiveCommandRepository, never()).updateRelation(any(), any(), any(), any(), any());
	}

	/**
	 * 测试批准复盘失败的场景，复盘状态不允许修改
	 */
	@Test
	void testApproveRetrospectiveFailNotModifiable() {
		Retrospective retrospective = this.createTestRetrospective();
		retrospective.setStatus(RetrospectiveStatus.FINISHED.name());
		when(this.retrospectiveQueryRepository.getRetrospectiveByCode(TEST_CODE)).thenReturn(retrospective);

		List<String> nextOperators = Collections.singletonList(TEST_NEXT_OPERATOR);
		ClientErrorCode result = this.retrospectiveService.approve(TEST_CODE, nextOperators, "Approved", TEST_UID);

		assertEquals(ClientErrorCode.ERROR_1452, result);
		verify(this.retrospectiveQueryRepository, times(1)).getRetrospectiveByCode(TEST_CODE);
		verify(this.retrospectiveCommandRepository, never()).updateRelation(any(), any(), any(), any(), any());
	}

	/**
	 * 测试拒绝复盘成功的场景
	 */
	@Test
	void testRejectRetrospectiveSuccess() {
		Retrospective retrospective = this.createTestRetrospective();
		List<String> nextOperators = Collections.singletonList(TEST_NEXT_OPERATOR);

		when(this.retrospectiveQueryRepository.getRetrospectiveByCode(TEST_CODE)).thenReturn(retrospective);

		ClientErrorCode result = this.retrospectiveService.reject(TEST_CODE, nextOperators, "Rejected", TEST_UID);

		assertNull(result);
		verify(this.retrospectiveQueryRepository, times(1)).getRetrospectiveByCode(TEST_CODE);
	}

	/**
	 * 测试拒绝复盘失败的场景，复盘不存在
	 */
	@Test
	void testRejectRetrospectiveFailNotFound() {
		when(this.retrospectiveQueryRepository.getRetrospectiveByCode(TEST_CODE)).thenReturn(null);

		List<String> nextOperators = Collections.singletonList(TEST_NEXT_OPERATOR);
		ClientErrorCode result = this.retrospectiveService.reject(TEST_CODE, nextOperators, "Rejected", TEST_UID);

		assertEquals(ClientErrorCode.ERROR_1450, result);
		verify(this.retrospectiveQueryRepository, times(1)).getRetrospectiveByCode(TEST_CODE);
		verify(this.retrospectiveCommandRepository, never()).updateRelation(any(), any(), any(), any(), any());
	}

	/**
	 * 测试拒绝复盘失败的场景，复盘状态不允许修改
	 */
	@Test
	void testRejectRetrospectiveFailNotModifiable() {
		Retrospective retrospective = this.createTestRetrospective();
		retrospective.setStatus(RetrospectiveStatus.FINISHED.name());
		when(this.retrospectiveQueryRepository.getRetrospectiveByCode(TEST_CODE)).thenReturn(retrospective);

		List<String> nextOperators = Collections.singletonList(TEST_NEXT_OPERATOR);
		ClientErrorCode result = this.retrospectiveService.reject(TEST_CODE, nextOperators, "Rejected", TEST_UID);

		assertEquals(ClientErrorCode.ERROR_1452, result);
		verify(this.retrospectiveQueryRepository, times(1)).getRetrospectiveByCode(TEST_CODE);
		verify(this.retrospectiveCommandRepository, never()).updateRelation(any(), any(), any(), any(), any());
	}

	/**
	 * 测试分配复盘成功的场景
	 */
	@Test
	void testAssignRetrospectiveSuccess() {
		Retrospective retrospective = this.createTestRetrospective();

		when(this.retrospectiveQueryRepository.getRetrospectiveByCode(TEST_CODE)).thenReturn(retrospective);

		ClientErrorCode result = this.retrospectiveService.assign(TEST_CODE, TEST_NEXT_OPERATOR, TEST_UID);

		assertNull(result);
		verify(this.retrospectiveQueryRepository, times(1)).getRetrospectiveByCode(TEST_CODE);
		verify(this.retrospectiveCommandRepository, times(1)).assignOperator(TEST_CODE, RetrospectiveStatus.HANDLING, TEST_NEXT_OPERATOR, TEST_UID);
	}

	/**
	 * 测试分配复盘失败的场景，复盘不存在
	 */
	@Test
	void testAssignRetrospectiveFailNotFound() {
		when(this.retrospectiveQueryRepository.getRetrospectiveByCode(TEST_CODE)).thenReturn(null);

		ClientErrorCode result = this.retrospectiveService.assign(TEST_CODE, TEST_NEXT_OPERATOR, TEST_UID);

		assertEquals(ClientErrorCode.ERROR_1450, result);
		verify(this.retrospectiveQueryRepository, times(1)).getRetrospectiveByCode(TEST_CODE);
		verify(this.retrospectiveCommandRepository, never()).assignOperator(any(), any(), any(), any());
	}

	/**
	 * 测试分配复盘失败的场景，复盘状态不允许修改
	 */
	@Test
	void testAssignRetrospectiveFailNotModifiable() {
		Retrospective retrospective = this.createTestRetrospective();
		retrospective.setStatus(RetrospectiveStatus.FINISHED.name());
		when(this.retrospectiveQueryRepository.getRetrospectiveByCode(TEST_CODE)).thenReturn(retrospective);

		ClientErrorCode result = this.retrospectiveService.assign(TEST_CODE, TEST_NEXT_OPERATOR, TEST_UID);

		assertEquals(ClientErrorCode.ERROR_1452, result);
		verify(this.retrospectiveQueryRepository, times(1)).getRetrospectiveByCode(TEST_CODE);
		verify(this.retrospectiveCommandRepository, never()).assignOperator(any(), any(), any(), any());
	}

	/**
	 * 测试取消分配复盘成功的场景
	 */
	@Test
	void testUnassignRetrospectiveSuccess() {
		Retrospective retrospective = this.createTestRetrospective();

		when(this.retrospectiveQueryRepository.getRetrospectiveByCode(TEST_CODE)).thenReturn(retrospective);
		when(this.retrospectiveCommandRepository.unassignOperator(TEST_CODE, RetrospectiveStatus.HANDLING, TEST_NEXT_OPERATOR, TEST_UID)).thenReturn(false);

		ClientErrorCode result = this.retrospectiveService.unassign(TEST_CODE, TEST_NEXT_OPERATOR, TEST_UID);

		assertNull(result);
		verify(this.retrospectiveQueryRepository, times(1)).getRetrospectiveByCode(TEST_CODE);
		verify(this.retrospectiveCommandRepository, times(1)).unassignOperator(TEST_CODE, RetrospectiveStatus.HANDLING, TEST_NEXT_OPERATOR, TEST_UID);
	}

	/**
	 * 测试取消分配复盘失败的场景，复盘不存在
	 */
	@Test
	void testUnassignRetrospectiveFailNotFound() {
		when(this.retrospectiveQueryRepository.getRetrospectiveByCode(TEST_CODE)).thenReturn(null);

		ClientErrorCode result = this.retrospectiveService.unassign(TEST_CODE, TEST_NEXT_OPERATOR, TEST_UID);

		assertEquals(ClientErrorCode.ERROR_1450, result);
		verify(this.retrospectiveQueryRepository, times(1)).getRetrospectiveByCode(TEST_CODE);
		verify(this.retrospectiveCommandRepository, never()).unassignOperator(any(), any(), any(), any());
	}

	/**
	 * 测试取消分配复盘失败的场景，复盘状态不允许修改
	 */
	@Test
	void testUnassignRetrospectiveFailNotModifiable() {
		Retrospective retrospective = this.createTestRetrospective();
		retrospective.setStatus(RetrospectiveStatus.FINISHED.name());
		when(this.retrospectiveQueryRepository.getRetrospectiveByCode(TEST_CODE)).thenReturn(retrospective);

		ClientErrorCode result = this.retrospectiveService.unassign(TEST_CODE, TEST_NEXT_OPERATOR, TEST_UID);

		assertEquals(ClientErrorCode.ERROR_1452, result);
		verify(this.retrospectiveQueryRepository, times(1)).getRetrospectiveByCode(TEST_CODE);
		verify(this.retrospectiveCommandRepository, never()).unassignOperator(any(), any(), any(), any());
	}
}
