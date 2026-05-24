package org.example.bankingsystemapi.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.bankingsystemapi.mapper.AccountMapper;
import org.example.bankingsystemapi.model.dto.request.AccountRequestDto;
import org.example.bankingsystemapi.model.dto.response.AccountResponseDto;
import org.example.bankingsystemapi.model.entity.Account;
import org.example.bankingsystemapi.model.entity.Card;
import org.example.bankingsystemapi.model.entity.User;
import org.example.bankingsystemapi.model.enums.AccountStatus;
import org.example.bankingsystemapi.model.enums.Currency;
import org.example.bankingsystemapi.repository.AccountRepository;
import org.example.bankingsystemapi.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final UserRepository userRepository;

    public AccountResponseDto createAccount(Long userId, AccountRequestDto accountRequestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Account account = accountMapper.toEntity(accountRequestDto);
        account.setUser(user);
        account.setAccountNumber(generateAccountNumber());
        account.setBalance(BigDecimal.ZERO);
        account.setCurrency(accountRequestDto.getCurrency());
        Card card = new Card();
        account.setCards(List.of(card));
        Account savedAccount = accountRepository.save(account);
        return accountMapper.toDto(savedAccount);

    }

    public void createDefaultAccount(User user) {

        Account account = new Account();

        account.setUser(user);
        account.setAccountNumber(generateAccountNumber());
        account.setBalance(BigDecimal.ZERO);
        account.setCurrency(Currency.AZN);
        account.setStatus(AccountStatus.ACTIVE);

        accountRepository.save(account);
    }

    public AccountResponseDto getAccountById(Long id) {

        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        return accountMapper.toDto(account);
    }

    public AccountResponseDto getAccountByAccountNumber(String accountNumber) {

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        return accountMapper.toDto(account);
    }

    public List<AccountResponseDto> getAllAccounts() {
        List<AccountResponseDto> accounts = accountRepository.findAll()
                .stream().map(accountMapper::toDto)
                .toList();
        return accounts;
    }

    public List<AccountResponseDto> getAccountByUserid(Long userId) {
        List<AccountResponseDto> accounts = accountRepository.findAccountByUserId(userId)
                .stream().map(accountMapper::toDto).toList();
        return accounts;
    }

    public List<AccountResponseDto> getAccountsByStatus(AccountStatus status) {
        List<AccountResponseDto> accountResponseDtoList = accountRepository.findAccountsByStatus(status)
                .stream().map(accountMapper::toDto).toList();
        return accountResponseDtoList;
    }

    public void activeAccount(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        if (account.getStatus() != AccountStatus.ACTIVE) {
            account.setStatus(AccountStatus.ACTIVE);
            accountRepository.save(account);
        }
    }

    public void deactivateAccount(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        if (account.getStatus() != AccountStatus.BLOCKED) {
            account.setStatus(AccountStatus.BLOCKED);
            accountRepository.save(account);
        }
    }
    public void closeAccount(Long accountId) {
        Account account =  accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        if (account.getStatus() != AccountStatus.CLOSED) {
            account.setStatus(AccountStatus.CLOSED);
            accountRepository.save(account);
        }
    }

    public String deposit(Long accountId, BigDecimal amount) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);
        return "Deposit successful";
    }

    public String withdraw(Long accountId, BigDecimal amount) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        if (account.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");}

        account.setBalance(account.getBalance().subtract(amount));
        accountRepository.save(account);
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
        return "Transfer successful";
    }


    private String generateAccountNumber() {

        String accountNumber;

        do {

            Random random = new Random();

            accountNumber = "AZ" +
                    (100000000 + random.nextInt(900000000));

        } while (accountRepository
                .existsByAccountNumber(accountNumber));

        return accountNumber;
    }


}
