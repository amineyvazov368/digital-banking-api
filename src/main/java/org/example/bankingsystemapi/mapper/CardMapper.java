package org.example.bankingsystemapi.mapper;

import org.example.bankingsystemapi.model.dto.request.CardRequestDto;
import org.example.bankingsystemapi.model.dto.response.CardResponseDto;
import org.example.bankingsystemapi.model.entity.Account;
import org.example.bankingsystemapi.model.entity.Card;

import java.time.LocalDate;
import java.util.Random;

public class CardMapper {

    public Card toEntity(CardRequestDto requestDto, Account account) {
        Card card = new Card();
        card.setCardType(requestDto.getCardType());
        card.setCardNumber(generateCardNumber());
        card.setCvv(generatedCvv());
        card.setExpiryDate(LocalDate.now().plusYears(5));
        return card;

    }

    private static String generateCardNumber() {
        Random random = new Random();

        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < 16; i++) {
            stringBuilder.append(random.nextInt(10));
        }
        return stringBuilder.toString();
    }

    private static String generatedCvv() {
        Random random = new Random();

        int cvv = 100 + random.nextInt(900);
        return String.valueOf(cvv);
    }

    public CardResponseDto toDto(Card card) {
        CardResponseDto responseDto = new CardResponseDto();
        responseDto.setCardNumber(card.getCardNumber());
        responseDto.setExpiryDate(card.getExpiryDate());
        responseDto.setCardType(card.getCardType());
        responseDto.setCardStatus(card.getCardStatus());
        responseDto.setCreatedAt(card.getCreatedAt());
        responseDto.setAccountId(card.getAccount().getId());
        return responseDto;
    }


}
