package org.example.bankingsystemapi.model.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.example.bankingsystemapi.model.enums.Currency;

@Data
public class AccountRequestDto {

    @NotNull(message = "UserId is required")
    private Long userId;

    @NotNull(message = "Currency is required")
    private Currency currency;


}
