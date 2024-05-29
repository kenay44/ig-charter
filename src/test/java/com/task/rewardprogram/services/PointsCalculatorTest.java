package com.task.rewardprogram.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

public class PointsCalculatorTest {

    PointsCalculator pointsCalculator;

    @BeforeEach
    void setUp() {
        pointsCalculator = new PointsCalculator();
    }

    @Test
    @DisplayName("Transaction with value below first level bonus should provide zero points")
    public void transactionBelowFirstLevelPoints() {
        Assertions.assertEquals(0, pointsCalculator.calculate(BigDecimal.valueOf(40)));
        Assertions.assertEquals(0, pointsCalculator.calculate(BigDecimal.valueOf(50)));
        Assertions.assertEquals(0, pointsCalculator.calculate(BigDecimal.valueOf(0)));
    }

    @Test
    @DisplayName("Transaction with value in first level bonus should provide first level points")
    public void transactionInFirstLevelPoints() {
        Assertions.assertEquals(1, pointsCalculator.calculate(BigDecimal.valueOf(51)));
        Assertions.assertEquals(20, pointsCalculator.calculate(BigDecimal.valueOf(70)));
        Assertions.assertEquals(50, pointsCalculator.calculate(BigDecimal.valueOf(100)));
    }

    @Test
    @DisplayName("Transaction with value in second level bonus should provide first level and second level points")
    public void transactionInSecondLevelPoints() {
        Assertions.assertEquals(52, pointsCalculator.calculate(BigDecimal.valueOf(101)));
        Assertions.assertEquals(90, pointsCalculator.calculate(BigDecimal.valueOf(120)));
    }
}
