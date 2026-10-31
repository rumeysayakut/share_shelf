package com.shareshelf.dto.request;

import com.shareshelf.entity.ReadingStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLibraryRequestDto {

    private Long bookId;

    private ReadingStatus readingStatus;
}