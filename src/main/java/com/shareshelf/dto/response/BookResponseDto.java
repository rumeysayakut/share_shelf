
package com.shareshelf.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class BookResponseDto {

    private Long id;
    private String title;
    private String author;
    private String isbn;
    private String publisher;
    private Integer publicationYear;
    private Integer pageCount;
    private String coverImageUrl;
    private String description;
    private Long createdById;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
