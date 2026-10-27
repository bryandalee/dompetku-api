package com.bryan.dompetku.repository;

import com.bryan.dompetku.entity.Budget;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Long> {

    @EntityGraph(attributePaths = "category")
    List<Budget> findByUserIdAndMonthStartOrderByCategoryNameAsc(Long userId, LocalDate monthStart);

    Optional<Budget> findByUserIdAndCategoryIdAndMonthStart(Long userId, Long categoryId, LocalDate monthStart);

    @EntityGraph(attributePaths = "category")
    Optional<Budget> findByIdAndUserId(Long id, Long userId);

    void deleteByCategoryId(Long categoryId);
}
