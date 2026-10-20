package com.shareshelf.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class MessageResponseDto {

    private Long id;

    private Long conversationId;

    private Long senderId;
    private String senderFirstName;
    private String senderLastName;

    private String content;

    private LocalDateTime sentAt;
}