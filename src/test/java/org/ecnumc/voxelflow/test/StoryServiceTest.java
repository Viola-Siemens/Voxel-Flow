package org.ecnumc.voxelflow.test;

import org.ecnumc.voxelflow.Application;
import org.ecnumc.voxelflow.converter.StoryConverter;
import org.ecnumc.voxelflow.enumeration.ClientErrorCode;
import org.ecnumc.voxelflow.enumeration.StoryStatus;
import org.ecnumc.voxelflow.enumeration.UserRole;
import org.ecnumc.voxelflow.po.Requirement;
import org.ecnumc.voxelflow.po.Story;
import org.ecnumc.voxelflow.repository.RequirementQueryRepository;
import org.ecnumc.voxelflow.repository.StoryCommandRepository;
import org.ecnumc.voxelflow.repository.StoryQueryRepository;
import org.ecnumc.voxelflow.repository.UserQueryRepository;
import org.ecnumc.voxelflow.resp.StoryResp;
import org.ecnumc.voxelflow.service.StoryService;
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
class StoryServiceTest {
	@Mock
	private StoryCommandRepository storyCommandRepository;

	@Mock
	private StoryQueryRepository storyQueryRepository;

	@Mock
	private UserQueryRepository userQueryRepository;

	@Mock
	private RequirementQueryRepository requirementQueryRepository;

	@Mock
	private StoryConverter storyConverter;

	@InjectMocks
	private StoryService storyService;

	private static final String TEST_CODE = "LDY-9527";
	private static final String TEST_TITLE = "测试故事";
	private static final String TEST_DESCRIPTION = "这是一个简介，你没有必要把它读完，因为它真的没什么用。";
	private static final Integer TEST_PRIORITY = 1;
	private static final String TEST_REQ_CODE = "REQ-001";
	private static final String TEST_UID = "00000000-0000-0000-0000-000000000001";
	private static final String TEST_NEXT_OPERATOR = "00000000-0000-0000-0000-000000000002";

	private Story createTestStory() {
		Story story = new Story();
		story.setId(1L);
		story.setCode(TEST_CODE);
		story.setTitle(TEST_TITLE);
		story.setDescription(TEST_DESCRIPTION);
		story.setStatus(StoryStatus.PROGRESSING.name());
		story.setPriority(TEST_PRIORITY);
		story.setReqCode(TEST_REQ_CODE);
		story.setCreatedBy(TEST_UID);
		story.setCreatedAt(new Date());
		story.setUpdatedBy(TEST_UID);
		story.setUpdatedAt(new Date());
		return story;
	}

	private StoryResp createTestStoryResp() {
		return StoryResp.builder()
				.code(TEST_CODE)
				.title(TEST_TITLE)
				.description(TEST_DESCRIPTION)
				.status(StoryStatus.PROGRESSING.name())
				.priority(TEST_PRIORITY)
				.reqCode(TEST_REQ_CODE)
				.createdBy(TEST_UID)
				.createdAt(new Date())
				.updatedBy(TEST_UID)
				.updatedAt(new Date())
				.build();
	}

	private Requirement createTestRequirement() {
		Requirement requirement = new Requirement();
		requirement.setCode(TEST_REQ_CODE);
		return requirement;
	}

	@BeforeEach
	void setUp() {
		reset(this.storyCommandRepository, this.storyQueryRepository, this.userQueryRepository,
				this.requirementQueryRepository, this.storyConverter);
	}

	/**
	 * 测试创建故事成功的场景
	 */
	@Test
	void testCreateStorySuccess() {
		Story story = this.createTestStory();
		StoryResp storyResp = this.createTestStoryResp();
		Requirement requirement = this.createTestRequirement();

		when(this.requirementQueryRepository.getRequirementByCode(TEST_REQ_CODE)).thenReturn(requirement);
		when(this.storyCommandRepository.createStory(TEST_TITLE, TEST_DESCRIPTION, TEST_PRIORITY, TEST_REQ_CODE, TEST_UID))
				.thenReturn(story);
		when(this.storyConverter.convertToResp(story)).thenReturn(storyResp);

		StoryResp result = this.storyService
				.createStory(TEST_TITLE, TEST_DESCRIPTION, TEST_PRIORITY, TEST_REQ_CODE, TEST_UID);

		assertNotNull(result);
		assertEquals(TEST_CODE, result.getCode());
		assertEquals(TEST_TITLE, result.getTitle());
		verify(this.requirementQueryRepository, times(1)).getRequirementByCode(TEST_REQ_CODE);
		verify(this.storyCommandRepository, times(1))
				.createStory(TEST_TITLE, TEST_DESCRIPTION, TEST_PRIORITY, TEST_REQ_CODE, TEST_UID);
		verify(this.storyConverter, times(1)).convertToResp(story);
	}

	/**
	 * 测试创建故事失败的场景，需求不存在
	 */
	@Test
	void testCreateStoryFailRequirementNotFound() {
		when(this.requirementQueryRepository.getRequirementByCode(TEST_REQ_CODE)).thenReturn(null);

		StoryResp result = this.storyService
				.createStory(TEST_TITLE, TEST_DESCRIPTION, TEST_PRIORITY, TEST_REQ_CODE, TEST_UID);

		assertNull(result);
		verify(this.requirementQueryRepository, times(1)).getRequirementByCode(TEST_REQ_CODE);
		verify(this.storyCommandRepository, never()).createStory(any(), any(), any(), any(), any());
		verify(this.storyConverter, never()).convertToResp(any());
	}

	/**
	 * 测试查询故事成功的场景
	 */
	@Test
	void testQueryStorySuccess() {
		Story story = this.createTestStory();
		StoryResp storyResp = this.createTestStoryResp();

		when(this.storyQueryRepository.getStoryByCode(TEST_CODE)).thenReturn(story);
		when(this.storyConverter.convertToResp(story)).thenReturn(storyResp);

		StoryResp result = this.storyService.queryByCode(TEST_CODE);

		assertNotNull(result);
		assertEquals(TEST_CODE, result.getCode());
		verify(this.storyQueryRepository, times(1)).getStoryByCode(TEST_CODE);
		verify(this.storyConverter, times(1)).convertToResp(story);
	}

	/**
	 * 测试查询故事失败的场景，故事不存在
	 */
	@Test
	void testQueryStoryFailNotFound() {
		when(this.storyQueryRepository.getStoryByCode(TEST_CODE)).thenReturn(null);

		StoryResp result = this.storyService.queryByCode(TEST_CODE);

		assertNull(result);
		verify(this.storyQueryRepository, times(1)).getStoryByCode(TEST_CODE);
		verify(this.storyConverter, never()).convertToResp(any());
	}

	/**
	 * 测试更新故事成功的场景
	 */
	@Test
	void testUpdateStorySuccess() {
		Story story = this.createTestStory();
		List<UserRole> userRoles = Collections.singletonList(UserRole.DEVELOPMENT);

		when(this.storyQueryRepository.getStoryByCode(TEST_CODE)).thenReturn(story);
		when(this.userQueryRepository.getUserRoles(TEST_UID)).thenReturn(userRoles);

		String newTitle = "Updated Title";
		String newDescription = "Updated Description";
		ClientErrorCode result = this.storyService
				.updateStory(TEST_CODE, newTitle, newDescription, 2, TEST_REQ_CODE, TEST_UID);

		assertNull(result);
		verify(this.storyQueryRepository, times(1)).getStoryByCode(TEST_CODE);
		verify(this.userQueryRepository, times(1)).getUserRoles(TEST_UID);
		verify(this.storyCommandRepository, times(1))
				.updateStory(TEST_CODE, newTitle, newDescription, 2, TEST_REQ_CODE, TEST_UID);
	}

	/**
	 * 测试更新故事失败的场景，故事不存在
	 */
	@Test
	void testUpdateStoryFailNotFound() {
		when(this.storyQueryRepository.getStoryByCode(TEST_CODE)).thenReturn(null);

		ClientErrorCode result = this.storyService
				.updateStory(TEST_CODE, "New Title", "New Description", 2, TEST_REQ_CODE, TEST_UID);

		assertEquals(ClientErrorCode.ERROR_1430, result);
		verify(this.storyQueryRepository, times(1)).getStoryByCode(TEST_CODE);
		verify(this.storyCommandRepository, never()).updateStory(any(), any(), any(), any(), any(), any());
	}

	/**
	 * 测试更新故事失败的场景，故事状态不允许修改
	 */
	@Test
	void testUpdateStoryFailNotModifiable() {
		Story story = this.createTestStory();
		story.setStatus(StoryStatus.FINISHED.name());
		when(this.storyQueryRepository.getStoryByCode(TEST_CODE)).thenReturn(story);

		ClientErrorCode result = this.storyService.updateStory(TEST_CODE, "New Title", "New Description", 2, TEST_REQ_CODE, TEST_UID);

		assertEquals(ClientErrorCode.ERROR_1432, result);
		verify(this.storyQueryRepository, times(1)).getStoryByCode(TEST_CODE);
		verify(this.storyCommandRepository, never()).updateStory(any(), any(), any(), any(), any(), any());
	}

	/**
	 * 测试更新故事失败的场景，用户没有权限
	 */
	@Test
	void testUpdateStoryFailNoPermission() {
		Story story = this.createTestStory();
		List<UserRole> userRoles = Collections.singletonList(UserRole.BUSINESS);

		when(this.storyQueryRepository.getStoryByCode(TEST_CODE)).thenReturn(story);
		when(this.userQueryRepository.getUserRoles(TEST_UID)).thenReturn(userRoles);

		ClientErrorCode result = this.storyService.updateStory(TEST_CODE, "New Title", "New Description", 2, TEST_REQ_CODE, TEST_UID);

		assertEquals(ClientErrorCode.ERROR_1491, result);
		verify(this.storyQueryRepository, times(1)).getStoryByCode(TEST_CODE);
		verify(this.userQueryRepository, times(1)).getUserRoles(TEST_UID);
		verify(this.storyCommandRepository, never()).updateStory(any(), any(), any(), any(), any(), any());
	}

	/**
	 * 测试批准故事成功的场景
	 */
	@Test
	void testApproveStorySuccess() {
		Story story = this.createTestStory();
		List<UserRole> userRoles = Collections.singletonList(UserRole.DEVELOPMENT);
		List<UserRole> nextOperatorRoles = Collections.singletonList(UserRole.TEST);
		List<String> nextOperators = Collections.singletonList(TEST_NEXT_OPERATOR);

		when(this.storyQueryRepository.getStoryByCode(TEST_CODE)).thenReturn(story);
		when(this.userQueryRepository.getUserRoles(TEST_UID)).thenReturn(userRoles);
		when(this.userQueryRepository.getUserRoles(TEST_NEXT_OPERATOR)).thenReturn(nextOperatorRoles);

		ClientErrorCode result = this.storyService.approve(TEST_CODE, nextOperators, "Approved", TEST_UID);

		assertNull(result);
		verify(this.storyQueryRepository, times(1)).getStoryByCode(TEST_CODE);
		verify(this.userQueryRepository, times(1)).getUserRoles(TEST_UID);
		verify(this.userQueryRepository, times(1)).getUserRoles(TEST_NEXT_OPERATOR);
		verify(this.storyCommandRepository, times(1)).updateStatus(TEST_CODE, StoryStatus.PROGRESSING, StoryStatus.TESTING, TEST_UID);
	}

	/**
	 * 测试批准故事失败的场景，故事不存在
	 */
	@Test
	void testApproveStoryFailNotFound() {
		when(this.storyQueryRepository.getStoryByCode(TEST_CODE)).thenReturn(null);

		List<String> nextOperators = Collections.singletonList(TEST_NEXT_OPERATOR);
		ClientErrorCode result = this.storyService.approve(TEST_CODE, nextOperators, "Approved", TEST_UID);

		assertEquals(ClientErrorCode.ERROR_1430, result);
		verify(this.storyQueryRepository, times(1)).getStoryByCode(TEST_CODE);
		verify(this.storyCommandRepository, never()).updateRelation(any(), any(), any(), any(), any());
	}

	/**
	 * 测试批准故事失败的场景，故事状态不允许修改
	 */
	@Test
	void testApproveStoryFailNotModifiable() {
		Story story = this.createTestStory();
		story.setStatus(StoryStatus.FINISHED.name());
		when(this.storyQueryRepository.getStoryByCode(TEST_CODE)).thenReturn(story);

		List<String> nextOperators = Collections.singletonList(TEST_NEXT_OPERATOR);
		ClientErrorCode result = this.storyService.approve(TEST_CODE, nextOperators, "Approved", TEST_UID);

		assertEquals(ClientErrorCode.ERROR_1432, result);
		verify(this.storyQueryRepository, times(1)).getStoryByCode(TEST_CODE);
		verify(this.storyCommandRepository, never()).updateRelation(any(), any(), any(), any(), any());
	}

	/**
	 * 测试批准故事失败的场景，用户没有权限
	 */
	@Test
	void testApproveStoryFailNoPermission() {
		Story story = this.createTestStory();
		List<UserRole> userRoles = Collections.singletonList(UserRole.BUSINESS);

		when(this.storyQueryRepository.getStoryByCode(TEST_CODE)).thenReturn(story);
		when(this.userQueryRepository.getUserRoles(TEST_UID)).thenReturn(userRoles);

		List<String> nextOperators = Collections.singletonList(TEST_NEXT_OPERATOR);
		ClientErrorCode result = this.storyService.approve(TEST_CODE, nextOperators, "Approved", TEST_UID);

		assertEquals(ClientErrorCode.ERROR_1491, result);
		verify(this.storyQueryRepository, times(1)).getStoryByCode(TEST_CODE);
		verify(this.userQueryRepository, times(1)).getUserRoles(TEST_UID);
		verify(this.storyCommandRepository, never()).updateRelation(any(), any(), any(), any(), any());
	}

	/**
	 * 测试批准故事失败的场景，下一个操作人没有权限
	 */
	@Test
	void testApproveStoryFailNextOperatorNoPermission() {
		Story story = this.createTestStory();
		List<UserRole> userRoles = Collections.singletonList(UserRole.DEVELOPMENT);
		List<UserRole> nextOperatorRoles = Collections.singletonList(UserRole.BUSINESS);
		List<String> nextOperators = Collections.singletonList(TEST_NEXT_OPERATOR);

		when(this.storyQueryRepository.getStoryByCode(TEST_CODE)).thenReturn(story);
		when(this.userQueryRepository.getUserRoles(TEST_UID)).thenReturn(userRoles);
		when(this.userQueryRepository.getUserRoles(TEST_NEXT_OPERATOR)).thenReturn(nextOperatorRoles);

		ClientErrorCode result = this.storyService.approve(TEST_CODE, nextOperators, "Approved", TEST_UID);

		assertEquals(ClientErrorCode.ERROR_1492, result);
		verify(this.storyQueryRepository, times(1)).getStoryByCode(TEST_CODE);
		verify(this.userQueryRepository, times(1)).getUserRoles(TEST_UID);
		verify(this.userQueryRepository, times(1)).getUserRoles(TEST_NEXT_OPERATOR);
		verify(this.storyCommandRepository, never()).updateRelation(any(), any(), any(), any(), any());
	}

	/**
	 * 测试拒绝故事成功的场景
	 */
	@Test
	void testRejectStorySuccess() {
		Story story = this.createTestStory();
		story.setStatus(StoryStatus.TESTING.name());
		List<UserRole> userRoles = Collections.singletonList(UserRole.TEST);
		List<UserRole> nextOperatorRoles = Collections.singletonList(UserRole.DEVELOPMENT);
		List<String> nextOperators = Collections.singletonList(TEST_NEXT_OPERATOR);

		when(this.storyQueryRepository.getStoryByCode(TEST_CODE)).thenReturn(story);
		when(this.userQueryRepository.getUserRoles(TEST_UID)).thenReturn(userRoles);
		when(this.userQueryRepository.getUserRoles(TEST_NEXT_OPERATOR)).thenReturn(nextOperatorRoles);

		ClientErrorCode result = this.storyService.reject(TEST_CODE, nextOperators, "Rejected", TEST_UID);

		assertNull(result);
		verify(this.storyQueryRepository, times(1)).getStoryByCode(TEST_CODE);
		verify(this.userQueryRepository, times(1)).getUserRoles(TEST_UID);
		verify(this.userQueryRepository, times(1)).getUserRoles(TEST_NEXT_OPERATOR);
	}

	/**
	 * 测试拒绝故事失败的场景，故事不存在
	 */
	@Test
	void testRejectStoryFailNotFound() {
		when(this.storyQueryRepository.getStoryByCode(TEST_CODE)).thenReturn(null);

		List<String> nextOperators = Collections.singletonList(TEST_NEXT_OPERATOR);
		ClientErrorCode result = this.storyService.reject(TEST_CODE, nextOperators, "Rejected", TEST_UID);

		assertEquals(ClientErrorCode.ERROR_1430, result);
		verify(this.storyQueryRepository, times(1)).getStoryByCode(TEST_CODE);
		verify(this.storyCommandRepository, never()).updateRelation(any(), any(), any(), any(), any());
	}

	/**
	 * 测试拒绝故事失败的场景，故事状态不允许修改
	 */
	@Test
	void testRejectStoryFailNotModifiable() {
		Story story = this.createTestStory();
		story.setStatus(StoryStatus.FINISHED.name());
		when(this.storyQueryRepository.getStoryByCode(TEST_CODE)).thenReturn(story);

		List<String> nextOperators = Collections.singletonList(TEST_NEXT_OPERATOR);
		ClientErrorCode result = this.storyService.reject(TEST_CODE, nextOperators, "Rejected", TEST_UID);

		assertEquals(ClientErrorCode.ERROR_1432, result);
		verify(this.storyQueryRepository, times(1)).getStoryByCode(TEST_CODE);
		verify(this.storyCommandRepository, never()).updateRelation(any(), any(), any(), any(), any());
	}

	/**
	 * 测试拒绝故事失败的场景，用户没有权限
	 */
	@Test
	void testRejectStoryFailNoPermission() {
		Story story = this.createTestStory();
		List<UserRole> userRoles = Collections.singletonList(UserRole.BUSINESS);

		when(this.storyQueryRepository.getStoryByCode(TEST_CODE)).thenReturn(story);
		when(this.userQueryRepository.getUserRoles(TEST_UID)).thenReturn(userRoles);

		List<String> nextOperators = Collections.singletonList(TEST_NEXT_OPERATOR);
		ClientErrorCode result = this.storyService.reject(TEST_CODE, nextOperators, "Rejected", TEST_UID);

		assertEquals(ClientErrorCode.ERROR_1491, result);
		verify(this.storyQueryRepository, times(1)).getStoryByCode(TEST_CODE);
		verify(this.userQueryRepository, times(1)).getUserRoles(TEST_UID);
		verify(this.storyCommandRepository, never()).updateRelation(any(), any(), any(), any(), any());
	}

	/**
	 * 测试拒绝故事失败的场景，下一个操作人没有权限
	 */
	@Test
	void testRejectStoryFailNextOperatorNoPermission() {
		Story story = this.createTestStory();
		story.setStatus(StoryStatus.TESTING.name());
		List<UserRole> userRoles = Collections.singletonList(UserRole.TEST);
		List<UserRole> nextOperatorRoles = Collections.singletonList(UserRole.BUSINESS);
		List<String> nextOperators = Collections.singletonList(TEST_NEXT_OPERATOR);

		when(this.storyQueryRepository.getStoryByCode(TEST_CODE)).thenReturn(story);
		when(this.userQueryRepository.getUserRoles(TEST_UID)).thenReturn(userRoles);
		when(this.userQueryRepository.getUserRoles(TEST_NEXT_OPERATOR)).thenReturn(nextOperatorRoles);

		ClientErrorCode result = this.storyService.reject(TEST_CODE, nextOperators, "Rejected", TEST_UID);

		assertEquals(ClientErrorCode.ERROR_1492, result);
		verify(this.storyQueryRepository, times(1)).getStoryByCode(TEST_CODE);
		verify(this.userQueryRepository, times(1)).getUserRoles(TEST_UID);
		verify(this.userQueryRepository, times(1)).getUserRoles(TEST_NEXT_OPERATOR);
		verify(this.storyCommandRepository, never()).updateRelation(any(), any(), any(), any(), any());
	}

	/**
	 * 测试分配故事成功的场景
	 */
	@Test
	void testAssignStorySuccess() {
		Story story = this.createTestStory();
		List<UserRole> userRoles = Collections.singletonList(UserRole.DEVELOPMENT);
		List<UserRole> assigneeRoles = Collections.singletonList(UserRole.DEVELOPMENT);

		when(this.storyQueryRepository.getStoryByCode(TEST_CODE)).thenReturn(story);
		when(this.userQueryRepository.getUserRoles(TEST_UID)).thenReturn(userRoles);
		when(this.userQueryRepository.getUserRoles(TEST_NEXT_OPERATOR)).thenReturn(assigneeRoles);

		ClientErrorCode result = this.storyService.assign(TEST_CODE, TEST_NEXT_OPERATOR, TEST_UID);

		assertNull(result);
		verify(this.storyQueryRepository, times(1)).getStoryByCode(TEST_CODE);
		verify(this.userQueryRepository, times(1)).getUserRoles(TEST_UID);
		verify(this.userQueryRepository, times(1)).getUserRoles(TEST_NEXT_OPERATOR);
		verify(this.storyCommandRepository, times(1)).assignOperator(TEST_CODE, StoryStatus.PROGRESSING, TEST_NEXT_OPERATOR, TEST_UID);
	}

	/**
	 * 测试分配故事失败的场景，故事不存在
	 */
	@Test
	void testAssignStoryFailNotFound() {
		when(this.storyQueryRepository.getStoryByCode(TEST_CODE)).thenReturn(null);

		ClientErrorCode result = this.storyService.assign(TEST_CODE, TEST_NEXT_OPERATOR, TEST_UID);

		assertEquals(ClientErrorCode.ERROR_1430, result);
		verify(this.storyQueryRepository, times(1)).getStoryByCode(TEST_CODE);
		verify(this.storyCommandRepository, never()).assignOperator(any(), any(), any(), any());
	}

	/**
	 * 测试分配故事失败的场景，故事状态不允许修改
	 */
	@Test
	void testAssignStoryFailNotModifiable() {
		Story story = this.createTestStory();
		story.setStatus(StoryStatus.FINISHED.name());
		when(this.storyQueryRepository.getStoryByCode(TEST_CODE)).thenReturn(story);

		ClientErrorCode result = this.storyService.assign(TEST_CODE, TEST_NEXT_OPERATOR, TEST_UID);

		assertEquals(ClientErrorCode.ERROR_1432, result);
		verify(this.storyQueryRepository, times(1)).getStoryByCode(TEST_CODE);
		verify(this.storyCommandRepository, never()).assignOperator(any(), any(), any(), any());
	}

	/**
	 * 测试分配故事失败的场景，用户没有权限
	 */
	@Test
	void testAssignStoryFailNoPermission() {
		Story story = this.createTestStory();
		List<UserRole> userRoles = Collections.singletonList(UserRole.BUSINESS);

		when(this.storyQueryRepository.getStoryByCode(TEST_CODE)).thenReturn(story);
		when(this.userQueryRepository.getUserRoles(TEST_UID)).thenReturn(userRoles);

		ClientErrorCode result = this.storyService.assign(TEST_CODE, TEST_NEXT_OPERATOR, TEST_UID);

		assertEquals(ClientErrorCode.ERROR_1491, result);
		verify(this.storyQueryRepository, times(1)).getStoryByCode(TEST_CODE);
		verify(this.userQueryRepository, times(1)).getUserRoles(TEST_UID);
		verify(this.storyCommandRepository, never()).assignOperator(any(), any(), any(), any());
	}

	/**
	 * 测试分配故事失败的场景，被分配者没有权限
	 */
	@Test
	void testAssignStoryFailAssigneeNoPermission() {
		Story story = this.createTestStory();
		List<UserRole> userRoles = Collections.singletonList(UserRole.DEVELOPMENT);
		List<UserRole> assigneeRoles = Collections.singletonList(UserRole.BUSINESS);

		when(this.storyQueryRepository.getStoryByCode(TEST_CODE)).thenReturn(story);
		when(this.userQueryRepository.getUserRoles(TEST_UID)).thenReturn(userRoles);
		when(this.userQueryRepository.getUserRoles(TEST_NEXT_OPERATOR)).thenReturn(assigneeRoles);

		ClientErrorCode result = this.storyService.assign(TEST_CODE, TEST_NEXT_OPERATOR, TEST_UID);

		assertEquals(ClientErrorCode.ERROR_1491, result);
		verify(this.storyQueryRepository, times(1)).getStoryByCode(TEST_CODE);
		verify(this.userQueryRepository, times(1)).getUserRoles(TEST_UID);
		verify(this.userQueryRepository, times(1)).getUserRoles(TEST_NEXT_OPERATOR);
		verify(this.storyCommandRepository, never()).assignOperator(any(), any(), any(), any());
	}

	/**
	 * 测试取消分配故事成功的场景
	 */
	@Test
	void testUnassignStorySuccess() {
		Story story = this.createTestStory();
		List<UserRole> userRoles = Collections.singletonList(UserRole.DEVELOPMENT);

		when(this.storyQueryRepository.getStoryByCode(TEST_CODE)).thenReturn(story);
		when(this.userQueryRepository.getUserRoles(TEST_UID)).thenReturn(userRoles);
		when(this.storyCommandRepository.unassignOperator(TEST_CODE, StoryStatus.PROGRESSING, TEST_NEXT_OPERATOR, TEST_UID)).thenReturn(false);

		ClientErrorCode result = this.storyService.unassign(TEST_CODE, TEST_NEXT_OPERATOR, TEST_UID);

		assertNull(result);
		verify(this.storyQueryRepository, times(1)).getStoryByCode(TEST_CODE);
		verify(this.userQueryRepository, times(1)).getUserRoles(TEST_UID);
		verify(this.storyCommandRepository, times(1)).unassignOperator(TEST_CODE, StoryStatus.PROGRESSING, TEST_NEXT_OPERATOR, TEST_UID);
	}

	/**
	 * 测试取消分配故事失败的场景，故事不存在
	 */
	@Test
	void testUnassignStoryFailNotFound() {
		when(this.storyQueryRepository.getStoryByCode(TEST_CODE)).thenReturn(null);

		ClientErrorCode result = this.storyService.unassign(TEST_CODE, TEST_NEXT_OPERATOR, TEST_UID);

		assertEquals(ClientErrorCode.ERROR_1430, result);
		verify(this.storyQueryRepository, times(1)).getStoryByCode(TEST_CODE);
		verify(this.storyCommandRepository, never()).unassignOperator(any(), any(), any(), any());
	}

	/**
	 * 测试取消分配故事失败的场景，故事状态不允许修改
	 */
	@Test
	void testUnassignStoryFailNotModifiable() {
		Story story = this.createTestStory();
		story.setStatus(StoryStatus.FINISHED.name());
		when(this.storyQueryRepository.getStoryByCode(TEST_CODE)).thenReturn(story);

		ClientErrorCode result = this.storyService.unassign(TEST_CODE, TEST_NEXT_OPERATOR, TEST_UID);

		assertEquals(ClientErrorCode.ERROR_1432, result);
		verify(this.storyQueryRepository, times(1)).getStoryByCode(TEST_CODE);
		verify(this.storyCommandRepository, never()).unassignOperator(any(), any(), any(), any());
	}

	/**
	 * 测试取消分配故事失败的场景，用户没有权限
	 */
	@Test
	void testUnassignStoryFailNoPermission() {
		Story story = this.createTestStory();
		List<UserRole> userRoles = Collections.singletonList(UserRole.BUSINESS);

		when(this.storyQueryRepository.getStoryByCode(TEST_CODE)).thenReturn(story);
		when(this.userQueryRepository.getUserRoles(TEST_UID)).thenReturn(userRoles);

		ClientErrorCode result = this.storyService.unassign(TEST_CODE, TEST_NEXT_OPERATOR, TEST_UID);

		assertEquals(ClientErrorCode.ERROR_1491, result);
		verify(this.storyQueryRepository, times(1)).getStoryByCode(TEST_CODE);
		verify(this.userQueryRepository, times(1)).getUserRoles(TEST_UID);
		verify(this.storyCommandRepository, never()).unassignOperator(any(), any(), any(), any());
	}
}
