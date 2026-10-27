package com.bryan.dompetku.service;

import com.bryan.dompetku.dto.CategoryRequest;
import com.bryan.dompetku.dto.CategoryResponse;
import com.bryan.dompetku.entity.Category;
import com.bryan.dompetku.entity.CategoryType;
import com.bryan.dompetku.exception.ConflictException;
import com.bryan.dompetku.exception.ResourceNotFoundException;
import com.bryan.dompetku.repository.BudgetRepository;
import com.bryan.dompetku.repository.CategoryRepository;
import com.bryan.dompetku.repository.TransactionRepository;
import com.bryan.dompetku.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    private static final Long USER_ID = 1L;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BudgetRepository budgetRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void create_trimsNameAndSavesCategory() {
        when(categoryRepository.existsByUserIdAndNameIgnoreCase(USER_ID, "Makan")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CategoryResponse response = categoryService.create(USER_ID, new CategoryRequest("  Makan  ", CategoryType.EXPENSE));

        assertThat(response.name()).isEqualTo("Makan");
        assertThat(response.type()).isEqualTo(CategoryType.EXPENSE);
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void create_throwsConflict_whenNameAlreadyExists() {
        when(categoryRepository.existsByUserIdAndNameIgnoreCase(USER_ID, "Gaji")).thenReturn(true);

        assertThatThrownBy(() -> categoryService.create(USER_ID, new CategoryRequest("Gaji", CategoryType.INCOME)))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("sudah ada");

        verify(categoryRepository, never()).save(any());
    }

    @Test
    void findById_throwsNotFound_whenCategoryBelongsToAnotherUser() {
        when(categoryRepository.findByIdAndUserId(99L, USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.findById(USER_ID, 99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_throwsConflict_whenCategoryIsStillUsed() {
        Category category = new Category("Makan", CategoryType.EXPENSE, null);
        when(categoryRepository.findByIdAndUserId(5L, USER_ID)).thenReturn(Optional.of(category));
        when(transactionRepository.countByCategoryId(5L)).thenReturn(3L);

        assertThatThrownBy(() -> categoryService.delete(USER_ID, 5L))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("3 transaksi");

        verify(categoryRepository, never()).delete(any());
    }
}
