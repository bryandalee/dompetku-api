package com.bryan.dompetku.dto;

import java.math.BigDecimal;

public enum BudgetStatus {
    SAFE,
    WARNING,
    EXCEEDED;

    private static final BigDecimal WARNING_PERCENTAGE = BigDecimal.valueOf(80);

    public static BudgetStatus of(BigDecimal spent, BigDecimal limit, BigDecimal percentage) {
        if (spent.compareTo(limit) > 0) {
            return EXCEEDED;
        }
        if (percentage.compareTo(WARNING_PERCENTAGE) >= 0) {
            return WARNING;
        }
        return SAFE;
    }
}
