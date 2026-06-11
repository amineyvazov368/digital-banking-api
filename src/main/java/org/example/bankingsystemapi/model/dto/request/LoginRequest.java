package org.example.bankingsystemapi.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "Email boş ola bilməz")
    @Email(message = "Düzgün email daxil edin")
    private String email;

    @NotBlank(message = "Password bis ola bilmez")
    private String password;


}
