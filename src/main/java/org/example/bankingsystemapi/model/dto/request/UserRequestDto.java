package org.example.bankingsystemapi.model.dto.request;

import lombok.Data;

@Data
public class UserRequestDto {

    private String name;
    private String surname;
    private String email;
    private String password;


}
