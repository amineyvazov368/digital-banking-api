package org.example.bankingsystemapi.service;

import lombok.RequiredArgsConstructor;
import org.example.bankingsystemapi.exceptions.BadRequestException;
import org.example.bankingsystemapi.exceptions.NotFoundException;
import org.example.bankingsystemapi.mapper.AccountMapper;
import org.example.bankingsystemapi.model.dto.request.AccountRequestDto;
import org.example.bankingsystemapi.model.dto.response.AccountResponseDto;
import org.example.bankingsystemapi.model.entity.Account;
import org.example.bankingsystemapi.model.entity.User;
import org.example.bankingsystemapi.model.enums.AccountStatus;
import org.example.bankingsystemapi.model.enums.Currency;
import org.example.bankingsystemapi.repository.AccountRepository;
import org.example.bankingsystemapi.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final UserRepository userRepository;

    private String getAuthenticatedUserEmail() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }
        return principal.toString();
    }

    public AccountResponseDto createAccount(Long userId, AccountRequestDto accountRequestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

        long activeAccountCount = accountRepository.countByUserEmailAndStatus(user.getEmail(), AccountStatus.ACTIVE);
        if (activeAccountCount >= 3) {
            throw new BadRequestException("Active account limit reached for this user (Maximum 3 accounts allowed)");
        }

        Account account = accountMapper.toEntity(accountRequestDto);
        account.setUser(user);
        account.setAccountNumber(generateAccountNumber());
        account.setBalance(BigDecimal.ZERO);
        account.setCurrency(accountRequestDto.getCurrency());
        account.setStatus(AccountStatus.ACTIVE);

        Account savedAccount = accountRepository.save(account);
        return accountMapper.toDto(savedAccount);
    }

    public void createDefaultAccount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

        Account account = new Account();
        account.setUser(user);
        account.setAccountNumber(generateAccountNumber());
        account.setBalance(BigDecimal.ZERO);
        account.setCurrency(Currency.AZN);
        account.setStatus(AccountStatus.ACTIVE);

        accountRepository.save(account);
    }

    @Transactional(readOnly = true)
    public BigDecimal getBalance(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new NotFoundException("Account not found with id: " + accountId));
        return account.getBalance();
    }

    @Transactional(readOnly = true)
    public AccountResponseDto getAccountById(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Account not found with id: " + id));
        return accountMapper.toDto(account);
    }

    @Transactional(readOnly = true)
    public AccountResponseDto getAccountByAccountNumber(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new NotFoundException("Account not found with account number: " + accountNumber));
        return accountMapper.toDto(account);
    }

    @Transactional(readOnly = true)
    public List<AccountResponseDto> getAllAccounts() {
        return accountRepository.findAll()
                .stream()
                .map(accountMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AccountResponseDto> getMyAccounts() {
        String currentUserEmail = getAuthenticatedUserEmail();

        return accountRepository.findByUserEmailAndStatus(currentUserEmail, AccountStatus.ACTIVE)
                .stream()
                .map(accountMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AccountResponseDto> getAccountsByStatus(AccountStatus status) {
        return accountRepository.findAccountsByStatus(status)
                .stream()
                .map(accountMapper::toDto)
                .toList();
    }

    @Transactional
    public void activeAccount(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new NotFoundException("Account not found with id: " + accountId));

        if (account.getStatus() == AccountStatus.ACTIVE) {
            throw new BadRequestException("Account is already active");
        }

        account.setStatus(AccountStatus.ACTIVE);
        accountRepository.save(account);
    }

    @Transactional
    public void deactivateAccount(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new NotFoundException("Account not found with id: " + accountId));

        if (account.getStatus() == AccountStatus.BLOCKED) {
            throw new BadRequestException("Account is already blocked");
        }

        account.setStatus(AccountStatus.BLOCKED);
        accountRepository.save(account);
    }

    @Transactional
    public void closeAccount(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new NotFoundException("Account not found with id: " + accountId));

        if (account.getStatus() == AccountStatus.CLOSED) {
            throw new BadRequestException("Bu hesab artıq bağlanıb.");
        }

        if (account.getBalance() != null && account.getBalance().compareTo(BigDecimal.ZERO) > 0) {
            throw new BadRequestException(
                    String.format(
                            "Hesabda %.2f %s məbləğində qalıq balans var. Hesabı bağlamazdan əvvəl " +
                                    "zəhmət olmasa balansı digər hesabınıza köçürün və ya nağdlaşdırın.",
                            account.getBalance(),
                            account.getCurrency()
                    )
            );
        }

        account.setStatus(AccountStatus.CLOSED);
        accountRepository.save(account);
    }

    private String generateAccountNumber() {
        SecureRandom random = new SecureRandom();
        String accountNumber;

        do {
            accountNumber = "AZ" + (100000000 + random.nextInt(900000000));
        } while (accountRepository.existsByAccountNumber(accountNumber));

        return accountNumber;
    }
}