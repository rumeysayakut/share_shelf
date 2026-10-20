
package com.shareshelf.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class BorrowingRequestDto {

    private Long bookShareId;
    private LocalDateTime dueDate;
}
