
package com.shareshelf.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class HandoverConfirmationResponseDto {

    private Long id;
    private Long verificationId;
    private Long userId;
    private String firstName;
    private String lastName;
    private LocalDateTime confirmedAt;
}
