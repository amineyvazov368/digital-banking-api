package org.example.bankingsystemapi.repository;

import org.example.bankingsystemapi.model.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {


}
