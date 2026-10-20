
package com.shareshelf.dto.request;

import com.shareshelf.entity.ShareType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class BookShareRequestDto {

    private Long bookId;
    private ShareType shareType;
    private BigDecimal depositAmount;
    private BigDecimal salePrice;
    private String conditionDescription;
    private String description;
}
