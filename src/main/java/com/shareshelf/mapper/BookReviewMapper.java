
package com.shareshelf.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.shareshelf.dto.request.BookReviewRequestDto;
import com.shareshelf.dto.response.BookReviewResponseDto;
import com.shareshelf.entity.BookReview;

@Mapper(componentModel = "spring")
public interface BookReviewMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "book", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    BookReview toEntity(BookReviewRequestDto dto);

    @Mapping(target = "bookId", source = "book.id")
    @Mapping(target = "bookTitle", source = "book.title")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "firstName", source = "user.firstName")
    @Mapping(target = "lastName", source = "user.lastName")
    BookReviewResponseDto toResponseDto(BookReview review);
}
