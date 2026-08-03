package org.example.bankingsystemapi.controller;

import lombok.RequiredArgsConstructor;
import org.example.bankingsystemapi.model.dto.response.CreditResponse;
import org.example.bankingsystemapi.service.CreditService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/credits")
@RequiredArgsConstructor
//@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class UserCreditController {

    private final CreditService creditService;

    @PostMapping("/take")
    public ResponseEntity<CreditResponse> takeCredit(
            @RequestParam Long accountId,
            @RequestParam BigDecimal amount,
            @RequestParam Integer termMonths
    ) {
        CreditResponse creditResponse = creditService.takeCredit(accountId, amount, termMonths);
        return ResponseEntity.status(HttpStatus.CREATED).body(creditResponse);
    }

    @PostMapping("/{creditId}/pay")
    public ResponseEntity<CreditResponse> payCredit(
            @RequestParam Long accountId,
            @PathVariable    Long creditId,
            @RequestParam BigDecimal amount
    ) {
        CreditResponse creditResponse = creditService.payCredit(accountId, creditId, amount);
        return ResponseEntity.status(HttpStatus.CREATED).body(creditResponse);
    }

    @GetMapping("/my-credits")
    public ResponseEntity<List<CreditResponse>> getMyCredits() {
        return ResponseEntity.ok(creditService.getMyCredits());
    }


    @GetMapping("/my-credits/{creditId}")
    public ResponseEntity<CreditResponse> getMyCreditById(@PathVariable Long creditId) {
        return ResponseEntity.ok(creditService.getMyCreditById(creditId));
    }


}
