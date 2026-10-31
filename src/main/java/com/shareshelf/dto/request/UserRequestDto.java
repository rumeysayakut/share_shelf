package com.shareshelf.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequestDto {

    @NotBlank(message = "Ad boş bırakılamaz.")
    @Size(max = 50, message = "Ad en fazla 50 karakter olabilir.")
    private String firstName;

    @NotBlank(message = "Soyad boş bırakılamaz.")
    @Size(max = 50, message = "Soyad en fazla 50 karakter olabilir.")
    private String lastName;

    @NotBlank(message = "E-posta boş bırakılamaz.")
    @Email(message = "Geçerli bir e-posta adresi giriniz.")
    @Size(max = 50, message = "E-posta en fazla 50 karakter olabilir.")
    private String email;

    @NotBlank(message = "Şifre boş bırakılamaz.")
    @Size(min = 8, max = 30, message = "Şifre 8-30 karakter arasında olmalıdır.")
    private String password;
}