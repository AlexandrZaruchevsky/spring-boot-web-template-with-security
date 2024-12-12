package ru.az.sandbox.security.model;

import static ru.az.sandbox.security.model.EntityStatus.ACTIVE;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "id")
@MappedSuperclass
public class EntityTemplate {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@Column(name = "created")
	private Timestamp created;
	
	@Column(name = "updated")
	private Timestamp updated;
	
	@Column(name = "status")
	@Enumerated(EnumType.STRING)
	private EntityStatus status;
	
	@PrePersist
	private void created() {
		this.created = Timestamp.from(Instant.now());
		this.status = Optional.ofNullable(this.status).orElse(ACTIVE);
	}

	@PreUpdate
	private void updated() {
		this.updated = Timestamp.from(Instant.now());
	}
	
}
