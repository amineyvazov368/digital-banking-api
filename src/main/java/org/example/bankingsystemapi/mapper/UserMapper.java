package org.example.bankingsystemapi.mapper;

import lombok.RequiredArgsConstructor;
import org.example.bankingsystemapi.model.dto.request.UserRequestDto;
import org.example.bankingsystemapi.model.dto.response.AccountResponseDto;
import org.example.bankingsystemapi.model.dto.response.UserResponseDto;
import org.example.bankingsystemapi.model.entity.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final AccountMapper accountMapper;
    private final PasswordEncoder passwordEncoder;

    public User toEntity(UserRequestDto userRequestDto) {
        User user = new User();
        user.setName(userRequestDto.getName());
        user.setSurname(userRequestDto.getSurname());
        user.setEmail(userRequestDto.getEmail());
        user.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));
        return user;

    }

    public UserResponseDto toResponseDto(User user) {
        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setName(user.getName());
        userResponseDto.setSurname(user.getSurname());
        userResponseDto.setEmail(user.getEmail());
        userResponseDto.setRole(user.getRole());

        userResponseDto.setUserStatus(user.getUserStatus());
        userResponseDto.setCreatedAt(user.getCreatedAt());

        List<AccountResponseDto> accountResponseDtoList =
                user.getAccounts() == null
                        ? List.of()
                        : user.getAccounts().stream()
                        .map(accountMapper::toDto)
                        .toList();

        userResponseDto.setAccounts(accountResponseDtoList);
        return userResponseDto;
    }

}
