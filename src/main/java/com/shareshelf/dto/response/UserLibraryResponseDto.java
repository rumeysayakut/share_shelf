package com.shareshelf.dto.response;

import java.time.LocalDateTime;
import com.shareshelf.entity.ReadingStatus;
import lombok.Data;

@Data
public class UserLibraryResponseDto {

    private Long id;
    private Long bookId;
    private String bookTitle;
    private String bookAuthor;
    private ReadingStatus readingStatus;
    private LocalDateTime addedAt;
}