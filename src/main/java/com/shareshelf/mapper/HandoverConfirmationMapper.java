
package com.shareshelf.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.shareshelf.dto.request.HandoverConfirmationRequestDto;
import com.shareshelf.dto.response.HandoverConfirmationResponseDto;
import com.shareshelf.entity.HandoverConfirmation;

@Mapper(componentModel = "spring")
public interface HandoverConfirmationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "verification", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "confirmedAt", ignore = true)
    HandoverConfirmation toEntity(HandoverConfirmationRequestDto dto);

    @Mapping(target = "verificationId", source = "verification.id")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "firstName", source = "user.firstName")
    @Mapping(target = "lastName", source = "user.lastName")
    HandoverConfirmationResponseDto toResponseDto(
            HandoverConfirmation confirmation);
}
