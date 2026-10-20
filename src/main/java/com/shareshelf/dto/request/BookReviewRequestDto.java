
package com.shareshelf.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookReviewRequestDto {

    private Long bookId;

    private Integer rating;

    private String comment;
}
