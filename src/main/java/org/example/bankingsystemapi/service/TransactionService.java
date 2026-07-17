package org.example.bankingsystemapi.service;

import org.example.bankingsystemapi.model.entity.Card;
import org.example.bankingsystemapi.model.entity.User;
import org.example.bankingsystemapi.model.enums.CardStatus;
import org.example.bankingsystemapi.repository.CardRepository;
import org.example.bankingsystemapi.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.bankingsystemapi.mapper.TransactionMapper;
import org.example.bankingsystemapi.model.dto.response.TransactionResponseDto;
import org.example.bankingsystemapi.model.entity.Account;
import org.example.bankingsystemapi.model.entity.Transaction;
import org.example.bankingsystemapi.model.enums.AccountStatus;
import org.example.bankingsystemapi.model.enums.TransactionStatus;
import org.example.bankingsystemapi.model.enums.TransactionType;
import org.example.bankingsystemapi.repository.AccountRepository;
import org.example.bankingsystemapi.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final TransactionMapper transactionMapper;
    private final CardRepository cardRepository;
    private final UserRepository userRepository;

    @Transactional
    public String deposit(String cardNumber, BigDecimal amount) {

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Amount must be greater than zero");
        }

        Account account = accountRepository.findByCardsCardNumber(cardNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new RuntimeException("Account is not active");
        }

        Card card = cardRepository.findByCardNumber(cardNumber)
                .orElseThrow(() -> new RuntimeException("Card not found"));
        if (card.getCardStatus() != CardStatus.ACTIVE){
            throw new RuntimeException("Card is not active");
        }
        Transaction transaction = new Transaction();
        transaction.setReceiverAccount(account);
        transaction.setSendAccount(null);
        transaction.setAmount(amount);
        transaction.setTransactionType(TransactionType.DEPOSIT);
        transaction.setStatus(TransactionStatus.PENDING);
        transactionRepository.save(transaction);

        try {
            account.setBalance(account.getBalance().add(amount));
            accountRepository.save(account);

            transaction.setStatus(TransactionStatus.SUCCESS);
            transactionRepository.save(transaction);

            return "Deposit successful";
        } catch (RuntimeException e) {
            transaction.setStatus(TransactionStatus.FAILED);
            transactionRepository.save(transaction);
            throw e;
        }
    }

    @Transactional
    public String withdraw(String cardNumber, BigDecimal amount) {

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Amount must be greater than zero");
        }

        Account account = accountRepository.findByCardsCardNumber(cardNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new RuntimeException("Account is not active");
        }

        Card card = cardRepository.findByCardNumber(cardNumber)
                .orElseThrow(() -> new RuntimeException("Card not found"));

        if (card.getCardStatus() != CardStatus.ACTIVE){
            throw new RuntimeException("Card is not active");
        }

        if (account.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        Transaction transaction = new Transaction();
        transaction.setSendAccount(account);
        transaction.setReceiverAccount(null);
        transaction.setAmount(amount);
        transaction.setTransactionType(TransactionType.WITHDRAW);
        transaction.setStatus(TransactionStatus.PENDING);
        transactionRepository.save(transaction);

        try {
            account.setBalance(account.getBalance().subtract(amount));
            accountRepository.save(account);

            transaction.setStatus(TransactionStatus.SUCCESS);
            transactionRepository.save(transaction);

            return "Withdraw successful";
        } catch (RuntimeException e) {
            transaction.setStatus(TransactionStatus.FAILED);
            transactionRepository.save(transaction);
            throw e;
        }
    }
    @Transactional
    public String transferByCardNumber(String fromCardNumber, String toCardNumber, BigDecimal amount) {

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Amount must be greater than zero");
        }

        if (fromCardNumber.equals(toCardNumber)) {
            throw new RuntimeException("Cannot transfer to same account");
        }

        Account fromAccount = accountRepository.findByCardsCardNumber(fromCardNumber)
                .orElseThrow(() -> new RuntimeException("Sender account not found"));

        Account toAccount = accountRepository.findByCardsCardNumber(toCardNumber)
                .orElseThrow(() -> new RuntimeException("Receiver account not found"));

        if (fromAccount.getStatus() != AccountStatus.ACTIVE) {
            throw new RuntimeException("Sender account is not active");
        }

        if (toAccount.getStatus() != AccountStatus.ACTIVE) {
            throw new RuntimeException("Receiver account is not active");
        }

        Card fromCard = cardRepository.findByCardNumber(fromCardNumber)
                .orElseThrow(() -> new RuntimeException("Sender card not found"));
        if (fromCard.getCardStatus() != CardStatus.ACTIVE) {
            throw new RuntimeException("Sender card is not active");
        }

        Card toCard = cardRepository.findByCardNumber(toCardNumber)
                .orElseThrow(() -> new RuntimeException("Receiver card not found"));
        if (toCard.getCardStatus() != CardStatus.ACTIVE) {
            throw new RuntimeException("Receiver card is not active");
        }

        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        Transaction transaction = new Transaction();
        transaction.setSendAccount(fromAccount);
        transaction.setReceiverAccount(toAccount);
        transaction.setAmount(amount);
        transaction.setTransactionType(TransactionType.TRANSFER);
        transaction.setStatus(TransactionStatus.PENDING);
        transaction.setDescription("Transfer from card " + fromCardNumber + " to card " + toCardNumber);
        transactionRepository.save(transaction);

        try {
            fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
            toAccount.setBalance(toAccount.getBalance().add(amount));

            accountRepository.save(fromAccount);
            accountRepository.save(toAccount);

            transaction.setStatus(TransactionStatus.SUCCESS);
            transactionRepository.save(transaction);

            return "Transfer successful";
        } catch (Exception e) {

            transaction.setStatus(TransactionStatus.FAILED);
            transactionRepository.save(transaction);

            throw new RuntimeException("Transfer failed: " + e.getMessage());
        }
    }

//    public List<TransactionResponseDto> getTransactionHistory(Long accountId, int limit) {
//
//        if (!accountRepository.existsById(accountId)) {
//            throw new RuntimeException("Account not found");
//        }
//
//        Pageable pageable = PageRequest.of(0,limit);
//
//        return transactionRepository.findBySendAccountIdOrReceiverAccountIdOrderByCreatedAtDesc(accountId, accountId,pageable)
//                .stream().map(transactionMapper::toDto).toList();
//    }

    public List<TransactionResponseDto> getMyTransactionHistory(String email, String type, String search, int limit) {
        // 1. İstifadəçini tapırıq
        User user = userRepository.findByEmail(email);

        if (user ==null){
            throw new RuntimeException("User not found");
        }

        // 2. Səhifələmə (limit) təyin edirik (məsələn, ən son tranzaksiyalar öncə gəlsin deyə)
        Pageable pageable = PageRequest.of(0, limit, Sort.by("createdAt").descending());

        // 3. İstifadəçinin ID-sinə görə tranzaksiyaları bazadan çəkirik
        List<Transaction> transactions = transactionRepository.findAllByUserId(user.getId(), pageable);

        // 4. Entity-ləri DTO-ya çevirib geri qaytarırıq
        return transactions.stream()
                .map(transactionMapper::toDto) // Sənin map etmə məntiqin
                .collect(Collectors.toList());
    }

    public TransactionResponseDto getTransactionById(Long transactionId) {

        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        return transactionMapper.toDto(transaction);
    }

    public List<TransactionResponseDto> getAllTransactions() {
        return transactionRepository.findAll()
                .stream().map(transactionMapper::toDto)
                .toList();
    }

    public List<TransactionResponseDto> getTransactionsByStatus(TransactionStatus status) {
        return transactionRepository.findByStatus(status)
                .stream().map(transactionMapper::toDto)
                .toList();

    }

    public List<TransactionResponseDto> getTransactionsByTransactionType(TransactionType transactionType) {
        return transactionRepository.findByTransactionType(transactionType)
                .stream().map(transactionMapper::toDto)
                .toList();
    }


}
