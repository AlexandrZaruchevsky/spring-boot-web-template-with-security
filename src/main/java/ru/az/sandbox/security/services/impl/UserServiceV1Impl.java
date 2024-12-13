package ru.az.sandbox.security.services.impl;

import static ru.az.sandbox.security.model.EntityStatus.ACTIVE;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import ru.az.sandbox.security.dto.UserRegistrationDtoV1;
import ru.az.sandbox.security.dto.UserResponseDtoV1;
import ru.az.sandbox.security.dto.UserUpdateDtoV1;
import ru.az.sandbox.security.dto.security.ChangePasswordRequestV1;
import ru.az.sandbox.security.model.User;
import ru.az.sandbox.security.model.exceptions.ZException;
import ru.az.sandbox.security.model.exceptions.ZNotFoundEntityException;
import ru.az.sandbox.security.repo.RoleRepoV1;
import ru.az.sandbox.security.repo.UserRepoV1;
import ru.az.sandbox.security.services.UserServiceV1;

@Service
@RequiredArgsConstructor
public class UserServiceV1Impl implements UserServiceV1 {
	
	private final UserRepoV1 userRepo;
	private final RoleRepoV1 roleRepo;
	private final PasswordEncoder passwordEncoder;
	
	@Value("${app.security.guest-role}")
	private String roleGuest;

	@Override
	@Transactional
	@CacheEvict(cacheNames = {"users"}, allEntries = true)
	public UserResponseDtoV1 registration(UserRegistrationDtoV1 registration) throws ZException {
		return roleRepo.findByRoleNameAndStatus(this.roleGuest, ACTIVE)
			.map(role ->{
				User user = User.builder()
						.username(registration.username())
						.email(registration.email())
						.password(passwordEncoder.encode(registration.password()))
						.roles(List.of(role))
						.build();
				user.setStatus(ACTIVE);
				return userRepo.save(user);
			}).map(UserResponseDtoV1::createWithRoles)
			.orElseThrow(() -> new ZNotFoundEntityException(
					String.format("Role [%s] not found", this.roleGuest)));
	}

	@Override
	@Transactional
	@CacheEvict(cacheNames = {"security-users", "users"}, allEntries = true)
	public UserResponseDtoV1 update(UserUpdateDtoV1 userUpdateDto) throws ZException {
		User user = _findById(userUpdateDto.id());
		BeanUtils.copyProperties(userUpdateDto, user, "id");
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
	public void changePassword(ChangePasswordRequestV1 changePasswordRequest) throws ZException {
		User user = userRepo.findByUsername(changePasswordRequest.username())
			.orElseThrow(() -> new ZNotFoundEntityException(
					String.format("User with username [ %s ] not found", changePasswordRequest.username())
					));
		if(passwordEncoder.matches(changePasswordRequest.oldPassword(), user.getPassword())) {
			user.setPassword(passwordEncoder.encode(changePasswordRequest.newPassword()));
		}else {
			throw new ZException(HttpStatus.BAD_REQUEST, "Old passwords don't match");
		}
	}
	
	private User _findById(Long id) throws ZException{
		return userRepo.findByIdAndStatus(id, ACTIVE)
				.orElseThrow(() -> new ZNotFoundEntityException(
						String.format("User with id [ %s ] not found", id)
				));
	}

}
