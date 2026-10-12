package com.bryan.dompetku.dto;

import com.bryan.dompetku.entity.Transaction;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionResponse(
        Long id,
        BigDecimal amount,
        LocalDate transactionDate,
        String note,
        CategoryResponse category
) {

    public static TransactionResponse from(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getAmount(),
                transaction.getTransactionDate(),
                transaction.getNote(),
                CategoryResponse.from(transaction.getCategory())
        );
    }
}