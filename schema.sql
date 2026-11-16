CREATE DATABASE IF NOT EXISTS fleetwise;
USE fleetwise;

CREATE TABLE IF NOT EXISTS vehicles (
    vehicle_id INT PRIMARY KEY AUTO_INCREMENT,
    model_name VARCHAR(100) NOT NULL,
    category VARCHAR(50) NOT NULL,
    daily_rate DECIMAL(15, 2) NOT NULL,
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

-- Seed rows are inserted only when absent, so this file can be run repeatedly.
INSERT INTO vehicles (model_name, category, daily_rate, status, fuel_type)
SELECT 'Toyota Camry', 'Sedan', 50.00, 'Available', 'Hybrid' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM vehicles WHERE model_name = 'Toyota Camry');
INSERT INTO vehicles (model_name, category, daily_rate, status, fuel_type)
SELECT 'Honda CR-V', 'SUV', 70.00, 'Available', 'Gasoline' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM vehicles WHERE model_name = 'Honda CR-V');
INSERT INTO vehicles (model_name, category, daily_rate, status, fuel_type)
SELECT 'Tesla Model 3', 'EV', 90.00, 'Available', 'Electric' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM vehicles WHERE model_name = 'Tesla Model 3');
INSERT INTO vehicles (model_name, category, daily_rate, status, fuel_type)
SELECT 'Ford Explorer', 'SUV', 85.00, 'Rented', 'Gasoline' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM vehicles WHERE model_name = 'Ford Explorer');
INSERT INTO vehicles (model_name, category, daily_rate, status, fuel_type)
SELECT 'Nissan Altima', 'Sedan', 45.00, 'Available', 'Gasoline' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM vehicles WHERE model_name = 'Nissan Altima');

INSERT IGNORE INTO customers (full_name, phone_number, license_number) VALUES
('Alice Smith', '555-0101', 'LIC12345'),
('Bob Johnson', '555-0102', 'LIC67890'),
('Charlie Brown', '555-0103', 'LIC11223');

-- Link by stable values rather than assuming auto-increment IDs are 1 and 4.
INSERT INTO rentals (vehicle_id, customer_id, rental_days, total_cost, status)
SELECT v.vehicle_id, c.customer_id, 3, 255.00, 'Active'
FROM vehicles v
JOIN customers c ON c.license_number = 'LIC12345'
WHERE v.model_name = 'Ford Explorer'
  AND NOT EXISTS (
      SELECT 1 FROM rentals r
      WHERE r.vehicle_id = v.vehicle_id
        AND r.customer_id = c.customer_id
        AND r.status = 'Active'
  );
