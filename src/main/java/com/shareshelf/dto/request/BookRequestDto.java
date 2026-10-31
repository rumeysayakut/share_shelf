
package com.shareshelf.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookRequestDto {

    @NotBlank(message = "Kitap adı boş bırakılamaz.")
    @Size(max = 200, message = "Kitap adı en fazla 200 karakter olabilir.")
    private String title;

    @NotBlank(message = "Yazar adı boş bırakılamaz.")
    @Size(max = 150, message = "Yazar adı en fazla 150 karakter olabilir.")
    private String author;

    @Size(max = 20)
    private String isbn;

    @Size(max = 150)
    private String publisher;

    @Min(value = 1, message = "Yayın yılı geçerli olmalıdır.")
    @Max(value = 2100, message = "Yayın yılı 2100'den büyük olamaz.")
    private Integer publicationYear;

    @Min(value = 1, message = "Sayfa sayısı en az 1 olmalıdır.")
    private Integer pageCount;

    @Size(max = 1000)
    private String coverImageUrl;

    @NotBlank(message = "Kitap açıklaması boş bırakılamaz.")
    private String description;
}
