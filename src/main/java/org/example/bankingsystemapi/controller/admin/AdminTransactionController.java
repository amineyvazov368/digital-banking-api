package org.example.bankingsystemapi.controller.admin;

import lombok.RequiredArgsConstructor;
import org.example.bankingsystemapi.model.dto.response.TransactionResponseDto;
import org.example.bankingsystemapi.model.enums.TransactionStatus;
import org.example.bankingsystemapi.model.enums.TransactionType;
import org.example.bankingsystemapi.service.TransactionService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/transactions")
@RequiredArgsConstructor
public class AdminTransactionController {

    private final TransactionService transactionService;

    @GetMapping
    public ResponseEntity<Page<TransactionResponseDto>> getAllTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        return ResponseEntity.ok(
                transactionService.getAllTransactions(page, size)
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
    public ResponseEntity<Page<TransactionResponseDto>> getTransactionsByStatus(
            @PathVariable TransactionStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                transactionService.getTransactionsByStatus(status,page,size)
        );
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<TransactionResponseDto>> getTransactionsByType(
            @PathVariable TransactionType type) {

        return ResponseEntity.ok(
                transactionService.getMyTransactionsByType(type)
        );
    }

    @PatchMapping("/{id}/approve")
    public ResponseEntity<String> approveTransaction(@PathVariable Long id) {
        transactionService.approveTransactionByAdmin(id);
        return ResponseEntity.ok("Transaction successfully approved and processed.");
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<String> rejectTransaction(@PathVariable Long id) {
        transactionService.rejectTransactionByAdmin(id);
        return ResponseEntity.ok("Transaction successfully rejected.");
    }

}