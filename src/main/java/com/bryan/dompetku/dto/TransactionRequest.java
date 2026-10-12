package com.bryan.dompetku.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionRequest(
        @NotNull(message = "Jumlah wajib diisi")
        @Positive(message = "Jumlah harus lebih dari 0")
        @Digits(integer = 13, fraction = 2, message = "Jumlah maksimal 13 digit dan 2 angka desimal")
        BigDecimal amount,

        @NotNull(message = "Tanggal transaksi wajib diisi")
        LocalDate transactionDate,

        @Size(max = 255, message = "Catatan maksimal 255 karakter")
        String note,

        @NotNull(message = "Kategori wajib diisi")
        Long categoryId
) {
}