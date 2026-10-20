
package com.shareshelf.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class BookQuoteResponseDto {

    private Long id;

    private Long bookId;

    private String bookTitle;

    private Long userId;

    private String firstName;

    private String lastName;

    private String content;

    private Integer pageNumber;

    private LocalDateTime createdAt;
}
