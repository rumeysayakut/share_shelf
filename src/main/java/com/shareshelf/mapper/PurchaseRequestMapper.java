
package com.shareshelf.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.shareshelf.dto.request.PurchaseRequestRequestDto;
import com.shareshelf.dto.response.PurchaseRequestResponseDto;
import com.shareshelf.entity.PurchaseRequest;

@Mapper(componentModel = "spring")
public interface PurchaseRequestMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "bookShare", ignore = true)
    @Mapping(target = "buyer", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "requestedAt", ignore = true)
    @Mapping(target = "acceptedAt", ignore = true)
    @Mapping(target = "completedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    PurchaseRequest toEntity(PurchaseRequestRequestDto dto);

    @Mapping(target = "bookShareId", source = "bookShare.id")
    @Mapping(target = "bookId", source = "bookShare.book.id")
    @Mapping(target = "bookTitle", source = "bookShare.book.title")
    @Mapping(target = "sellerId", source = "bookShare.user.id")
    @Mapping(target = "sellerFirstName", source = "bookShare.user.firstName")
    @Mapping(target = "sellerLastName", source = "bookShare.user.lastName")
    @Mapping(target = "buyerId", source = "buyer.id")
    @Mapping(target = "buyerFirstName", source = "buyer.firstName")
    @Mapping(target = "buyerLastName", source = "buyer.lastName")
    PurchaseRequestResponseDto toResponseDto(PurchaseRequest purchaseRequest);
}
