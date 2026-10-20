
package com.shareshelf.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserResponseDto {

    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private LocalDateTime createdAt;
}
