
package com.shareshelf.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.shareshelf.dto.request.UserLibraryRequestDto;
import com.shareshelf.dto.response.UserLibraryResponseDto;
import com.shareshelf.entity.UserLibrary;

@Mapper(componentModel = "spring")
public interface UserLibraryMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "book", ignore = true)
    @Mapping(target = "addedAt", ignore = true)
    UserLibrary toEntity(UserLibraryRequestDto dto);

    @Mapping(target = "bookId", source = "book.id")
    @Mapping(target = "bookTitle", source = "book.title")
    @Mapping(target = "bookAuthor", source = "book.author")
    UserLibraryResponseDto toResponseDto(UserLibrary userLibrary);
}
