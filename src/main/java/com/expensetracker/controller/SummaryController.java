package com.expensetracker.controller;

import com.expensetracker.dto.CategorySummaryDTO;
import com.expensetracker.dto.MonthlySummaryDTO;
import com.expensetracker.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/summary")
@RequiredArgsConstructor
public class SummaryController {

    private final ExpenseService expenseService;

    @GetMapping("/total")
    public ResponseEntity<Map<String, Object>> getTotalExpenses(
            Authentication authentication,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        String username = authentication.getName();
        BigDecimal total = expenseService.getTotalExpenses(username, startDate, endDate);
        Map<String, Object> response = new HashMap<>();
        response.put("total", total);
        response.put("startDate", startDate);
        response.put("endDate", endDate);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-category")
    public ResponseEntity<List<CategorySummaryDTO>> getCategorySummary(
            Authentication authentication,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        String username = authentication.getName();
        return ResponseEntity.ok(expenseService.getCategorySummary(username, startDate, endDate));
    }

    @GetMapping("/monthly")
    public ResponseEntity<List<MonthlySummaryDTO>> getMonthlySummary(
            Authentication authentication,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        String username = authentication.getName();
        return ResponseEntity.ok(expenseService.getMonthlySummary(username, startDate, endDate));
    }
}
