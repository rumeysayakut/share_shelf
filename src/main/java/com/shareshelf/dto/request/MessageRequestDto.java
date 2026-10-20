package com.shareshelf.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageRequestDto {

    private Long conversationId;
    private String content;
}