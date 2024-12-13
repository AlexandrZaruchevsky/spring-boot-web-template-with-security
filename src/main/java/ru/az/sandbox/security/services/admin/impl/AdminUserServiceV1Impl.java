package ru.az.sandbox.security.services.admin.impl;

import static ru.az.sandbox.security.model.EntityStatus.ACTIVE;
import static ru.az.sandbox.security.model.EntityStatus.UNDEFINED;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import ru.az.sandbox.security.dto.PageRequestDtoV2;
import ru.az.sandbox.security.dto.UserCreateOrUpdateDtoV1;
import ru.az.sandbox.security.dto.UserResponseDtoV1;
import ru.az.sandbox.security.dto.security.ChangePasswordRequestV1;
import ru.az.sandbox.security.model.EntityStatus;
import ru.az.sandbox.security.model.Role;
import ru.az.sandbox.security.model.User;
import ru.az.sandbox.security.model.exceptions.ZException;
import ru.az.sandbox.security.model.exceptions.ZNotFoundEntityException;
import ru.az.sandbox.security.repo.RoleRepoV1;
import ru.az.sandbox.security.repo.UserRepoV1;
import ru.az.sandbox.security.services.admin.AdminUserServiceV1;
import ru.az.sandbox.security.utils.StrUtil;

@Service("adminUserServiceV1Impl")
@Primary
@RequiredArgsConstructor
public class AdminUserServiceV1Impl implements AdminUserServiceV1 {
	
	private final UserRepoV1 userRepo;
	private final RoleRepoV1 roleRepo;
	
	private final PasswordEncoder passwordEncoder;
	
	@Value("${app.security.guest-role}")
	private String roleGuest;


	@Override
	@Transactional
	@CacheEvict(cacheNames = {"users"}, allEntries = true)
	public UserResponseDtoV1 create(UserCreateOrUpdateDtoV1 dto) throws ZException {
		User user = new User();
		_fromDto(dto, user);
		user.setPassword(passwordEncoder.encode(dto.password()));
		return UserResponseDtoV1.createWithRoles(userRepo.save(user));
	}

	@Override
	@Transactional
	@CacheEvict(cacheNames = {"security-users", "users"}, allEntries = true)
	public UserResponseDtoV1 update(UserCreateOrUpdateDtoV1 dto) throws ZException {
		User user = _findByIdAndStatus(dto.id(), ACTIVE);
		_fromDto(dto, user);
		return UserResponseDtoV1.createWithRoles(user);
	}

	@Override
	@Transactional
	@CacheEvict(cacheNames = {"security-users", "users"}, allEntries = true)
	public UserResponseDtoV1 deleteById(Long id) throws ZException {
		User user = _findByIdAndStatus(id, ACTIVE);
		user.setStatus(EntityStatus.DELETED);
		return UserResponseDtoV1.createWithRoles(user);
	}

	@Override
	@Cacheable(cacheNames = "users", key = "#id")
	public UserResponseDtoV1 findById(Long id) throws ZException {
		User user = _findById(id);
		return UserResponseDtoV1.createWithRoles(user);
	}

	@Override
	@Transactional
	@CacheEvict(cacheNames = {"security-users", "users"}, allEntries = true)
	public void changeEntityStatus(Long id, String status) throws ZException {
		EntityStatus st = EntityStatus.getStatus(status);
		if (st.equals(UNDEFINED)) {
			throw new ZNotFoundEntityException(
					String.format("Entity status: %s not exists", status)
			);
		}
		_findById(id).setStatus(st);
	}

	@Override
	@Transactional
	@CacheEvict(cacheNames = {"security-users", "users"}, allEntries = true)
	public void changePassword(ChangePasswordRequestV1 changePasswordRequest) throws ZException {
		User user = userRepo.findByUsername(changePasswordRequest.username())
			.orElseThrow(() -> new ZNotFoundEntityException(
					String.format("User with username '%s' not found", changePasswordRequest.username())
			));
		if(passwordEncoder.matches(changePasswordRequest.oldPassword(), user.getPassword())) {
			user.setPassword(passwordEncoder.encode(changePasswordRequest.newPassword()));
		}else {
			throw new ZException(HttpStatus.BAD_REQUEST, "Old passwords don't match");
		}
	}

	@Override
	@Cacheable(
			cacheNames = "users", 
			key = "{#request?.filter, #request?.sortBy, #request?.statusEntity, #request?.isAllEntity}"
	)
	public List<UserResponseDtoV1> findAll(PageRequestDtoV2 request) {
		EntityStatus status = EntityStatus.getStatus(request.statusEntity());
		status = UNDEFINED.equals(status) ? ACTIVE : status;
		UserSort userSort = _sortBy(request.sortBy());
		String[] fio = StrUtil.getFioFromStr(request.filter());
		List<User> users = userSort.equals(UserSort.SORT_USERNAME) 
			? request.isAllEntity()
					? userRepo.findAllByUsernameContainsIgnoringCase(request.filter())
					: userRepo.findAllByUsernameContainsIgnoringCaseAndStatus(request.filter(), status)
			: request.isAllEntity()
					? userRepo.findAllByLastNameStartsWithIgnoringCaseAndFirstNameStartsWithIgnoringCaseAndMiddleNameStartsWithIgnoringCase(fio[0], fio[1], fio[2])
					: userRepo.findAllByLastNameStartsWithIgnoringCaseAndFirstNameStartsWithIgnoringCaseAndMiddleNameStartsWithIgnoringCaseAndStatus(fio[0], fio[1], fio[2], status);
		return users.stream()
				.sorted(_sortForList(userSort))
				.map(UserResponseDtoV1::create)
				.toList();
	}

	@Override
	@Cacheable(
			cacheNames = "users", 
			key = "{#request?.filter, #request?.sortBy, #request?.statusEntity, #request?.isAllEntity, #request?.pageNumber, #request?.pageSize}"
	)
	public Page<UserResponseDtoV1> findAllAsPage(PageRequestDtoV2 request) {
		EntityStatus status = EntityStatus.getStatus(request.statusEntity());
		status = UNDEFINED.equals(status) ? ACTIVE : status;
		UserSort userSort = _sortBy(request.sortBy());
		String[] fio = StrUtil.getFioFromStr(request.filter());
		PageRequest pageRequest = PageRequest.of(
										request.pageNumber(), 
										request.pageSize(), 
										_sortForPage(userSort));
		Page<User> users = userSort.equals(UserSort.SORT_USERNAME)
				? request.isAllEntity()
						? userRepo.findAllByUsernameContainsIgnoringCase(request.filter(), pageRequest)
						: userRepo.findAllByUsernameContainsIgnoringCaseAndStatus(request.filter(), status, pageRequest)
				: request.isAllEntity()
						? userRepo.findAllByLastNameStartsWithIgnoringCaseAndFirstNameStartsWithIgnoringCaseAndMiddleNameStartsWithIgnoringCase(fio[0], fio[1], fio[2], pageRequest)
						: userRepo.findAllByLastNameStartsWithIgnoringCaseAndFirstNameStartsWithIgnoringCaseAndMiddleNameStartsWithIgnoringCaseAndStatus(fio[0], fio[1], fio[2], status, pageRequest);
		return users.map(UserResponseDtoV1::create);
	}

	@Override
	public long count() {
		return userRepo.count();
	}

	@Override
	public long count(EntityStatus status) {
		return userRepo.countByStatus(status);
	}

	@Cacheable(cacheNames = "security-roles", key = "role-guest")
	private Role _findRoleGuest() throws ZNotFoundEntityException {
		return roleRepo.findByRoleNameAndStatus(roleGuest, ACTIVE)
					.orElseThrow(() -> new ZNotFoundEntityException(
							String.format("Role [ %s ] not found", roleGuest)
					));
	}
	
	
	private User _findById(Long id) throws ZNotFoundEntityException {
		return userRepo.findById(id)
				.orElseThrow(() -> new ZNotFoundEntityException(
						String.format("User with id: %s not found", id)
				));
				
	}
	
	private User _findByIdAndStatus(Long id, EntityStatus status) throws ZNotFoundEntityException {
		User user = _findById(id);
		if (user.getStatus().equals(status))
			return user;
		 throw new ZNotFoundEntityException(
				 	String.format("User with id: %s and status: %s not found", id, status.name())
				 );
	}

	private void _fromDto(UserCreateOrUpdateDtoV1 dto, User user) throws ZNotFoundEntityException {
		user.setUsername(dto.username());
		user.setEmail(dto.email());
		user.setLastName(dto.lastName());
		user.setFirstName(dto.firstName());
		user.setMiddleName(dto.middleName());
		List<Role> roles = 
				new ArrayList<>(
					roleRepo.findAllById(dto.roles())
						.stream()
						.filter(r -> ACTIVE.equals(r.getStatus()))
						.toList()
				); 
		if (
			roles.stream()
				.filter(role -> roleGuest.equalsIgnoreCase(role.getRoleName()))
				.findFirst()
				.isEmpty()
		) {
			roles.add(_findRoleGuest());
		}
		user.setRoles(roles);
	}

	
	private Comparator<User> _sortForList(UserSort userSort) {
		return userSort.equals(UserSort.SORT_FIO) 
					? Comparator
						.comparing(User::getLastName)
						.thenComparing(User::getFirstName)
						.thenComparing(User::getMiddleName)
					: Comparator.comparing(User::getUsername);
	}

	private Sort _sortForPage(UserSort userSort) {
		return userSort.equals(UserSort.SORT_FIO)
				? Sort.by("lastName", "firstName", "middleName")
				: Sort.by("username");
	}
	
	private UserSort _sortBy(String str) {
		return switch(str.toUpperCase()) {
					case "FIO" -> UserSort.SORT_FIO;
					default -> UserSort.SORT_USERNAME;
		};
	}
	
	static enum UserSort{
		SORT_FIO,
		SORT_USERNAME;
	}
	
}
