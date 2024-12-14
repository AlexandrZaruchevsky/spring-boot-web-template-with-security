package ru.az.sandbox.security.model;

import java.util.List;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedEntityGraphs;
import jakarta.persistence.NamedSubgraph;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true, exclude = "roles")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(schema = "security", name = "users")
@NamedEntityGraphs({
	@NamedEntityGraph(
			name = "User.withRoles",
			attributeNodes = @NamedAttributeNode(value = "roles", subgraph = "role.permission"),
			subgraphs = @NamedSubgraph(name = "role.permission", attributeNodes = @NamedAttributeNode("permissions"))
	)
})
public class User extends EntityTemplate {
	
	@Column(name = "username")
	private String username;
	@Column(name = "email")
	private String email;
	@JsonIgnore
	@Column(name = "password")
	private String password;
	@Column(name = "last_name")
	private String lastName;
	@Column(name = "first_name")
	private String firstName;
	@Column(name = "middleName")
	private String middleName;

	
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
			schema = "security",
			name = "user_role",
			joinColumns = @JoinColumn(name = "user_id"),
			inverseJoinColumns = @JoinColumn(name = "role_id")
	)
	private List<Role> roles;
	
	public String getFio() {
		return new StringBuilder(StringUtils.hasLength(lastName) ? (lastName + " ") :"" )
				.append(StringUtils.hasLength(firstName) ? (firstName + " ") : "")
				.append(StringUtils.hasLength(middleName) ? middleName : "")
				.toString();
	}
	
}
