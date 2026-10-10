package com.bryan.dompetku.repository;

import com.bryan.dompetku.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
