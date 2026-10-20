
package com.shareshelf.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReadingListRequestDto {

    private String name;

    private String description;

    private Boolean isPublic;
}
