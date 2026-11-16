import os
from PIL import Image, ImageDraw, ImageFont
import pygments
from pygments.lexers import SqlLexer
from pygments.token import Token

def create_code_screenshot(code_text, filename, title="query.sql"):
    # Window dimensions and padding
    font_size = 18
    line_height = 28
    char_width = 11  # approximate for consolas
    
    lines = code_text.strip().split('\n')
    max_len = max(len(line) for line in lines)
    
    pad_x = 35
    pad_y = 25
    header_height = 42
    
    width = max(900, max_len * char_width + pad_x * 2 + 60)
    height = len(lines) * line_height + pad_y * 2 + header_height
    
    # Create image with high-res dark editor palette
    bg_color = (24, 26, 32)        # Deep obsidian
    header_color = (32, 35, 43)    # Slightly lighter header
    border_color = (48, 52, 64)    # Subtle border
    
    img = Image.new("RGBA", (width, height), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    
    # Rounded container
    radius = 16
    draw.rounded_rectangle([0, 0, width - 1, height - 1], radius=radius, fill=bg_color, outline=border_color, width=2)
    draw.rounded_rectangle([0, 0, width - 1, header_height], radius=radius, fill=header_color)
    # Square bottom of header
    draw.rectangle([0, header_height - radius, width - 1, header_height], fill=header_color)
    draw.line([(0, header_height), (width - 1, header_height)], fill=border_color, width=1)
    
    # macOS style buttons
    btn_y = header_height // 2
    draw.ellipse([18, btn_y - 6, 30, btn_y + 6], fill=(255, 95, 86))   # Red
    draw.ellipse([38, btn_y - 6, 50, btn_y + 6], fill=(255, 189, 46))  # Yellow
    draw.ellipse([58, btn_y - 6, 70, btn_y + 6], fill=(39, 201, 63))   # Green
    
    # Title font & Text font
    try:
        font_mono = ImageFont.truetype("C:/Windows/Fonts/consola.ttf", font_size)
        font_header = ImageFont.truetype("C:/Windows/Fonts/segoeui.ttf", 15)
        font_linenr = ImageFont.truetype("C:/Windows/Fonts/consola.ttf", 15)
    except:
        font_mono = ImageFont.load_default()
        font_header = ImageFont.load_default()
        font_linenr = ImageFont.load_default()
        
    # Draw header title
    draw.text((width // 2, btn_y), title, fill=(160, 168, 184), font=font_header, anchor="mm")
    
    # Token color mapping
    token_colors = {
        Token.Keyword: (200, 120, 240),        # Purple
        Token.Keyword.Type: (100, 180, 246),   # Cyan/Blue
        Token.Name.Builtin: (240, 150, 100),   # Orange
        Token.String: (160, 220, 120),         # Green
        Token.Number: (255, 180, 84),          # Orange-Yellow
        Token.Comment: (108, 117, 137),        # Muted gray
        Token.Comment.Single: (108, 117, 137),
        Token.Operator: (130, 200, 240),       # Light Blue
        Token.Punctuation: (180, 185, 200),    # Off white
        Token.Name: (220, 225, 235),           # White
        Token.Text: (220, 225, 235),
    }
    
    lexer = SqlLexer()
    tokens = list(pygments.lex(code_text, lexer))
    
    # Render lines with line numbers & tokens
    curr_line = 1
    curr_x = pad_x + 45
    curr_y = header_height + pad_y
    
    # Draw first line number
    draw.text((pad_x + 25, curr_y), f"{curr_line:2d}", fill=(80, 88, 108), font=font_linenr, anchor="ra")
    
    for ttype, value in tokens:
        color = token_colors.get(ttype, (220, 225, 235))
        # Fallback for parent tokens
        if color == (220, 225, 235):
            for parent, col in token_colors.items():
                if ttype in parent:
                    color = col
                    break
        
        parts = value.split('\n')
        for idx, part in enumerate(parts):
            if idx > 0:
                curr_line += 1
                curr_y += line_height
                curr_x = pad_x + 45
                if curr_line <= len(lines):
                    draw.text((pad_x + 25, curr_y), f"{curr_line:2d}", fill=(80, 88, 108), font=font_linenr, anchor="ra")
            
            if part:
                draw.text((curr_x, curr_y), part, fill=color, font=font_mono)
                # Advance X
                bbox = font_mono.getbbox(part)
                curr_x += (bbox[2] - bbox[0])
                
    img.save(filename, "PNG")
    print(f"Generated {filename}")

# 1. DDL Schema
sql_ddl = """-- 1. Database & Core Relational Schema
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
);"""

# 2. DML Seeding & Inventory Queries
sql_dml = """-- 2. DML Seeding & Filtered Inventory Queries
INSERT INTO vehicles (model_name, category, daily_rate, status, fuel_type) VALUES
('Toyota Camry', 'Sedan', 50.00, 'Available', 'Hybrid'),
('Honda CR-V', 'SUV', 70.00, 'Available', 'Gasoline'),
('Tesla Model 3', 'EV', 90.00, 'Available', 'Electric'),
('Ford Explorer', 'SUV', 85.00, 'Rented', 'Gasoline');

INSERT INTO customers (full_name, phone_number, license_number) VALUES
('Alice Smith', '555-0101', 'LIC12345'),
('Bob Johnson', '555-0102', 'LIC67890');

-- Real-time Application Queries for Live GUI Tables
SELECT * FROM vehicles WHERE status = 'Available';

SELECT rental_id, vehicle_id, customer_id, rental_days, total_cost, rental_date 
FROM rentals 
WHERE status = 'Active';"""

# 3. Transactions & Analytics
sql_trans = """-- 3. Atomic Rental Transactions & Revenue Analytics
-- Step A: Atomic Vehicle Rental Transaction
START TRANSACTION;
INSERT INTO rentals (vehicle_id, customer_id, rental_days, total_cost, status)
VALUES (?, ?, ?, ?, 'Active');

UPDATE vehicles 
SET status = 'Rented' 
WHERE vehicle_id = ?;
COMMIT;

-- Step B: Vehicle Return & State Synchronization
START TRANSACTION;
UPDATE rentals SET status = 'Completed' WHERE rental_id = ?;
UPDATE vehicles SET status = 'Available' WHERE vehicle_id = ?;
COMMIT;

-- Step C: Analytical Revenue Aggregation
SELECT SUM(total_cost) AS total_revenue 
FROM rentals 
WHERE status = 'Completed';"""

create_code_screenshot(sql_ddl, "sql_ddl_screenshot.png", "01_schema_ddl.sql")
create_code_screenshot(sql_dml, "sql_dml_screenshot.png", "02_seeding_and_queries.sql")
create_code_screenshot(sql_trans, "sql_trans_screenshot.png", "03_transactions_and_analytics.sql")
