package org.example.bankingsystemapi.service;

import lombok.RequiredArgsConstructor;
import org.example.bankingsystemapi.exceptions.BadRequestException;
import org.example.bankingsystemapi.exceptions.ForbiddenException;
import org.example.bankingsystemapi.exceptions.NotFoundException;
import org.example.bankingsystemapi.mapper.CardMapper;
import org.example.bankingsystemapi.model.dto.request.CardRequestDto;
import org.example.bankingsystemapi.model.dto.response.CardOwnerDto;
import org.example.bankingsystemapi.model.dto.response.CardResponseDto;
import org.example.bankingsystemapi.model.entity.Account;
import org.example.bankingsystemapi.model.entity.Card;
import org.example.bankingsystemapi.model.entity.User;
import org.example.bankingsystemapi.model.enums.AccountStatus;
import org.example.bankingsystemapi.model.enums.CardStatus;
import org.example.bankingsystemapi.repository.AccountRepository;
import org.example.bankingsystemapi.repository.CardRepository;
import org.example.bankingsystemapi.repository.UserRepository;
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
    private final UserRepository userRepository;

    public CardResponseDto createCard(Long accountId, CardRequestDto cardRequestDto) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new NotFoundException("Account not found" + accountId));

        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new BadRequestException("Account is not active");
        }
        long cardCount = cardRepository.countByAccountAndCardStatus(account, CardStatus.ACTIVE);
        if (cardCount >= 3) {
            throw new BadRequestException("Card count exceeds 3");
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
                .orElseThrow(() -> new NotFoundException("Card not found"+ id));
        return cardMapper.toDto(card);
    }

    @Transactional(readOnly = true)
    public List<CardResponseDto> getCardsByAccountId(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new NotFoundException("Account not found"+ accountId));
        List<CardStatus> allowStatus = List.of(CardStatus.ACTIVE,CardStatus.BLOCKED);

        return cardRepository.findCardsByAccountAndCardStatusIn(account,allowStatus)
                .stream().map(cardMapper::toDto).toList();

    }

    @Transactional(readOnly = true)
    public List<CardResponseDto> getCardsByAccountIdAndStatus(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new NotFoundException("Account not found" + accountId));

        return cardRepository.findCardsByAccountAndCardStatus(account,CardStatus.BLOCKED)
                .stream().map(cardMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<CardResponseDto> getActiveCardByAccountId(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new NotFoundException("Account not found" + accountId));
        return cardRepository.findActiveCardsByAccount(account)
                .stream().map(cardMapper::toDto)
                .toList();
    }

    @Transactional()
    public void deleteCardById(Long cardId, Long userId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new NotFoundException("Card not found"+ cardId));
        validateOwnership(card, userId);

        card.setCardStatus(CardStatus.CLOSED);
        cardRepository.save(card);

    }

    @Transactional
    public void blockCard(Long cardId, Long userId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new NotFoundException("Card not found"+ cardId));
        validateOwnership(card, userId);

        if (card.getCardStatus() == CardStatus.BLOCKED) {
            throw new BadRequestException("Card is already blocked");
        }
        card.setCardStatus(CardStatus.BLOCKED);
        cardRepository.save(card);

    }

    @Transactional
    public void activeCard(Long cardId, Long userId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new NotFoundException("Card not found"+ cardId));

        validateOwnership(card, userId);

        if (card.getCardStatus() == CardStatus.ACTIVE) {
            throw new BadRequestException("Card is already active");
        }
        card.setCardStatus(CardStatus.ACTIVE);
        cardRepository.save(card);
    }

    @Transactional
    public CardResponseDto replaceCard(Long cardId, Long userId) {
        Card oldCard = cardRepository.findById(cardId)
                .orElseThrow(() -> new NotFoundException("Card not found" + cardId));

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

    public List<CardResponseDto> getCardsByUsername(String username) {
        User user = userRepository.findByEmail(username); // və ya findByUsername
        if (user == null) {
            throw new NotFoundException("User not found" + username);
        }

        List<CardStatus> allowStatus = List.of(CardStatus.ACTIVE,CardStatus.BLOCKED);

        List<Card> cards = cardRepository.findByAccount_User_IdAndCardStatusIn(user.getId(),allowStatus);

        return cards.stream()
                .map(cardMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public CardOwnerDto getCardOwnerByNumber(String cardNumber) {
        Card card = cardRepository.findCardWithUserByCardNumber(cardNumber)
                .orElseThrow(() -> new NotFoundException("Kart tapılmadı: " + cardNumber));

        User user = card.getAccount().getUser();

        String fullName = (user.getName() + " " + (user.getSurname() != null ? user.getSurname() : "")).trim();

        return new CardOwnerDto(card.getCardNumber(), fullName);
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
            throw new ForbiddenException("Unauthorized"+ card.getId());
        }
    }


}
