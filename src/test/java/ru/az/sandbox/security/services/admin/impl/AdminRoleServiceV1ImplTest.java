package ru.az.sandbox.security.services.admin.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.az.sandbox.security.model.EntityStatus.ACTIVE;
import static ru.az.sandbox.security.model.EntityStatus.DELETED;
import static ru.az.sandbox.security.model.EntityStatus.NOT_ACTIVE;

import java.util.List;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

import ru.az.sandbox.security.dto.PageRequestDtoV2;
import ru.az.sandbox.security.dto.RoleDtoV1;
import ru.az.sandbox.security.model.exceptions.ZException;

@SpringBootTest
@Sql(
		scripts = "classpath:sql/admin-role-service/clean-tables.sql",
		executionPhase = ExecutionPhase.AFTER_TEST_CLASS
)
@TestMethodOrder(OrderAnnotation.class)
class AdminRoleServiceV1ImplTest {

	@Autowired
	private AdminRoleServiceV1Impl adminRoleService;	
	
	@Test
	@Order(1)
	void testCreate() {
		List<RoleDtoV1> roles = List.of(
				new RoleDtoV1(null, "ROLE_GUEST", List.of("guest"), null),
				new RoleDtoV1(null, "ROLE_USER_READ", List.of("user_read"), null),
				new RoleDtoV1(null, "ROLE_USER_FULL", List.of("user_read", "user_write"), null)
		);
		roles.forEach(r->{
			assertDoesNotThrow(() -> {
				RoleDtoV1 roleCreated = adminRoleService.create(r);
				assertNotNull(roleCreated);
			});
		});
		// Duplicate role_name
		assertDoesNotThrow(() -> {
			try {
				adminRoleService.create(new RoleDtoV1(null, "ROLE_GUEST", List.of("guest"), null));
			} catch (DataIntegrityViolationException e) {
				assertEquals(true, e.getCause().getMessage().contains("duplicate key value"));
			}
		});
	}

	@Test
	@Order(2)
	void testUpdate() {
		assertDoesNotThrow(() -> {
			RoleDtoV1 roleUpdated = adminRoleService.update(new RoleDtoV1(1l, "ROLE_GUEST1", List.of("guest"), null));
			assertEquals("ROLE_GUEST1", roleUpdated.roleName());
		});
	}

	@Test
	@Order(3)
	void testDeleteById() {
		assertDoesNotThrow(() -> {
			RoleDtoV1 roleDeleted = adminRoleService.deleteById(1l);
			assertEquals("DELETED", roleDeleted.status());
		});
		assertThrows(ZException.class, () -> {
			adminRoleService.deleteById(1l);
		});
	}

	@Test
	@Order(4)
	void testFindById() {
		assertDoesNotThrow(()->{
			RoleDtoV1 role = adminRoleService.findById(1l);
			assertNotNull(role);
			assertEquals("DELETED", role.status());
			assertEquals("ACTIVE", adminRoleService.findById(2l).status());
		});
		assertThrows(ZException.class, () -> {
			adminRoleService.findById(Long.MAX_VALUE);
		});
	}

	@Test
	@Order(5)
	void testFindAll() {
		PageRequestDtoV2 request = new PageRequestDtoV2(0, 10, null, null, null, true);
		assertEquals(3, adminRoleService.findAll(request).size());
		request = new PageRequestDtoV2(0, 10, null, null, null, false);
		assertEquals(2, adminRoleService.findAll(request).size());
		request = new PageRequestDtoV2(0, 10, null, null, "DELETED", false);
		assertEquals(1, adminRoleService.findAll(request).size());
		request = new PageRequestDtoV2(0, 10, null, null, "ACTIVE", false);
		assertEquals(2, adminRoleService.findAll(request).size());
		request = new PageRequestDtoV2(0, 10, "guest", null, "ACTIVE", false);
		assertEquals(0, adminRoleService.findAll(request).size());
		request = new PageRequestDtoV2(0, 10, "guest", null, "DELETED", false);
		assertEquals(1, adminRoleService.findAll(request).size());
		request = new PageRequestDtoV2(0, 10, "guest", null, "ACTIVE", true);
		assertEquals(1, adminRoleService.findAll(request).size());
	}

	@Test
	@Order(6)
	void testFindAllAsPage() {
		PageRequestDtoV2 request = new PageRequestDtoV2(0, 10, null, null, null, true);
		assertEquals(3, adminRoleService.findAllAsPage(request).getContent().size());
		request = new PageRequestDtoV2(0, 10, null, null, null, false);
		assertEquals(2, adminRoleService.findAllAsPage(request).getContent().size());
		request = new PageRequestDtoV2(0, 10, null, null, "DELETED", false);
		assertEquals(1, adminRoleService.findAllAsPage(request).getContent().size());
		request = new PageRequestDtoV2(0, 10, null, null, "ACTIVE", false);
		assertEquals(2, adminRoleService.findAllAsPage(request).getContent().size());
		request = new PageRequestDtoV2(0, 10, "guest", null, "ACTIVE", false);
		assertEquals(0, adminRoleService.findAllAsPage(request).getContent().size());
		request = new PageRequestDtoV2(0, 10, "guest", null, "DELETED", false);
		assertEquals(1, adminRoleService.findAllAsPage(request).getContent().size());
		request = new PageRequestDtoV2(0, 10, "guest", null, "ACTIVE", true);
		assertEquals(1, adminRoleService.findAllAsPage(request).getContent().size());
	}

	@Test
	@Order(7)
	void testCount() {
		assertEquals(3, adminRoleService.count());
	}

	@Test
	@Order(8)
	void testCountEntityStatus() {
		assertEquals(2, adminRoleService.count(ACTIVE));
		assertEquals(1, adminRoleService.count(DELETED));
		assertEquals(0, adminRoleService.count(NOT_ACTIVE));
	}

}
