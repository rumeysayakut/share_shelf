
package com.shareshelf.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ReadingListItemResponseDto {

    private Long id;

    private Long bookId;

    private String bookTitle;

    private String bookAuthor;

    private LocalDateTime addedAt;
}
