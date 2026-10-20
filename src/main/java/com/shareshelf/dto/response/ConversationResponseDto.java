
package com.shareshelf.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ConversationResponseDto {

    private Long id;

    private Long bookShareId;
    private String bookTitle;

    private Long userOneId;
    private String userOneFirstName;
    private String userOneLastName;

    private Long userTwoId;
    private String userTwoFirstName;
    private String userTwoLastName;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
