
package com.shareshelf.dto.response;

import com.shareshelf.entity.PurchaseStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PurchaseRequestResponseDto {

    private Long id;

    private Long bookShareId;
    private Long bookId;
    private String bookTitle;
    private Long sellerId;
    private String sellerFirstName;
    private String sellerLastName;

    private Long buyerId;
    private String buyerFirstName;
    private String buyerLastName;

    private PurchaseStatus status;

    private LocalDateTime requestedAt;
    private LocalDateTime acceptedAt;
    private LocalDateTime completedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
