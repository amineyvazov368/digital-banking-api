package org.example.bankingsystemapi.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.bankingsystemapi.model.enums.CreditStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreditResponse {
    private Long id;
    private Long accountId;
    private BigDecimal originalAmount;
    private BigDecimal totalAmount;
    private BigDecimal remainingAmount;
    private BigDecimal monthlyPayment;
    private Double interestRate;
    private LocalDateTime nextPaymentDate;
    private CreditStatus status;
}