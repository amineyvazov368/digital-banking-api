package org.example.bankingsystemapi.controller.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.bankingsystemapi.model.dto.request.UserRequestDto;
import org.example.bankingsystemapi.model.dto.request.UserUpdateDto;
import org.example.bankingsystemapi.model.dto.response.UserResponseDto;
import org.example.bankingsystemapi.model.dto.response.UserSummaryDto;
import org.example.bankingsystemapi.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

//    @Transactional(readOnly = true)
//    @GetMapping
//    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
//        List<UserResponseDto> userResponseDto = userService.getAllUsers();
//        return ResponseEntity.ok(userResponseDto);
//    }

    @Transactional(readOnly = true)
    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUsers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status
    ) {

        List<UserResponseDto> userResponseDto = userService.getAllUsers(search, status);
        return ResponseEntity.ok(userResponseDto);

    }

    @Transactional(readOnly = true)
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        UserResponseDto userResponseDto = userService.getUserById(id);
        return ResponseEntity.ok(userResponseDto);
    }

    @PatchMapping("/{id}/block")
    public ResponseEntity<Void> blockUserById(@PathVariable Long id) {
        userService.blockUserById(id);
        return ResponseEntity.noContent().build();
    }


    @DeleteMapping({"/{id}"})
    public ResponseEntity<Void> deleteUserById(@PathVariable Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activateUser(@PathVariable Long id) {

        userService.activateUser(id);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateUserById(@PathVariable Long id,
                                                          @RequestBody @Valid UserUpdateDto userUpdateDto) {
        UserResponseDto userResponseDto = userService.updateUser(id, userUpdateDto);
        return ResponseEntity.ok(userResponseDto);

    }

    @Transactional(readOnly = true)
    @GetMapping("/summary")
    public ResponseEntity<UserSummaryDto> getUserSummary() {
        return ResponseEntity.ok(userService.getUserSummary());
    }

}
