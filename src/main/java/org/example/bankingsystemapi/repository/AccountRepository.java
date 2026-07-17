package org.example.bankingsystemapi.repository;

import org.example.bankingsystemapi.model.entity.Account;
import org.example.bankingsystemapi.model.enums.AccountStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    boolean existsByAccountNumber(String accountNumber);

    Optional<Account> findByAccountNumber(String accountNumber);

    Optional<Account> findByCardsCardNumber(String cardNumber);

    List<Account> findAccountByUserId(Long userId);

    List<Account> findAccountsByStatus(AccountStatus status);
}
