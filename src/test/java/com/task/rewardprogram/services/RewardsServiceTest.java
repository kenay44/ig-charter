package com.task.rewardprogram.services;

import com.task.rewardprogram.dto.CustomerPoints;
import com.task.rewardprogram.entities.Transaction;
import com.task.rewardprogram.jpa.TransactionsRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;


@Transactional
@SpringBootTest
public class RewardsServiceTest {
    AtomicLong transactionIdGenerator, customerIdGenerator;
    @Autowired
    private RewardsService rewardsService;

    @Autowired
    private TransactionsRepository transactionsRepository;

    @BeforeEach
    public void setUp() {
        transactionIdGenerator = new AtomicLong(1);
        customerIdGenerator = new AtomicLong(1);
        transactionsRepository.deleteAll();
    }

    @Test
    @DisplayName("Transaction date should not be within range of period based on provided end month")
    public void transactionsNotInRewardsPeriodAreNotIncludedInCalculation() {
        setupTransactionAtDate(LocalDateTime.of(2024, 3, 1, 0, 1));
        setupTransactionAtDate(LocalDateTime.of(2023, 11, 30, 23, 59));

        List<CustomerPoints> result = rewardsService.calculateForAllCustomers(YearMonth.of(2024, 2));

        assert result != null;
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Transaction date should be within range of period based on provided end month")
    public void transactionAtOneDayBeforeRewardsEndDateAreIncludedInCalculation() {
        setupTransactionAtDate(LocalDateTime.of(2024, 2, 29, 23, 59));

        List<CustomerPoints> result = rewardsService.calculateForAllCustomers(YearMonth.of(2024, 2));

        assert result != null;
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(50, result.get(0).pointsSum());
    }

    @Test
    @DisplayName("Transaction date should be within range of period based on provided end month")
    public void transactionDateIsAfterFirstDateOfRewardsPeriod() {
        setupTransactionAtDate(LocalDateTime.of(2024, 3, 1, 0, 1));

        List<CustomerPoints> result = rewardsService.calculateForAllCustomers(YearMonth.of(2024, 5));

        assert result != null;
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(50, result.get(0).pointsSum());
    }

    private void setupTransactionAtDate(LocalDateTime transactionDate) {
        long customerId = 1L;
        Transaction transaction = new Transaction();
        transaction.setCustomerId(customerId);
        transaction.setTransactionValue(BigDecimal.valueOf(100L));
        transaction.setTransactionDate(transactionDate);
        transaction.setId(transactionIdGenerator.getAndIncrement());

        transactionsRepository.save(transaction);
    }
}
