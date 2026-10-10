package com.bryan.dompetku.dto;
import java.time.LocalDateTime;
public record GreetingResponse(String message, LocalDateTime timestamp) {
}
