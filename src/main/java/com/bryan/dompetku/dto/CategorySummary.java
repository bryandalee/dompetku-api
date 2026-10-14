package com.bryan.dompetku.dto;

import com.bryan.dompetku.entity.CategoryType;

import java.math.BigDecimal;

public record CategorySummary(
        Long categoryId,
        String categoryName,
        CategoryType type,
        BigDecimal total
) {
}