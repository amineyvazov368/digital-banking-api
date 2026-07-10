package org.example.bankingsystemapi.service;

import lombok.RequiredArgsConstructor;
import org.example.bankingsystemapi.mapper.CardMapper;
import org.example.bankingsystemapi.model.dto.request.CardRequestDto;
import org.example.bankingsystemapi.model.dto.response.CardResponseDto;
import org.example.bankingsystemapi.model.entity.Account;
import org.example.bankingsystemapi.model.entity.Card;
import org.example.bankingsystemapi.model.enums.AccountStatus;
import org.example.bankingsystemapi.model.enums.CardStatus;
import org.example.bankingsystemapi.repository.AccountRepository;
import org.example.bankingsystemapi.repository.CardRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
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

        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new RuntimeException("Account is not active");
        }
        if (account.getCards().size() >= 3) {
            throw new RuntimeException("Card limit reached");
        }

        Card card = cardMapper.toEntity(cardRequestDto, account);
        card.setCardNumber(generateCardNumber());
        card.setCvv(generateCVV());
        card.setExpiryDate(generateExpiryDate());
        Card saveCard = cardRepository.save(card);
        return cardMapper.toDto(saveCard);
    }

    @Transactional(readOnly = true)
    public CardResponseDto getCardById(Long id) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Card not found"));
        return cardMapper.toDto(card);
    }

    @Transactional(readOnly = true)
    public List<CardResponseDto> getCardsByAccountId(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        return cardRepository.findCardsByAccount(account)
                .stream().map(cardMapper::toDto).toList();

    }

    @Transactional(readOnly = true)
    public List<CardResponseDto> getActiveCardByAccountId(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        return cardRepository.findActiveCardsByAccount(account)
                .stream().map(cardMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public void deleteCardById(Long cardId, Long userId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Card not found"));
        validateOwnership(card, userId);

        card.setCardStatus(CardStatus.CLOSED);
        cardRepository.save(card);

    }

    @Transactional
    public void blockCard(Long cardId, Long userId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Card not found"));
        validateOwnership(card, userId);

        if (card.getCardStatus() == CardStatus.BLOCKED) {
            throw new RuntimeException("Card is already blocked");
        }
        card.setCardStatus(CardStatus.BLOCKED);
        cardRepository.save(card);

    }

    @Transactional
    public void activeCard(Long cardId, Long userId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Card not found"));

        validateOwnership(card, userId);

        if (card.getCardStatus() == CardStatus.ACTIVE) {
            throw new RuntimeException("Card is already active");
        }
        card.setCardStatus(CardStatus.ACTIVE);
        cardRepository.save(card);
    }

    public CardResponseDto replaceCard(Long cardId, Long userId) {
        Card oldCard = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Card not found"));

        validateOwnership(oldCard,userId);

        oldCard.setCardStatus(CardStatus.CLOSED);
        cardRepository.save(oldCard);


        Card newCard = new Card();
        newCard.setAccount(oldCard.getAccount());
        newCard.setCardNumber(generateCardNumber());
        newCard.setCvv(generateCVV());
        newCard.setExpiryDate(generateExpiryDate());
        newCard.setCardType(oldCard.getCardType());
        newCard.setCardStatus(CardStatus.ACTIVE);

        return cardMapper.toDto(cardRepository.save(newCard));

    }

    public String generateCardNumber() {
        SecureRandom random = new SecureRandom();
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

    public String generateCVV() {
        Random random = new SecureRandom();
        int cvv = 100 + random.nextInt(900);
        return String.valueOf(cvv);
    }

    public LocalDate generateExpiryDate() {
        return LocalDate.now().plusYears(4);
    }

    private void validateOwnership(Card card, Long userId) {
        if (!card.getAccount().getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }
    }


}
