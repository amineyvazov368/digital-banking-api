package org.example.bankingsystemapi.mapper;

import org.example.bankingsystemapi.model.dto.request.TransactionRequestDto;
import org.example.bankingsystemapi.model.dto.response.TransactionResponseDto;
import org.example.bankingsystemapi.model.entity.Account;
import org.example.bankingsystemapi.model.entity.Transaction;

public class TransactionMapper {

    public Transaction toEntity(TransactionRequestDto transactionRequestDto,
                                Account sendAccount, Account receiveAccount) {
        Transaction transaction = new Transaction();
        transaction.setSendAccount(sendAccount);
        transaction.setReceiverAccount(receiveAccount);
        transaction.setAmount(transactionRequestDto.getAmount());
        transaction.setDescription(transactionRequestDto.getDescription());
        return transaction;
    }

    public TransactionResponseDto toDto(Transaction transaction) {
        TransactionResponseDto transactionResponseDto = new TransactionResponseDto();
        transactionResponseDto.setSenderAccountNumber(transaction.getSendAccount().getAccountNumber());
        transactionResponseDto.setReceiverAccountNumber(transaction.getReceiverAccount().getAccountNumber());
        transactionResponseDto.setAmount(transaction.getAmount());
        transactionResponseDto.setStatus(transaction.getStatus());
        transactionResponseDto.setDescription(transaction.getDescription());
        transactionResponseDto.setCreatedAt(transaction.getCreatedAt());
        return transactionResponseDto;

    }
}
