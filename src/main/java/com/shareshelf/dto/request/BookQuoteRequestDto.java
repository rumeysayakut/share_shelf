
package com.shareshelf.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookQuoteRequestDto {

    private Long bookId;

    private String content;

    private Integer pageNumber;
}
