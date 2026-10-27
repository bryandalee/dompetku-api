package com.bryan.dompetku.dto;

import com.bryan.dompetku.entity.Budget;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;

public record BudgetResponse(
        Long id,
        Long categoryId,
        String categoryName,
        YearMonth month,
        BigDecimal amount,
        BigDecimal spent,
        BigDecimal remaining,
        BigDecimal percentage,
        BudgetStatus status
) {

    public static BudgetResponse from(Budget budget, BigDecimal spent) {
        BigDecimal amount = budget.getAmount();
        BigDecimal remaining = amount.subtract(spent);
        BigDecimal percentage = spent.multiply(BigDecimal.valueOf(100))
                .divide(amount, 1, RoundingMode.HALF_UP);

        return new BudgetResponse(
                budget.getId(),
                budget.getCategory().getId(),
                budget.getCategory().getName(),
                YearMonth.from(budget.getMonthStart()),
                amount,
                spent,
                remaining,
                percentage,
                BudgetStatus.of(spent, amount, percentage)
        );
    }
}
