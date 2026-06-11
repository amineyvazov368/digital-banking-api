package org.example.bankingsystemapi.controller.admin;

import lombok.RequiredArgsConstructor;
import org.example.bankingsystemapi.model.dto.response.TransactionResponseDto;
import org.example.bankingsystemapi.model.enums.TransactionStatus;
import org.example.bankingsystemapi.model.enums.TransactionType;
import org.example.bankingsystemapi.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/transactions")
@RequiredArgsConstructor
public class AdminTransactionController {

    private final TransactionService transactionService;

    @GetMapping
    public ResponseEntity<List<TransactionResponseDto>> getAllTransactions() {

        return ResponseEntity.ok(
                transactionService.getAllTransactions()
        );
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionResponseDto> getTransactionById(
            @PathVariable Long transactionId) {

        return ResponseEntity.ok(
                transactionService.getTransactionById(transactionId)
        );
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<TransactionResponseDto>> getTransactionsByStatus(
            @PathVariable TransactionStatus status) {

        return ResponseEntity.ok(
                transactionService.getTransactionsByStatus(status)
        );
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<TransactionResponseDto>> getTransactionsByType(
            @PathVariable TransactionType type) {

        return ResponseEntity.ok(
                transactionService.getTransactionsByTransactionType(type)
        );
    }

}