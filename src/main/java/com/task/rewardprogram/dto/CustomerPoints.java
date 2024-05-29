package com.task.rewardprogram.dto;

import java.time.YearMonth;
import java.util.Map;

public record CustomerPoints(Long customerId, Map<YearMonth, Long> monthlyPoints, long pointsSum) {
}
