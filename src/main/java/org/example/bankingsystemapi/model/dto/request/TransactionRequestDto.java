package org.example.bankingsystemapi.model.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.example.bankingsystemapi.model.entity.Account;

import java.math.BigDecimal;

@Data
public class TransactionRequestDto {

    @NotBlank(message = "Sender cardNumber is required")
    private String fromCardNumber;

    @NotBlank(message = "Receiver cardNumber is required")
    private String toCardNumber;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "1.0", message = "Amount must be greater than 0")
    private BigDecimal amount;

    private String description;

}
