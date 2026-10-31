package com.shareshelf.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateRequestDto {

    @NotBlank(message = "Ad boş bırakılamaz.")
    @Size(max = 50, message = "Ad en fazla 50 karakter olabilir.")
    private String firstName;

    @NotBlank(message = "Soyad boş bırakılamaz.")
    @Size(max = 50, message = "Soyad en fazla 50 karakter olabilir.")
    private String lastName;

    @NotBlank(message = "E-posta boş bırakılamaz.")
    @Email(message = "Geçerli bir e-posta adresi giriniz.")
    @Size(max = 150, message = "E-posta en fazla 150 karakter olabilir.")
    private String email;
}