package com.bryan.dompetku.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.YearMonth;

public record BudgetRequest(
        @NotNull(message = "Kategori wajib diisi")
        Long categoryId,

        @NotNull(message = "Bulan wajib diisi (format: 2026-10)")
        YearMonth month,

        @NotNull(message = "Jumlah budget wajib diisi")
        @Positive(message = "Jumlah budget harus lebih dari 0")
        @Digits(integer = 13, fraction = 2, message = "Jumlah maksimal 13 digit dan 2 angka desimal")
        BigDecimal amount
) {
}
