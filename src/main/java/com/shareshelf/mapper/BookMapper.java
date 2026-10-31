
package com.shareshelf.mapper;

import com.shareshelf.dto.request.BookRequestDto;
import com.shareshelf.dto.response.BookResponseDto;
import com.shareshelf.entity.Book;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BookMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Book toEntity(BookRequestDto bookRequestDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(BookRequestDto dto, @MappingTarget Book book);

    @Mapping(target = "createdById", source = "createdBy.id")
    BookResponseDto toResponseDto(Book book);
}
