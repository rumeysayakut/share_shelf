
package com.shareshelf.dto.response;

import com.shareshelf.entity.BorrowingStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class BorrowingResponseDto {

    private Long id;

    private Long bookShareId;
    private Long bookId;
    private String bookTitle;
    private Long ownerId;
    private String ownerFirstName;
    private String ownerLastName;

    private Long borrowerId;
    private String borrowerFirstName;
    private String borrowerLastName;

    private BorrowingStatus status;

    private LocalDateTime requestedAt;
    private LocalDateTime acceptedAt;
    private LocalDateTime dueDate;
    private LocalDateTime borrowedAt;
    private LocalDateTime returnRequestedAt;
    private LocalDateTime returnedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
