package org.example.bankingsystemapi.mapper;

import org.example.bankingsystemapi.model.dto.request.UserRequestDto;
import org.example.bankingsystemapi.model.dto.response.AccountResponseDto;
import org.example.bankingsystemapi.model.dto.response.UserResponseDto;
import org.example.bankingsystemapi.model.entity.User;
import org.example.bankingsystemapi.model.enums.Role;
import org.example.bankingsystemapi.model.enums.UserStatus;

import java.util.ArrayList;
import java.util.List;

public class UserMapper {

    private AccountMapper accountMapper;

    public User toEntity(UserRequestDto userRequestDto){
        User user = new User();
        user.setName(userRequestDto.getName());
        user.setSurname(userRequestDto.getSurname());
        user.setEmail(userRequestDto.getEmail());
        user.setPassword(userRequestDto.getPassword());
        return user;

    }

    public UserResponseDto toResponseDto(User user){
        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setName(user.getName());
        userResponseDto.setSurname(user.getSurname());
        userResponseDto.setEmail(user.getEmail());
        userResponseDto.setRole(user.getRole());

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
