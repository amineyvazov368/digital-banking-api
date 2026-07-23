package org.example.bankingsystemapi.controller;

import lombok.RequiredArgsConstructor;
import org.example.bankingsystemapi.model.dto.request.AccountRequestDto;
import org.example.bankingsystemapi.model.dto.response.AccountResponseDto;
import org.example.bankingsystemapi.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class UserAccountController {

    private final AccountService accountService;

    @PostMapping("/{userId}")
    public ResponseEntity<AccountResponseDto> createAccount(
            @PathVariable Long userId,
            @RequestBody AccountRequestDto requestDto) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(accountService.createAccount(userId, requestDto));
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<AccountResponseDto> getAccountById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                accountService.getAccountById(id)
        );
    }

    @GetMapping("/number/{accountNumber}")
    public ResponseEntity<AccountResponseDto> getAccountByNumber(
            @PathVariable String accountNumber) {

        return ResponseEntity.ok(
                accountService.getAccountByAccountNumber(accountNumber)
        );
    }

    @GetMapping("/my-accounts")
    public ResponseEntity<List<AccountResponseDto>> getMyAccounts() {
        return ResponseEntity.ok(
                accountService.getMyAccounts()
        );
    }

    @GetMapping("/{id}/balance")
    public ResponseEntity<BigDecimal> getBalance(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                accountService.getBalance(id)
        );
    }
    @PatchMapping("/{accountId}/close")
    public ResponseEntity<Void> closeAccount(
            @PathVariable Long accountId) {

        accountService.closeAccount(accountId);
        return ResponseEntity.ok().build();
    }
}
