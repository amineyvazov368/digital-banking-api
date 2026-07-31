package org.example.bankingsystemapi.model.dto.response;

import lombok.Data;
import org.example.bankingsystemapi.model.enums.TransactionStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionResponseDto {
    private Long id;

    private String senderAccountNumber;
    private String senderName;

    private String receiverAccountNumber;
    private String receiverName;

    private BigDecimal amount;

    private TransactionStatus status;

    private String description;

    private LocalDateTime createdAt;
}
