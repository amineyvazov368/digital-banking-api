package org.example.bankingsystemapi.mapper;

import org.example.bankingsystemapi.model.dto.request.TransactionRequestDto;
import org.example.bankingsystemapi.model.dto.response.TransactionResponseDto;
import org.example.bankingsystemapi.model.entity.Account;
import org.example.bankingsystemapi.model.entity.Transaction;
import org.example.bankingsystemapi.model.entity.User;
import org.springframework.stereotype.Component;

@Component
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

//    public TransactionResponseDto toDto(Transaction transaction) {
//        TransactionResponseDto transactionResponseDto = new TransactionResponseDto();
//        transactionResponseDto.setSenderAccountNumber(transaction.getSendAccount().getAccountNumber());
//        transactionResponseDto.setReceiverAccountNumber(transaction.getReceiverAccount().getAccountNumber());
//        transactionResponseDto.setAmount(transaction.getAmount());
//        transactionResponseDto.setStatus(transaction.getStatus());
//        transactionResponseDto.setDescription(transaction.getDescription());
//        transactionResponseDto.setCreatedAt(transaction.getCreatedAt());
//        return transactionResponseDto;
//
//    }

    public TransactionResponseDto toDto(Transaction transaction) {
        if (transaction == null) {
            return null;
        }

        TransactionResponseDto transactionResponseDto = new TransactionResponseDto();

        if (transaction.getSendAccount() != null) {
            transactionResponseDto.setSenderAccountNumber(transaction.getSendAccount().getAccountNumber());

            if (transaction.getSendAccount().getUser() != null) {
                User senderUser = transaction.getSendAccount().getUser();
                String senderFullName = buildFullName(senderUser.getName(), senderUser.getSurname());
                transactionResponseDto.setSenderName(senderFullName);
            } else {
                transactionResponseDto.setSenderName("Naməlum Göndərən");
            }
        } else {
            transactionResponseDto.setSenderAccountNumber("SYSTEM");
            transactionResponseDto.setSenderName("Sistem / Nağd");
        }

        if (transaction.getReceiverAccount() != null) {
            transactionResponseDto.setReceiverAccountNumber(transaction.getReceiverAccount().getAccountNumber());

            if (transaction.getReceiverAccount().getUser() != null) {
                User receiverUser = transaction.getReceiverAccount().getUser();
                String receiverFullName = buildFullName(receiverUser.getName(), receiverUser.getSurname());
                transactionResponseDto.setReceiverName(receiverFullName);
            } else {
                transactionResponseDto.setReceiverName("Naməlum Qəbul Edən");
            }
        } else {
            transactionResponseDto.setReceiverAccountNumber("SYSTEM");
            transactionResponseDto.setReceiverName("Sistem / Nağd");
        }

        transactionResponseDto.setAmount(transaction.getAmount());
        transactionResponseDto.setStatus(transaction.getStatus());
        transactionResponseDto.setDescription(transaction.getDescription());
        transactionResponseDto.setCreatedAt(transaction.getCreatedAt());

        return transactionResponseDto;
    }

    private String buildFullName(String name, String surname) {
        if (name == null && surname == null) {
            return "İstifadəçi";
        }
        if (surname == null || surname.isBlank()) {
            return name;
        }
        if (name == null || name.isBlank()) {
            return surname;
        }
        return name.trim() + " " + surname.trim();
    }
}
