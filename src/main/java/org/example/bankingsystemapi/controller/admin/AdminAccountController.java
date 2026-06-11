package org.example.bankingsystemapi.controller.admin;

import lombok.RequiredArgsConstructor;
import org.example.bankingsystemapi.model.dto.response.AccountResponseDto;
import org.example.bankingsystemapi.model.enums.AccountStatus;
import org.example.bankingsystemapi.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/accounts")
@RequiredArgsConstructor
public class AdminAccountController {

    private final AccountService accountService;

    @GetMapping
    public ResponseEntity<List<AccountResponseDto>> getAllAccounts() {

        return ResponseEntity.ok(
                accountService.getAllAccounts()
        );
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<AccountResponseDto>> getAccountsByStatus(
            @PathVariable AccountStatus status) {

        return ResponseEntity.ok(
                accountService.getAccountsByStatus(status)
        );
    }

    @PatchMapping("/{accountId}/activate")
    public ResponseEntity<Void> activateAccount(
            @PathVariable Long accountId) {

        accountService.activeAccount(accountId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{accountId}/block")
    public ResponseEntity<Void> blockAccount(
            @PathVariable Long accountId) {

        accountService.deactivateAccount(accountId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{accountId}/close")
    public ResponseEntity<Void> closeAccount(
            @PathVariable Long accountId) {

        accountService.closeAccount(accountId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AccountResponseDto>> getAccountsByUserId(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                accountService.getAccountByUserid(userId)
        );
    }
}

