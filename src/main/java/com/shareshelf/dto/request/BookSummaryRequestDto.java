
package com.shareshelf.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookSummaryRequestDto {

    @NotBlank(message = "Kitap adı boş bırakılamaz.")
    @Size(max = 200)
    private String title;

    @NotBlank(message = "Yazar adı boş bırakılamaz.")
    @Size(max = 150)
    private String author;
}
