package ru.az.sandbox.security.services.admin;

import ru.az.sandbox.security.dto.PageRequestDtoV2;
import ru.az.sandbox.security.dto.UserCreateOrUpdateDtoV1;
import ru.az.sandbox.security.dto.UserResponseDtoV1;
import ru.az.sandbox.security.dto.security.ChangePasswordRequestV1;
import ru.az.sandbox.security.model.exceptions.ZException;
import ru.az.sandbox.security.services.CrudServiceV1;

public interface AdminUserServiceV1 
	extends CrudServiceV1<UserCreateOrUpdateDtoV1, UserResponseDtoV1, PageRequestDtoV2>{

	void changePassword(ChangePasswordRequestV1 changePasswordRequest) throws ZException;
	
}
