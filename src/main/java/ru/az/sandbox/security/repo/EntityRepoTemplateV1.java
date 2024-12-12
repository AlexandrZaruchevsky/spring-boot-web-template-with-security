package ru.az.sandbox.security.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import ru.az.sandbox.security.model.EntityStatus;
import ru.az.sandbox.security.model.EntityTemplate;

@NoRepositoryBean
public interface EntityRepoTemplateV1<T extends EntityTemplate> extends ListCrudRepository<T, Long> {

	List<T> findAllByStatus(EntityStatus status);
	Page<T> findAll(Pageable pageable);
	Page<T> findAllByStatus(EntityStatus status, Pageable pageable);
	Optional<T> findByIdAndStatus(Long id, EntityStatus status);
	long countByStatus(EntityStatus status);
	
}
