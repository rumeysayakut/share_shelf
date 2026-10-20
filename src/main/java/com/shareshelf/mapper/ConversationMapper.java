
package com.shareshelf.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.shareshelf.dto.request.ConversationRequestDto;
import com.shareshelf.dto.response.ConversationResponseDto;
import com.shareshelf.entity.Conversation;

@Mapper(componentModel = "spring")
public interface ConversationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "bookShare", ignore = true)
    @Mapping(target = "userOne", ignore = true)
    @Mapping(target = "userTwo", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Conversation toEntity(ConversationRequestDto dto);

    @Mapping(target = "bookShareId", source = "bookShare.id")
    @Mapping(target = "bookTitle", source = "bookShare.book.title")
    @Mapping(target = "userOneId", source = "userOne.id")
    @Mapping(target = "userOneFirstName", source = "userOne.firstName")
    @Mapping(target = "userOneLastName", source = "userOne.lastName")
    @Mapping(target = "userTwoId", source = "userTwo.id")
    @Mapping(target = "userTwoFirstName", source = "userTwo.firstName")
    @Mapping(target = "userTwoLastName", source = "userTwo.lastName")
    ConversationResponseDto toResponseDto(Conversation conversation);
}
