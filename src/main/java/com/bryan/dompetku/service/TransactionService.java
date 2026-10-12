package com.bryan.dompetku.service;

import com.bryan.dompetku.dto.PageResponse;
import com.bryan.dompetku.dto.TransactionRequest;
import com.bryan.dompetku.dto.TransactionResponse;
import com.bryan.dompetku.entity.Category;
import com.bryan.dompetku.entity.Transaction;
import com.bryan.dompetku.exception.ResourceNotFoundException;
import com.bryan.dompetku.repository.CategoryRepository;
import com.bryan.dompetku.repository.TransactionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;

@Service
@Transactional(readOnly = true)
public class TransactionService {

    private static final int MAX_PAGE_SIZE = 100;

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;

    public TransactionService(TransactionRepository transactionRepository, CategoryRepository categoryRepository) {
        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;
    }

    public PageResponse<TransactionResponse> findAll(YearMonth month, Long categoryId, int page, int size) {
        YearMonth selectedMonth = (month != null) ? month : YearMonth.now();
        LocalDate start = selectedMonth.atDay(1);
        LocalDate end = selectedMonth.atEndOfMonth();

        Pageable pageable = PageRequest.of(
                Math.max(page, 0),
                Math.min(Math.max(size, 1), MAX_PAGE_SIZE),
                Sort.by(Sort.Direction.DESC, "transactionDate", "id"));

        Page<Transaction> transactions;
        if (categoryId == null) {
            transactions = transactionRepository.findByTransactionDateBetween(start, end, pageable);
        } else {
            transactions = transactionRepository.findByCategoryIdAndTransactionDateBetween(
                    categoryId, start, end, pageable);
        }

        return PageResponse.from(transactions.map(TransactionResponse::from));
    }

    public TransactionResponse findById(Long id) {
        return TransactionResponse.from(getTransactionOrThrow(id));
    }

    @Transactional
    public TransactionResponse create(TransactionRequest request) {
        Category category = getCategoryOrThrow(request.categoryId());
        Transaction transaction = new Transaction(
                request.amount(), request.transactionDate(), request.note(), category);
        Transaction saved = transactionRepository.save(transaction);
        return TransactionResponse.from(saved);
    }

    @Transactional
    public TransactionResponse update(Long id, TransactionRequest request) {
        Transaction transaction = getTransactionOrThrow(id);
        Category category = getCategoryOrThrow(request.categoryId());
        transaction.setAmount(request.amount());
        transaction.setTransactionDate(request.transactionDate());
        transaction.setNote(request.note());
        transaction.setCategory(category);
        Transaction saved = transactionRepository.save(transaction);
        return TransactionResponse.from(saved);
    }

    @Transactional
    public void delete(Long id) {
        Transaction transaction = getTransactionOrThrow(id);
        transactionRepository.delete(transaction);
    }

    private Transaction getTransactionOrThrow(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaksi dengan id " + id + " tidak ditemukan"));
    }

    private Category getCategoryOrThrow(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kategori dengan id " + id + " tidak ditemukan"));
    }
}