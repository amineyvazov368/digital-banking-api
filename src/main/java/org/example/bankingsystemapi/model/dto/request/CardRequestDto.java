package org.example.bankingsystemapi.model.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.example.bankingsystemapi.model.entity.Account;
import org.example.bankingsystemapi.model.enums.CardStatus;
import org.example.bankingsystemapi.model.enums.CardType;

@Data
public class CardRequestDto {
    @NotNull(message = "AccountId is required")
    private Long accountId;

    @NotNull(message = "Card type is required")
    private CardType cardType;


}
