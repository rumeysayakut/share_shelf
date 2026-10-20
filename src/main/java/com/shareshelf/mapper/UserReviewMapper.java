
package com.shareshelf.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.shareshelf.dto.request.UserReviewRequestDto;
import com.shareshelf.dto.response.UserReviewResponseDto;
import com.shareshelf.entity.UserReview;

@Mapper(componentModel = "spring")
public interface UserReviewMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "reviewer", ignore = true)
    @Mapping(target = "reviewedUser", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    UserReview toEntity(UserReviewRequestDto dto);

    @Mapping(target = "reviewerId", source = "reviewer.id")
    @Mapping(target = "reviewerFirstName", source = "reviewer.firstName")
    @Mapping(target = "reviewerLastName", source = "reviewer.lastName")
    @Mapping(target = "reviewedUserId", source = "reviewedUser.id")
    @Mapping(target = "reviewedUserFirstName", source = "reviewedUser.firstName")
    @Mapping(target = "reviewedUserLastName", source = "reviewedUser.lastName")
    UserReviewResponseDto toResponseDto(UserReview userReview);
}
