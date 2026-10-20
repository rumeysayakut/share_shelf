
package com.shareshelf.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookRequestDto {

    private String title;

    private String author;

    private String isbn;

    private String publisher;

    private Integer publicationYear;

    private Integer pageCount;

    private String coverImageUrl;

    private String description;
}
