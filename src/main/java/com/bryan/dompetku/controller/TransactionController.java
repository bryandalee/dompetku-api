package com.bryan.dompetku.controller;

import com.bryan.dompetku.dto.PageResponse;
import com.bryan.dompetku.dto.TransactionRequest;
import com.bryan.dompetku.dto.TransactionResponse;
import com.bryan.dompetku.security.CurrentUser;
import com.bryan.dompetku.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.YearMonth;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public PageResponse<TransactionResponse> findAll(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(required = false) YearMonth month,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return transactionService.findAll(CurrentUser.id(jwt), month, categoryId, page, size);
    }

    @GetMapping("/{id}")
    public TransactionResponse findById(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        return transactionService.findById(CurrentUser.id(jwt), id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse create(@AuthenticationPrincipal Jwt jwt,
                                      @Valid @RequestBody TransactionRequest request) {
        return transactionService.create(CurrentUser.id(jwt), request);
    }

    @PutMapping("/{id}")
    public TransactionResponse update(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id,
                                      @Valid @RequestBody TransactionRequest request) {
        return transactionService.update(CurrentUser.id(jwt), id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        transactionService.delete(CurrentUser.id(jwt), id);
    }
}
