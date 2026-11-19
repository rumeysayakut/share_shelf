
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

    public String getStatusLabel() {

        if (status == null) {
            return "Beklemede";
        }

        return switch (status) {
            case PENDING -> "Beklemede";
            case ACCEPTED -> "Kabul Edildi";
            case REJECTED -> "Reddedildi";
            case CANCELLED -> "İptal Edildi";
            case BORROWED -> "Kitap Teslim Alındı";
            case RETURN_PENDING -> "İade Bekleniyor";
            case RETURNED -> "İade Edildi";
            case OVERDUE -> "İade Süresi Geçti";
            case DISPUTED -> "İtirazlı İşlem";
        };
    }
}
