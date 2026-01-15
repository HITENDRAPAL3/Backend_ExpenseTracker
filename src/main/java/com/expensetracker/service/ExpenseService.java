package com.expensetracker.service;

import com.expensetracker.dto.CategorySummaryDTO;
import com.expensetracker.dto.ExpenseDTO;
import com.expensetracker.dto.MonthlySummaryDTO;
import com.expensetracker.model.Category;
import com.expensetracker.model.Expense;
import com.expensetracker.model.User;
import com.expensetracker.repository.CategoryRepository;
import com.expensetracker.repository.ExpenseRepository;
import com.expensetracker.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    private User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + username));
    }

    public List<ExpenseDTO> getAllExpenses(String username, Long categoryId, LocalDate startDate, LocalDate endDate) {
        User user = getUserByUsername(username);
        List<Expense> expenses = expenseRepository.findWithFiltersByUser(user, categoryId, startDate, endDate);
        return expenses.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ExpenseDTO getExpenseById(String username, Long id) {
        User user = getUserByUsername(username);
        Expense expense = expenseRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new EntityNotFoundException("Expense not found with id: " + id));
        return convertToDTO(expense);
    }

    public ExpenseDTO createExpense(String username, ExpenseDTO expenseDTO) {
        User user = getUserByUsername(username);
        Category category = categoryRepository.findById(expenseDTO.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + expenseDTO.getCategoryId()));

        Expense expense = new Expense();
        expense.setDescription(expenseDTO.getDescription());
        expense.setAmount(expenseDTO.getAmount());
        expense.setDate(expenseDTO.getDate());
        expense.setCategory(category);
        expense.setUser(user);

        Expense savedExpense = expenseRepository.save(expense);
        return convertToDTO(savedExpense);
    }

    public ExpenseDTO updateExpense(String username, Long id, ExpenseDTO expenseDTO) {
        User user = getUserByUsername(username);
        Expense expense = expenseRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new EntityNotFoundException("Expense not found with id: " + id));

        Category category = categoryRepository.findById(expenseDTO.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + expenseDTO.getCategoryId()));

        expense.setDescription(expenseDTO.getDescription());
        expense.setAmount(expenseDTO.getAmount());
        expense.setDate(expenseDTO.getDate());
        expense.setCategory(category);

        Expense updatedExpense = expenseRepository.save(expense);
        return convertToDTO(updatedExpense);
    }

    public void deleteExpense(String username, Long id) {
        User user = getUserByUsername(username);
        Expense expense = expenseRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new EntityNotFoundException("Expense not found with id: " + id));
        expenseRepository.delete(expense);
    }

    public BigDecimal getTotalExpenses(String username, LocalDate startDate, LocalDate endDate) {
        User user = getUserByUsername(username);
        BigDecimal total = expenseRepository.getTotalAmountByUser(user, startDate, endDate);
        return total != null ? total : BigDecimal.ZERO;
    }

    public List<CategorySummaryDTO> getCategorySummary(String username, LocalDate startDate, LocalDate endDate) {
        User user = getUserByUsername(username);
        List<Object[]> results = expenseRepository.getCategorySummaryByUser(user, startDate, endDate);
        BigDecimal totalAmount = getTotalExpenses(username, startDate, endDate);

        return results.stream()
                .map(row -> {
                    CategorySummaryDTO dto = new CategorySummaryDTO(
                            (Long) row[0],
                            (String) row[1],
                            (BigDecimal) row[2],
                            (Long) row[3]
                    );
                    if (totalAmount.compareTo(BigDecimal.ZERO) > 0) {
                        dto.setPercentage(dto.getTotalAmount()
                                .divide(totalAmount, 4, RoundingMode.HALF_UP)
                                .multiply(BigDecimal.valueOf(100))
                                .doubleValue());
                    } else {
                        dto.setPercentage(0.0);
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<MonthlySummaryDTO> getMonthlySummary(String username, LocalDate startDate, LocalDate endDate) {
        User user = getUserByUsername(username);
        List<Object[]> results = expenseRepository.getMonthlySummaryByUser(user, startDate, endDate);

        return results.stream()
                .map(row -> new MonthlySummaryDTO(
                        (Integer) row[0],
                        (Integer) row[1],
                        (BigDecimal) row[2],
                        (Long) row[3]
                ))
                .collect(Collectors.toList());
    }

    private ExpenseDTO convertToDTO(Expense expense) {
        ExpenseDTO dto = new ExpenseDTO();
        dto.setId(expense.getId());
        dto.setDescription(expense.getDescription());
        dto.setAmount(expense.getAmount());
        dto.setDate(expense.getDate());
        dto.setCategoryId(expense.getCategory().getId());
        dto.setCategoryName(expense.getCategory().getName());
        return dto;
    }
}
