package com.expensetracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategorySummaryDTO {

    private Long categoryId;
    private String categoryName;
    private BigDecimal totalAmount;
    private Long expenseCount;
    private Double percentage;

    public CategorySummaryDTO(Long categoryId, String categoryName, BigDecimal totalAmount, Long expenseCount) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.totalAmount = totalAmount;
        this.expenseCount = expenseCount;
    }
}
