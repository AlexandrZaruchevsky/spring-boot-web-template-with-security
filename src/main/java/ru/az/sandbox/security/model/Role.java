package ru.az.sandbox.security.model;

import static jakarta.persistence.FetchType.EAGER;
import static jakarta.persistence.FetchType.LAZY;

import java.util.List;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedEntityGraphs;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true, exclude = { "users", "permissions"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(schema = "security", name = "roles")
@NamedEntityGraphs({
	@NamedEntityGraph(
			name = "Role.withPermissions",
			attributeNodes = @NamedAttributeNode("permissions")
	)
})
public class Role extends EntityTemplate {

	@Column(name = "role_name")
	private String roleName;
	
	@ElementCollection(targetClass = Permission.class, fetch = EAGER)
	@JoinTable(schema = "security", name = "permissions", joinColumns = @JoinColumn(name = "role_id"))
	@Column(name = "permission")
	@Enumerated(EnumType.STRING)
	private Set<Permission> permissions;
	
	@ManyToMany(fetch = LAZY)
	@JoinTable(
			schema = "security", 
			name = "user_role",
			joinColumns = @JoinColumn(name = "role_id"),
			inverseJoinColumns = @JoinColumn(name = "user_id")
	)
	private List<User> users;
	
}
