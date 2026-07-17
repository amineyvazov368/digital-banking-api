package org.example.bankingsystemapi.service;

import lombok.RequiredArgsConstructor;
import org.example.bankingsystemapi.mapper.UserMapper;
import org.example.bankingsystemapi.model.dto.request.LoginRequest;
import org.example.bankingsystemapi.model.dto.request.RefreshTokenRequest;
import org.example.bankingsystemapi.model.dto.request.UserRequestDto;
import org.example.bankingsystemapi.model.dto.response.LoginResponseDto;
import org.example.bankingsystemapi.model.dto.response.RefreshTokenResponse;
import org.example.bankingsystemapi.model.dto.response.UserResponseDto;
import org.example.bankingsystemapi.model.entity.Account;
import org.example.bankingsystemapi.model.entity.User;
import org.example.bankingsystemapi.model.enums.Currency;
import org.example.bankingsystemapi.model.enums.UserStatus;
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

    @Transactional(readOnly = true)
    public LoginResponseDto loginUser(LoginRequest loginRequest) {

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

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        String accessToken = jwtService.generateAccessToken(user.getId(), user.getEmail());
        String refreshToken = jwtService.generateRefreshToken(user.getId(), user.getEmail());

        UserResponseDto userResponseDto = userMapper.toResponseDto(user);
        return new LoginResponseDto(user.getId(),userResponseDto,accessToken,refreshToken);

    }

    private final java.util.Set<String> tokenBlacklist = java.util.Collections.synchronizedSet(new java.util.HashSet<>());

    public void logout(String authHeader){
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String accessToken = authHeader.substring(7);
            tokenBlacklist.add(accessToken);
        }
    }

    public RefreshTokenResponse refresh(RefreshTokenRequest request) {
        String refreshToken = request.refreshToken();

        if (!jwtService.isValid(refreshToken)) {
            throw new RuntimeException("Refresh token is invalid");
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

    public UserResponseDto getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        return userMapper.toResponseDto(user);

    }

    public void blockUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setUserStatus(UserStatus.BLOCKED);
        userRepository.save(user);

    }

    public UserResponseDto updateUser(Long id, UserRequestDto userRequestDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        User existingUser = userRepository.findByEmail(userRequestDto.getEmail());

        if (existingUser != null && !existingUser.getId().equals(id)) {
            throw new RuntimeException("Email already exists");
        }
        user.setName(userRequestDto.getName());
        user.setSurname(userRequestDto.getSurname());
        user.setEmail(userRequestDto.getEmail());

        if (userRequestDto.getPassword() != null && !userRequestDto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));
        }

        return userMapper.toResponseDto(userRepository.save(user));


    }

    @Transactional
    public void activateUser(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getUserStatus() == UserStatus.ACTIVE) {
            throw new RuntimeException("User is already active");
        }

        user.setUserStatus(UserStatus.ACTIVE);

        userRepository.save(user);
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
