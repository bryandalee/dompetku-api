package com.bryan.dompetku.service;

import com.bryan.dompetku.dto.CategorySummary;
import com.bryan.dompetku.dto.MonthlySummaryResponse;
import com.bryan.dompetku.entity.CategoryType;
import com.bryan.dompetku.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ReportService {

    private final TransactionRepository transactionRepository;

    public ReportService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public MonthlySummaryResponse getMonthlySummary(YearMonth month) {
        YearMonth selectedMonth = (month != null) ? month : YearMonth.now();
        List<CategorySummary> categories = transactionRepository.summarizeByCategory(
                selectedMonth.atDay(1), selectedMonth.atEndOfMonth());

        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpense = BigDecimal.ZERO;
        for (CategorySummary category : categories) {
            if (category.type() == CategoryType.INCOME) {
                totalIncome = totalIncome.add(category.total());
            } else {
                totalExpense = totalExpense.add(category.total());
            }
        }

        BigDecimal balance = totalIncome.subtract(totalExpense);
        return new MonthlySummaryResponse(selectedMonth, totalIncome, totalExpense, balance, categories);
    }
}