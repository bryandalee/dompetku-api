package com.bryan.dompetku.service;

import com.bryan.dompetku.dto.BudgetRequest;
import com.bryan.dompetku.dto.BudgetResponse;
import com.bryan.dompetku.dto.CategorySummary;
import com.bryan.dompetku.entity.Budget;
import com.bryan.dompetku.entity.Category;
import com.bryan.dompetku.entity.CategoryType;
import com.bryan.dompetku.exception.BadRequestException;
import com.bryan.dompetku.exception.ResourceNotFoundException;
import com.bryan.dompetku.repository.BudgetRepository;
import com.bryan.dompetku.repository.CategoryRepository;
import com.bryan.dompetku.repository.TransactionRepository;
import com.bryan.dompetku.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public BudgetService(BudgetRepository budgetRepository,
                         CategoryRepository categoryRepository,
                         TransactionRepository transactionRepository,
                         UserRepository userRepository) {
        this.budgetRepository = budgetRepository;
        this.categoryRepository = categoryRepository;
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    public List<BudgetResponse> findAll(Long userId, YearMonth month) {
        YearMonth selectedMonth = (month != null) ? month : YearMonth.now();
        Map<Long, BigDecimal> spentByCategory = getSpentByCategory(userId, selectedMonth);

        return budgetRepository.findByUserIdAndMonthStartOrderByCategoryNameAsc(userId, selectedMonth.atDay(1))
                .stream()
                .map(budget -> BudgetResponse.from(budget, spentOf(spentByCategory, budget)))
                .toList();
    }

    @Transactional
    public BudgetResponse save(Long userId, BudgetRequest request) {
        Category category = categoryRepository.findByIdAndUserId(request.categoryId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Kategori dengan id " + request.categoryId() + " tidak ditemukan"));
        if (category.getType() != CategoryType.EXPENSE) {
            throw new BadRequestException("Budget hanya bisa dibuat untuk kategori pengeluaran (EXPENSE)");
        }

        LocalDate monthStart = request.month().atDay(1);
        Budget budget = budgetRepository
                .findByUserIdAndCategoryIdAndMonthStart(userId, category.getId(), monthStart)
                .orElseGet(() -> new Budget(request.amount(), monthStart, category,
                        userRepository.getReferenceById(userId)));
        budget.setAmount(request.amount());
        Budget saved = budgetRepository.save(budget);

        Map<Long, BigDecimal> spentByCategory = getSpentByCategory(userId, request.month());
        return BudgetResponse.from(saved, spentOf(spentByCategory, saved));
    }

    @Transactional
    public void delete(Long userId, Long id) {
        Budget budget = budgetRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget dengan id " + id + " tidak ditemukan"));
        budgetRepository.delete(budget);
    }

    private Map<Long, BigDecimal> getSpentByCategory(Long userId, YearMonth month) {
        List<CategorySummary> summaries = transactionRepository.summarizeByCategory(
                userId, month.atDay(1), month.atEndOfMonth());

        Map<Long, BigDecimal> spentByCategory = new HashMap<>();
        for (CategorySummary summary : summaries) {
            spentByCategory.put(summary.categoryId(), summary.total());
        }
        return spentByCategory;
    }

    private BigDecimal spentOf(Map<Long, BigDecimal> spentByCategory, Budget budget) {
        return spentByCategory.getOrDefault(budget.getCategory().getId(), BigDecimal.ZERO);
    }
}
