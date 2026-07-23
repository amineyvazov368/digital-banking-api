package org.example.bankingsystemapi.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardOwnerDto {
    private String cardNumber;
    private String ownerFullName;
}