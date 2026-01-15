-- Seed data for Expense Tracker

-- Insert Categories
INSERT INTO categories (name, description) VALUES ('Food & Dining', 'Restaurants, groceries, and food delivery');
INSERT INTO categories (name, description) VALUES ('Transportation', 'Gas, public transit, rideshare, and parking');
INSERT INTO categories (name, description) VALUES ('Shopping', 'Clothing, electronics, and general purchases');
INSERT INTO categories (name, description) VALUES ('Entertainment', 'Movies, games, subscriptions, and hobbies');
INSERT INTO categories (name, description) VALUES ('Bills & Utilities', 'Electricity, water, internet, and phone');
INSERT INTO categories (name, description) VALUES ('Healthcare', 'Medical expenses, pharmacy, and insurance');
INSERT INTO categories (name, description) VALUES ('Travel', 'Flights, hotels, and vacation expenses');
INSERT INTO categories (name, description) VALUES ('Education', 'Courses, books, and learning materials');
INSERT INTO categories (name, description) VALUES ('Personal Care', 'Grooming, gym, and wellness');
INSERT INTO categories (name, description) VALUES ('Other', 'Miscellaneous expenses');

-- Insert Sample Expenses
INSERT INTO expenses (description, amount, date, category_id, created_at, updated_at) 
VALUES ('Grocery shopping at Walmart', 125.50, '2026-01-10', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO expenses (description, amount, date, category_id, created_at, updated_at) 
VALUES ('Monthly bus pass', 85.00, '2026-01-01', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO expenses (description, amount, date, category_id, created_at, updated_at) 
VALUES ('Netflix subscription', 15.99, '2026-01-05', 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO expenses (description, amount, date, category_id, created_at, updated_at) 
VALUES ('Electric bill', 95.75, '2026-01-08', 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO expenses (description, amount, date, category_id, created_at, updated_at) 
VALUES ('Dinner at Italian restaurant', 68.00, '2026-01-12', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO expenses (description, amount, date, category_id, created_at, updated_at) 
VALUES ('New running shoes', 129.99, '2026-01-07', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO expenses (description, amount, date, category_id, created_at, updated_at) 
VALUES ('Pharmacy - vitamins', 35.50, '2026-01-09', 6, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO expenses (description, amount, date, category_id, created_at, updated_at) 
VALUES ('Online Python course', 49.99, '2026-01-03', 8, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO expenses (description, amount, date, category_id, created_at, updated_at) 
VALUES ('Gasoline', 55.00, '2026-01-11', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO expenses (description, amount, date, category_id, created_at, updated_at) 
VALUES ('Coffee shop', 12.50, '2026-01-13', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- December 2025 expenses for monthly comparison
INSERT INTO expenses (description, amount, date, category_id, created_at, updated_at) 
VALUES ('Christmas groceries', 250.00, '2025-12-23', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO expenses (description, amount, date, category_id, created_at, updated_at) 
VALUES ('Holiday gifts', 350.00, '2025-12-20', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO expenses (description, amount, date, category_id, created_at, updated_at) 
VALUES ('Internet bill', 75.00, '2025-12-15', 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO expenses (description, amount, date, category_id, created_at, updated_at) 
VALUES ('Gym membership', 45.00, '2025-12-01', 9, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO expenses (description, amount, date, category_id, created_at, updated_at) 
VALUES ('Movie tickets', 32.00, '2025-12-28', 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
