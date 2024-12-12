create table if not exists security.users(
	id bigserial not null primary key,
	created timestamp not null default now(),
	updated timestamp,
	status varchar(32) not null default 'ACTIVE' check (status in ('ACTIVE', 'NOT_ACTIVE', 'DELETED')),
	username varchar(128) not null unique,
	email varchar(256) not null unique,
	password varchar(1024) not null,
	last_name varchar(64),
	first_name varchar(64),
	middle_name varchar(64)
);

create index idx_security_users_fio on security.users(last_name, first_name, middle_name);

create table if not exists security.roles(
	id bigserial not null primary key,
	created timestamp not null default now(),
	updated timestamp,
	status varchar(32) not null default 'ACTIVE' check (status in ('ACTIVE', 'NOT_ACTIVE', 'DELETED')),
	role_name varchar(64) not null
);

create unique index unq_security_roles_role_name on security.roles(role_name, status) where (status = 'ACTIVE');

create table if not exists security.user_role(
	user_id bigint not null,
	role_id bigint not null,
	primary key(user_id, role_id),
	constraint fk_security_user_role_user_id foreign key(user_id)
		references security.users(id),
	constraint fj_security_user_role_role_id foreign key(role_id)
		references security.roles(id)
);

create table if not exists security.permissions(
	role_id bigint not null,
	permission varchar(64),
	primary key (role_id, permission),
	constraint fk_security_permissions_role_id foreign key (role_id)
		references security.roles(id)
);