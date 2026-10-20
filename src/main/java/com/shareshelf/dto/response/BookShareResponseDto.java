
package com.shareshelf.dto.response;

import com.shareshelf.entity.BookShareStatus;
import com.shareshelf.entity.ShareType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class BookShareResponseDto {

    private Long id;

    private Long userId;
    private String userFirstName;
    private String userLastName;

    private Long bookId;
    private String bookTitle;
    private String bookAuthor;

    private ShareType shareType;
    private BookShareStatus status;

    private BigDecimal depositAmount;
    private BigDecimal salePrice;

    private String conditionDescription;
    private String description;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
