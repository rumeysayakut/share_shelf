
package com.shareshelf.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.shareshelf.dto.request.BorrowingRequestDto;
import com.shareshelf.dto.response.BorrowingResponseDto;
import com.shareshelf.entity.Borrowing;

@Mapper(componentModel = "spring")
public interface BorrowingMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "bookShare", ignore = true)
    @Mapping(target = "borrower", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "requestedAt", ignore = true)
    @Mapping(target = "acceptedAt", ignore = true)
    @Mapping(target = "borrowedAt", ignore = true)
    @Mapping(target = "returnRequestedAt", ignore = true)
    @Mapping(target = "returnedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Borrowing toEntity(BorrowingRequestDto dto);

    @Mapping(target = "bookShareId", source = "bookShare.id")
    @Mapping(target = "bookId", source = "bookShare.book.id")
    @Mapping(target = "bookTitle", source = "bookShare.book.title")
    @Mapping(target = "ownerId", source = "bookShare.user.id")
    @Mapping(target = "ownerFirstName", source = "bookShare.user.firstName")
    @Mapping(target = "ownerLastName", source = "bookShare.user.lastName")
    @Mapping(target = "borrowerId", source = "borrower.id")
    @Mapping(target = "borrowerFirstName", source = "borrower.firstName")
    @Mapping(target = "borrowerLastName", source = "borrower.lastName")
    BorrowingResponseDto toResponseDto(Borrowing borrowing);
}
