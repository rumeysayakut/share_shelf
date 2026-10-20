
package com.shareshelf.dto.response;

import com.shareshelf.entity.HandoverType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class HandoverVerificationResponseDto {

    private Long id;
    private Long borrowingId;
    private HandoverType handoverType;
    private LocalDateTime expiresAt;
    private LocalDateTime verifiedAt;
    private Boolean used;
    private LocalDateTime createdAt;
}
