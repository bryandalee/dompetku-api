package com.bryan.dompetku.controller;

import com.bryan.dompetku.dto.MonthlySummaryResponse;
import com.bryan.dompetku.security.CurrentUser;
import com.bryan.dompetku.service.ReportService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.YearMonth;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/monthly")
    public MonthlySummaryResponse getMonthlySummary(@AuthenticationPrincipal Jwt jwt,
                                                    @RequestParam(required = false) YearMonth month) {
        return reportService.getMonthlySummary(CurrentUser.id(jwt), month);
    }
}
