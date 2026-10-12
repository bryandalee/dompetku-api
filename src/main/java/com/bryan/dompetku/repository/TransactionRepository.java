package com.bryan.dompetku.repository;

import com.bryan.dompetku.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @EntityGraph(attributePaths = "category")
    Page<Transaction> findByTransactionDateBetween(LocalDate start, LocalDate end, Pageable pageable);

    @EntityGraph(attributePaths = "category")
    Page<Transaction> findByCategoryIdAndTransactionDateBetween(
            Long categoryId, LocalDate start, LocalDate end, Pageable pageable);

    long countByCategoryId(Long categoryId);
}