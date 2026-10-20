
package com.shareshelf.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ReadingListResponseDto {

    private Long id;

    private String name;

    private String description;

    private Boolean isPublic;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
