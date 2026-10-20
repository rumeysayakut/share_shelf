
package com.shareshelf.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.shareshelf.dto.request.ReadingListItemRequestDto;
import com.shareshelf.dto.response.ReadingListItemResponseDto;
import com.shareshelf.entity.ReadingListItem;

@Mapper(componentModel = "spring")
public interface ReadingListItemMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "readingList", ignore = true)
    @Mapping(target = "book", ignore = true)
    @Mapping(target = "addedAt", ignore = true)
    ReadingListItem toEntity(ReadingListItemRequestDto dto);

    @Mapping(target = "bookId", source = "book.id")
    @Mapping(target = "bookTitle", source = "book.title")
    @Mapping(target = "bookAuthor", source = "book.author")
    ReadingListItemResponseDto toResponseDto(ReadingListItem item);
}
