package ru.az.sandbox.security.services;

import ru.az.sandbox.security.dto.UserRegistrationDtoV1;
import ru.az.sandbox.security.dto.UserResponseDtoV1;
import ru.az.sandbox.security.dto.UserUpdateDtoV1;
import ru.az.sandbox.security.dto.security.ChangePasswordRequestV1;
import ru.az.sandbox.security.model.exceptions.ZException;

public interface UserServiceV1  {
	
	UserResponseDtoV1 registration(UserRegistrationDtoV1 registration) throws ZException;
	UserResponseDtoV1 update(UserUpdateDtoV1 userUpdateDto) throws ZException;
	UserResponseDtoV1 findById(Long id) throws ZException;
	void changePassword(ChangePasswordRequestV1 changePasswordRequest) throws ZException;
	
}
