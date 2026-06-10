package org.example.bankingsystemapi.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final TransactionMapper transactionMapper;

    @Transactional
    public String deposit(Long accountId, BigDecimal amount) {

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Amount must be greater than zero");
        }
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new RuntimeException("Account is not active");
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
    public String withdraw(Long accountId, BigDecimal amount) {

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Amount must be greater than zero");
        }

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new RuntimeException("Account is not active");
        }

        Transaction transaction = new Transaction();
        transaction.setSendAccount(account);
        transaction.setReceiverAccount(null);
        transaction.setAmount(amount);
        transaction.setTransactionType(TransactionType.WITHDRAW);
        transaction.setStatus(TransactionStatus.PENDING);
        transactionRepository.save(transaction);

        try {
            if (account.getBalance().compareTo(amount) < 0) {
                throw new RuntimeException("Insufficient balance");
            }
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
    public String transfer(Long fromAccountId, Long toAccountId, BigDecimal amount) {

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Amount must be greater than zero");
        }

        if (fromAccountId.equals(toAccountId)) {
            throw new RuntimeException("Cannot transfer to same account");
        }

        Account fromAccount = accountRepository.findById(fromAccountId)
                .orElseThrow(() -> new RuntimeException("Sender account not found"));

        Account toAccount = accountRepository.findById(toAccountId)
                .orElseThrow(() -> new RuntimeException("Receiver account not found"));

        if (fromAccount.getStatus() != AccountStatus.ACTIVE) {
            throw new RuntimeException("Account is not active");
        }

        if (toAccount.getStatus() != AccountStatus.ACTIVE) {
            throw new RuntimeException("Account is not active");
        }
        Transaction transaction = new Transaction();
        transaction.setSendAccount(fromAccount);
        transaction.setReceiverAccount(toAccount);
        transaction.setAmount(amount);
        transaction.setTransactionType(TransactionType.TRANSFER);
        transaction.setStatus(TransactionStatus.PENDING);
        transaction.setDescription(
                "Transfer from " + fromAccount.getAccountNumber()
                        + " to " + toAccount.getAccountNumber()
        );
        transactionRepository.save(transaction);

        try {

            if (fromAccount.getBalance().compareTo(amount) < 0) {
                throw new RuntimeException("Insufficient balance");
            }
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

            throw new RuntimeException("Transfer failed");
        }
    }

    public List<TransactionResponseDto> getTransactionHistory(Long accountId, int limit) {

        if (!accountRepository.existsById(accountId)) {
            throw new RuntimeException("Account not found");
        }

        Pageable pageable = PageRequest.of(0,limit);

        return transactionRepository.findBySendAccountIdOrReceiverAccountIdOrderByCreatedAtDesc(accountId, accountId,pageable)
                .stream().map(transactionMapper::toDto).toList();
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
