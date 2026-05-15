package org.example.bankingsystemapi.mapper;

import org.example.bankingsystemapi.model.dto.request.AccountRequestDto;
import org.example.bankingsystemapi.model.dto.response.AccountResponseDto;
import org.example.bankingsystemapi.model.entity.Account;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {

    public Account toEntity(AccountRequestDto accountRequestDto) {
        Account account = new Account();
        account.setCurrency(accountRequestDto.getCurrency());
        return account;
    }

    public AccountResponseDto toDto(Account account) {
     AccountResponseDto accountResponseDto = new AccountResponseDto();
     accountResponseDto.setAccountNumber(account.getAccountNumber());
     accountResponseDto.setBalance(account.getBalance());
     accountResponseDto.setCurrency(account.getCurrency());
     accountResponseDto.setAccountStatus(account.getStatus());
     return accountResponseDto;
    }
}
