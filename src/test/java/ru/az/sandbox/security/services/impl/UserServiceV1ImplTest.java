package ru.az.sandbox.security.services.impl;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

import ru.az.sandbox.security.dto.UserRegistrationDtoV1;
import ru.az.sandbox.security.dto.UserResponseDtoV1;
import ru.az.sandbox.security.dto.UserUpdateDtoV1;
import ru.az.sandbox.security.dto.security.ChangePasswordRequestV1;
import ru.az.sandbox.security.model.exceptions.ZException;
import ru.az.sandbox.security.model.exceptions.ZNotFoundEntityException;
import ru.az.sandbox.security.services.UserServiceV1;

@SpringBootTest
@Sql(
		scripts = "classpath:sql/user-service/clean-users.sql",
		executionPhase = ExecutionPhase.AFTER_TEST_CLASS
)
@Sql(
		scripts = "classpath:sql/user-service/add-role.sql",
		executionPhase = ExecutionPhase.BEFORE_TEST_CLASS
)
@TestMethodOrder(OrderAnnotation.class)
class UserServiceV1ImplTest {
	
	@Autowired
	private UserServiceV1 userService;

	@Test
	@Order(1)
	void testRegistration() throws ZException {
		UserRegistrationDtoV1 userReg = new UserRegistrationDtoV1("user", "user@mail", "P@ssw0rd");
		UserResponseDtoV1 userCreated = userService.registration(userReg);
		assertEquals(1l, userCreated.id());
		assertEquals("user", userCreated.username());
		assertEquals("user@mail", userCreated.email());
	}

	@Test
	@Order(2)
	void testRegistration_throw_duplicate() throws ZException {
		UserRegistrationDtoV1 userReg = new UserRegistrationDtoV1("user", "user@mail", "P@ssw0rd");
		assertThrows(DataIntegrityViolationException.class, () -> userService.registration(userReg));
		try {
			userService.registration(userReg);
		} catch (DataIntegrityViolationException e) {
			assertEquals(true, e.getCause().getMessage().contains("duplicate key value"));
		} 
	}

	@Test
	@Order(3)
	void testUpdate() throws ZException {
		UserUpdateDtoV1 user = new UserUpdateDtoV1(1l, "user1@m", "Lastname", "Firstname", "Middlename");
		UserResponseDtoV1 userUpdated = userService.update(user);
		assertEquals(user.lastName(), userUpdated.lastName());
	}

	@Test
	@Order(4)
	void testFindById() throws ZException {
		assertNotNull(userService.findById(1l));
		assertThrows(ZNotFoundEntityException.class, () -> userService.findById(Long.MAX_VALUE));
	}

	@Test
	@Order(5)
	void testChangePassword() {
		assertDoesNotThrow(() -> userService.changePassword(new ChangePasswordRequestV1("user", "P@ssw0rd", "P@ssw0rd1")));
		assertThrows(ZException.class, () -> userService.changePassword(new ChangePasswordRequestV1("user", "P@ssw0rd", "P@ssw0rd1")));
	}

}
