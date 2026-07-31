package org.example.bankingsystemapi.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.bankingsystemapi.model.enums.CreditStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "credits")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Credit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long accountId;
    private BigDecimal originalAmount;
    private BigDecimal totalAmount;
    private BigDecimal remainingAmount;
    private BigDecimal monthlyPayment;
    private Double interestRate;
    private LocalDateTime nextPaymentDate;

    @Enumerated(EnumType.STRING)
    private CreditStatus status;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
