package org.example.bankingsystemapi.controller.admin;

import lombok.RequiredArgsConstructor;
import org.example.bankingsystemapi.model.dto.response.CreditResponse;
import org.example.bankingsystemapi.model.enums.CreditStatus;
import org.example.bankingsystemapi.service.CreditService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/admin/credit")
@RequiredArgsConstructor
public class AdminCreditController {

    private final CreditService creditService;

    @GetMapping
    public ResponseEntity<Page<CreditResponse>> getAllCredits(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(
                creditService.getAllCreditsForAdmin(page, size)
        );

    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<CreditResponse>> getCreditByStatus(@PathVariable CreditStatus status){
        return ResponseEntity.ok(
                creditService.getCreditsByStatusForAdmin(status)
        );
    }


}
