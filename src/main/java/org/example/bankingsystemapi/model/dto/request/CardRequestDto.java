package org.example.bankingsystemapi.model.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.example.bankingsystemapi.model.entity.Account;
import org.example.bankingsystemapi.model.enums.CardForm;
import org.example.bankingsystemapi.model.enums.CardStatus;
import org.example.bankingsystemapi.model.enums.CardType;

@Data
public class CardRequestDto {

    @NotNull(message = "Card type is required")
    private CardType cardType;
    @NotNull(message = "Card form is required")
    private CardForm cardForm;


}
