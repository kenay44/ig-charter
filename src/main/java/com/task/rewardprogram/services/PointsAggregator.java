package com.task.rewardprogram.services;

import com.task.rewardprogram.dto.CustomerPoints;
import com.task.rewardprogram.entities.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Component
public class PointsAggregator {

    private final PointsCalculator pointsCalculator;

    @Autowired
    public PointsAggregator(PointsCalculator pointsCalculator) {
        this.pointsCalculator = pointsCalculator;
    }

    public List<CustomerPoints> aggregate(List<Transaction> transactions) {
        return transactions.stream()
                .map(this::toTransactionPoints)
                .filter(transactionsWithPointsOnly())
                .collect(groupByCustomerId())
                .entrySet().stream()
                .map(this::toCustomerPoints)
                .toList();
    }

    private static Predicate<TransactionPoints> transactionsWithPointsOnly() {
        return transactionPoints -> transactionPoints.points() > 0;
    }

    private static Collector<TransactionPoints, ?, Map<Long, Map<YearMonth, Long>>> groupByCustomerId() {
        return Collectors.groupingBy(transactionPoints -> transactionPoints.customerId, groupByTransactionMonth());
    }

    private static Collector<TransactionPoints, ?, Map<YearMonth, Long>> groupByTransactionMonth() {
        return Collectors.groupingBy(transactionPoints -> transactionPoints.transactionMonth, getCustomerPointsInMonth());
    }

    private static Collector<TransactionPoints, ?, Long> getCustomerPointsInMonth() {
        return Collectors.reducing(0L, TransactionPoints::points, Long::sum);
    }

    private CustomerPoints toCustomerPoints(Map.Entry<Long, Map<YearMonth, Long>> customerMonths) {
        return new CustomerPoints(
                customerMonths.getKey(),
                customerMonths.getValue(),
                allPointsGrantedToCustomer(customerMonths));
    }

    private static Long allPointsGrantedToCustomer(Map.Entry<Long, Map<YearMonth, Long>> customerMonths) {
        return customerMonths.getValue().values().stream()
                .reduce(Long::sum)
                .orElse(0L);
    }

    private TransactionPoints toTransactionPoints(Transaction transaction) {
        return new TransactionPoints(
                transaction.getCustomerId(),
                pointsCalculator.calculate(transaction.getTransactionValue()),
                YearMonth.from(transaction.getTransactionDate())
        );
    }

    private record TransactionPoints(Long customerId, Long points, YearMonth transactionMonth) {
    }
}
