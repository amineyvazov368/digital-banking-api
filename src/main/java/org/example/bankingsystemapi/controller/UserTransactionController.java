package org.example.bankingsystemapi.controller;

import lombok.RequiredArgsConstructor;
import org.example.bankingsystemapi.model.dto.response.TransactionResponseDto;
import org.example.bankingsystemapi.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class UserTransactionController {

    private final TransactionService transactionService;

    @PostMapping("/deposit")
    public ResponseEntity<String> deposit(
            @RequestParam Long accountId,
            @RequestParam BigDecimal amount) {

        return ResponseEntity.ok(
                transactionService.deposit(accountId, amount)
        );
    }

    @PostMapping("/withdraw")
    public ResponseEntity<String> withdraw(
            @RequestParam Long accountId,
            @RequestParam BigDecimal amount) {

        return ResponseEntity.ok(
                transactionService.withdraw(accountId, amount)
        );
    }

    @PostMapping("/transfer")
    public ResponseEntity<String> transfer(
            @RequestParam Long fromAccountId,
            @RequestParam Long toAccountId,
            @RequestParam BigDecimal amount) {

        return ResponseEntity.ok(
                transactionService.transfer(fromAccountId, toAccountId, amount)
        );
    }

    @GetMapping("/history/{accountId}")
    public ResponseEntity<List<TransactionResponseDto>> getHistory(
            @PathVariable Long accountId,
            @RequestParam(defaultValue = "10") int limit) {

        return ResponseEntity.ok(
                transactionService.getTransactionHistory(accountId, limit)
        );
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionResponseDto> getTransactionById(
            @PathVariable Long transactionId) {

        return ResponseEntity.ok(
                transactionService.getTransactionById(transactionId)
        );
    }

}
