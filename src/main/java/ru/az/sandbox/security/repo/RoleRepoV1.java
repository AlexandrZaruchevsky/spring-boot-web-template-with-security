package ru.az.sandbox.security.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import ru.az.sandbox.security.model.EntityStatus;
import ru.az.sandbox.security.model.Role;

public interface RoleRepoV1 extends EntityRepoTemplateV1<Role>{

	Optional<Role> findByRoleNameAndStatus(String roleName, EntityStatus status);
	
	List<Role> findAllByRoleNameContainsAllIgnoreCase(String roleName);
	List<Role> findAllByRoleNameContainsAllIgnoreCaseAndStatus(String roleName, EntityStatus status);
	
	Page<Role> findAllByRoleNameContainsAllIgnoreCase(String roleName, Pageable pageable);
	Page<Role> findAllByRoleNameContainsAllIgnoreCaseAndStatus(String roleName, EntityStatus status, Pageable pageable);
	
	Optional<Role> findByIdAndStatus(Long id, EntityStatus status);
	
}
