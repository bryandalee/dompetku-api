package com.bryan.dompetku.repository;

import com.bryan.dompetku.dto.CategorySummary;
import com.bryan.dompetku.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @EntityGraph(attributePaths = "category")
    Page<Transaction> findByUserIdAndTransactionDateBetween(
            Long userId, LocalDate start, LocalDate end, Pageable pageable);

    @EntityGraph(attributePaths = "category")
    Page<Transaction> findByUserIdAndCategoryIdAndTransactionDateBetween(
            Long userId, Long categoryId, LocalDate start, LocalDate end, Pageable pageable);

    @EntityGraph(attributePaths = "category")
    Optional<Transaction> findByIdAndUserId(Long id, Long userId);

    long countByCategoryId(Long categoryId);

    @Query("""
            select new com.bryan.dompetku.dto.CategorySummary(c.id, c.name, c.type, sum(t.amount))
            from Transaction t
            join t.category c
            where t.user.id = :userId
              and t.transactionDate between :start and :end
            group by c.id, c.name, c.type
            order by sum(t.amount) desc
            """)
    List<CategorySummary> summarizeByCategory(
            @Param("userId") Long userId, @Param("start") LocalDate start, @Param("end") LocalDate end);
}
