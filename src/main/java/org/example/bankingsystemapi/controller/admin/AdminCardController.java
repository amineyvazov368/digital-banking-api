package org.example.bankingsystemapi.controller.admin;

import lombok.RequiredArgsConstructor;
import org.example.bankingsystemapi.model.dto.response.CardResponseDto;
import org.example.bankingsystemapi.service.CardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/cards")
@RequiredArgsConstructor
public class AdminCardController {

    private final CardService cardService;

    @GetMapping("/{id}")
    public ResponseEntity<CardResponseDto> getCardById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                cardService.getCardById(id)
        );
    }

    @GetMapping
    public ResponseEntity<List<CardResponseDto>> getAllCards() {
       return ResponseEntity.ok(cardService.getAllCards());
    }

    @GetMapping("/account/{accountId}/active")
    public ResponseEntity<List<CardResponseDto>> getActiveCards(
            @PathVariable Long accountId) {

        return ResponseEntity.ok(
                cardService.getActiveCardByAccountId(accountId)
        );
    }

    @PatchMapping("/{cardId}/block")
    public ResponseEntity<Void> blockCard(
            @PathVariable Long cardId,
            @RequestParam(required = false) Long userId) {

        cardService.blockCard(cardId, userId);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{cardId}")
    public ResponseEntity<Void> deleteCard(
            @PathVariable Long cardId,
            @RequestParam Long userId) {

        cardService.deleteCardById(cardId, userId);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{cardId}/activate")
    public ResponseEntity<Void> activateCard(
            @PathVariable Long cardId,
            @RequestParam Long userId) {

        cardService.activeCard(cardId, userId);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<CardResponseDto>> getCardsByAccountId(
            @PathVariable Long accountId) {

        return ResponseEntity.ok(
                cardService.getCardsByAccountId(accountId)
        );
    }

}