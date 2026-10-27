package com.bryan.dompetku.dto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class BudgetStatusTest {

    @Test
    void safe_whenSpentBelow80Percent() {
        assertThat(BudgetStatus.of(money(50_000), money(100_000), percent(50))).isEqualTo(BudgetStatus.SAFE);
    }

    @Test
    void warning_whenSpentAtLeast80Percent() {
        assertThat(BudgetStatus.of(money(80_000), money(100_000), percent(80))).isEqualTo(BudgetStatus.WARNING);
    }

    @Test
    void warning_whenSpentExactlyAtLimit() {
        assertThat(BudgetStatus.of(money(100_000), money(100_000), percent(100))).isEqualTo(BudgetStatus.WARNING);
    }

    @Test
    void exceeded_whenSpentOverLimit() {
        assertThat(BudgetStatus.of(money(120_000), money(100_000), percent(120))).isEqualTo(BudgetStatus.EXCEEDED);
    }

    private BigDecimal money(long value) {
        return BigDecimal.valueOf(value);
    }

    private BigDecimal percent(long value) {
        return BigDecimal.valueOf(value);
    }
}
