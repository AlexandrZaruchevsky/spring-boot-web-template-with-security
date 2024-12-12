package ru.az.sandbox.security.services.admin.impl;

import static ru.az.sandbox.security.model.EntityStatus.*;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import ru.az.sandbox.security.dto.PageRequestDtoV2;
import ru.az.sandbox.security.dto.RoleDtoV1;
import ru.az.sandbox.security.model.EntityStatus;
import ru.az.sandbox.security.model.Permission;
import ru.az.sandbox.security.model.Role;
import ru.az.sandbox.security.model.exceptions.ZException;
import ru.az.sandbox.security.model.exceptions.ZNotFoundEntityException;
import ru.az.sandbox.security.repo.RoleRepoV1;
import ru.az.sandbox.security.services.admin.AdminRoleServiceV1;

@Service("adminRoleService")
@Primary
@RequiredArgsConstructor
public class AdminRoleServiceV1Impl implements AdminRoleServiceV1 {

	private final RoleRepoV1 roleRepo;
	
	@Override
	public RoleDtoV1 create(RoleDtoV1 dto) throws ZException {
		Role role = Role.builder().build();
		fromDto(dto, role);
		return RoleDtoV1.create(roleRepo.save(role));
	}

	@Override
	@Transactional
	public RoleDtoV1 update(RoleDtoV1 dto) throws ZException {
		Role role = _findByIdAndStatus(dto.id(), ACTIVE);
		fromDto(dto, role);
		return RoleDtoV1.create(roleRepo.save(role));
	}

	@Override
	@Transactional
	public RoleDtoV1 deleteById(Long id) throws ZException {
		Role role = _findByIdAndStatus(id, ACTIVE);
		role.setStatus(DELETED);
		return RoleDtoV1.create(roleRepo.save(role));
	}

	@Override
	public RoleDtoV1 findById(Long id) throws ZException {
		return RoleDtoV1.create(_findById(id));
	}

	@Override
	@Transactional
	public void changeEntityStatus(Long id, String status) throws ZException {
		EntityStatus st = EntityStatus.getStatus(status);
		if (st.equals(UNDEFINED)) {
			throw new ZNotFoundEntityException(
					String.format("Entity status: %s not exists", status)
			);
		}
		Role role = _findById(id);
		role.setStatus(st);
	}

	@Override
	public List<RoleDtoV1> findAll(PageRequestDtoV2 request) {
		List<Role> roleList = List.of();
		EntityStatus status = EntityStatus.getStatus(request.statusEntity());
		if(request.isAllEntity()) {
			roleList = roleRepo.findAllByRoleNameContainsAllIgnoreCase(request.filter());
		}else {
			status = UNDEFINED.equals(status) ? ACTIVE : status;
			roleList = roleRepo.findAllByRoleNameContainsAllIgnoreCaseAndStatus(request.filter(), status);
		}
		return roleList.stream()
				.sorted(Comparator.comparing(Role::getRoleName))
				.map(RoleDtoV1::create).toList();
	}

	@Override
	public Page<RoleDtoV1> findAllAsPage(PageRequestDtoV2 request) {
		PageRequest pageRequest = PageRequest.of(request.pageNumber(), request.pageSize(), Sort.by("roleName"));
		EntityStatus status = EntityStatus.getStatus(request.statusEntity());
		Page<Role> page = Page.empty();
		if(request.isAllEntity()) {
			page = roleRepo.findAllByRoleNameContainsAllIgnoreCase(request.filter(), pageRequest);
		}else {
			page = roleRepo.findAllByRoleNameContainsAllIgnoreCaseAndStatus(request.filter(), status, pageRequest);
		}
		return page.map(RoleDtoV1::create);
	}

	@Override
	public long count() {
		return roleRepo.count();
	}

	@Override
	public long count(EntityStatus status) {
		return roleRepo.countByStatus(status);
	}

	private void fromDto(RoleDtoV1 dto, Role role) {
		Set<Permission> perms = dto.permissions().stream()
									.map(Permission::getPermission)
									.collect(Collectors.toSet());
		if (!perms.contains(Permission.GUEST)) {
			perms.add(Permission.GUEST);
		}
		role.setRoleName(dto.roleName());
		role.setPermissions(perms);
	}

	private Role _findByIdAndStatus(Long id, EntityStatus status) throws ZNotFoundEntityException {
		Role role = _findById(id);
		 if (role.getStatus().equals(status))
			 return role;
		 throw new ZNotFoundEntityException(
				 	String.format("Role with id: %s and status: %s not found", id, status.name())
				 );
	}
	
	private Role _findById(Long id) throws ZNotFoundEntityException {
		return roleRepo.findById(id)
				.orElseThrow(() -> new ZNotFoundEntityException(
						String.format("Role with id: %s not found", id)
				));
	}
	
}
