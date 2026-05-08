package org.example.bankingsystemapi.model.dto.request;

import lombok.Data;
import org.example.bankingsystemapi.model.enums.Currency;

@Data
public class AccountRequestDto {

    private Long userId;

    private Currency currency;



}
