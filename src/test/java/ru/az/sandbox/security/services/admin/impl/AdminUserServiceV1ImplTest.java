package ru.az.sandbox.security.services.admin.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

import ru.az.sandbox.security.dto.PageRequestDtoV2;
import ru.az.sandbox.security.dto.RoleDtoV1;
import ru.az.sandbox.security.dto.UserCreateOrUpdateDtoV1;
import ru.az.sandbox.security.dto.UserResponseDtoV1;
import ru.az.sandbox.security.dto.security.ChangePasswordRequestV1;
import ru.az.sandbox.security.model.EntityStatus;
import ru.az.sandbox.security.model.exceptions.ZException;

@SpringBootTest
@Sql(
		scripts = "classpath:sql/admin-user-service/clean-tables.sql",
		executionPhase = ExecutionPhase.AFTER_TEST_CLASS
)
@TestMethodOrder(OrderAnnotation.class)
class AdminUserServiceV1ImplTest {

	@Autowired
	private AdminUserServiceV1Impl adminUserService;
	@Autowired
	private AdminRoleServiceV1Impl adminRoleService;
	
	@Value("${app.security.guest-role}")
	private String roleGuest;
	
	@Test
	@Order(1)
	void testCreate() {
		assertDoesNotThrow(() ->{
			RoleDtoV1 roleGuest = adminRoleService.create(new RoleDtoV1(null, this.roleGuest, List.of(), null));
			RoleDtoV1 roleUser = adminRoleService.create(new RoleDtoV1(null, "ROLE_USER", List.of("user:read", "user:write"), null));
			RoleDtoV1 roleAdmin = adminRoleService.create(new RoleDtoV1(null, "ROLE_ADMIN", List.of("admin:read", "admin:write"), null));
			List.of(
					new UserCreateOrUpdateDtoV1(null, "guest", "guest@mail", "P@ssw0rd", "Guest", "Admin", "User", List.of(roleGuest.id())),
					new UserCreateOrUpdateDtoV1(null, "admin", "admin@mail", "P@ssw0rd", "Admin", "Guest", "User", List.of(roleGuest.id(), roleAdmin.id())),
					new UserCreateOrUpdateDtoV1(null, "user", "user@mail", "P@ssw0rd", "User", "Admin", "Guest", List.of(roleUser.id()))
			).forEach(u -> {
				assertDoesNotThrow(()->{
					UserResponseDtoV1 userCreated = adminUserService.create(u);
					assertNotNull(userCreated);
					assertEquals("ACTIVE", userCreated.status());
				});
			});
			assertDoesNotThrow(() -> {
				try {
					adminUserService.create(new UserCreateOrUpdateDtoV1(null, "guest", "guest@mail", "P@ssw0rd", "Guest", "Admin", "User", List.of(roleGuest.id())));
				}catch (Exception e) {
					assertEquals(true, e.getCause().getMessage().contains("duplicate key value"));
				}
			});
		});
	}

	@Test
	@Order(2)
	void testUpdate() {
		assertDoesNotThrow(() -> {
			RoleDtoV1 roleDtoV1 = adminRoleService.findAll(new PageRequestDtoV2(0, 10, "GUEST", null, "ACTIVE", null)).get(0);
			UserCreateOrUpdateDtoV1 us = new UserCreateOrUpdateDtoV1(1l, "GuestNew", "guest@mail", "P@ssw0rd", "Guest", "Admin", "User", List.of(roleDtoV1.id()));
			UserResponseDtoV1 userUpdated = adminUserService.update(us);
			assertEquals("GuestNew", userUpdated.username());
		});
	}

	@Test
	@Order(3)
	void testDeleteById() {
		assertDoesNotThrow(() -> {
			UserResponseDtoV1 userDeleted = adminUserService.deleteById(1l);
			assertEquals("DELETED", userDeleted.status());
		});
		assertThrows(ZException.class, () -> adminUserService.deleteById(1l));
		assertThrows(ZException.class, () -> adminUserService.deleteById(Long.MAX_VALUE));
	}

	@Test
	@Order(4)
	void testFindById() {
		assertDoesNotThrow(() -> {
			UserResponseDtoV1 user = adminUserService.findById(1l);
			assertEquals("DELETED", user.status());
			user = adminUserService.findById(2l);
			assertEquals("ACTIVE", user.status());
		});
		assertThrows(ZException.class, () -> adminUserService.findById(Long.MAX_VALUE));
	}

	@Test
	@Order(5)
	void testChangeEntityStatus() {
		assertDoesNotThrow(() ->{
			adminUserService.changeEntityStatus(1l, "NOT_ACTIVE");
			assertEquals("NOT_ACTIVE", adminUserService.findById(1l).status());
		});
		assertThrows(ZException.class, () -> adminUserService.changeEntityStatus(Long.MAX_VALUE, "NOT_ACTIVE"));
	}

	@Test
	@Order(6)
	void testChangePassword() {
		assertDoesNotThrow(() -> {
			adminUserService.changePassword(new ChangePasswordRequestV1("admin", "P@ssw0rd", "P@ssw0rd1"));
		});
		assertThrows(ZException.class, () -> {
			adminUserService.changePassword(new ChangePasswordRequestV1("admin", "P@ssw0rd", "P@ssw0rd1"));
		});
	}

	@Test
	@Order(7)
	void testFindAll() {
		List<UserResponseDtoV1> list = adminUserService.findAll(new PageRequestDtoV2(null, null, null, null, null, null));
		assertEquals(2, list.size());
		list = adminUserService.findAll(new PageRequestDtoV2(null, null, null, null, null, true));
		assertEquals(3, list.size());
		list = adminUserService.findAll(new PageRequestDtoV2(null, null, null, null, EntityStatus.NOT_ACTIVE.name(), null));
		assertEquals(1, list.size());
		list = adminUserService.findAll(new PageRequestDtoV2(null, null, "gues", "username", EntityStatus.ACTIVE.name(), null));
		assertEquals(0, list.size());
		list = adminUserService.findAll(new PageRequestDtoV2(null, null, "gues", "fio", EntityStatus.ACTIVE.name(), true));
		assertEquals(1, list.size());
	}

	@Test
	@Order(8)
	void testFindAllAsPage() {
		Page<UserResponseDtoV1> page = adminUserService.findAllAsPage(new PageRequestDtoV2(null, null, null, null, null, null));
		assertEquals(2, page.getContent().size());
		page = adminUserService.findAllAsPage(new PageRequestDtoV2(null, null, null, null, null, true));
		assertEquals(3, page.getContent().size());
		page = adminUserService.findAllAsPage(new PageRequestDtoV2(null, null, null, null, EntityStatus.NOT_ACTIVE.name(), null));
		assertEquals(1, page.getContent().size());
		page = adminUserService.findAllAsPage(new PageRequestDtoV2(null, null, "gues", "username", EntityStatus.ACTIVE.name(), null));
		assertEquals(0, page.getContent().size());
		page = adminUserService.findAllAsPage(new PageRequestDtoV2(null, null, "gues", "fio", EntityStatus.ACTIVE.name(), true));
		assertEquals(1, page.getContent().size());
	}

	@Test
	@Order(9)
	void testCount() {
		assertEquals(3, adminUserService.count());
	}

	@Test
	@Order(10)
	void testCountEntityStatus() {
		assertEquals(2, adminUserService.count(EntityStatus.ACTIVE));
		assertEquals(1, adminUserService.count(EntityStatus.NOT_ACTIVE));
	}

}
