package org.example.bankingsystemapi.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.bankingsystemapi.model.dto.request.LoginRequest;
import org.example.bankingsystemapi.model.dto.request.RefreshTokenRequest;
import org.example.bankingsystemapi.model.dto.request.UserRequestDto;
import org.example.bankingsystemapi.model.dto.response.LoginResponseDto;
import org.example.bankingsystemapi.model.dto.response.RefreshTokenResponse;
import org.example.bankingsystemapi.model.dto.response.UserResponseDto;
import org.example.bankingsystemapi.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
//@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class UserController {
    

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> registerUser(@RequestBody @Valid UserRequestDto userRequestDto) {
        UserResponseDto saveUser = userService.registerUser(userRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saveUser);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> loginUser(@RequestBody @Valid LoginRequest loginRequest) {
        return ResponseEntity.ok(userService.loginUser(loginRequest));


    }

    @PostMapping("/refresh")
    public RefreshTokenResponse refreshToken(@RequestBody @Valid RefreshTokenRequest refreshTokenRequest) {
        return userService.refresh(refreshTokenRequest);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logOut(@RequestHeader("Authorization") String outHeader) {
        userService.logout(outHeader);
        return ResponseEntity.ok("Logout successful");
    }

}
