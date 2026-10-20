
package com.shareshelf.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserReviewRequestDto {

    private Long reviewedUserId;
    private Integer rating;
    private String comment;
}
