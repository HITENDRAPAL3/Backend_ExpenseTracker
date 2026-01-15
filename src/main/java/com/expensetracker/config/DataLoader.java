package com.expensetracker.config;

import com.expensetracker.model.Category;
import com.expensetracker.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final CategoryRepository categoryRepository;

    @Override
    public void run(String... args) {
        if (categoryRepository.count() == 0) {
            log.info("Loading seed data...");
            loadCategories();
            log.info("Seed data loaded successfully!");
        } else {
            log.info("Data already exists, skipping seed data loading.");
        }
    }

    private void loadCategories() {
        List<Category> categories = Arrays.asList(
            new Category("Food & Dining", "Restaurants, groceries, and food delivery"),
            new Category("Transportation", "Gas, public transit, rideshare, and parking"),
            new Category("Shopping", "Clothing, electronics, and general purchases"),
            new Category("Entertainment", "Movies, games, subscriptions, and hobbies"),
            new Category("Bills & Utilities", "Electricity, water, internet, and phone"),
            new Category("Healthcare", "Medical expenses, pharmacy, and insurance"),
            new Category("Travel", "Flights, hotels, and vacation expenses"),
            new Category("Education", "Courses, books, and learning materials"),
            new Category("Personal Care", "Grooming, gym, and wellness"),
            new Category("Other", "Miscellaneous expenses")
        );
        categoryRepository.saveAll(categories);
        log.info("Loaded {} categories", categories.size());
    }
}
