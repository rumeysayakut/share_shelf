
package com.shareshelf.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.shareshelf.dto.request.BookShareRequestDto;
import com.shareshelf.dto.response.BookShareResponseDto;
import com.shareshelf.entity.BookShare;

@Mapper(componentModel = "spring")
public interface BookShareMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "book", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    BookShare toEntity(BookShareRequestDto dto);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userFirstName", source = "user.firstName")
    @Mapping(target = "userLastName", source = "user.lastName")
    @Mapping(target = "bookId", source = "book.id")
    @Mapping(target = "bookTitle", source = "book.title")
    @Mapping(target = "bookAuthor", source = "book.author")
    BookShareResponseDto toResponseDto(BookShare bookShare);
}
