package org.example.bankingsystemapi.mapper;

import org.example.bankingsystemapi.model.dto.response.CreditResponse;
import org.example.bankingsystemapi.model.entity.Credit;
import org.springframework.stereotype.Component;

@Component
public class CreditMapper {

    public CreditResponse toResponse(Credit credit) {
        if (credit == null) {
            return null;
        }

        return CreditResponse.builder()
                .id(credit.getId())
                .accountId(credit.getAccountId())
                .originalAmount(credit.getOriginalAmount())
                .totalAmount(credit.getTotalAmount())
                .remainingAmount(credit.getRemainingAmount())
                .monthlyPayment(credit.getMonthlyPayment())
                .interestRate(credit.getInterestRate())
                .nextPaymentDate(credit.getNextPaymentDate())
                .status(credit.getStatus())
                .build();
    }
}
