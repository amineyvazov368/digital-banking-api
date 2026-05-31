package org.example.bankingsystemapi.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.bankingsystemapi.mapper.TransactionMapper;
import org.example.bankingsystemapi.model.dto.response.TransactionResponseDto;
import org.example.bankingsystemapi.model.entity.Account;
import org.example.bankingsystemapi.model.entity.Transaction;
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

    public String deposit(Long accountId, BigDecimal amount) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setReceiverAccount(account);
        transaction.setSendAccount(null);
        transaction.setAmount(amount);
        transaction.setTransactionType(TransactionType.DEPOSIT);
        transaction.setStatus(TransactionStatus.SUCCESS);
        transactionRepository.save(transaction);


        return "Deposit successful";
    }

    public String withdraw(Long accountId, BigDecimal amount) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        if (account.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        account.setBalance(account.getBalance().subtract(amount));
        accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setReceiverAccount(account);
        transaction.setReceiverAccount(null);
        transaction.setAmount(amount);
        transaction.setTransactionType(TransactionType.WITHDRAW);
        transaction.setStatus(TransactionStatus.SUCCESS);
        transactionRepository.save(transaction);

        return "Withdraw successful";
    }

    @Transactional
    public String transfer(Long fromAccountId, Long toAccountId, BigDecimal amount) {

        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Amount must be greater than zero");
        }

        if (fromAccountId.equals(toAccountId)) {
            throw new RuntimeException("Cannot transfer to same account");
        }

        Account fromAccount = accountRepository.findById(fromAccountId)
                .orElseThrow(() -> new RuntimeException("Sender account not found"));

        Account toAccount = accountRepository.findById(toAccountId)
                .orElseThrow(() -> new RuntimeException("Receiver account not found"));

        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }
        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        toAccount.setBalance(toAccount.getBalance().add(amount));
        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);
        Transaction transaction = new Transaction();
        transaction.setSendAccount(fromAccount);
        transaction.setReceiverAccount(toAccount);
        transaction.setAmount(amount);
        transaction.setTransactionType(TransactionType.TRANSFER);
        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction.setDescription(
                "Transfer from " + fromAccount.getAccountNumber()
                        + " to " + toAccount.getAccountNumber()
        );
        transactionRepository.save(transaction);


        return "Transfer successful";
    }

    public List<TransactionResponseDto> getTransactionHistory(Long accountId) {

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        return transactionRepository.findBySendAccountIdOrReceiverAccountId(accountId, accountId)
                .stream().map(transactionMapper::toDto).toList();
    }

    public TransactionResponseDto getTransactionById(Long transactionId) {

        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        return transactionMapper.toDto(transaction);
    }


}
