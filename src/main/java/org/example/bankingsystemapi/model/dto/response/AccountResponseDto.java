package org.example.bankingsystemapi.model.dto.response;

import lombok.Data;
import org.example.bankingsystemapi.model.enums.AccountStatus;
import org.example.bankingsystemapi.model.enums.Currency;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class AccountResponseDto {

    private Long id;
    private String accountNumber;
    private BigDecimal balance;
    private Currency currency;
    private AccountStatus accountStatus;
    private Long userId;
    private String userName;
    private LocalDateTime createdAt;
    private List<CardResponseDto> cards;



}
