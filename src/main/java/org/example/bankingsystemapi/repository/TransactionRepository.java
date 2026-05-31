package org.example.bankingsystemapi.repository;

import org.example.bankingsystemapi.model.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findBySendAccountIdOrReceiverAccountId(Long sendAccountId, Long receiveAccountId);

}
