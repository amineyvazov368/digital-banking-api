package org.example.bankingsystemapi.mapper;

import org.example.bankingsystemapi.model.dto.request.CardRequestDto;
import org.example.bankingsystemapi.model.dto.response.AccountResponseDto;
import org.example.bankingsystemapi.model.dto.response.CardResponseDto;
import org.example.bankingsystemapi.model.entity.Account;
import org.example.bankingsystemapi.model.entity.Card;
import org.springframework.stereotype.Component;

@Component
public class CardMapper {

    public Card toEntity(CardRequestDto requestDto, Account account) {
        Card card = new Card();
        card.setCardType(requestDto.getCardType());
        card.setCardForm(requestDto.getCardForm());
        card.setAccount(account);
        return card;
    }

    public CardResponseDto toDto(Card card) {
        if (card == null) {
            return null;
        }

        CardResponseDto responseDto = new CardResponseDto();
        responseDto.setId(card.getId());
        responseDto.setCardNumber(card.getCardNumber());
        responseDto.setExpiryDate(card.getExpiryDate());
        responseDto.setCardType(card.getCardType());
        responseDto.setCardForm(card.getCardForm());
        responseDto.setCardStatus(card.getCardStatus());
        responseDto.setCreatedAt(card.getCreatedAt());

        if (card.getAccount() != null) {
            Account account = card.getAccount();
            responseDto.setAccountId(account.getId());

            AccountResponseDto accountDto = new AccountResponseDto();
            accountDto.setId(account.getId());
            accountDto.setAccountNumber(account.getAccountNumber());
            accountDto.setBalance(account.getBalance());
            accountDto.setCurrency(account.getCurrency());

            if (account.getUser() != null) {
                accountDto.setUserId(account.getUser().getId());

                String fullName = account.getUser().getName() + " " + account.getUser().getSurname();
                accountDto.setUserName(fullName);
            }
            responseDto.setAccount(accountDto);
        }

        return responseDto;
    }
}