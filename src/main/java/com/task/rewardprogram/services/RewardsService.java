package com.task.rewardprogram.services;

import com.task.rewardprogram.dto.CustomerPoints;
import com.task.rewardprogram.entities.Transaction;
import com.task.rewardprogram.jpa.TransactionsRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@Service
public class RewardsService {

    private final PointsAggregator pointsAggregator;
    private final TransactionsRepository transactionsRepository;
    private final static int reportPeriodMonths = 3;

    public RewardsService(PointsAggregator pointsAggregator, TransactionsRepository transactionsRepository) {
        this.pointsAggregator = pointsAggregator;
        this.transactionsRepository = transactionsRepository;
    }

    public List<CustomerPoints> calculateForAllCustomers(YearMonth endMonthForPointsCalculation) {
        LocalDateTime endDate = pointsCalculationEndDate(endMonthForPointsCalculation);
        List<Transaction> transactions = transactionsRepository
                .findAllByTransactionDateBetween(endDate.minusMonths(reportPeriodMonths), endDate);

        return pointsAggregator.aggregate(transactions);
    }

    public List<CustomerPoints> calculateForSingleCustomer(Long customerId, YearMonth endMonthForPointsCalculation) {
        LocalDateTime endDate = pointsCalculationEndDate(endMonthForPointsCalculation);
        List<Transaction> transactions = transactionsRepository
                .findAllByCustomerIdAndTransactionDateBetween(customerId, endDate.minusMonths(reportPeriodMonths), endDate);

        return pointsAggregator.aggregate(transactions);
    }

    private static LocalDateTime pointsCalculationEndDate(YearMonth endDateForPointsCalculation) {
        return LocalDateTime.from(endDateForPointsCalculation.atEndOfMonth().plusDays(1).atStartOfDay());
    }
}
