package org.example.bankingsystemapi.service;

import lombok.RequiredArgsConstructor;
import org.example.bankingsystemapi.exceptions.AlreadyExistsException;
import org.example.bankingsystemapi.exceptions.BadRequestException;
import org.example.bankingsystemapi.exceptions.NotFoundException;
import org.example.bankingsystemapi.mapper.UserMapper;
import org.example.bankingsystemapi.model.dto.request.LoginRequest;
import org.example.bankingsystemapi.model.dto.request.RefreshTokenRequest;
import org.example.bankingsystemapi.model.dto.request.UserRequestDto;
import org.example.bankingsystemapi.model.dto.request.UserUpdateDto;
import org.example.bankingsystemapi.model.dto.response.LoginResponseDto;
import org.example.bankingsystemapi.model.dto.response.RefreshTokenResponse;
import org.example.bankingsystemapi.model.dto.response.UserResponseDto;
import org.example.bankingsystemapi.model.dto.response.UserSummaryDto;
import org.example.bankingsystemapi.model.entity.Account;
import org.example.bankingsystemapi.model.entity.User;
import org.example.bankingsystemapi.model.enums.Currency;
import org.example.bankingsystemapi.model.enums.UserStatus;
import org.example.bankingsystemapi.repository.AccountRepository;
import org.example.bankingsystemapi.repository.UserRepository;
import org.example.bankingsystemapi.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final AccountService accountService;
    private final JwtService jwtService;
    private final UserResponseDto userResponseDto;
    private final AuthenticationManager authenticationManager;
    private final AccountRepository accountRepository;


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
            throw new AlreadyExistsException("Email already exists" + userRequestDto.getEmail());
        }

        User user = userMapper.toEntity(userRequestDto);

        user.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));

        User savedUser = userRepository.save(user);

        accountService.createDefaultAccount(savedUser.getId());

        return userMapper.toResponseDto(savedUser);
    }

    @Transactional(readOnly = true)
    public LoginResponseDto loginUser(LoginRequest loginRequest) {

        User user = userRepository.findByEmail(loginRequest.getEmail());

        if (user == null) {
            throw new NotFoundException("User not found" + loginRequest.getEmail());
        }
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new BadRequestException("Wrong password" + loginRequest.getPassword());
        }

        if (user.getUserStatus() != UserStatus.ACTIVE) {
            throw new BadRequestException("User is not active");
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        String accessToken = jwtService.generateAccessToken(user.getId(), user.getEmail());
        String refreshToken = jwtService.generateRefreshToken(user.getId(), user.getEmail());

        UserResponseDto userResponseDto = userMapper.toResponseDto(user);
        return new LoginResponseDto(user.getId(), userResponseDto, accessToken, refreshToken);

    }

    private final java.util.Set<String> tokenBlacklist = java.util.Collections.synchronizedSet(new java.util.HashSet<>());

    public void logout(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String accessToken = authHeader.substring(7);
            tokenBlacklist.add(accessToken);
        }
    }

    public RefreshTokenResponse refresh(RefreshTokenRequest request) {
        String refreshToken = request.refreshToken();

        if (!jwtService.isValid(refreshToken)) {
            throw new BadRequestException("Refresh token is invalid");
        }
        Long userId = jwtService.extractUserId(refreshToken);
        String userName = jwtService.extractEmail(refreshToken);

        String newAccessToken = jwtService.generateAccessToken(userId, userName);
        return new RefreshTokenResponse(newAccessToken, refreshToken);

    }

    public List<UserResponseDto> getAllUsers() {
        List<UserResponseDto> users = userRepository.findAll()
                .stream().map(userMapper::toResponseDto)
                .toList();
        return users;
    }

    public List<UserResponseDto> getAllUsers(String search, String status) {
        return userRepository.findAll()
                .stream()
                .filter(user -> user.getUserStatus() != UserStatus.DELETED)
                .filter(user -> {
                    boolean matchesSearch = true;
                    boolean matchesStatus = true;

                    if (search != null && !search.isBlank()) {
                        String query = search.trim().toLowerCase();
                        matchesSearch = (user.getName() != null && user.getName().toLowerCase().contains(query)) ||
                                (user.getSurname() != null && user.getSurname().toLowerCase().contains(query)) ||
                                (user.getEmail() != null && user.getEmail().toLowerCase().contains(query));
                    }
                    if (status != null && !status.isBlank() && !"ALL".equalsIgnoreCase(status)) {
                        matchesStatus = user.getUserStatus() != null &&
                                user.getUserStatus().name().equalsIgnoreCase(status);
                    }

                    return matchesSearch && matchesStatus;
                })
                .map(userMapper::toResponseDto)
                .toList();
    }

    public UserSummaryDto getUserSummary() {
        long totalUsers = userRepository.count();
        long activeUsers = userRepository.countByUserStatus(UserStatus.ACTIVE);
        long blockedUsers = userRepository.countByUserStatus(UserStatus.BLOCKED);
        return  UserSummaryDto.builder()
                .totalUsers(totalUsers)
                .activeUsers(activeUsers)
                .blockedUsers(blockedUsers)
                .build();
    }

    public UserResponseDto getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
        return userMapper.toResponseDto(user);

    }

    public void blockUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
        user.setUserStatus(UserStatus.BLOCKED);
        userRepository.save(user);

    }

    @Transactional
    public void deleteUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));

        user.setUserStatus(UserStatus.DELETED);
        userRepository.save(user);
    }


    public UserResponseDto updateUser(Long id, UserUpdateDto userUpdateDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found" + id));

        User existingUser = userRepository.findByEmail(userUpdateDto.getEmail());

        if (existingUser != null && !existingUser.getId().equals(id)) {
            throw new AlreadyExistsException("Email already exists" + userUpdateDto.getEmail());
        }

        userMapper.updateEntityFromDto(userUpdateDto, user);

        if (userUpdateDto.getPassword() != null && !userUpdateDto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(userUpdateDto.getPassword()));
        }

        return userMapper.toResponseDto(userRepository.save(user));


    }

    @Transactional
    public void activateUser(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found" + id));

        if (user.getUserStatus() == UserStatus.ACTIVE) {
            throw new BadRequestException("User is already active");
        }

        user.setUserStatus(UserStatus.ACTIVE);

        userRepository.save(user);
    }

    private void validateRegisterRequest(UserRequestDto request) {

        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new BadRequestException("Email cannot be empty");
        }

        if (request.getPassword() == null || request.getPassword().length() < 6) {
            throw new BadRequestException("Password must be at least 6 characters");
        }

        if (request.getName() == null || request.getName().isBlank()) {
            throw new BadRequestException("Name cannot be empty");
        }
    }


}
