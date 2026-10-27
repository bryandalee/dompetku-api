package com.bryan.dompetku.controller;

import com.bryan.dompetku.dto.BudgetRequest;
import com.bryan.dompetku.dto.BudgetResponse;
import com.bryan.dompetku.security.CurrentUser;
import com.bryan.dompetku.service.BudgetService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/budgets")
public class BudgetController {

    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @GetMapping
    public List<BudgetResponse> findAll(@AuthenticationPrincipal Jwt jwt,
                                        @RequestParam(required = false) YearMonth month) {
        return budgetService.findAll(CurrentUser.id(jwt), month);
    }

    @PutMapping
    public BudgetResponse save(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody BudgetRequest request) {
        return budgetService.save(CurrentUser.id(jwt), request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        budgetService.delete(CurrentUser.id(jwt), id);
    }
}
