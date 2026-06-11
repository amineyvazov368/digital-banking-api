package org.example.bankingsystemapi.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.bankingsystemapi.model.dto.request.LoginRequest;
import org.example.bankingsystemapi.model.dto.request.UserRequestDto;
import org.example.bankingsystemapi.model.dto.response.UserResponseDto;
import org.example.bankingsystemapi.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> registerUser(@RequestBody @Valid UserRequestDto userRequestDto) {
        UserResponseDto saveUser = userService.registerUser(userRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saveUser);

    }

    @PostMapping("/login")
    public ResponseEntity<UserResponseDto> loginUser(@RequestBody @Valid LoginRequest loginRequest) {
        UserResponseDto login = userService.loginUser(loginRequest);
        return ResponseEntity.ok(login);

    }




}
