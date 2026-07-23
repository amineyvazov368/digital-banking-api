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
            "(t.receiverAccount IS NOT NULL AND t.receiverAccount.user.id = :userId)" +
            "order by  t.createdAt DESC ")
    List<Transaction> findAllByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT t FROM Transaction t WHERE t.sendAccount.id = :accountId OR t.receiverAccount.id = :accountId")
    List<Transaction> findByAccountId(@Param("accountId") Long accountId);

    @Query("SELECT t FROM Transaction t WHERE " +
            "(t.sendAccount.user.email = :email OR t.receiverAccount.user.email = :email) " +
            "AND t.transactionType = :transactionType")
    List<Transaction> findAllByUserEmailAndTransactionType(
            @Param("email") String email,
            @Param("transactionType") TransactionType transactionType
    );


    @Query("SELECT t FROM Transaction t WHERE " +
            "(t.sendAccount.user.email = :email OR t.receiverAccount.user.email = :email) " +
            "AND (:accountId IS NULL OR t.sendAccount.id = :accountId OR t.receiverAccount.id = :accountId) " +
            "AND (:transactionType IS NULL OR t.transactionType = :transactionType)"+
            "order by  t.createdAt DESC "
    )
    List<Transaction> findTransactionsWithFilters(
            @Param("email") String email,
            @Param("accountId") Long accountId,
            @Param("transactionType") TransactionType transactionType
    );

    @Query("SELECT t FROM Transaction t " +
            "WHERE t.sendAccount IN (SELECT c.account FROM Card c WHERE c.cardNumber = :cardNumber) " +
            "OR t.receiverAccount IN (SELECT c.account FROM Card c WHERE c.cardNumber = :cardNumber)" +
            "order by  t.createdAt DESC ")
    List<Transaction> findAllByCardNumber(@Param("cardNumber") String cardNumber);

}
