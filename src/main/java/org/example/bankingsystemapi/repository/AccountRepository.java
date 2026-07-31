package org.example.bankingsystemapi.repository;

import org.example.bankingsystemapi.model.entity.Account;
import org.example.bankingsystemapi.model.entity.User;
import org.example.bankingsystemapi.model.enums.AccountStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    boolean existsByAccountNumber(String accountNumber);

    Optional<Account> findByAccountNumber(String accountNumber);

    List<Account> findByUserEmailAndStatus(String email, AccountStatus status);
    long countByUserEmailAndStatus(String email, AccountStatus status);

    List<Account> findAllByUserId(Long userId);

    Optional<Account> findByCardsCardNumber(String cardNumber);

    List<Account> findByUserId(Long userId);

    List<Account> findAccountByUserId(Long userId);

    List<Account> findAccountsByStatus(AccountStatus status);

    boolean existsByIdAndUserEmail(Long id, String email);

    Long countByUserEmail(String email);

    void deleteAllByUser(User user);
}
