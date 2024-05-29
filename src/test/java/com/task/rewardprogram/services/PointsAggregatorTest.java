package com.task.rewardprogram.services;

import com.task.rewardprogram.dto.CustomerPoints;
import com.task.rewardprogram.entities.Transaction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class PointsAggregatorTest {

    PointsAggregator pointsAggregator;

    LocalDateTime aprilTransactionDate = LocalDateTime.of(2024, 4, 20, 10, 10);
    LocalDateTime marchTransactionDate = LocalDateTime.of(2024, 3, 20, 10, 10);
    YearMonth april = YearMonth.of(2024, 4);
    YearMonth march = YearMonth.of(2024, 3);

    long gregId = 1L;
    long tomId = 2L;

    AtomicLong transactionIdGenerator;

    @BeforeEach
    void setUp() {
        transactionIdGenerator = new AtomicLong(1);
        pointsAggregator = new PointsAggregator(new PointsCalculator());
    }

    @Test
    @DisplayName("For a transaction with value below first level points should return empty points result")
    public void testRewardCalculationForTransactionBelowFirstLevelPoints() {
        List<CustomerPoints> result = pointsAggregator.aggregate(List.of(getTransaction(gregId, BigDecimal.valueOf(10), aprilTransactionDate)));
        Assertions.assertEquals(List.of(), result);
    }

    @Test
    @DisplayName("For a transaction with value above first level points should return result with first level points")
    public void testRewardsCalculationForTransactionWithValueAboveFirstLevelPoints() {
        List<CustomerPoints> result = pointsAggregator.aggregate(List.of(getTransaction(gregId, BigDecimal.valueOf(100), aprilTransactionDate)));
        Assertions.assertEquals(List.of(new CustomerPoints(gregId, Map.of(april, 50L), 50L)), result);
    }

    @Test
    @DisplayName("For multiple transactions of single customer should return correctly calculated Points")
    public void testRewardsCalculationForMultipleTransactions() {
        List<CustomerPoints> result = pointsAggregator.aggregate(List.of(
                getTransaction(gregId, BigDecimal.valueOf(100), aprilTransactionDate),
                getTransaction(gregId, BigDecimal.valueOf(20), aprilTransactionDate),
                getTransaction(gregId, BigDecimal.valueOf(120), aprilTransactionDate)));
        Assertions.assertEquals(List.of(new CustomerPoints(gregId, Map.of(april, 140L), 140L)), result);
    }

    @Test
    @DisplayName("For multiple transactions in different months should return correctly calculated Points")
    public void testRewardsCalculationForMultipleTransactionsInDifferentMonths() {
        List<CustomerPoints> result = pointsAggregator.aggregate(List.of(
                getTransaction(gregId, BigDecimal.valueOf(100), aprilTransactionDate),
                getTransaction(gregId, BigDecimal.valueOf(20), aprilTransactionDate),
                getTransaction(gregId, BigDecimal.valueOf(120), marchTransactionDate)));
        Assertions.assertEquals(List.of(new CustomerPoints(gregId, Map.of(april, 50L, march, 90L), 140L)), result);
    }

    @Test
    @DisplayName("For multiple transactions and multiple customers should return correctly calculated and aggregated points")
    public void testRewardsCalculationForMultipleTransactionsOfDifferentCustomers() {
        List<CustomerPoints> result = pointsAggregator.aggregate(List.of(
                getTransaction(gregId, BigDecimal.valueOf(100), aprilTransactionDate),
                getTransaction(gregId, BigDecimal.valueOf(20), aprilTransactionDate),
                getTransaction(tomId, BigDecimal.valueOf(120), marchTransactionDate)));

        List<CustomerPoints> expectedResult = List.of(
                new CustomerPoints(gregId, Map.of(april, 50L), 50L),
                new CustomerPoints(tomId, Map.of(march, 90L), 90L)
        );
        Assertions.assertEquals(expectedResult.size(), result.size());
        Assertions.assertTrue(expectedResult.containsAll(result));
    }

    Transaction getTransaction(Long customerId, BigDecimal value, LocalDateTime transactionDate) {
        return new Transaction(transactionIdGenerator.getAndIncrement(), customerId, value, transactionDate);
    }
}
