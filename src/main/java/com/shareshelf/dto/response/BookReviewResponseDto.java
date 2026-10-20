
package com.shareshelf.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class BookReviewResponseDto {

    private Long id;

    private Long bookId;

    private String bookTitle;

    private Long userId;

    private String firstName;

    private String lastName;

    private Integer rating;

    private String comment;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
