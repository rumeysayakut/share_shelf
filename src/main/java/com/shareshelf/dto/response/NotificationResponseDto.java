package com.shareshelf.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class NotificationResponseDto {

    private Long id;

    private Long userId;
    private String userFirstName;
    private String userLastName;

    private String title;
    private String message;
    private Boolean isRead;

    private LocalDateTime createdAt;
}