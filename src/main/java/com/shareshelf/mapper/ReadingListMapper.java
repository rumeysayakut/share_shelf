
package com.shareshelf.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.shareshelf.dto.request.ReadingListRequestDto;
import com.shareshelf.dto.response.ReadingListResponseDto;
import com.shareshelf.entity.ReadingList;

@Mapper(componentModel = "spring")
public interface ReadingListMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ReadingList toEntity(ReadingListRequestDto dto);

    ReadingListResponseDto toResponseDto(ReadingList readingList);
}
