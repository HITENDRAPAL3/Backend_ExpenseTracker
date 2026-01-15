package com.expensetracker.controller;

import com.expensetracker.dto.ExpenseDTO;
import com.expensetracker.service.ExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    @GetMapping
    public ResponseEntity<List<ExpenseDTO>> getAllExpenses(
            Authentication authentication,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        String username = authentication.getName();
        return ResponseEntity.ok(expenseService.getAllExpenses(username, categoryId, startDate, endDate));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExpenseDTO> getExpenseById(Authentication authentication, @PathVariable Long id) {
        String username = authentication.getName();
        return ResponseEntity.ok(expenseService.getExpenseById(username, id));
    }

    @PostMapping
    public ResponseEntity<ExpenseDTO> createExpense(Authentication authentication, @Valid @RequestBody ExpenseDTO expenseDTO) {
        String username = authentication.getName();
        ExpenseDTO created = expenseService.createExpense(username, expenseDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExpenseDTO> updateExpense(Authentication authentication, @PathVariable Long id, @Valid @RequestBody ExpenseDTO expenseDTO) {
        String username = authentication.getName();
        return ResponseEntity.ok(expenseService.updateExpense(username, id, expenseDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(Authentication authentication, @PathVariable Long id) {
        String username = authentication.getName();
        expenseService.deleteExpense(username, id);
        return ResponseEntity.noContent().build();
    }
}
