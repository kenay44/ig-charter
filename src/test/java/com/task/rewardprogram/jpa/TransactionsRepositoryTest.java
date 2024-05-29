package com.task.rewardprogram.jpa;

import com.task.rewardprogram.entities.Transaction;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class TransactionsRepositoryTest {

    @Autowired
    TransactionsRepository transactionsRepository;

    @Test
    public void testCreateTransaction() {
        //given
        long customerId = 1L;
        Transaction transaction = new Transaction();
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setCustomerId(customerId);
        transaction.setTransactionValue(BigDecimal.valueOf(100));

        //when
        transactionsRepository.save(transaction);

        //then
        Transaction savedTransaction = transactionsRepository.findById(transaction.getId()).orElseThrow();
        assertEquals(transaction, savedTransaction);
    }
}
