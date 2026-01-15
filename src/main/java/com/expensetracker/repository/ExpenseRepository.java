package com.expensetracker.repository;

import com.expensetracker.model.Expense;
import com.expensetracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByCategoryId(Long categoryId);

    List<Expense> findByDateBetween(LocalDate startDate, LocalDate endDate);

    List<Expense> findByCategoryIdAndDateBetween(Long categoryId, LocalDate startDate, LocalDate endDate);

    // User-filtered queries
    List<Expense> findByUserOrderByDateDesc(User user);

    Optional<Expense> findByIdAndUser(Long id, User user);

    @Query("SELECT e FROM Expense e WHERE e.user = :user AND " +
           "(:categoryId IS NULL OR e.category.id = :categoryId) AND " +
           "(:startDate IS NULL OR e.date >= :startDate) AND " +
           "(:endDate IS NULL OR e.date <= :endDate) " +
           "ORDER BY e.date DESC")
    List<Expense> findWithFiltersByUser(
            @Param("user") User user,
            @Param("categoryId") Long categoryId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT e FROM Expense e WHERE " +
           "(:categoryId IS NULL OR e.category.id = :categoryId) AND " +
           "(:startDate IS NULL OR e.date >= :startDate) AND " +
           "(:endDate IS NULL OR e.date <= :endDate) " +
           "ORDER BY e.date DESC")
    List<Expense> findWithFilters(
            @Param("categoryId") Long categoryId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.user = :user AND " +
           "(:startDate IS NULL OR e.date >= :startDate) AND " +
           "(:endDate IS NULL OR e.date <= :endDate)")
    BigDecimal getTotalAmountByUser(
            @Param("user") User user,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT SUM(e.amount) FROM Expense e WHERE " +
           "(:startDate IS NULL OR e.date >= :startDate) AND " +
           "(:endDate IS NULL OR e.date <= :endDate)")
    BigDecimal getTotalAmount(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT e.category.id, e.category.name, SUM(e.amount), COUNT(e) FROM Expense e " +
           "WHERE e.user = :user AND " +
           "(:startDate IS NULL OR e.date >= :startDate) AND " +
           "(:endDate IS NULL OR e.date <= :endDate) " +
           "GROUP BY e.category.id, e.category.name " +
           "ORDER BY SUM(e.amount) DESC")
    List<Object[]> getCategorySummaryByUser(
            @Param("user") User user,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT e.category.id, e.category.name, SUM(e.amount), COUNT(e) FROM Expense e " +
           "WHERE (:startDate IS NULL OR e.date >= :startDate) AND " +
           "(:endDate IS NULL OR e.date <= :endDate) " +
           "GROUP BY e.category.id, e.category.name " +
           "ORDER BY SUM(e.amount) DESC")
    List<Object[]> getCategorySummary(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT YEAR(e.date), MONTH(e.date), SUM(e.amount), COUNT(e) FROM Expense e " +
           "WHERE e.user = :user AND " +
           "(:startDate IS NULL OR e.date >= :startDate) AND " +
           "(:endDate IS NULL OR e.date <= :endDate) " +
           "GROUP BY YEAR(e.date), MONTH(e.date) " +
           "ORDER BY YEAR(e.date) DESC, MONTH(e.date) DESC")
    List<Object[]> getMonthlySummaryByUser(
            @Param("user") User user,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT YEAR(e.date), MONTH(e.date), SUM(e.amount), COUNT(e) FROM Expense e " +
           "WHERE (:startDate IS NULL OR e.date >= :startDate) AND " +
           "(:endDate IS NULL OR e.date <= :endDate) " +
           "GROUP BY YEAR(e.date), MONTH(e.date) " +
           "ORDER BY YEAR(e.date) DESC, MONTH(e.date) DESC")
    List<Object[]> getMonthlySummary(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    boolean existsByCategoryId(Long categoryId);
}
