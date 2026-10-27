package com.bryan.dompetku.service;

import com.bryan.dompetku.dto.CategorySummary;
import com.bryan.dompetku.dto.MonthlySummaryResponse;
import com.bryan.dompetku.entity.CategoryType;
import com.bryan.dompetku.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private ReportService reportService;

    @Test
    void getMonthlySummary_calculatesTotalsAndBalance() {
        YearMonth october = YearMonth.of(2026, 10);
        when(transactionRepository.summarizeByCategory(1L, october.atDay(1), october.atEndOfMonth()))
                .thenReturn(List.of(
                        new CategorySummary(1L, "Gaji", CategoryType.INCOME, new BigDecimal("5000000")),
                        new CategorySummary(2L, "Makan & Minum", CategoryType.EXPENSE, new BigDecimal("750000")),
                        new CategorySummary(3L, "Transportasi", CategoryType.EXPENSE, new BigDecimal("250000"))));

        MonthlySummaryResponse summary = reportService.getMonthlySummary(1L, october);

        assertThat(summary.totalIncome()).isEqualByComparingTo("5000000");
        assertThat(summary.totalExpense()).isEqualByComparingTo("1000000");
        assertThat(summary.balance()).isEqualByComparingTo("4000000");
        assertThat(summary.categories()).hasSize(3);
    }

    @Test
    void getMonthlySummary_returnsZero_whenThereAreNoTransactions() {
        YearMonth august = YearMonth.of(2026, 8);
        when(transactionRepository.summarizeByCategory(1L, august.atDay(1), august.atEndOfMonth()))
                .thenReturn(List.of());

        MonthlySummaryResponse summary = reportService.getMonthlySummary(1L, august);

        assertThat(summary.totalIncome()).isEqualByComparingTo("0");
        assertThat(summary.totalExpense()).isEqualByComparingTo("0");
        assertThat(summary.balance()).isEqualByComparingTo("0");
    }
}
