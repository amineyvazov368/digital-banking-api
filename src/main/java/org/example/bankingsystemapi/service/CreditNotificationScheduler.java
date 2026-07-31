package org.example.bankingsystemapi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.bankingsystemapi.model.entity.Account;
import org.example.bankingsystemapi.model.entity.Credit;
import org.example.bankingsystemapi.model.enums.CreditStatus;
import org.example.bankingsystemapi.repository.AccountRepository;
import org.example.bankingsystemapi.repository.CreditRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CreditNotificationScheduler {

    private final CreditRepository creditRepository;
    private final AccountRepository accountRepository;
    private final NotificationService notificationService;

    @Scheduled(fixedRate = 60000)
    public void checkUpcomingCreditPayments(){
        log.info("Scheduler işə düşdü...");
        LocalDateTime localDateTime = LocalDateTime.now();

        List<Credit> dueCredit = creditRepository.findAllByStatusAndNextPaymentDateBefore(CreditStatus.ACTIVE,localDateTime);

        for(Credit credit : dueCredit){
            Account account= accountRepository.findById(credit.getAccountId()).get();

            if (account != null && account.getUser() != null ){
                Long userId = account.getUser().getId();

                String title = "Kredit Ödənişi Vaxtı!";
                String message = String.format(
                        "Hörmətli müştəri, #%d nömrəli kreditinizin %.2f AZN məbləğində növbəti aylıq ödəniş vaxtı çatdı.",
                        credit.getId(),
                        credit.getMonthlyPayment()
                );

                notificationService.createNotification(userId,title,message);
                credit.setNextPaymentDate(LocalDateTime.now().plusMinutes(5));
                creditRepository.save(credit);

                log.info("Kredit ID #{} üçün User ID #{} istifadəçisinə bildiriş göndərildi.", credit.getId(), userId);

            }
        }
    }


}
