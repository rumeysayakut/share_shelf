
package com.shareshelf.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserReviewResponseDto {

    private Long id;
    private Long reviewerId;
    private String reviewerFirstName;
    private String reviewerLastName;
    private Long reviewedUserId;
    private String reviewedUserFirstName;
    private String reviewedUserLastName;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
