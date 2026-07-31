package org.example.bankingsystemapi.mapper;

import lombok.RequiredArgsConstructor;
import org.example.bankingsystemapi.model.dto.request.AccountRequestDto;
import org.example.bankingsystemapi.model.dto.response.AccountResponseDto;
import org.example.bankingsystemapi.model.entity.Account;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountMapper {

    private final CardMapper cardMapper;

    public Account toEntity(AccountRequestDto accountRequestDto) {
        Account account = new Account();
        account.setCurrency(accountRequestDto.getCurrency());
        return account;
    }

    public AccountResponseDto toDto(Account account) {
        AccountResponseDto accountResponseDto = new AccountResponseDto();
        accountResponseDto.setId(account.getId());
        accountResponseDto.setAccountNumber(account.getAccountNumber());
        accountResponseDto.setBalance(account.getBalance());
        accountResponseDto.setCurrency(account.getCurrency());
        accountResponseDto.setAccountStatus(account.getStatus());
        accountResponseDto.setUserId(account.getUser().getId());
        accountResponseDto.setCreatedAt(account.getCreatedAt());
        accountResponseDto.setCards(
                account.getCards()
                        .stream()
                        .map(cardMapper::toDto)
                        .toList()
        );

        if (account.getUser() != null) {
            accountResponseDto.setUserId(account.getUser().getId());
            String fullName = account.getUser().getName() + " " + account.getUser().getSurname();
            accountResponseDto.setUserName(fullName);
        }
        return accountResponseDto;
    }
}
