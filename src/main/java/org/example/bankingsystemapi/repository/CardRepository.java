package org.example.bankingsystemapi.repository;

import org.example.bankingsystemapi.model.entity.Account;
import org.example.bankingsystemapi.model.entity.Card;
import org.example.bankingsystemapi.model.enums.CardStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long> {

    boolean existsByCardNumber(String cardNumber);

    Optional<Card> findByCardNumber(String cardNumber);

    List<Card> findCardsByAccountAndCardStatusIn(Account account, List<CardStatus> statuses);
   List<Card> findActiveCardsByAccount(Account account);
   List<Card> findCardsByAccountAndCardStatus(Account account,CardStatus cardStatus);

    List<Card> findByAccount_User_IdAndCardStatusIn(Long ownerId, List<CardStatus> status);

   Long countByAccountAndCardStatus(Account account, CardStatus cardStatus);

    @Query("SELECT c FROM Card c " +
            "JOIN FETCH c.account a " +
            "JOIN FETCH a.user u " +
            "WHERE c.cardNumber = :cardNumber")
    Optional<Card> findCardWithUserByCardNumber(@Param("cardNumber") String cardNumber);
}
