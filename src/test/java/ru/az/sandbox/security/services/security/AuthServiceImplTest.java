package ru.az.sandbox.security.services.security;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.util.StringUtils;

import ru.az.sandbox.security.dto.security.AuthReponseDtoV1;
import ru.az.sandbox.security.dto.security.AuthRequestDtoV1;
import ru.az.sandbox.security.services.InitServiceV1;

@SpringBootTest
@Sql(
		scripts = {
				"classpath:sql/user-service/clean-users.sql"
		},
		executionPhase = ExecutionPhase.AFTER_TEST_CLASS
)
@TestMethodOrder(OrderAnnotation.class)
class AuthServiceImplTest {

	@Autowired
	private AuthService authService;
	
	@Autowired
	private InitServiceV1 initService;
	
	@BeforeEach
	void initData() {
		initService.initSecurity();
	}
	
	static String accessToken;
	static String refreshToken;
	
	@Test
	@Order(1)
	void testLogin() {
		assertDoesNotThrow(() -> {
			AuthReponseDtoV1 response = authService.login(new AuthRequestDtoV1("adm", "P@ssw0rd"));
			AuthServiceImplTest.accessToken = response.accessToken();
			AuthServiceImplTest.refreshToken = response.refreshToken();
			assertNotNull(response);
		});
	}

	@Test
	@Order(2)
	void testLogin_throw_UsernameNotFoundException() {
		assertThrows(UsernameNotFoundException.class, () -> authService.login(new AuthRequestDtoV1("adm1", "P@ssw0rd")));
	}

	@Test
	@Order(3)
	void testGetAccesToken() {
		AuthReponseDtoV1 authReponse = authService.getAccesToken(AuthServiceImplTest.refreshToken);
		assertNotNull(authReponse);
		assertEquals(true, StringUtils.hasLength(authReponse.accessToken()));
	}

	@Test
	@Order(4)
	void testRefresh() {
		AuthReponseDtoV1 authReponse = authService.refresh(AuthServiceImplTest.refreshToken);
		assertNotNull(authReponse);
		assertEquals(true, StringUtils.hasLength(authReponse.accessToken()));
		assertEquals(true, StringUtils.hasLength(authReponse.refreshToken()));
	}

}
