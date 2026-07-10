package org.example.bankingsystemapi.mapper;

import lombok.RequiredArgsConstructor;
import org.example.bankingsystemapi.model.dto.request.CardRequestDto;
import org.example.bankingsystemapi.model.dto.response.CardResponseDto;
import org.example.bankingsystemapi.model.entity.Account;
import org.example.bankingsystemapi.model.entity.Card;
import org.example.bankingsystemapi.service.CardService;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class CardMapper {


    public Card toEntity(CardRequestDto requestDto, Account account) {
        Card card = new Card();
        card.setCardType(requestDto.getCardType());
        card.setAccount(account);
        return card;

    }



    public CardResponseDto toDto(Card card) {
        CardResponseDto responseDto = new CardResponseDto();
        responseDto.setCardNumber(card.getCardNumber());
        responseDto.setExpiryDate(card.getExpiryDate());
        responseDto.setCardType(card.getCardType());
        responseDto.setCardForm(card.getCardForm());
        responseDto.setCardStatus(card.getCardStatus());
        responseDto.setCreatedAt(card.getCreatedAt());
        responseDto.setAccountId(card.getAccount().getId());
        return responseDto;
    }


}
