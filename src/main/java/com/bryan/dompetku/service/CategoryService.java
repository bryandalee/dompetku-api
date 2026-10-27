package com.bryan.dompetku.service;

import com.bryan.dompetku.dto.CategoryRequest;
import com.bryan.dompetku.dto.CategoryResponse;
import com.bryan.dompetku.entity.Category;
import com.bryan.dompetku.entity.User;
import com.bryan.dompetku.exception.ConflictException;
import com.bryan.dompetku.exception.ResourceNotFoundException;
import com.bryan.dompetku.repository.BudgetRepository;
import com.bryan.dompetku.repository.CategoryRepository;
import com.bryan.dompetku.repository.TransactionRepository;
import com.bryan.dompetku.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final BudgetRepository budgetRepository;

    public CategoryService(CategoryRepository categoryRepository,
                           TransactionRepository transactionRepository,
                           UserRepository userRepository,
                           BudgetRepository budgetRepository) {
        this.categoryRepository = categoryRepository;
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.budgetRepository = budgetRepository;
    }

    public List<CategoryResponse> findAll(Long userId) {
        return categoryRepository.findByUserIdOrderByNameAsc(userId)
                .stream()
                .map(CategoryResponse::from)
                .toList();
    }

    public CategoryResponse findById(Long userId, Long id) {
        return CategoryResponse.from(getCategoryOrThrow(userId, id));
    }

    @Transactional
    public CategoryResponse create(Long userId, CategoryRequest request) {
        String name = request.name().trim();
        if (categoryRepository.existsByUserIdAndNameIgnoreCase(userId, name)) {
            throw new ConflictException("Kategori '" + name + "' sudah ada");
        }
        User user = userRepository.getReferenceById(userId);
        Category saved = categoryRepository.save(new Category(name, request.type(), user));
        return CategoryResponse.from(saved);
    }

    @Transactional
    public CategoryResponse update(Long userId, Long id, CategoryRequest request) {
        Category category = getCategoryOrThrow(userId, id);
        String name = request.name().trim();
        if (categoryRepository.existsByUserIdAndNameIgnoreCaseAndIdNot(userId, name, id)) {
            throw new ConflictException("Kategori '" + name + "' sudah ada");
        }
        category.setName(name);
        category.setType(request.type());
        Category saved = categoryRepository.save(category);
        return CategoryResponse.from(saved);
    }

    @Transactional
    public void delete(Long userId, Long id) {
        Category category = getCategoryOrThrow(userId, id);
        long usedCount = transactionRepository.countByCategoryId(id);
        if (usedCount > 0) {
            throw new ConflictException(
                    "Kategori tidak bisa dihapus karena masih dipakai oleh " + usedCount + " transaksi");
        }
        budgetRepository.deleteByCategoryId(id);
        categoryRepository.delete(category);
    }

    private Category getCategoryOrThrow(Long userId, Long id) {
        return categoryRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Kategori dengan id " + id + " tidak ditemukan"));
    }
}
