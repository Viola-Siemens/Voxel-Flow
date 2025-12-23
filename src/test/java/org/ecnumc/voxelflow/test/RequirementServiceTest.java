package org.ecnumc.voxelflow.test;

import org.ecnumc.voxelflow.Application;
import org.ecnumc.voxelflow.converter.RequirementConverter;
import org.ecnumc.voxelflow.enumeration.ClientErrorCode;
import org.ecnumc.voxelflow.enumeration.RequirementStatus;
import org.ecnumc.voxelflow.enumeration.UserRole;
import org.ecnumc.voxelflow.po.Requirement;
import org.ecnumc.voxelflow.repository.RequirementCommandRepository;
import org.ecnumc.voxelflow.repository.RequirementQueryRepository;
import org.ecnumc.voxelflow.repository.UserQueryRepository;
import org.ecnumc.voxelflow.resp.RequirementResp;
import org.ecnumc.voxelflow.service.RequirementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = Application.class)
class RequirementServiceTest {
	@Mock
	private RequirementCommandRepository requirementCommandRepository;

	@Mock
	private RequirementQueryRepository requirementQueryRepository;

	@Mock
	private UserQueryRepository userQueryRepository;

	@Mock
	private RequirementConverter requirementConverter;

	@InjectMocks
	private RequirementService requirementService;

	private static final String TEST_CODE = "REQ-001";
	private static final String TEST_TITLE = "测试需求";
	private static final String TEST_DESCRIPTION = "这是一个简介，你没有必要把它读完，因为它真的没什么用。";
	private static final Integer TEST_PRIORITY = 1;
	private static final String TEST_REQUIREMENT_TYPE = "BUILDING";
	private static final String TEST_UID = "00000000-0000-0000-0000-000000000001";
	private static final String TEST_NEXT_OPERATOR = "00000000-0000-0000-0000-000000000002";

	private Requirement createTestRequirement() {
		Requirement requirement = new Requirement();
		requirement.setId(1L);
		requirement.setCode(TEST_CODE);
		requirement.setTitle(TEST_TITLE);
		requirement.setDescription(TEST_DESCRIPTION);
		requirement.setStatus(RequirementStatus.REVIEWING.name());
		requirement.setPriority(TEST_PRIORITY);
		requirement.setRequirementType(TEST_REQUIREMENT_TYPE);
		requirement.setCreatedBy(TEST_UID);
		requirement.setCreatedAt(new Date());
		requirement.setUpdatedBy(TEST_UID);
		requirement.setUpdatedAt(new Date());
		return requirement;
	}

	private RequirementResp createTestRequirementResp() {
		return RequirementResp.builder()
				.code(TEST_CODE)
				.title(TEST_TITLE)
				.description(TEST_DESCRIPTION)
				.status(RequirementStatus.REVIEWING.name())
				.priority(TEST_PRIORITY)
				.requirementType(TEST_REQUIREMENT_TYPE)
				.createdBy(TEST_UID)
				.createdAt(new Date())
				.updatedBy(TEST_UID)
				.updatedAt(new Date())
				.build();
	}

	@BeforeEach
	void setUp() {
		reset(this.requirementCommandRepository, this.requirementQueryRepository, this.userQueryRepository, this.requirementConverter);
	}

	/**
	 * 测试创建需求成功的场景
	 */
	@Test
	void testCreateRequirementSuccess() {
		Requirement requirement = this.createTestRequirement();
		RequirementResp requirementResp = this.createTestRequirementResp();

		when(this.requirementCommandRepository.createRequirement(TEST_TITLE, TEST_DESCRIPTION, TEST_PRIORITY, TEST_REQUIREMENT_TYPE, TEST_UID))
				.thenReturn(requirement);
		when(this.requirementConverter.convertToResp(requirement)).thenReturn(requirementResp);

		RequirementResp result = this.requirementService
				.createRequirement(TEST_TITLE, TEST_DESCRIPTION, TEST_PRIORITY, TEST_REQUIREMENT_TYPE, TEST_UID);

		assertNotNull(result);
		assertEquals(TEST_CODE, result.getCode());
		assertEquals(TEST_TITLE, result.getTitle());
		verify(this.requirementCommandRepository, times(1))
				.createRequirement(TEST_TITLE, TEST_DESCRIPTION, TEST_PRIORITY, TEST_REQUIREMENT_TYPE, TEST_UID);
		verify(this.requirementConverter, times(1)).convertToResp(requirement);
	}

	/**
	 * 测试创建需求失败的场景，需求类型无效
	 */
	@Test
	void testCreateRequirementFailInvalidType() {
		String invalidType = "INVALID_TYPE";

		RequirementResp result = this.requirementService
				.createRequirement(TEST_TITLE, TEST_DESCRIPTION, TEST_PRIORITY, invalidType, TEST_UID);

		assertNull(result);
		verify(this.requirementCommandRepository, never()).createRequirement(any(), any(), any(), any(), any());
		verify(this.requirementConverter, never()).convertToResp(any());
	}

	/**
	 * 测试查询需求成功的场景
	 */
	@Test
	void testQueryRequirementSuccess() {
		Requirement requirement = this.createTestRequirement();
		RequirementResp requirementResp = this.createTestRequirementResp();

		when(this.requirementQueryRepository.getRequirementByCode(TEST_CODE)).thenReturn(requirement);
		when(this.requirementConverter.convertToResp(requirement)).thenReturn(requirementResp);

		RequirementResp result = this.requirementService.queryRequirement(TEST_CODE);

		assertNotNull(result);
		assertEquals(TEST_CODE, result.getCode());
		verify(this.requirementQueryRepository, times(1)).getRequirementByCode(TEST_CODE);
		verify(this.requirementConverter, times(1)).convertToResp(requirement);
	}

	/**
	 * 测试查询需求失败的场景，需求不存在
	 */
	@Test
	void testQueryRequirementFailNotFound() {
		when(this.requirementQueryRepository.getRequirementByCode(TEST_CODE)).thenReturn(null);

		RequirementResp result = this.requirementService.queryRequirement(TEST_CODE);

		assertNull(result);
		verify(this.requirementQueryRepository, times(1)).getRequirementByCode(TEST_CODE);
		verify(this.requirementConverter, never()).convertToResp(any());
	}

	/**
	 * 测试更新需求成功的场景
	 */
	@Test
	void testUpdateRequirementSuccess() {
		Requirement requirement = this.createTestRequirement();
		List<UserRole> userRoles = Collections.singletonList(UserRole.BUSINESS);

		when(this.requirementQueryRepository.getRequirementByCode(TEST_CODE)).thenReturn(requirement);
		when(this.userQueryRepository.getUserRoles(TEST_UID)).thenReturn(userRoles);

		String newTitle = "Updated Title";
		String newDescription = "Updated Description";
		ClientErrorCode result = this.requirementService
				.updateRequirement(TEST_CODE, newTitle, newDescription, 2, TEST_REQUIREMENT_TYPE, TEST_UID);

		assertNull(result);
		verify(this.requirementQueryRepository, times(1)).getRequirementByCode(TEST_CODE);
		verify(this.userQueryRepository, times(1)).getUserRoles(TEST_UID);
		verify(this.requirementCommandRepository, times(1))
				.updateRequirement(TEST_CODE, newTitle, newDescription, 2, TEST_REQUIREMENT_TYPE, TEST_UID);
	}

	/**
	 * 测试更新需求失败的场景，需求不存在
	 */
	@Test
	void testUpdateRequirementFailNotFound() {
		when(this.requirementQueryRepository.getRequirementByCode(TEST_CODE)).thenReturn(null);

		ClientErrorCode result = this.requirementService
				.updateRequirement(TEST_CODE, "New Title", "New Description", 2, TEST_REQUIREMENT_TYPE, TEST_UID);

		assertEquals(ClientErrorCode.ERROR_1420, result);
		verify(this.requirementQueryRepository, times(1)).getRequirementByCode(TEST_CODE);
		verify(this.requirementCommandRepository, never()).updateRequirement(any(), any(), any(), any(), any(), any());
	}

	/**
	 * 测试更新需求失败的场景，需求类型无效
	 */
	@Test
	void testUpdateRequirementFailInvalidType() {
		Requirement requirement = this.createTestRequirement();
		when(this.requirementQueryRepository.getRequirementByCode(TEST_CODE)).thenReturn(requirement);

		String invalidType = "INVALID_TYPE";
		ClientErrorCode result = this.requirementService
				.updateRequirement(TEST_CODE, "New Title", "New Description", 2, invalidType, TEST_UID);

		assertEquals(ClientErrorCode.ERROR_1421, result);
		verify(this.requirementQueryRepository, times(1)).getRequirementByCode(TEST_CODE);
		verify(this.requirementCommandRepository, never()).updateRequirement(any(), any(), any(), any(), any(), any());
	}

	/**
	 * 测试更新需求失败的场景，需求状态不允许修改
	 */
	@Test
	void testUpdateRequirementFailNotModifiable() {
		Requirement requirement = this.createTestRequirement();
		requirement.setStatus(RequirementStatus.RELEASED.name());
		when(this.requirementQueryRepository.getRequirementByCode(TEST_CODE)).thenReturn(requirement);

		ClientErrorCode result = this.requirementService.updateRequirement(TEST_CODE, "New Title", "New Description", 2, TEST_REQUIREMENT_TYPE, TEST_UID);

		assertEquals(ClientErrorCode.ERROR_1422, result);
		verify(this.requirementQueryRepository, times(1)).getRequirementByCode(TEST_CODE);
		verify(this.requirementCommandRepository, never()).updateRequirement(any(), any(), any(), any(), any(), any());
	}

	/**
	 * 测试更新需求失败的场景，用户没有权限
	 */
	@Test
	void testUpdateRequirementFailNoPermission() {
		Requirement requirement = this.createTestRequirement();
		List<UserRole> userRoles = Collections.singletonList(UserRole.DEVELOPMENT);

		when(this.requirementQueryRepository.getRequirementByCode(TEST_CODE)).thenReturn(requirement);
		when(this.userQueryRepository.getUserRoles(TEST_UID)).thenReturn(userRoles);

		ClientErrorCode result = this.requirementService.updateRequirement(TEST_CODE, "New Title", "New Description", 2, TEST_REQUIREMENT_TYPE, TEST_UID);

		assertEquals(ClientErrorCode.ERROR_1491, result);
		verify(this.requirementQueryRepository, times(1)).getRequirementByCode(TEST_CODE);
		verify(this.userQueryRepository, times(1)).getUserRoles(TEST_UID);
		verify(this.requirementCommandRepository, never()).updateRequirement(any(), any(), any(), any(), any(), any());
	}

	/**
	 * 测试批准需求成功的场景
	 */
	@Test
	void testApproveRequirementSuccess() {
		Requirement requirement = this.createTestRequirement();
		List<UserRole> userRoles = Collections.singletonList(UserRole.BUSINESS);
		List<UserRole> nextOperatorRoles = Collections.singletonList(UserRole.PRODUCT);
		List<String> nextOperators = Collections.singletonList(TEST_NEXT_OPERATOR);

		when(this.requirementQueryRepository.getRequirementByCode(TEST_CODE)).thenReturn(requirement);
		when(this.userQueryRepository.getUserRoles(TEST_UID)).thenReturn(userRoles);
		when(this.userQueryRepository.getUserRoles(TEST_NEXT_OPERATOR)).thenReturn(nextOperatorRoles);
		when(this.requirementQueryRepository.getPendingRelationCount(TEST_CODE, RequirementStatus.REVIEWING)).thenReturn(0);

		ClientErrorCode result = this.requirementService.approveRequirement(TEST_CODE, nextOperators, "Approved", TEST_UID);

		assertNull(result);
		verify(this.requirementQueryRepository, times(1)).getRequirementByCode(TEST_CODE);
		verify(this.userQueryRepository, times(1)).getUserRoles(TEST_UID);
		verify(this.userQueryRepository, times(1)).getUserRoles(TEST_NEXT_OPERATOR);
		verify(this.requirementCommandRepository, times(1)).updateStatus(TEST_CODE, RequirementStatus.REVIEWING, RequirementStatus.COUNTERSIGNING, TEST_UID);
	}

	/**
	 * 测试批准需求失败的场景，需求不存在
	 */
	@Test
	void testApproveRequirementFailNotFound() {
		when(this.requirementQueryRepository.getRequirementByCode(TEST_CODE)).thenReturn(null);

		List<String> nextOperators = Collections.singletonList(TEST_NEXT_OPERATOR);
		ClientErrorCode result = this.requirementService.approveRequirement(TEST_CODE, nextOperators, "Approved", TEST_UID);

		assertEquals(ClientErrorCode.ERROR_1420, result);
		verify(this.requirementQueryRepository, times(1)).getRequirementByCode(TEST_CODE);
		verify(this.requirementCommandRepository, never()).updateRelation(any(), any(), any(), any(), any());
	}

	/**
	 * 测试批准需求失败的场景，需求状态不允许修改
	 */
	@Test
	void testApproveRequirementFailNotModifiable() {
		Requirement requirement = this.createTestRequirement();
		requirement.setStatus(RequirementStatus.RELEASED.name());
		when(this.requirementQueryRepository.getRequirementByCode(TEST_CODE)).thenReturn(requirement);

		List<String> nextOperators = Collections.singletonList(TEST_NEXT_OPERATOR);
		ClientErrorCode result = this.requirementService.approveRequirement(TEST_CODE, nextOperators, "Approved", TEST_UID);

		assertEquals(ClientErrorCode.ERROR_1422, result);
		verify(this.requirementQueryRepository, times(1)).getRequirementByCode(TEST_CODE);
		verify(this.requirementCommandRepository, never()).updateRelation(any(), any(), any(), any(), any());
	}

	/**
	 * 测试批准需求失败的场景，用户没有权限
	 */
	@Test
	void testApproveRequirementFailNoPermission() {
		Requirement requirement = this.createTestRequirement();
		List<UserRole> userRoles = Collections.singletonList(UserRole.DEVELOPMENT);

		when(this.requirementQueryRepository.getRequirementByCode(TEST_CODE)).thenReturn(requirement);
		when(this.userQueryRepository.getUserRoles(TEST_UID)).thenReturn(userRoles);

		List<String> nextOperators = Collections.singletonList(TEST_NEXT_OPERATOR);
		ClientErrorCode result = this.requirementService.approveRequirement(TEST_CODE, nextOperators, "Approved", TEST_UID);

		assertEquals(ClientErrorCode.ERROR_1491, result);
		verify(this.requirementQueryRepository, times(1)).getRequirementByCode(TEST_CODE);
		verify(this.userQueryRepository, times(1)).getUserRoles(TEST_UID);
		verify(this.requirementCommandRepository, never()).updateRelation(any(), any(), any(), any(), any());
	}

	/**
	 * 测试批准需求失败的场景，下一个操作人没有权限
	 */
	@Test
	void testApproveRequirementFailNextOperatorNoPermission() {
		Requirement requirement = this.createTestRequirement();
		List<UserRole> userRoles = Collections.singletonList(UserRole.BUSINESS);
		List<UserRole> nextOperatorRoles = Collections.singletonList(UserRole.TEST);
		List<String> nextOperators = Collections.singletonList(TEST_NEXT_OPERATOR);

		when(this.requirementQueryRepository.getRequirementByCode(TEST_CODE)).thenReturn(requirement);
		when(this.userQueryRepository.getUserRoles(TEST_UID)).thenReturn(userRoles);
		when(this.userQueryRepository.getUserRoles(TEST_NEXT_OPERATOR)).thenReturn(nextOperatorRoles);

		ClientErrorCode result = this.requirementService.approveRequirement(TEST_CODE, nextOperators, "Approved", TEST_UID);

		assertEquals(ClientErrorCode.ERROR_1492, result);
		verify(this.requirementQueryRepository, times(1)).getRequirementByCode(TEST_CODE);
		verify(this.userQueryRepository, times(1)).getUserRoles(TEST_UID);
		verify(this.userQueryRepository, times(1)).getUserRoles(TEST_NEXT_OPERATOR);
		verify(this.requirementCommandRepository, never()).updateRelation(any(), any(), any(), any(), any());
	}

	/**
	 * 测试拒绝需求成功的场景
	 */
	@Test
	void testRejectRequirementSuccess() {
		Requirement requirement = this.createTestRequirement();
		requirement.setStatus(RequirementStatus.DEVELOPING.name());
		List<UserRole> userRoles = Collections.singletonList(UserRole.DEVELOPMENT);
		List<UserRole> nextOperatorRoles = Collections.singletonList(UserRole.PRODUCT);
		List<String> nextOperators = Collections.singletonList(TEST_NEXT_OPERATOR);

		when(this.requirementQueryRepository.getRequirementByCode(TEST_CODE)).thenReturn(requirement);
		when(this.userQueryRepository.getUserRoles(TEST_UID)).thenReturn(userRoles);
		when(this.userQueryRepository.getUserRoles(TEST_NEXT_OPERATOR)).thenReturn(nextOperatorRoles);

		ClientErrorCode result = this.requirementService.rejectRequirement(TEST_CODE, nextOperators, "Rejected", TEST_UID);

		assertNull(result);
		verify(this.requirementQueryRepository, times(1)).getRequirementByCode(TEST_CODE);
		verify(this.userQueryRepository, times(1)).getUserRoles(TEST_UID);
		verify(this.userQueryRepository, times(1)).getUserRoles(TEST_NEXT_OPERATOR);
	}

	/**
	 * 测试拒绝需求失败的场景，需求不存在
	 */
	@Test
	void testRejectRequirementFailNotFound() {
		when(this.requirementQueryRepository.getRequirementByCode(TEST_CODE)).thenReturn(null);

		List<String> nextOperators = Collections.singletonList(TEST_NEXT_OPERATOR);
		ClientErrorCode result = this.requirementService.rejectRequirement(TEST_CODE, nextOperators, "Rejected", TEST_UID);

		assertEquals(ClientErrorCode.ERROR_1420, result);
		verify(this.requirementQueryRepository, times(1)).getRequirementByCode(TEST_CODE);
		verify(this.requirementCommandRepository, never()).updateRelation(any(), any(), any(), any(), any());
	}

	/**
	 * 测试拒绝需求失败的场景，需求状态不允许修改
	 */
	@Test
	void testRejectRequirementFailNotModifiable() {
		Requirement requirement = this.createTestRequirement();
		requirement.setStatus(RequirementStatus.RELEASED.name());
		when(this.requirementQueryRepository.getRequirementByCode(TEST_CODE)).thenReturn(requirement);

		List<String> nextOperators = Collections.singletonList(TEST_NEXT_OPERATOR);
		ClientErrorCode result = this.requirementService.rejectRequirement(TEST_CODE, nextOperators, "Rejected", TEST_UID);

		assertEquals(ClientErrorCode.ERROR_1422, result);
		verify(this.requirementQueryRepository, times(1)).getRequirementByCode(TEST_CODE);
		verify(this.requirementCommandRepository, never()).updateRelation(any(), any(), any(), any(), any());
	}

	/**
	 * 测试拒绝需求失败的场景，用户没有权限
	 */
	@Test
	void testRejectRequirementFailNoPermission() {
		Requirement requirement = this.createTestRequirement();
		List<UserRole> userRoles = Collections.singletonList(UserRole.DEVELOPMENT);

		when(this.requirementQueryRepository.getRequirementByCode(TEST_CODE)).thenReturn(requirement);
		when(this.userQueryRepository.getUserRoles(TEST_UID)).thenReturn(userRoles);

		List<String> nextOperators = Collections.singletonList(TEST_NEXT_OPERATOR);
		ClientErrorCode result = this.requirementService.rejectRequirement(TEST_CODE, nextOperators, "Rejected", TEST_UID);

		assertEquals(ClientErrorCode.ERROR_1491, result);
		verify(this.requirementQueryRepository, times(1)).getRequirementByCode(TEST_CODE);
		verify(this.userQueryRepository, times(1)).getUserRoles(TEST_UID);
		verify(this.requirementCommandRepository, never()).updateRelation(any(), any(), any(), any(), any());
	}

	/**
	 * 测试拒绝需求失败的场景，下一个操作人没有权限
	 */
	@Test
	void testRejectRequirementFailNextOperatorNoPermission() {
		Requirement requirement = this.createTestRequirement();
		requirement.setStatus(RequirementStatus.TESTING.name());
		List<UserRole> userRoles = Collections.singletonList(UserRole.TEST);
		List<UserRole> nextOperatorRoles = Arrays.asList(UserRole.PRODUCT, UserRole.SECURITY);
		List<String> nextOperators = Collections.singletonList(TEST_NEXT_OPERATOR);

		when(this.requirementQueryRepository.getRequirementByCode(TEST_CODE)).thenReturn(requirement);
		when(this.userQueryRepository.getUserRoles(TEST_UID)).thenReturn(userRoles);
		when(this.userQueryRepository.getUserRoles(TEST_NEXT_OPERATOR)).thenReturn(nextOperatorRoles);

		ClientErrorCode result = this.requirementService.rejectRequirement(TEST_CODE, nextOperators, "Rejected", TEST_UID);

		assertEquals(ClientErrorCode.ERROR_1492, result);
		verify(this.requirementQueryRepository, times(1)).getRequirementByCode(TEST_CODE);
		verify(this.userQueryRepository, times(1)).getUserRoles(TEST_UID);
		verify(this.userQueryRepository, times(1)).getUserRoles(TEST_NEXT_OPERATOR);
		verify(this.requirementCommandRepository, never()).updateRelation(any(), any(), any(), any(), any());
	}
}
