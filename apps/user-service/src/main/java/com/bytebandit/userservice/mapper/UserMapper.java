package com.bytebandit.userservice.mapper;

import com.bytebandit.userservice.dto.UserRegistrationResponse;
import com.bytebandit.userservice.model.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "fullName", target = "fullName")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "verified", target = "verified")
    UserRegistrationResponse toUserRegistrationResponse(UserEntity userEntity);
}
