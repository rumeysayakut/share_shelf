package com.shareshelf.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationRequestDto {

    private Long userId;
    private String title;
    private String message;
}