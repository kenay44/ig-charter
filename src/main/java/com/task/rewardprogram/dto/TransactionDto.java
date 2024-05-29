package com.task.rewardprogram.dto;

import com.task.rewardprogram.entities.Transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionDto(Long customerId, BigDecimal value, LocalDateTime date) {
    public static TransactionDto of(Transaction transaction) {
        return new TransactionDto(transaction.getCustomerId(),
                transaction.getTransactionValue(),
                transaction.getTransactionDate());
    }
}
