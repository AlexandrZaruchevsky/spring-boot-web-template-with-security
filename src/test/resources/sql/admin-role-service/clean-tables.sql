delete from security.permissions;
delete from security.roles;

alter sequence security.roles_id_seq restart 1; 