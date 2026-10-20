
package com.shareshelf.dto.request;

import com.shareshelf.entity.HandoverType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HandoverVerificationRequestDto {

    private Long borrowingId;
    private HandoverType handoverType;
}
