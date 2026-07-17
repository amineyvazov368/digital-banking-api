package org.example.bankingsystemapi.repository;

import org.example.bankingsystemapi.model.entity.Transaction;
import org.example.bankingsystemapi.model.enums.TransactionStatus;
import org.example.bankingsystemapi.model.enums.TransactionType;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findBySendAccountIdOrReceiverAccountIdOrderByCreatedAtDesc(Long sendAccountId, Long receiveAccountId, Pageable pageable);

    List<Transaction> findByStatus(TransactionStatus status);

    List<Transaction> findByTransactionType(TransactionType transactionType);

    @Query("SELECT t FROM Transaction t WHERE " +
            "(t.sendAccount IS NOT NULL AND t.sendAccount.user.id = :userId) OR " +
            "(t.receiverAccount IS NOT NULL AND t.receiverAccount.user.id = :userId)")
    List<Transaction> findAllByUserId(@Param("userId") Long userId, Pageable pageable);



}
