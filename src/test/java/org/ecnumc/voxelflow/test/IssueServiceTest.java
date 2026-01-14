package org.ecnumc.voxelflow.test;

import org.ecnumc.voxelflow.Application;
import org.ecnumc.voxelflow.converter.IssueConverter;
import org.ecnumc.voxelflow.enumeration.ClientErrorCode;
import org.ecnumc.voxelflow.enumeration.IssueStatus;
import org.ecnumc.voxelflow.enumeration.UserRole;
import org.ecnumc.voxelflow.po.Issue;
import org.ecnumc.voxelflow.repository.IssueCommandRepository;
import org.ecnumc.voxelflow.repository.IssueQueryRepository;
import org.ecnumc.voxelflow.repository.UserQueryRepository;
import org.ecnumc.voxelflow.resp.IssueResp;
import org.ecnumc.voxelflow.service.IssueService;
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
class IssueServiceTest {
	@Mock
	private IssueCommandRepository issueCommandRepository;

	@Mock
	private IssueQueryRepository issueQueryRepository;

	@Mock
	private UserQueryRepository userQueryRepository;

	@Mock
	private IssueConverter issueConverter;

	@InjectMocks
	private IssueService issueService;

	private static final String TEST_CODE = "BUG-001";
	private static final String TEST_TITLE = "测试缺陷";
	private static final String TEST_DESCRIPTION = "这是一个简介，你没有必要把它读完，因为它真的没什么用。";
	private static final Integer TEST_PRIORITY = 1;
	private static final String TEST_UID = "00000000-0000-0000-0000-000000000001";
	private static final String TEST_NEXT_OPERATOR = "00000000-0000-0000-0000-000000000002";

	private Issue createTestIssue() {
		Issue issue = new Issue();
		issue.setId(1L);
		issue.setCode(TEST_CODE);
		issue.setTitle(TEST_TITLE);
		issue.setDescription(TEST_DESCRIPTION);
		issue.setStatus(IssueStatus.HANDLING.name());
		issue.setPriority(TEST_PRIORITY);
		issue.setCreatedBy(TEST_UID);
		issue.setCreatedAt(new Date());
		issue.setUpdatedBy(TEST_UID);
		issue.setUpdatedAt(new Date());
		return issue;
	}

	private IssueResp createTestIssueResp() {
		return IssueResp.builder()
				.code(TEST_CODE)
				.title(TEST_TITLE)
				.description(TEST_DESCRIPTION)
				.status(IssueStatus.HANDLING.name())
				.priority(TEST_PRIORITY)
				.createdBy(TEST_UID)
				.createdAt(new Date())
				.updatedBy(TEST_UID)
				.updatedAt(new Date())
				.build();
	}

	@BeforeEach
	void setUp() {
		reset(this.issueCommandRepository, this.issueQueryRepository, this.userQueryRepository, this.issueConverter);
	}

	/**
	 * 测试创建缺陷成功的场景
	 */
	@Test
	void testCreateIssueSuccess() {
		Issue issue = this.createTestIssue();
		IssueResp issueResp = this.createTestIssueResp();

		when(this.issueCommandRepository.createIssue(TEST_TITLE, TEST_DESCRIPTION, TEST_PRIORITY, TEST_UID))
				.thenReturn(issue);
		when(this.issueConverter.convertToResp(issue)).thenReturn(issueResp);

		IssueResp result = this.issueService
				.createIssue(TEST_TITLE, TEST_DESCRIPTION, TEST_PRIORITY, TEST_UID);

		assertNotNull(result);
		assertEquals(TEST_CODE, result.getCode());
		assertEquals(TEST_TITLE, result.getTitle());
		verify(this.issueCommandRepository, times(1))
				.createIssue(TEST_TITLE, TEST_DESCRIPTION, TEST_PRIORITY, TEST_UID);
		verify(this.issueConverter, times(1)).convertToResp(issue);
	}

	/**
	 * 测试查询缺陷成功的场景
	 */
	@Test
	void testQueryIssueSuccess() {
		Issue issue = this.createTestIssue();
		IssueResp issueResp = this.createTestIssueResp();

		when(this.issueQueryRepository.getIssueByCode(TEST_CODE)).thenReturn(issue);
		when(this.issueConverter.convertToResp(issue)).thenReturn(issueResp);

		IssueResp result = this.issueService.queryByCode(TEST_CODE);

		assertNotNull(result);
		assertEquals(TEST_CODE, result.getCode());
		verify(this.issueQueryRepository, times(1)).getIssueByCode(TEST_CODE);
		verify(this.issueConverter, times(1)).convertToResp(issue);
	}

	/**
	 * 测试查询缺陷失败的场景，缺陷不存在
	 */
	@Test
	void testQueryIssueFailNotFound() {
		when(this.issueQueryRepository.getIssueByCode(TEST_CODE)).thenReturn(null);

		IssueResp result = this.issueService.queryByCode(TEST_CODE);

		assertNull(result);
		verify(this.issueQueryRepository, times(1)).getIssueByCode(TEST_CODE);
		verify(this.issueConverter, never()).convertToResp(any());
	}

	/**
	 * 测试更新缺陷成功的场景
	 */
	@Test
	void testUpdateIssueSuccess() {
		Issue issue = this.createTestIssue();
		List<UserRole> userRoles = Collections.singletonList(UserRole.DEVELOPMENT);

		when(this.issueQueryRepository.getIssueByCode(TEST_CODE)).thenReturn(issue);
		when(this.userQueryRepository.getUserRoles(TEST_UID)).thenReturn(userRoles);

		String newTitle = "Updated Title";
		String newDescription = "Updated Description";
		ClientErrorCode result = this.issueService
				.updateIssue(TEST_CODE, newTitle, newDescription, 2, TEST_UID);

		assertNull(result);
		verify(this.issueQueryRepository, times(1)).getIssueByCode(TEST_CODE);
		verify(this.userQueryRepository, times(1)).getUserRoles(TEST_UID);
		verify(this.issueCommandRepository, times(1))
				.updateIssue(TEST_CODE, newTitle, newDescription, 2, TEST_UID);
	}

	/**
	 * 测试更新缺陷失败的场景，缺陷不存在
	 */
	@Test
	void testUpdateIssueFailNotFound() {
		when(this.issueQueryRepository.getIssueByCode(TEST_CODE)).thenReturn(null);

		ClientErrorCode result = this.issueService
				.updateIssue(TEST_CODE, "New Title", "New Description", 2, TEST_UID);

		assertEquals(ClientErrorCode.ERROR_1440, result);
		verify(this.issueQueryRepository, times(1)).getIssueByCode(TEST_CODE);
		verify(this.issueCommandRepository, never()).updateIssue(any(), any(), any(), any(), any());
	}

	/**
	 * 测试更新缺陷失败的场景，缺陷状态不允许修改
	 */
	@Test
	void testUpdateIssueFailNotModifiable() {
		Issue issue = this.createTestIssue();
		issue.setStatus(IssueStatus.RELEASED.name());
		when(this.issueQueryRepository.getIssueByCode(TEST_CODE)).thenReturn(issue);

		ClientErrorCode result = this.issueService.updateIssue(TEST_CODE, "New Title", "New Description", 2, TEST_UID);

		assertEquals(ClientErrorCode.ERROR_1442, result);
		verify(this.issueQueryRepository, times(1)).getIssueByCode(TEST_CODE);
		verify(this.issueCommandRepository, never()).updateIssue(any(), any(), any(), any(), any());
	}

	/**
	 * 测试更新缺陷失败的场景，用户没有权限
	 */
	@Test
	void testUpdateIssueFailNoPermission() {
		Issue issue = this.createTestIssue();
		List<UserRole> userRoles = Collections.singletonList(UserRole.BUSINESS);

		when(this.issueQueryRepository.getIssueByCode(TEST_CODE)).thenReturn(issue);
		when(this.userQueryRepository.getUserRoles(TEST_UID)).thenReturn(userRoles);

		ClientErrorCode result = this.issueService.updateIssue(TEST_CODE, "New Title", "New Description", 2, TEST_UID);

		assertEquals(ClientErrorCode.ERROR_1491, result);
		verify(this.issueQueryRepository, times(1)).getIssueByCode(TEST_CODE);
		verify(this.userQueryRepository, times(1)).getUserRoles(TEST_UID);
		verify(this.issueCommandRepository, never()).updateIssue(any(), any(), any(), any(), any());
	}

	/**
	 * 测试批准缺陷成功的场景
	 */
	@Test
	void testApproveIssueSuccess() {
		Issue issue = this.createTestIssue();
		List<UserRole> userRoles = Collections.singletonList(UserRole.DEVELOPMENT);
		List<UserRole> nextOperatorRoles = Collections.singletonList(UserRole.TEST);
		List<String> nextOperators = Collections.singletonList(TEST_NEXT_OPERATOR);

		when(this.issueQueryRepository.getIssueByCode(TEST_CODE)).thenReturn(issue);
		when(this.userQueryRepository.getUserRoles(TEST_UID)).thenReturn(userRoles);
		when(this.userQueryRepository.getUserRoles(TEST_NEXT_OPERATOR)).thenReturn(nextOperatorRoles);

		ClientErrorCode result = this.issueService.approve(TEST_CODE, nextOperators, "Approved", TEST_UID);

		assertNull(result);
		verify(this.issueQueryRepository, times(1)).getIssueByCode(TEST_CODE);
		verify(this.userQueryRepository, times(1)).getUserRoles(TEST_UID);
		verify(this.userQueryRepository, times(1)).getUserRoles(TEST_NEXT_OPERATOR);
		verify(this.issueCommandRepository, times(1)).updateStatus(TEST_CODE, IssueStatus.HANDLING, IssueStatus.TESTING, TEST_UID);
	}

	/**
	 * 测试批准缺陷失败的场景，缺陷不存在
	 */
	@Test
	void testApproveIssueFailNotFound() {
		when(this.issueQueryRepository.getIssueByCode(TEST_CODE)).thenReturn(null);

		List<String> nextOperators = Collections.singletonList(TEST_NEXT_OPERATOR);
		ClientErrorCode result = this.issueService.approve(TEST_CODE, nextOperators, "Approved", TEST_UID);

		assertEquals(ClientErrorCode.ERROR_1440, result);
		verify(this.issueQueryRepository, times(1)).getIssueByCode(TEST_CODE);
		verify(this.issueCommandRepository, never()).updateRelation(any(), any(), any(), any(), any());
	}

	/**
	 * 测试批准缺陷失败的场景，缺陷状态不允许修改
	 */
	@Test
	void testApproveIssueFailNotModifiable() {
		Issue issue = this.createTestIssue();
		issue.setStatus(IssueStatus.RELEASED.name());
		when(this.issueQueryRepository.getIssueByCode(TEST_CODE)).thenReturn(issue);

		List<String> nextOperators = Collections.singletonList(TEST_NEXT_OPERATOR);
		ClientErrorCode result = this.issueService.approve(TEST_CODE, nextOperators, "Approved", TEST_UID);

		assertEquals(ClientErrorCode.ERROR_1442, result);
		verify(this.issueQueryRepository, times(1)).getIssueByCode(TEST_CODE);
		verify(this.issueCommandRepository, never()).updateRelation(any(), any(), any(), any(), any());
	}

	/**
	 * 测试批准缺陷失败的场景，用户没有权限
	 */
	@Test
	void testApproveIssueFailNoPermission() {
		Issue issue = this.createTestIssue();
		List<UserRole> userRoles = Collections.singletonList(UserRole.BUSINESS);

		when(this.issueQueryRepository.getIssueByCode(TEST_CODE)).thenReturn(issue);
		when(this.userQueryRepository.getUserRoles(TEST_UID)).thenReturn(userRoles);

		List<String> nextOperators = Collections.singletonList(TEST_NEXT_OPERATOR);
		ClientErrorCode result = this.issueService.approve(TEST_CODE, nextOperators, "Approved", TEST_UID);

		assertEquals(ClientErrorCode.ERROR_1491, result);
		verify(this.issueQueryRepository, times(1)).getIssueByCode(TEST_CODE);
		verify(this.userQueryRepository, times(1)).getUserRoles(TEST_UID);
		verify(this.issueCommandRepository, never()).updateRelation(any(), any(), any(), any(), any());
	}

	/**
	 * 测试批准缺陷失败的场景，下一个操作人没有权限
	 */
	@Test
	void testApproveIssueFailNextOperatorNoPermission() {
		Issue issue = this.createTestIssue();
		List<UserRole> userRoles = Collections.singletonList(UserRole.DEVELOPMENT);
		List<UserRole> nextOperatorRoles = Collections.singletonList(UserRole.BUSINESS);
		List<String> nextOperators = Collections.singletonList(TEST_NEXT_OPERATOR);

		when(this.issueQueryRepository.getIssueByCode(TEST_CODE)).thenReturn(issue);
		when(this.userQueryRepository.getUserRoles(TEST_UID)).thenReturn(userRoles);
		when(this.userQueryRepository.getUserRoles(TEST_NEXT_OPERATOR)).thenReturn(nextOperatorRoles);

		ClientErrorCode result = this.issueService.approve(TEST_CODE, nextOperators, "Approved", TEST_UID);

		assertEquals(ClientErrorCode.ERROR_1492, result);
		verify(this.issueQueryRepository, times(1)).getIssueByCode(TEST_CODE);
		verify(this.userQueryRepository, times(1)).getUserRoles(TEST_UID);
		verify(this.userQueryRepository, times(1)).getUserRoles(TEST_NEXT_OPERATOR);
		verify(this.issueCommandRepository, never()).updateRelation(any(), any(), any(), any(), any());
	}

	/**
	 * 测试拒绝缺陷成功的场景
	 */
	@Test
	void testRejectIssueSuccess() {
		Issue issue = this.createTestIssue();
		issue.setStatus(IssueStatus.TESTING.name());
		List<UserRole> userRoles = Collections.singletonList(UserRole.TEST);
		List<UserRole> nextOperatorRoles = Collections.singletonList(UserRole.DEVELOPMENT);
		List<String> nextOperators = Collections.singletonList(TEST_NEXT_OPERATOR);

		when(this.issueQueryRepository.getIssueByCode(TEST_CODE)).thenReturn(issue);
		when(this.userQueryRepository.getUserRoles(TEST_UID)).thenReturn(userRoles);
		when(this.userQueryRepository.getUserRoles(TEST_NEXT_OPERATOR)).thenReturn(nextOperatorRoles);

		ClientErrorCode result = this.issueService.reject(TEST_CODE, nextOperators, "Rejected", TEST_UID);

		assertNull(result);
		verify(this.issueQueryRepository, times(1)).getIssueByCode(TEST_CODE);
		verify(this.userQueryRepository, times(1)).getUserRoles(TEST_UID);
		verify(this.userQueryRepository, times(1)).getUserRoles(TEST_NEXT_OPERATOR);
	}

	/**
	 * 测试拒绝缺陷失败的场景，缺陷不存在
	 */
	@Test
	void testRejectIssueFailNotFound() {
		when(this.issueQueryRepository.getIssueByCode(TEST_CODE)).thenReturn(null);

		List<String> nextOperators = Collections.singletonList(TEST_NEXT_OPERATOR);
		ClientErrorCode result = this.issueService.reject(TEST_CODE, nextOperators, "Rejected", TEST_UID);

		assertEquals(ClientErrorCode.ERROR_1440, result);
		verify(this.issueQueryRepository, times(1)).getIssueByCode(TEST_CODE);
		verify(this.issueCommandRepository, never()).updateRelation(any(), any(), any(), any(), any());
	}

	/**
	 * 测试拒绝缺陷失败的场景，缺陷状态不允许修改
	 */
	@Test
	void testRejectIssueFailNotModifiable() {
		Issue issue = this.createTestIssue();
		issue.setStatus(IssueStatus.RELEASED.name());
		when(this.issueQueryRepository.getIssueByCode(TEST_CODE)).thenReturn(issue);

		List<String> nextOperators = Collections.singletonList(TEST_NEXT_OPERATOR);
		ClientErrorCode result = this.issueService.reject(TEST_CODE, nextOperators, "Rejected", TEST_UID);

		assertEquals(ClientErrorCode.ERROR_1442, result);
		verify(this.issueQueryRepository, times(1)).getIssueByCode(TEST_CODE);
		verify(this.issueCommandRepository, never()).updateRelation(any(), any(), any(), any(), any());
	}

	/**
	 * 测试拒绝缺陷失败的场景，用户没有权限
	 */
	@Test
	void testRejectIssueFailNoPermission() {
		Issue issue = this.createTestIssue();
		List<UserRole> userRoles = Collections.singletonList(UserRole.BUSINESS);

		when(this.issueQueryRepository.getIssueByCode(TEST_CODE)).thenReturn(issue);
		when(this.userQueryRepository.getUserRoles(TEST_UID)).thenReturn(userRoles);

		List<String> nextOperators = Collections.singletonList(TEST_NEXT_OPERATOR);
		ClientErrorCode result = this.issueService.reject(TEST_CODE, nextOperators, "Rejected", TEST_UID);

		assertEquals(ClientErrorCode.ERROR_1491, result);
		verify(this.issueQueryRepository, times(1)).getIssueByCode(TEST_CODE);
		verify(this.userQueryRepository, times(1)).getUserRoles(TEST_UID);
		verify(this.issueCommandRepository, never()).updateRelation(any(), any(), any(), any(), any());
	}

	/**
	 * 测试拒绝缺陷失败的场景，下一个操作人没有权限
	 */
	@Test
	void testRejectIssueFailNextOperatorNoPermission() {
		Issue issue = this.createTestIssue();
		issue.setStatus(IssueStatus.TESTING.name());
		List<UserRole> userRoles = Collections.singletonList(UserRole.TEST);
		List<UserRole> nextOperatorRoles = Collections.singletonList(UserRole.BUSINESS);
		List<String> nextOperators = Collections.singletonList(TEST_NEXT_OPERATOR);

		when(this.issueQueryRepository.getIssueByCode(TEST_CODE)).thenReturn(issue);
		when(this.userQueryRepository.getUserRoles(TEST_UID)).thenReturn(userRoles);
		when(this.userQueryRepository.getUserRoles(TEST_NEXT_OPERATOR)).thenReturn(nextOperatorRoles);

		ClientErrorCode result = this.issueService.reject(TEST_CODE, nextOperators, "Rejected", TEST_UID);

		assertEquals(ClientErrorCode.ERROR_1492, result);
		verify(this.issueQueryRepository, times(1)).getIssueByCode(TEST_CODE);
		verify(this.userQueryRepository, times(1)).getUserRoles(TEST_UID);
		verify(this.userQueryRepository, times(1)).getUserRoles(TEST_NEXT_OPERATOR);
		verify(this.issueCommandRepository, never()).updateRelation(any(), any(), any(), any(), any());
	}

	/**
	 * 测试分配缺陷成功的场景
	 */
	@Test
	void testAssignIssueSuccess() {
		Issue issue = this.createTestIssue();
		List<UserRole> userRoles = Collections.singletonList(UserRole.DEVELOPMENT);
		List<UserRole> assigneeRoles = Collections.singletonList(UserRole.DEVELOPMENT);

		when(this.issueQueryRepository.getIssueByCode(TEST_CODE)).thenReturn(issue);
		when(this.userQueryRepository.getUserRoles(TEST_UID)).thenReturn(userRoles);
		when(this.userQueryRepository.getUserRoles(TEST_NEXT_OPERATOR)).thenReturn(assigneeRoles);

		ClientErrorCode result = this.issueService.assign(TEST_CODE, TEST_NEXT_OPERATOR, TEST_UID);

		assertNull(result);
		verify(this.issueQueryRepository, times(1)).getIssueByCode(TEST_CODE);
		verify(this.userQueryRepository, times(1)).getUserRoles(TEST_UID);
		verify(this.userQueryRepository, times(1)).getUserRoles(TEST_NEXT_OPERATOR);
		verify(this.issueCommandRepository, times(1)).assignOperator(TEST_CODE, IssueStatus.HANDLING, TEST_NEXT_OPERATOR, TEST_UID);
	}

	/**
	 * 测试分配缺陷失败的场景，缺陷不存在
	 */
	@Test
	void testAssignIssueFailNotFound() {
		when(this.issueQueryRepository.getIssueByCode(TEST_CODE)).thenReturn(null);

		ClientErrorCode result = this.issueService.assign(TEST_CODE, TEST_NEXT_OPERATOR, TEST_UID);

		assertEquals(ClientErrorCode.ERROR_1440, result);
		verify(this.issueQueryRepository, times(1)).getIssueByCode(TEST_CODE);
		verify(this.issueCommandRepository, never()).assignOperator(any(), any(), any(), any());
	}

	/**
	 * 测试分配缺陷失败的场景，缺陷状态不允许修改
	 */
	@Test
	void testAssignIssueFailNotModifiable() {
		Issue issue = this.createTestIssue();
		issue.setStatus(IssueStatus.RELEASED.name());
		when(this.issueQueryRepository.getIssueByCode(TEST_CODE)).thenReturn(issue);

		ClientErrorCode result = this.issueService.assign(TEST_CODE, TEST_NEXT_OPERATOR, TEST_UID);

		assertEquals(ClientErrorCode.ERROR_1442, result);
		verify(this.issueQueryRepository, times(1)).getIssueByCode(TEST_CODE);
		verify(this.issueCommandRepository, never()).assignOperator(any(), any(), any(), any());
	}

	/**
	 * 测试分配缺陷失败的场景，用户没有权限
	 */
	@Test
	void testAssignIssueFailNoPermission() {
		Issue issue = this.createTestIssue();
		List<UserRole> userRoles = Collections.singletonList(UserRole.BUSINESS);

		when(this.issueQueryRepository.getIssueByCode(TEST_CODE)).thenReturn(issue);
		when(this.userQueryRepository.getUserRoles(TEST_UID)).thenReturn(userRoles);

		ClientErrorCode result = this.issueService.assign(TEST_CODE, TEST_NEXT_OPERATOR, TEST_UID);

		assertEquals(ClientErrorCode.ERROR_1491, result);
		verify(this.issueQueryRepository, times(1)).getIssueByCode(TEST_CODE);
		verify(this.userQueryRepository, times(1)).getUserRoles(TEST_UID);
		verify(this.issueCommandRepository, never()).assignOperator(any(), any(), any(), any());
	}

	/**
	 * 测试分配缺陷失败的场景，被分配者没有权限
	 */
	@Test
	void testAssignIssueFailAssigneeNoPermission() {
		Issue issue = this.createTestIssue();
		List<UserRole> userRoles = Collections.singletonList(UserRole.DEVELOPMENT);
		List<UserRole> assigneeRoles = Collections.singletonList(UserRole.BUSINESS);

		when(this.issueQueryRepository.getIssueByCode(TEST_CODE)).thenReturn(issue);
		when(this.userQueryRepository.getUserRoles(TEST_UID)).thenReturn(userRoles);
		when(this.userQueryRepository.getUserRoles(TEST_NEXT_OPERATOR)).thenReturn(assigneeRoles);

		ClientErrorCode result = this.issueService.assign(TEST_CODE, TEST_NEXT_OPERATOR, TEST_UID);

		assertEquals(ClientErrorCode.ERROR_1491, result);
		verify(this.issueQueryRepository, times(1)).getIssueByCode(TEST_CODE);
		verify(this.userQueryRepository, times(1)).getUserRoles(TEST_UID);
		verify(this.userQueryRepository, times(1)).getUserRoles(TEST_NEXT_OPERATOR);
		verify(this.issueCommandRepository, never()).assignOperator(any(), any(), any(), any());
	}

	/**
	 * 测试取消分配缺陷成功的场景
	 */
	@Test
	void testUnassignIssueSuccess() {
		Issue issue = this.createTestIssue();
		List<UserRole> userRoles = Collections.singletonList(UserRole.DEVELOPMENT);

		when(this.issueQueryRepository.getIssueByCode(TEST_CODE)).thenReturn(issue);
		when(this.userQueryRepository.getUserRoles(TEST_UID)).thenReturn(userRoles);
		when(this.issueCommandRepository.unassignOperator(TEST_CODE, IssueStatus.HANDLING, TEST_NEXT_OPERATOR, TEST_UID)).thenReturn(false);

		ClientErrorCode result = this.issueService.unassign(TEST_CODE, TEST_NEXT_OPERATOR, TEST_UID);

		assertNull(result);
		verify(this.issueQueryRepository, times(1)).getIssueByCode(TEST_CODE);
		verify(this.userQueryRepository, times(1)).getUserRoles(TEST_UID);
		verify(this.issueCommandRepository, times(1)).unassignOperator(TEST_CODE, IssueStatus.HANDLING, TEST_NEXT_OPERATOR, TEST_UID);
	}

	/**
	 * 测试取消分配缺陷失败的场景，缺陷不存在
	 */
	@Test
	void testUnassignIssueFailNotFound() {
		when(this.issueQueryRepository.getIssueByCode(TEST_CODE)).thenReturn(null);

		ClientErrorCode result = this.issueService.unassign(TEST_CODE, TEST_NEXT_OPERATOR, TEST_UID);

		assertEquals(ClientErrorCode.ERROR_1440, result);
		verify(this.issueQueryRepository, times(1)).getIssueByCode(TEST_CODE);
		verify(this.issueCommandRepository, never()).unassignOperator(any(), any(), any(), any());
	}

	/**
	 * 测试取消分配缺陷失败的场景，缺陷状态不允许修改
	 */
	@Test
	void testUnassignIssueFailNotModifiable() {
		Issue issue = this.createTestIssue();
		issue.setStatus(IssueStatus.RELEASED.name());
		when(this.issueQueryRepository.getIssueByCode(TEST_CODE)).thenReturn(issue);

		ClientErrorCode result = this.issueService.unassign(TEST_CODE, TEST_NEXT_OPERATOR, TEST_UID);

		assertEquals(ClientErrorCode.ERROR_1442, result);
		verify(this.issueQueryRepository, times(1)).getIssueByCode(TEST_CODE);
		verify(this.issueCommandRepository, never()).unassignOperator(any(), any(), any(), any());
	}

	/**
	 * 测试取消分配缺陷失败的场景，用户没有权限
	 */
	@Test
	void testUnassignIssueFailNoPermission() {
		Issue issue = this.createTestIssue();
		List<UserRole> userRoles = Collections.singletonList(UserRole.BUSINESS);

		when(this.issueQueryRepository.getIssueByCode(TEST_CODE)).thenReturn(issue);
		when(this.userQueryRepository.getUserRoles(TEST_UID)).thenReturn(userRoles);

		ClientErrorCode result = this.issueService.unassign(TEST_CODE, TEST_NEXT_OPERATOR, TEST_UID);

		assertEquals(ClientErrorCode.ERROR_1491, result);
		verify(this.issueQueryRepository, times(1)).getIssueByCode(TEST_CODE);
		verify(this.userQueryRepository, times(1)).getUserRoles(TEST_UID);
		verify(this.issueCommandRepository, never()).unassignOperator(any(), any(), any(), any());
	}
}
