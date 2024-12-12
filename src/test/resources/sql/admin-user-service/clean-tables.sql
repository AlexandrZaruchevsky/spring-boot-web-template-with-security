delete from security.permissions;
delete from security.user_role;
delete from security.roles;
delete from security.users;

alter sequence security.roles_id_seq restart 1; 
alter sequence security.users_id_seq restart 1;