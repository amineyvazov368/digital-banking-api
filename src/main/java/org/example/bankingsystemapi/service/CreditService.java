package org.example.bankingsystemapi.service;

import lombok.RequiredArgsConstructor;
import org.example.bankingsystemapi.exceptions.BadRequestException;
import org.example.bankingsystemapi.exceptions.NotFoundException;
import org.example.bankingsystemapi.mapper.CreditMapper;
import org.example.bankingsystemapi.model.dto.response.CreditResponse;
import org.example.bankingsystemapi.model.entity.Account;
import org.example.bankingsystemapi.model.entity.Credit;
import org.example.bankingsystemapi.model.entity.User;
import org.example.bankingsystemapi.model.enums.CreditStatus;
import org.example.bankingsystemapi.repository.AccountRepository;
import org.example.bankingsystemapi.repository.CreditRepository;
import org.example.bankingsystemapi.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CreditService {

    private final CreditRepository creditRepository;
    private final AccountRepository accountRepository;
    private final CreditMapper creditMapper;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

//
//    private User getAuthenticatedUser() {
//        String email = SecurityContextHolder.getContext().getAuthentication().getName();
//        return userRepository.findByEmail(email);
//
//    }

    private User getAuthenticatedUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof User)) {
            throw new BadRequestException("İstifadəçi autentifikasiya olunmayıb!");
        }

        return (User) authentication.getPrincipal();
    }

    private void validateAccountOwner(Account account, User user) {
        if (!account.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("Bu hesab sizə aid deyil!");
        }
    }


    @Transactional
    public CreditResponse takeCredit(Long accountId, BigDecimal amount, Integer termMonths) {

        User user = getAuthenticatedUser();

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new NotFoundException("Hesab tapilmadi"));

        if (amount == null || amount.compareTo(BigDecimal.valueOf(100)) < 0) {
            throw new BadRequestException("Minimum kredit məbləği 100 AZN olmalıdır.");
        }

        validateAccountOwner(account, user);

        double annualInterestRate = 12.00;

        BigDecimal interest = amount.multiply(BigDecimal.valueOf(annualInterestRate))
                .divide(BigDecimal.valueOf(100));


        BigDecimal totalAmount = amount.add(interest);

        BigDecimal creditAmount = totalAmount.divide(BigDecimal.valueOf(termMonths), 2, RoundingMode.HALF_UP);

        Credit credit = new Credit();
        credit.setAccountId(account.getId());
        credit.setOriginalAmount(amount);
        credit.setTotalAmount(totalAmount);
        credit.setRemainingAmount(totalAmount);
        credit.setMonthlyPayment(creditAmount);
        credit.setInterestRate(annualInterestRate);
        credit.setNextPaymentDate(LocalDateTime.now().plusMonths(1));
        credit.setStatus(CreditStatus.ACTIVE);
        creditRepository.save(credit);

        String title = "Yeni Kredit Təsdiqləndi";
        String message = String.format("%.2f AZN məbləğində kreditiniz təsdiqləndi və hesabınıza köçürüldü.", amount);

        notificationService.createNotification(
                user.getId(),
                title, message
        );

        String adminTitle = "Yeni Kredit Götürüldü";
        String adminMessage = String.format("İstifadəçi: %s (ID: %d) tərəfindən %.2f AZN məbləğində yeni kredit götürüldü.",
                user.getName(), user.getId(), amount);
        notificationService.createNotificationForAdmins(adminTitle, adminMessage);

        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);
        return creditMapper.toResponse(credit);

    }

    @Transactional
    public CreditResponse payCredit(Long accountId, Long creditId, BigDecimal amount) {

        User user = getAuthenticatedUser();

        Credit credit = creditRepository.findById(creditId)
                .orElseThrow(() -> new NotFoundException("Credit tapilmadi"));

        if (credit.getStatus() == CreditStatus.PAID_OFF) {
            throw new BadRequestException("Bu kredit artıq tam ödənilib");
        }

        BigDecimal minRequiredAmount = credit.getRemainingAmount().compareTo(credit.getMonthlyPayment()) < 0
                ? credit.getRemainingAmount()
                : credit.getMonthlyPayment();

        if (amount.compareTo(minRequiredAmount) < 0) {
            throw new BadRequestException("Ödənilən məbləğ minimum tələb olunan məbləğdən (" + minRequiredAmount + " AZN) az ola bilməz.");
        }

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new NotFoundException("Hesab tapilmadi"));

        validateAccountOwner(account, user);

        if (account.getBalance().compareTo(amount) < 0) {
            throw new BadRequestException("Hesabda kifayət qədər vəsaət yoxdur");
        }

        account.setBalance(account.getBalance().subtract(amount));
        accountRepository.save(account);
        BigDecimal newRemainingAmount = credit.getRemainingAmount().subtract(amount);

        if (newRemainingAmount.compareTo(BigDecimal.ZERO) <= 0) {
            credit.setRemainingAmount(BigDecimal.ZERO);
            credit.setStatus(CreditStatus.PAID_OFF);
        } else {
            credit.setRemainingAmount(newRemainingAmount);
            credit.setNextPaymentDate(credit.getNextPaymentDate().plusMonths(1));
        }

        Credit updatedCredit = creditRepository.save(credit);

        String title = "Kredit Ödənişi Tamamlandı";

        String message;
        if (updatedCredit.getRemainingAmount().compareTo(BigDecimal.ZERO) <= 0) {
            message = String.format("Təbriklər! %.2f AZN ödəniş edildi və #%d nömrəli kreditiniz tamamilə bağlandı.", amount, creditId);
        } else {
            message = String.format("Kreditiniz üzrə %.2f AZN ödəniş qəbul edildi. Qalıq borc: %.2f AZN.", amount, updatedCredit.getRemainingAmount());
        }
        notificationService.createNotification(
                user.getId(), title, message
        );

        String adminTitle = "Kredit Ödənişi Edildi";
        String adminMessage = String.format("İstifadəçi: %s (ID: %d), #%d nömrəli kredit üzrə %.2f AZN ödəniş etdi.",
                user.getName(), user.getId(), creditId, amount);
        notificationService.createNotificationForAdmins(adminTitle, adminMessage);

        return creditMapper.toResponse(updatedCredit);


    }

    public List<CreditResponse> getMyCredits() {
        User user = getAuthenticatedUser();

        List<Long> accountIds = accountRepository.findAllByUserId(user.getId())
                .stream()
                .map(Account::getId)
                .toList();

        List<Credit> credits = creditRepository.findAllByAccountIdIn(accountIds);
        return credits.stream().map(creditMapper::toResponse).toList();
    }

    public CreditResponse getMyCreditById(Long creditId) {
        User user = getAuthenticatedUser();
        Credit credit = creditRepository.findById(creditId)
                .orElseThrow(() -> new NotFoundException("Kredit tapılmadı"));

        Account account = accountRepository.findById(credit.getAccountId())
                .orElseThrow(() -> new NotFoundException("Hesab tapılmadı"));

        validateAccountOwner(account, user);

        return creditMapper.toResponse(credit);
    }


    public Page<CreditResponse> getAllCreditsForAdmin(int page, int size) {
        return creditRepository.findAll(PageRequest.of(page, size))
                .map(creditMapper::toResponse);
    }

    public List<CreditResponse> getCreditsByStatusForAdmin(CreditStatus status) {
        return creditRepository.findAllByStatus(status)
                .stream()
                .map(creditMapper::toResponse)
                .toList();
    }


}
