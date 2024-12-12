package ru.az.sandbox.security.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;

import ru.az.sandbox.security.model.EntityStatus;
import ru.az.sandbox.security.model.User;

public interface UserRepoV1 extends EntityRepoTemplateV1<User>{
	
	@EntityGraph("User.withRoles")
	Optional<User> findById(Long id);
	
	@EntityGraph("User.withRoles")
	Optional<User> findByIdAndStatus(Long id, EntityStatus status);
	
	@EntityGraph("User.withRoles")
	Optional<User> findByUsername(String username);
	
	@EntityGraph("User.withRoles")
	Optional<User> findByUsernameAndStatus(String username, EntityStatus status);
	
	@EntityGraph("User.withRoles")
	Optional<User> findByEmail(String email);
	
	List<User> findAllByUsernameContainsIgnoringCase(String username);
	List<User> findAllByUsernameContainsIgnoringCaseAndStatus(String username, EntityStatus status);
	
	List<User> findAllByLastNameStartsWithIgnoringCaseAndFirstNameStartsWithIgnoringCaseAndMiddleNameStartsWithIgnoringCase(
			String lastName, String firstName, String middleName);
	List<User> findAllByLastNameStartsWithIgnoringCaseAndFirstNameStartsWithIgnoringCaseAndMiddleNameStartsWithIgnoringCaseAndStatus(
			String lastName, String firstName, String middleName, EntityStatus status);

	Page<User> findAllByUsernameContainsIgnoringCase(
			String username, Pageable pageable);
	Page<User> findAllByUsernameContainsIgnoringCaseAndStatus(
			String username, EntityStatus status, Pageable pageable);

	Page<User> findAllByLastNameStartsWithIgnoringCaseAndFirstNameStartsWithIgnoringCaseAndMiddleNameStartsWithIgnoringCase(
			String lastName, String firstName, String middleName, Pageable pageable);
	Page<User> findAllByLastNameStartsWithIgnoringCaseAndFirstNameStartsWithIgnoringCaseAndMiddleNameStartsWithIgnoringCaseAndStatus(
			String lastName, String firstName, String middleName, EntityStatus status, Pageable pageable);
	
}
