package com.task.rewardprogram.dto;

import com.task.rewardprogram.entities.Transaction;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record TransactionRequest(
        @NotNull(message = "Transaction does not have customer assigned")
        @Positive(message = "CustomerID can not be less then 1")
        Long customerId,
        @NotNull(message = "Transaction is missing transaction value")
        @PositiveOrZero(message = "Transaction value can not be less then 0")
        @Digits(integer = 10, fraction = 2)
        BigDecimal transactionValue
) {
    public TransactionRequest(Long customerId, BigDecimal transactionValue) {
        this.customerId = customerId;
        this.transactionValue = transactionValue;
    }

    public static TransactionRequest of(Transaction transaction) {
        return new TransactionRequest(transaction.getId(), transaction.getTransactionValue());
    }
}
