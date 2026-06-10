package org.example.bankingsystemapi.repository;

import org.example.bankingsystemapi.model.entity.Account;
import org.example.bankingsystemapi.model.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CardRepository extends JpaRepository<Card, Long> {

    boolean existsByCardNumber(String cardNumber);

   List<Card> findCardsByAccount(Account account);

   List<Card> findActiveCardsByAccount(Account account);
}
