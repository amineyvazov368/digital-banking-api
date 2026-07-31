package org.example.bankingsystemapi.service;

import lombok.RequiredArgsConstructor;
import org.example.bankingsystemapi.exceptions.BadRequestException;
import org.example.bankingsystemapi.exceptions.ForbiddenException;
import org.example.bankingsystemapi.exceptions.NotFoundException;
import org.example.bankingsystemapi.mapper.TransactionMapper;
import org.example.bankingsystemapi.model.dto.response.TransactionResponseDto;
import org.example.bankingsystemapi.model.entity.Account;
import org.example.bankingsystemapi.model.entity.Card;
import org.example.bankingsystemapi.model.entity.Transaction;
import org.example.bankingsystemapi.model.entity.User;
import org.example.bankingsystemapi.model.enums.AccountStatus;
import org.example.bankingsystemapi.model.enums.CardStatus;
import org.example.bankingsystemapi.model.enums.TransactionStatus;
import org.example.bankingsystemapi.model.enums.TransactionType;
import org.example.bankingsystemapi.repository.AccountRepository;
import org.example.bankingsystemapi.repository.CardRepository;
import org.example.bankingsystemapi.repository.TransactionRepository;
import org.example.bankingsystemapi.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final NotificationService notificationService;

    private String getAuthenticatedUserEmail() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }
        return principal.toString();
    }

    @Transactional
    public String deposit(String cardNumber, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Amount must be greater than zero");
        }

        Account account = accountRepository.findByCardsCardNumber(cardNumber)
                .orElseThrow(() -> new NotFoundException("Account not found for card: " + cardNumber));

        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new BadRequestException("Account is not active");
        }

        Card card = cardRepository.findByCardNumber(cardNumber)
                .orElseThrow(() -> new NotFoundException("Card not found with number: " + cardNumber));

        if (card.getCardStatus() != CardStatus.ACTIVE) {
            throw new BadRequestException("Card is not active");
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
            String maskCard = "*" + cardNumber.substring(cardNumber.length() - 4);

            notificationService.createNotification(
                    account.getUser().getId(),
                    "Nağdlaşdırma",
                    maskCard + " kartınızdan " + amount + " AZN məbləğində nağd pul çıxarıldı."
            );

            return "Deposit successful";
        } catch (Exception e) {
            transaction.setStatus(TransactionStatus.FAILED);
            transactionRepository.save(transaction);
            throw new BadRequestException("Deposit failed: " + e.getMessage());
        }
    }

    @Transactional
    public String withdraw(String cardNumber, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Amount must be greater than zero");
        }

        Account account = accountRepository.findByCardsCardNumber(cardNumber)
                .orElseThrow(() -> new NotFoundException("Account not found for card: " + cardNumber));

        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new BadRequestException("Account is not active");
        }

        Card card = cardRepository.findByCardNumber(cardNumber)
                .orElseThrow(() -> new NotFoundException("Card not found with number: " + cardNumber));

        if (card.getCardStatus() != CardStatus.ACTIVE) {
            throw new BadRequestException("Card is not active");
        }

        if (account.getBalance().compareTo(amount) < 0) {
            throw new BadRequestException("Insufficient balance");
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

            String maskCard = "*" + cardNumber.substring(cardNumber.length() - 4);

            notificationService.createNotification(
                    account.getUser().getId(),
                    "Nağdlaşdırma",
                    maskCard + " kartınızdan " + amount + " AZN məbləğində nağd pul çıxarıldı."
            );


            return "Withdraw successful";
        } catch (Exception e) {
            transaction.setStatus(TransactionStatus.FAILED);
            transactionRepository.save(transaction);
            throw new BadRequestException("Withdraw failed: " + e.getMessage());
        }
    }

    @Transactional
    public String transferByCardNumber(String fromCardNumber, String toCardNumber, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Amount must be greater than zero");
        }

        if (fromCardNumber.equals(toCardNumber)) {
            throw new BadRequestException("Cannot transfer to the same card/account");
        }

        Account fromAccount = accountRepository.findByCardsCardNumber(fromCardNumber)
                .orElseThrow(() -> new NotFoundException("Sender account not found for card: " + fromCardNumber));

        Account toAccount = accountRepository.findByCardsCardNumber(toCardNumber)
                .orElseThrow(() -> new NotFoundException("Receiver account not found for card: " + toCardNumber));

        if (fromAccount.getStatus() != AccountStatus.ACTIVE) {
            throw new BadRequestException("Sender account is not active");
        }

        if (toAccount.getStatus() != AccountStatus.ACTIVE) {
            throw new BadRequestException("Receiver account is not active");
        }

        Card fromCard = cardRepository.findByCardNumber(fromCardNumber)
                .orElseThrow(() -> new NotFoundException("Sender card not found: " + fromCardNumber));

        if (fromCard.getCardStatus() != CardStatus.ACTIVE) {
            throw new BadRequestException("Sender card is not active");
        }

        Card toCard = cardRepository.findByCardNumber(toCardNumber)
                .orElseThrow(() -> new NotFoundException("Receiver card not found: " + toCardNumber));

        if (toCard.getCardStatus() != CardStatus.ACTIVE) {
            throw new BadRequestException("Receiver card is not active");
        }

        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new BadRequestException("Insufficient balance");
        }

        Transaction transaction = new Transaction();
        transaction.setSendAccount(fromAccount);
        transaction.setReceiverAccount(toAccount);
        transaction.setAmount(amount);
        transaction.setTransactionType(TransactionType.TRANSFER);
        transaction.setDescription("Transfer from card " + fromCardNumber + " to card " + toCardNumber);

        BigDecimal threshold = new BigDecimal("5000");

        if (amount.compareTo(threshold) >= 0) {
            transaction.setStatus(TransactionStatus.FLAGGED);
            transactionRepository.save(transaction);

            notificationService.createNotification(
                    fromAccount.getUser().getId(),
                    "Əməliyyat Yoxlamada",
                    "Məbləğ böyük olduğu üçün " + amount + " AZN köçürməniz təhlükəsizlik yoxlamasındadır."
            );

            notificationService.createNotificationForAdmins(
                    "Şübhəli/Böyük Köçürmə",
                    fromAccount.getUser().getName() + " " + fromAccount.getUser().getSurname() +
                            " tərəfindən " + amount + " AZN məbləğində köçürmə təsdiq gözləyir."
            );

            return "Transfer is flagged for high amount and pending admin approval";
        }

        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        toAccount.setBalance(toAccount.getBalance().add(amount));

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        transaction.setStatus(TransactionStatus.SUCCESS);
        transactionRepository.save(transaction);

        String maskFromCard = "*" + fromCardNumber.substring(fromCardNumber.length() - 4);
        String maskToCard = "*" + toCardNumber.substring(toCardNumber.length() - 4);

        notificationService.createNotification(
                fromAccount.getUser().getId(),
                "Pul Çıxarışı",
                maskFromCard + " kartınızdan " + amount + " AZN məbləğində pul silindi."
        );

        notificationService.createNotification(
                toAccount.getUser().getId(),
                "Mədaxil",
                maskToCard + " kartınıza " + amount + " AZN məbləğində pul daxil oldu."
        );

        return "Transfer successful";
    }

    public List<TransactionResponseDto> getMyTransactionHistory(String email, String type, String search, int limit) {
        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new NotFoundException("User not found with email: " + email);
        }

        Pageable pageable = PageRequest.of(0, limit, Sort.by("createdAt").descending());
        List<Transaction> transactions = transactionRepository.findAllByUserId(user.getId(), pageable);

        return transactions.stream()
                .map(transactionMapper::toDto)
                .collect(Collectors.toList());
    }

    public TransactionResponseDto getTransactionById(Long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new NotFoundException("Transaction not found with id: " + transactionId));

        return transactionMapper.toDto(transaction);
    }

    public Page<TransactionResponseDto> getAllTransactions(int page, int size) {

        Pageable pageable = PageRequest.of(page, size);
        return transactionRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(transactionMapper::toDto);
    }

    public Page<TransactionResponseDto> getTransactionsByStatus(TransactionStatus status,int page, int size) {

        Pageable pageable1 = PageRequest.of(page, size);
        return transactionRepository.findByStatus(status,pageable1)
                .map(transactionMapper::toDto);
    }

    public List<TransactionResponseDto> getFilteredTransactions(String email, Long accountId, TransactionType type) {
        List<Transaction> transactions = transactionRepository.findTransactionsWithFilters(email, accountId, type);
        return transactions.stream()
                .map(transactionMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<TransactionResponseDto> getMyTransactionsByAccountId(Long accountId) {
        String currentUserEmail = getAuthenticatedUserEmail();

        boolean ownsAccount = accountRepository.existsByIdAndUserEmail(accountId, currentUserEmail);

        if (!ownsAccount) {
            throw new ForbiddenException("Bu hesaba daxil olmaq və ya tranzaksiyalarını görmək icazəniz yoxdur!");
        }

        return transactionRepository.findByAccountId(accountId)
                .stream()
                .map(transactionMapper::toDto)
                .toList();
    }

    public List<TransactionResponseDto> getMyTransactionsByType(TransactionType transactionType) {
        String currentUserEmail = getAuthenticatedUserEmail();

        return transactionRepository.findAllByUserEmailAndTransactionType(currentUserEmail, transactionType)
                .stream()
                .map(transactionMapper::toDto)
                .toList();
    }

    public List<TransactionResponseDto> getMyTransactionsByCardNumber(String cardNumber) {
        return transactionRepository.findAllByCardNumber(cardNumber)
                .stream()
                .map(transactionMapper::toDto)
                .toList();
    }

    @Transactional
    public void approveTransactionByAdmin(Long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new NotFoundException("Transaction not found"));

        if (transaction.getStatus() != TransactionStatus.FLAGGED) {
            throw new BadRequestException("Only FLAGGED transactions can be approved");
        }

        Account fromAccount = transaction.getSendAccount();
        Account toAccount = transaction.getReceiverAccount();
        BigDecimal amount = transaction.getAmount();

        if (fromAccount.getBalance().compareTo(amount) < 0) {
            transaction.setStatus(TransactionStatus.FAILED);
            transactionRepository.save(transaction);

            notificationService.createNotification(
                    fromAccount.getUser().getId(),
                    "Köçürmə Ləğv Edildi",
                    "Hesabınızda kifayət qədər vəsaət olmadığı üçün " + amount + " AZN məbləğində köçürmə ləğv edildi."
            );

            throw new BadRequestException("Insufficient balance to approve transaction");
        }

        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        toAccount.setBalance(toAccount.getBalance().add(amount));

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        transaction.setStatus(TransactionStatus.SUCCESS);
        transactionRepository.save(transaction);

        notificationService.createNotification(
                fromAccount.getUser().getId(),
                "Köçürmə Təsdiqləndi",
                amount + " AZN məbləğində köçürməniz admin tərəfindən təsdiqləndi və icra olundu."
        );

        notificationService.createNotification(
                toAccount.getUser().getId(),
                "Mədaxil (Köçürmə)",
                "Hesabınıza admin təsdiqindən sonra " + amount + " AZN məbləğində pul daxil oldu."
        );
    }

    @Transactional
    public void rejectTransactionByAdmin(Long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new NotFoundException("Transaction not found"));

        if (transaction.getStatus() != TransactionStatus.FLAGGED) {
            throw new BadRequestException("Only FLAGGED transactions can be rejected");
        }

        transaction.setStatus(TransactionStatus.FAILED);
        transactionRepository.save(transaction);

        Account fromAccount = transaction.getSendAccount();
        Account toAccount = transaction.getReceiverAccount();
        BigDecimal amount = transaction.getAmount();

        notificationService.createNotification(
                fromAccount.getUser().getId(),
                "Köçürmə Ləğv Edildi",
                amount + " AZN məbləğində köçürmə sorğunuz admin tərəfindən rədd edildi."
        );

        if (toAccount != null && toAccount.getUser() != null) {
            notificationService.createNotification(
                    toAccount.getUser().getId(),
                    "Köçürmə İmtina Edildi",
                    "Hesabınıza gözlənilən " + amount + " AZN məbləğində köçürmə admin tərəfindən rədd edildi."
            );
        }
    }
}