package org.example.bankingsystemapi.controller;

import lombok.RequiredArgsConstructor;
import org.example.bankingsystemapi.model.dto.request.CardRequestDto;
import org.example.bankingsystemapi.model.dto.response.CardResponseDto;
import org.example.bankingsystemapi.service.CardService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class UserCardController {

    private final CardService cardService;

    @PostMapping("/{accountId}")
    public ResponseEntity<CardResponseDto> createCard(
            @PathVariable Long accountId,
            @RequestBody CardRequestDto requestDto) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cardService.createCard(accountId, requestDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CardResponseDto> getCardById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                cardService.getCardById(id)
        );
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<CardResponseDto>> getCardsByAccountId(
            @PathVariable Long accountId) {

        return ResponseEntity.ok(
                cardService.getCardsByAccountId(accountId)
        );
    }

    @DeleteMapping("/{cardId}")
    public ResponseEntity<Void> deleteCard(
            @PathVariable Long cardId,
            @RequestParam Long userId) {

        cardService.deleteCardById(cardId, userId);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{cardId}/replace")
    public ResponseEntity<CardResponseDto> replaceCard(
            @PathVariable Long cardId,
            @RequestParam Long userId) {

        return ResponseEntity.ok(
                cardService.replaceCard(cardId, userId)
        );
    }

}