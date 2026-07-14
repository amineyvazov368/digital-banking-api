package org.example.bankingsystemapi.model.dto.response;

import lombok.Data;
import org.example.bankingsystemapi.model.entity.Account;
import org.example.bankingsystemapi.model.enums.Role;
import org.example.bankingsystemapi.model.enums.UserStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Component
public class UserResponseDto {

    private String name;
    private String surname;
    private String email;
    private Role role;
    private List<AccountResponseDto> accounts;
    private UserStatus userStatus;
    private LocalDateTime createdAt;


}
