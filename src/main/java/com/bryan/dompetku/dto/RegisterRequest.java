package com.bryan.dompetku.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Nama wajib diisi")
        @Size(max = 50, message = "Nama maksimal 50 karakter")
        String name,

        @NotBlank(message = "Email wajib diisi")
        @Email(message = "Format email tidak valid")
        @Size(max = 100, message = "Email maksimal 100 karakter")
        String email,

        @NotBlank(message = "Password wajib diisi")
        @Size(min = 8, max = 72, message = "Password harus 8 sampai 72 karakter")
        String password
) {
}
