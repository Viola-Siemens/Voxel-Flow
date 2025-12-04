package org.ecnumc.voxelflow.test;

import org.ecnumc.voxelflow.Application;
import org.ecnumc.voxelflow.po.User;
import org.ecnumc.voxelflow.repository.UserCommandRepository;
import org.ecnumc.voxelflow.repository.UserQueryRepository;
import org.ecnumc.voxelflow.repository.UserValidationRepository;
import org.ecnumc.voxelflow.resp.TokenResp;
import org.ecnumc.voxelflow.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = Application.class)
class UserServiceTest {
	@Mock
	private UserQueryRepository userQueryRepository;

	@Mock
	private UserCommandRepository userCommandRepository;

	@Mock
	private UserValidationRepository userValidationRepository;

	@InjectMocks
	private UserService userService;

	private static final String TEST_USERNAME = "testuser";
	private static final String TEST_PASSWORD = "testpassword";
	private static final String TEST_EMAIL = "test@example.com";
	private static final String TEST_UID = "00000000-0000-0000-0000-000000000001";

	private User createTestUser() {
		User user = new User();
		user.setId(1L);
		user.setUid(TEST_UID);
		user.setUsername(TEST_USERNAME);
		user.setPassword(TEST_PASSWORD);
		user.setEmail(TEST_EMAIL);
		user.setEmailVerified("VERIFIED");
		user.setUserStatus("ACTIVE");
		user.setCreatedBy("SYSTEM");
		user.setCreatedAt(new Date());
		user.setUpdatedBy("SYSTEM");
		user.setUpdatedAt(new Date());
		return user;
	}

	@BeforeEach
	void setUp() {
		reset(this.userQueryRepository, this.userCommandRepository, this.userValidationRepository);
	}

	/**
	 * 测试注册成功的场景
	 */
	@Test
	void testSignUpSuccess() {
		when(this.userQueryRepository.getByUsername(TEST_USERNAME)).thenReturn(null);

		boolean result = this.userService.signUp(TEST_USERNAME, TEST_PASSWORD, TEST_EMAIL);

		assertTrue(result);
		verify(this.userQueryRepository, times(1)).getByUsername(TEST_USERNAME);
		verify(this.userCommandRepository, times(1)).addUser(TEST_USERNAME, TEST_PASSWORD, TEST_EMAIL);
	}

	/**
	 * 测试注册失败的场景，用户名已存在
	 */
	@Test
	void testSignUpFailUsernameExists() {
		User existingUser = this.createTestUser();
		when(this.userQueryRepository.getByUsername(TEST_USERNAME)).thenReturn(existingUser);

		boolean result = this.userService.signUp(TEST_USERNAME, TEST_PASSWORD, TEST_EMAIL);

		assertFalse(result);
		verify(this.userQueryRepository, times(1)).getByUsername(TEST_USERNAME);
		verify(this.userCommandRepository, never()).addUser(any(), any(), any());
	}

	/**
	 * 测试登录成功并返回 token 的场景
	 */
	@Test
	void testLogInSuccess() {
		User user = this.createTestUser();
		when(this.userQueryRepository.getByUsernameAndPassword(TEST_USERNAME, TEST_PASSWORD)).thenReturn(user);

		TokenResp result = this.userService.logIn(TEST_USERNAME, TEST_PASSWORD);

		assertNotNull(result);
		assertEquals(TEST_UID, result.getUid());
		assertNotNull(result.getToken());
		verify(this.userQueryRepository, times(1)).getByUsernameAndPassword(TEST_USERNAME, TEST_PASSWORD);
		verify(this.userValidationRepository, times(1)).setToken(eq(TEST_UID), any());
	}

	/**
	 * 测试登录失败的场景，用户名或密码错误
	 */
	@Test
	void testLogInFailWrongCredentials() {
		when(this.userQueryRepository.getByUsernameAndPassword(TEST_USERNAME, TEST_PASSWORD)).thenReturn(null);

		TokenResp result = this.userService.logIn(TEST_USERNAME, TEST_PASSWORD);

		assertNull(result);
		verify(this.userQueryRepository, times(1)).getByUsernameAndPassword(TEST_USERNAME, TEST_PASSWORD);
		verify(this.userValidationRepository, never()).setToken(any(), any());
	}

	/**
	 * 测试每次登录生成的 token 是唯一的
	 */
	@Test
	void testLogInTokenIsUnique() {
		User user = this.createTestUser();
		when(this.userQueryRepository.getByUsernameAndPassword(TEST_USERNAME, TEST_PASSWORD)).thenReturn(user);

		TokenResp result1 = this.userService.logIn(TEST_USERNAME, TEST_PASSWORD);
		TokenResp result2 = this.userService.logIn(TEST_USERNAME, TEST_PASSWORD);

		assertNotNull(result1);
		assertNotNull(result2);
		assertNotEquals(result1.getToken(), result2.getToken());
		verify(this.userValidationRepository, times(2)).setToken(eq(TEST_UID), any());
	}
}
