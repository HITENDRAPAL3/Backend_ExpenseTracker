package com.expensetracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthlySummaryDTO {

    private Integer year;
    private Integer month;
    private String monthName;
    private BigDecimal totalAmount;
    private Long expenseCount;

    public MonthlySummaryDTO(Integer year, Integer month, BigDecimal totalAmount, Long expenseCount) {
        this.year = year;
        this.month = month;
        this.totalAmount = totalAmount;
        this.expenseCount = expenseCount;
        this.monthName = getMonthNameFromNumber(month);
    }

    private String getMonthNameFromNumber(Integer month) {
        String[] months = {"January", "February", "March", "April", "May", "June",
                          "July", "August", "September", "October", "November", "December"};
        if (month >= 1 && month <= 12) {
            return months[month - 1];
        }
        return "Unknown";
    }
}
