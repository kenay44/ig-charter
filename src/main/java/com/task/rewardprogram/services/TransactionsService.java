package com.task.rewardprogram.services;

import com.task.rewardprogram.dto.TransactionDto;
import com.task.rewardprogram.dto.TransactionRequest;
import com.task.rewardprogram.entities.Transaction;
import com.task.rewardprogram.exceptions.DtoValidationException;
import com.task.rewardprogram.jpa.TransactionsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class TransactionsService {

    private final static Logger logger = LoggerFactory.getLogger(TransactionsService.class);
    private final TransactionsRepository transactionsRepository;

    @Autowired
    public TransactionsService(TransactionsRepository transactionsRepository) {
        this.transactionsRepository = transactionsRepository;
    }

    public Long addTransaction(TransactionRequest transactionRequest) {
        Transaction transaction = createTransaction(transactionRequest);
        transactionsRepository.save(transaction);
        return transaction.getId();
    }

    @Transactional
    public void updateTransaction(TransactionRequest transactionRequest, Long id) {
        transactionsRepository.findById(id)
                .ifPresentOrElse(transaction -> updateTransaction(transaction, transactionRequest),
                        throwTransactionNotFound(id));
    }

    public Optional<TransactionDto> getTransaction(Long id) {
        return transactionsRepository.findById(id).map(TransactionDto::of);
    }

    private static Runnable throwTransactionNotFound(Long id) {
        return () -> {
            logger.warn("Request to update transaction with ID: {} that does not exists in the repository", id);
            throw new DtoValidationException("Transaction not found", HttpStatus.NOT_FOUND);
        };
    }

    private Transaction createTransaction(TransactionRequest transactionRequest) {
        Transaction transaction = new Transaction();
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setCustomerId(transactionRequest.customerId());
        transaction.setTransactionValue(transactionRequest.transactionValue());
        return transaction;
    }

    private void updateTransaction(Transaction transaction, TransactionRequest transactionRequest) {
        transaction.setTransactionValue(transactionRequest.transactionValue());
        transaction.setCustomerId(transactionRequest.customerId());
    }
}
