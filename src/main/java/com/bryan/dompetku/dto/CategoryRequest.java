package com.bryan.dompetku.dto;

import com.bryan.dompetku.entity.CategoryType;

public record CategoryRequest(String name, CategoryType type) {
}