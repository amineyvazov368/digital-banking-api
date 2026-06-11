package org.example.bankingsystemapi.service;

import lombok.RequiredArgsConstructor;
import org.example.bankingsystemapi.mapper.UserMapper;
import org.example.bankingsystemapi.model.dto.request.LoginRequest;
import org.example.bankingsystemapi.model.dto.request.UserRequestDto;
import org.example.bankingsystemapi.model.dto.response.UserResponseDto;
import org.example.bankingsystemapi.model.entity.Account;
import org.example.bankingsystemapi.model.entity.User;
import org.example.bankingsystemapi.model.enums.Currency;
import org.example.bankingsystemapi.model.enums.UserStatus;
import org.example.bankingsystemapi.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final AccountService accountService;


//    public UserResponseDto registerUser(UserRequestDto userRequestDto) {
//
//        if(userRepository.existsByEmail(userRequestDto.getEmail())) {
//            throw new RuntimeException("Email already exists");
//        }
//
//        User user =userMapper.toEntity(userRequestDto);
//        Account account = new Account();
//        account.setBalance(BigDecimal.ZERO);
//        account.setCurrency(Currency.AZN);
//        account.setUser(user);
//        user.setAccounts(List.of(account));
//        User savedUser = userRepository.save(user);
//        return userMapper.toResponseDto(savedUser);
//
//
//    }

    @Transactional
    public UserResponseDto registerUser(UserRequestDto userRequestDto) {

        validateRegisterRequest(userRequestDto);

        if (userRepository.existsByEmail(userRequestDto.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = userMapper.toEntity(userRequestDto);

        user.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));

        User savedUser = userRepository.save(user);

        accountService.createDefaultAccount(savedUser.getId());

        return userMapper.toResponseDto(savedUser);
    }

    @Transactional
    public UserResponseDto loginUser(LoginRequest loginRequest) {

        User user = userRepository.findByEmail(loginRequest.getEmail());
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("Wrong password");
        }

        if (user.getUserStatus() != UserStatus.ACTIVE) {
            throw new RuntimeException("User is not active");
        }

        UserResponseDto userResponseDto = userMapper.toResponseDto(user);
        return userResponseDto;
    }

    public List<UserResponseDto> getAllUsers() {
        List<UserResponseDto> users = userRepository.findAll()
                .stream().map(userMapper::toResponseDto)
                .toList();
        return users;
    }

    public UserResponseDto getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        return userMapper.toResponseDto(user);

    }

    public void deleteUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setUserStatus(UserStatus.BLOCKED);
        userRepository.save(user);

    }

    public UserResponseDto updateUser(Long id, UserRequestDto userRequestDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setName(userRequestDto.getName());
        user.setSurname(userRequestDto.getSurname());
        user.setEmail(userRequestDto.getEmail());
        user.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));
        return userMapper.toResponseDto(userRepository.save(user));


    }

    private void validateRegisterRequest(UserRequestDto request) {

        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new RuntimeException("Email cannot be empty");
        }

        if (request.getPassword() == null || request.getPassword().length() < 6) {
            throw new RuntimeException("Password must be at least 6 characters");
        }

        if (request.getName() == null || request.getName().isBlank()) {
            throw new RuntimeException("Name cannot be empty");
        }
    }


}
