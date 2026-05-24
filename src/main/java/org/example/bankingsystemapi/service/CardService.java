package org.example.bankingsystemapi.service;

import lombok.RequiredArgsConstructor;
import org.example.bankingsystemapi.mapper.CardMapper;
import org.example.bankingsystemapi.model.dto.request.CardRequestDto;
import org.example.bankingsystemapi.model.dto.response.CardResponseDto;
import org.example.bankingsystemapi.model.entity.Account;
import org.example.bankingsystemapi.model.entity.Card;
import org.example.bankingsystemapi.model.enums.CardStatus;
import org.example.bankingsystemapi.repository.AccountRepository;
import org.example.bankingsystemapi.repository.CardRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final CardMapper cardMapper;
    private final AccountRepository accountRepository;

    public CardResponseDto createCard(Long accountId, CardRequestDto cardRequestDto) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        Card card = new Card();
        card.setAccount(account);
        card.setCardNumber(generateCardNumber());
        card.setCvv(generateCVV());
        card.setExpiryDate(generateExpiryDate());
        card.setCardType(cardRequestDto.getCardType());
        Card saveCard = cardRepository.save(card);
        return cardMapper.toDto(saveCard);
    }

    public CardResponseDto getCardById(Long id) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Card not found"));
        return cardMapper.toDto(card);
    }

    public List<CardResponseDto> getCardsByAccountId(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        return cardRepository.findCardsByAccount(account)
                .stream().map(cardMapper::toDto).toList();

    }

    public void blockCard(Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Card not found"));

        if (card.getCardStatus() == CardStatus.BLOCKED) {
            throw new RuntimeException("Card is already blocked");
        }
        card.setCardStatus(CardStatus.BLOCKED);
        cardRepository.save(card);

    }

    public void activeCard(Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Card not found"));

        if (card.getCardStatus() == CardStatus.ACTIVE) {
            throw new RuntimeException("Card is already active");
        }
        card.setCardStatus(CardStatus.ACTIVE);
        cardRepository.save(card);
    }

    private String generateCardNumber() {
        Random random = new Random();
        String cardNumber;

        do {
            StringBuilder stringBuilder = new StringBuilder("41697388");

            for (int i = 0; i < 8; i++) {
                stringBuilder.append(random.nextInt(10));
            }
            cardNumber = stringBuilder.toString();

        } while (cardRepository.existsByCardNumber(cardNumber));

        return cardNumber;
    }

    private String generateCVV() {
        Random random = new Random();
        int cvv = 100 + random.nextInt(900);
        return String.valueOf(cvv);
    }

    private LocalDate generateExpiryDate() {
        return LocalDate.now().plusYears(4);
    }


}
