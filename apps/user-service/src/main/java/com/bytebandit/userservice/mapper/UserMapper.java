package com.bytebandit.userservice.mapper;

import com.bytebandit.userservice.dto.UserRegistrationResponse;
import com.bytebandit.userservice.model.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserRegistrationResponse toUserRegistrationResponse(UserEntity userEntity);
}
