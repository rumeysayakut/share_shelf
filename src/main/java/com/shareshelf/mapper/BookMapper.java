
package com.shareshelf.mapper;

import org.mapstruct.Mapper;

import com.shareshelf.dto.request.BookRequestDto;
import com.shareshelf.dto.response.BookResponseDto;
import com.shareshelf.entity.Book;

@Mapper(componentModel = "spring")
public interface BookMapper {

    Book toEntity(BookRequestDto bookRequestDto);

    BookResponseDto toResponseDto(Book book);
}
