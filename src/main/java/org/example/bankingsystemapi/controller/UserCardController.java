package org.example.bankingsystemapi.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.example.bankingsystemapi.model.dto.request.CardRequestDto;
import org.example.bankingsystemapi.model.dto.response.CardOwnerDto;
import org.example.bankingsystemapi.model.dto.response.CardResponseDto;
import org.example.bankingsystemapi.service.CardService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class UserCardController {

    private final CardService cardService;

    @PostMapping("/{accountId}")
    public ResponseEntity<CardResponseDto> createCard(
            @PathVariable Long accountId,
           @Valid @RequestBody CardRequestDto requestDto) {

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

    @GetMapping("/my")
    public ResponseEntity<List<CardResponseDto>> getMyCards(Authentication authentication) {
        String username = authentication.getName();

        return ResponseEntity.ok(
                cardService.getCardsByUsername(username)
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

    @PatchMapping("/{cardId}/activate")
    public ResponseEntity<Void> activateCard(
            @PathVariable Long cardId,
            @RequestParam Long userId) {

        cardService.activeCard(cardId, userId);

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{cardId}/block")
    public ResponseEntity<Void> blockCard(
            @PathVariable Long cardId,
            @RequestParam Long userId) {

        cardService.blockCard(cardId, userId);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/owner/{cardNumber}")
    public ResponseEntity<CardOwnerDto> getCardOwner(@PathVariable String cardNumber) {
        CardOwnerDto cardOwner = cardService.getCardOwnerByNumber(cardNumber);
        return ResponseEntity.ok(cardOwner);
    }

}