package com.bryan.dompetku.repository;

import com.bryan.dompetku.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findAllByOrderByTransactionDateDescIdDesc();
}