package org.ecnumc.voxelflow.test;

import org.ecnumc.voxelflow.Application;
import org.ecnumc.voxelflow.controller.UserController;
import org.ecnumc.voxelflow.enumeration.ClientErrorCode;
import org.ecnumc.voxelflow.req.UserLogInReq;
import org.ecnumc.voxelflow.req.UserSignUpReq;
import org.ecnumc.voxelflow.resp.BaseResp;
import org.ecnumc.voxelflow.resp.TokenResp;
import org.ecnumc.voxelflow.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = Application.class)
class UserControllerTest {
	@Mock
	private UserService userService;

	@InjectMocks
	private UserController userController;

	private static final String TEST_USERNAME = "testuser";
	private static final String TEST_PASSWORD = "testpassword";
	private static final String TEST_EMAIL = "test@example.com";
	private static final String TEST_UID = "00000000-0000-0000-0000-000000000001";
	private static final String TEST_TOKEN = "test-token-12345";

	@BeforeEach
	void setUp() {
		try {
			Field serviceField = UserController.class.getDeclaredField("userService");
			serviceField.setAccessible(true);
			serviceField.set(this.userController, this.userService);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
		reset(this.userService);
	}

	/**
	 * 测试注册接口成功返回 200
	 */
	@Test
	void testSignUpSuccess() {
		UserSignUpReq req = UserSignUpReq.builder()
				.username(TEST_USERNAME)
				.password(TEST_PASSWORD)
				.email(TEST_EMAIL)
				.build();

		when(this.userService.signUp(TEST_USERNAME, TEST_PASSWORD, TEST_EMAIL)).thenReturn(true);

		BaseResp<?> response = this.userController.signUp(req);

		assertNotNull(response);
		assertEquals(200, response.getCode());
		assertNull(response.getData());
		verify(this.userService, times(1)).signUp(TEST_USERNAME, TEST_PASSWORD, TEST_EMAIL);
	}

	/**
	 * 测试注册接口用户名已存在返回 1400
	 */
	@Test
	void testSignUpFailUsernameExists() {
		UserSignUpReq req = UserSignUpReq.builder()
				.username(TEST_USERNAME)
				.password(TEST_PASSWORD)
				.email(TEST_EMAIL)
				.build();

		when(this.userService.signUp(TEST_USERNAME, TEST_PASSWORD, TEST_EMAIL)).thenReturn(false);

		BaseResp<?> response = this.userController.signUp(req);

		assertNotNull(response);
		assertEquals(ClientErrorCode.ERROR_1400.getCode(), response.getCode());
		assertEquals(ClientErrorCode.ERROR_1400.getMessage(), response.getMessage());
		assertNull(response.getData());
		verify(this.userService, times(1)).signUp(TEST_USERNAME, TEST_PASSWORD, TEST_EMAIL);
	}

	/**
	 * 测试登录接口成功返回 token
	 */
	@Test
	void testLogInSuccess() {
		UserLogInReq req = UserLogInReq.builder()
				.username(TEST_USERNAME)
				.password(TEST_PASSWORD)
				.build();

		TokenResp tokenResp = TokenResp.builder()
				.uid(TEST_UID)
				.token(TEST_TOKEN)
				.build();

		when(this.userService.logIn(TEST_USERNAME, TEST_PASSWORD)).thenReturn(tokenResp);

		BaseResp<TokenResp> response = this.userController.logIn(req);

		assertNotNull(response);
		assertEquals(200, response.getCode());
		assertNotNull(response.getData());
		assertEquals(TEST_UID, response.getData().getUid());
		assertEquals(TEST_TOKEN, response.getData().getToken());
		verify(this.userService, times(1)).logIn(TEST_USERNAME, TEST_PASSWORD);
	}

	/**
	 * 测试登录接口用户名或密码错误返回 1410
	 */
	@Test
	void testLogInFailWrongCredentials() {
		UserLogInReq req = UserLogInReq.builder()
				.username(TEST_USERNAME)
				.password(TEST_PASSWORD)
				.build();

		when(this.userService.logIn(TEST_USERNAME, TEST_PASSWORD)).thenReturn(null);

		BaseResp<TokenResp> response = this.userController.logIn(req);

		assertNotNull(response);
		assertEquals(ClientErrorCode.ERROR_1410.getCode(), response.getCode());
		assertEquals(ClientErrorCode.ERROR_1410.getMessage(), response.getMessage());
		assertNull(response.getData());
		verify(this.userService, times(1)).logIn(TEST_USERNAME, TEST_PASSWORD);
	}

	/**
	 * 测试多个用户注册成功
	 */
	@Test
	void testSignUpWithDifferentCredentials() {
		UserSignUpReq req1 = UserSignUpReq.builder()
				.username("user1")
				.password("password1")
				.email("user1@example.com")
				.build();

		UserSignUpReq req2 = UserSignUpReq.builder()
				.username("user2")
				.password("password2")
				.email("user2@example.com")
				.build();

		when(this.userService.signUp("user1", "password1", "user1@example.com")).thenReturn(true);
		when(this.userService.signUp("user2", "password2", "user2@example.com")).thenReturn(true);

		BaseResp<?> response1 = this.userController.signUp(req1);
		BaseResp<?> response2 = this.userController.signUp(req2);

		assertEquals(200, response1.getCode());
		assertEquals(200, response2.getCode());
		verify(this.userService, times(1)).signUp("user1", "password1", "user1@example.com");
		verify(this.userService, times(1)).signUp("user2", "password2", "user2@example.com");
	}

	/**
	 * 测试多个用户登录成功
	 */
	@Test
	void testLogInWithDifferentUsers() {
		UserLogInReq req1 = UserLogInReq.builder()
				.username("user1")
				.password("password1")
				.build();

		UserLogInReq req2 = UserLogInReq.builder()
				.username("user2")
				.password("password2")
				.build();

		TokenResp token1 = TokenResp.builder()
				.uid("uid1")
				.token("token1")
				.build();

		TokenResp token2 = TokenResp.builder()
				.uid("uid2")
				.token("token2")
				.build();

		when(this.userService.logIn("user1", "password1")).thenReturn(token1);
		when(this.userService.logIn("user2", "password2")).thenReturn(token2);

		BaseResp<TokenResp> response1 = this.userController.logIn(req1);
		BaseResp<TokenResp> response2 = this.userController.logIn(req2);

		assertEquals(200, response1.getCode());
		assertNotNull(response1.getData());
		assertEquals("uid1", response1.getData().getUid());
		assertEquals(200, response2.getCode());
		assertNotNull(response2.getData());
		assertEquals("uid2", response2.getData().getUid());
	}
}
