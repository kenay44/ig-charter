package com.task.rewardprogram.services;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PointsCalculator {

    private static final long SECOND_LEVEL_THRESHOLD = 100;
    private static final long FIRST_LEVEL_THRESHOLD = 50;
    private static final long SECOND_LEVEL_POINTS = 2;
    private static final long FIRST_LEVEL_POINTS = 1;
    private static final long BASE_LEVEL_POINTS = 0;

    public long calculate(BigDecimal transactionValue) {
        long transactionValueRoundedDown = transactionValue.toBigInteger().longValue();
        return secondLevelBonus(transactionValueRoundedDown) + firstLevelPoints(transactionValueRoundedDown);
    }

    private  long firstLevelPoints(long value) {
        if (value <= FIRST_LEVEL_THRESHOLD) {
            return BASE_LEVEL_POINTS;
        }
        return (firstLevelUpperThreshold(value) - FIRST_LEVEL_THRESHOLD) * FIRST_LEVEL_POINTS;
    }

    private  long firstLevelUpperThreshold(long value) {
        return Math.min(SECOND_LEVEL_THRESHOLD, value);
    }

    private  long secondLevelBonus(long value) {
        if (value <= SECOND_LEVEL_THRESHOLD) {
            return BASE_LEVEL_POINTS;
        }
        return (value - SECOND_LEVEL_THRESHOLD) * SECOND_LEVEL_POINTS;
    }
}
