package org.example.bankingsystemapi.controller;

import lombok.RequiredArgsConstructor;
import org.example.bankingsystemapi.model.dto.request.TransactionRequestDto;
import org.example.bankingsystemapi.model.dto.response.TransactionResponseDto;
import org.example.bankingsystemapi.model.enums.TransactionType;
import org.example.bankingsystemapi.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
            @RequestBody TransactionRequestDto request) {
        return ResponseEntity.ok(
                transactionService.deposit(
                        request.getToCardNumber(),
                        request.getAmount()
                )
        );
    }

    @PostMapping("/withdraw")
    public ResponseEntity<String> withdraw(
            @RequestBody TransactionRequestDto request) {
        return ResponseEntity.ok(
                transactionService.withdraw(
                        request.getFromCardNumber(),
                        request.getAmount()
                )
        );
    }

    @PostMapping("/transfer")
    public ResponseEntity<String> transfer(
            @RequestBody TransactionRequestDto transactionRequestDto) {

        return ResponseEntity.ok(
                transactionService.transferByCardNumber(
                        transactionRequestDto.getFromCardNumber(),
                        transactionRequestDto.getToCardNumber(),
                        transactionRequestDto.getAmount())
        );
    }

//    @GetMapping("/history/{accountId}")
//    public ResponseEntity<List<TransactionResponseDto>> getHistory(
//            @PathVariable Long accountId,
//            @RequestParam(required = false) String type,
//            @RequestParam(required = false) String search,
//            @RequestParam(defaultValue = "10") int limit) {
//
//        return ResponseEntity.ok(
//                transactionService.getTransactionHistory(accountId, limit)
//        );
//    }

//    @GetMapping("/history/my")
//    public ResponseEntity<List<TransactionResponseDto>> getMyTransactionHistory(
//            Authentication authentication,
//            @RequestParam(required = false) String type,
//            @RequestParam(required = false) String search,
//            @RequestParam(defaultValue = "10") int limit) {
//
//        if (authentication == null) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//        }
//        String email = authentication.getName();
//
//        List<TransactionResponseDto> history = transactionService.getMyTransactionHistory(email, type, search, limit);
//        return ResponseEntity.ok(history);
//    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionResponseDto> getTransactionById(
            @PathVariable Long transactionId) {

        return ResponseEntity.ok(
                transactionService.getTransactionById(transactionId)
        );
    }
    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<TransactionResponseDto>> getMyTransactionsByAccount(@PathVariable Long accountId) {
        List<TransactionResponseDto> transactions = transactionService.getMyTransactionsByAccountId(accountId);
        return ResponseEntity.ok(transactions);
    }


    @GetMapping("/type")
    public ResponseEntity<List<TransactionResponseDto>> getMyTransactionsByType(@RequestParam TransactionType type) {
        List<TransactionResponseDto> transactions = transactionService.getMyTransactionsByType(type);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/cardNumber")
    public ResponseEntity<List<TransactionResponseDto>> getMyTransactionsByCardNumber(@RequestParam String cardNumber) {
        List<TransactionResponseDto> transactions = transactionService.getMyTransactionsByCardNumber(cardNumber);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/history/my")
    public ResponseEntity<List<TransactionResponseDto>> getMyTransactions(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) Long accountId,
            @RequestParam(required = false) String type) {

        TransactionType txType = null;
        if (type != null && !type.trim().isEmpty() && !type.equalsIgnoreCase("ALL")) {
            txType = TransactionType.valueOf(type.toUpperCase());
        }

        String email = userDetails.getUsername();

        List<TransactionResponseDto> transactions = transactionService.getFilteredTransactions(email, accountId, txType);
        return ResponseEntity.ok(transactions);
    }


}
