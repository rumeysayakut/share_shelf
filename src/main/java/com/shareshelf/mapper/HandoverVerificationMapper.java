
package com.shareshelf.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.shareshelf.dto.request.HandoverVerificationRequestDto;
import com.shareshelf.dto.response.HandoverVerificationResponseDto;
import com.shareshelf.entity.HandoverVerification;

@Mapper(componentModel = "spring")
public interface HandoverVerificationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "borrowing", ignore = true)
    @Mapping(target = "codeHash", ignore = true)
    @Mapping(target = "expiresAt", ignore = true)
    @Mapping(target = "verifiedAt", ignore = true)
    @Mapping(target = "used", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    HandoverVerification toEntity(HandoverVerificationRequestDto dto);

    @Mapping(target = "borrowingId", source = "borrowing.id")
    HandoverVerificationResponseDto toResponseDto(
            HandoverVerification verification);
}
