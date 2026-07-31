package org.example.bankingsystemapi.model.dto.response;

import lombok.Data;
import org.example.bankingsystemapi.model.enums.CardForm;
import org.example.bankingsystemapi.model.enums.CardStatus;
import org.example.bankingsystemapi.model.enums.CardType;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CardResponseDto {

    private Long id;
    private String cardNumber;
    private LocalDate expiryDate;
    private CardType cardType;
    private CardStatus cardStatus;
    private LocalDateTime createdAt;
    private Long accountId;
    private CardForm cardForm;

    private AccountResponseDto account;
}
