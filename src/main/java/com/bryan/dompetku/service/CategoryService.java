package com.bryan.dompetku.service;

import com.bryan.dompetku.dto.CategoryRequest;
import com.bryan.dompetku.dto.CategoryResponse;
import com.bryan.dompetku.entity.Category;
import com.bryan.dompetku.exception.ResourceNotFoundException;
import com.bryan.dompetku.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<CategoryResponse> findAll() {
        return categoryRepository.findAll()
                .stream()
                .map(CategoryResponse::from)
                .toList();
    }

    public CategoryResponse findById(Long id) {
        return CategoryResponse.from(getCategoryOrThrow(id));
    }

    public CategoryResponse create(CategoryRequest request) {
        Category category = new Category(request.name().trim(), request.type());
        Category saved = categoryRepository.save(category);
        return CategoryResponse.from(saved);
    }

    public CategoryResponse update(Long id, CategoryRequest request) {
        Category category = getCategoryOrThrow(id);
        category.setName(request.name().trim());
        category.setType(request.type());
        Category saved = categoryRepository.save(category);
        return CategoryResponse.from(saved);
    }

    public void delete(Long id) {
        Category category = getCategoryOrThrow(id);
        categoryRepository.delete(category);
    }

    private Category getCategoryOrThrow(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kategori dengan id " + id + " tidak ditemukan"));
    }
}