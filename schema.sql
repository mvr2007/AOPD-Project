CREATE DATABASE IF NOT EXISTS fleetwise;
USE fleetwise;

CREATE TABLE IF NOT EXISTS vehicles (
    vehicle_id INT PRIMARY KEY AUTO_INCREMENT,
    model_name VARCHAR(100) NOT NULL,
    category VARCHAR(50) NOT NULL,
    daily_rate DECIMAL(10, 2) NOT NULL,
    status VARCHAR(20) NOT NULL, -- Available, Rented, Maintenance
    fuel_type VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS customers (
    customer_id INT PRIMARY KEY AUTO_INCREMENT,
    full_name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    license_number VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS rentals (
    rental_id INT PRIMARY KEY AUTO_INCREMENT,
    vehicle_id INT NOT NULL,
    customer_id INT NOT NULL,
    rental_days INT NOT NULL,
    total_cost DECIMAL(10, 2) NOT NULL,
    rental_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) NOT NULL, -- Active, Completed
    FOREIGN KEY (vehicle_id) REFERENCES vehicles(vehicle_id),
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id)
);

INSERT INTO vehicles (model_name, category, daily_rate, status, fuel_type) VALUES
('Toyota Camry', 'Sedan', 50.00, 'Available', 'Hybrid'),
('Honda CR-V', 'SUV', 70.00, 'Available', 'Gasoline'),
('Tesla Model 3', 'EV', 90.00, 'Available', 'Electric'),
('Ford Explorer', 'SUV', 85.00, 'Rented', 'Gasoline'),
('Nissan Altima', 'Sedan', 45.00, 'Available', 'Gasoline');

INSERT INTO customers (full_name, phone_number, license_number) VALUES
('Alice Smith', '555-0101', 'LIC12345'),
('Bob Johnson', '555-0102', 'LIC67890'),
('Charlie Brown', '555-0103', 'LIC11223');

INSERT INTO rentals (vehicle_id, customer_id, rental_days, total_cost, status) VALUES
(4, 1, 3, 255.00, 'Active');
