
package com.shareshelf.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.shareshelf.dto.request.BookQuoteRequestDto;
import com.shareshelf.dto.response.BookQuoteResponseDto;
import com.shareshelf.entity.BookQuote;

@Mapper(componentModel = "spring")
public interface BookQuoteMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "book", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    BookQuote toEntity(BookQuoteRequestDto dto);

    @Mapping(target = "bookId", source = "book.id")
    @Mapping(target = "bookTitle", source = "book.title")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "firstName", source = "user.firstName")
    @Mapping(target = "lastName", source = "user.lastName")
    BookQuoteResponseDto toResponseDto(BookQuote quote);
}
