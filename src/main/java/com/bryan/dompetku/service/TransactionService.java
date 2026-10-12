package com.bryan.dompetku.service;

import com.bryan.dompetku.dto.TransactionRequest;
import com.bryan.dompetku.dto.TransactionResponse;
import com.bryan.dompetku.entity.Category;
import com.bryan.dompetku.entity.Transaction;
import com.bryan.dompetku.exception.ResourceNotFoundException;
import com.bryan.dompetku.repository.CategoryRepository;
import com.bryan.dompetku.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;

    public TransactionService(TransactionRepository transactionRepository, CategoryRepository categoryRepository) {
        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<TransactionResponse> findAll() {
        return transactionRepository.findAllByOrderByTransactionDateDescIdDesc()
                .stream()
                .map(TransactionResponse::from)
                .toList();
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