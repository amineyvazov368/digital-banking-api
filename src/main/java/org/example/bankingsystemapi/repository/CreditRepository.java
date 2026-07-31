package org.example.bankingsystemapi.repository;

import org.example.bankingsystemapi.model.entity.Credit;
import org.example.bankingsystemapi.model.enums.CreditStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface CreditRepository extends JpaRepository<Credit, Long> {

    List<Credit> findAllByAccountIdIn(List<Long> accountIds);

    List<Credit> findAllByStatus(CreditStatus status);

    List<Credit> findAllByStatusAndNextPaymentDateBefore(CreditStatus status, LocalDateTime nextPaymentDate);
}
