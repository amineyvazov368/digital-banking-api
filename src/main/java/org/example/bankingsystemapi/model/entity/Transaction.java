package org.example.bankingsystemapi.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.bankingsystemapi.model.enums.TransactionStatus;
import org.example.bankingsystemapi.model.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "send_account_id")
    private Account sendAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "receiver_account_id")
    private Account receiverAccount;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    private String description;

    private LocalDateTime createdAt;

    @PrePersist
    private void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.status = TransactionStatus.PENDING;
    }
}
