
package com.shareshelf.mapper;

import org.mapstruct.Mapper;

import com.shareshelf.dto.request.UserRequestDto;
import com.shareshelf.dto.response.UserResponseDto;
import com.shareshelf.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(UserRequestDto userRequestDto);

    UserResponseDto toResponseDto(User user);
}
