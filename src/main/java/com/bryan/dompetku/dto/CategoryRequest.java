package com.bryan.dompetku.dto;

import com.bryan.dompetku.entity.CategoryType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CategoryRequest(
        @NotBlank(message = "Nama kategori wajib diisi")
        @Size(max = 50, message = "Nama kategori maksimal 50 karakter")
        String name,

        @NotNull(message = "Tipe kategori wajib diisi (INCOME atau EXPENSE)")
        CategoryType type
) {
}