package com.shareshelf.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.shareshelf.dto.request.MessageRequestDto;
import com.shareshelf.dto.response.MessageResponseDto;
import com.shareshelf.entity.Message;

@Mapper(componentModel = "spring")
public interface MessageMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "conversation", ignore = true)
    @Mapping(target = "sender", ignore = true)
    @Mapping(target = "sentAt", ignore = true)
    Message toEntity(MessageRequestDto dto);

    @Mapping(target = "conversationId", source = "conversation.id")
    @Mapping(target = "senderId", source = "sender.id")
    @Mapping(target = "senderFirstName", source = "sender.firstName")
    @Mapping(target = "senderLastName", source = "sender.lastName")
    MessageResponseDto toResponseDto(Message message);
}