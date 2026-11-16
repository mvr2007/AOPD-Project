import java.sql.*;
import javax.swing.table.DefaultTableModel;
import java.util.*;
import java.io.*;
import java.security.MessageDigest;

public class FleetDAO {

    private String lastError = "";

    public String getLastError() { return lastError; }

    // ------------------------------------------------------------------ //
    //  Utility
    // ------------------------------------------------------------------ //

    private String sha256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(input.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            return null;
        }
    }

    // ------------------------------------------------------------------ //
    //  Auth & Audit
    // ------------------------------------------------------------------ //

    public String authenticateUser(String username, String password) {
        String hash = sha256(password);
        if (hash == null) return null;
        String sql = "SELECT role FROM users WHERE username=? AND password_hash=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, hash);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("role");
        } catch (SQLException e) {
            lastError = e.getMessage();
        }
        return null;
    }

    public void logActivity(String username, String role, String actionType, String details) {
        String sql = "INSERT INTO audit_logs(username, role, action_type, details) VALUES(?,?,?,?)";
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            if (conn == null) { lastError = "Database unavailable"; return; }
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, role);
            ps.setString(3, actionType);
            ps.setString(4, details);
            ps.executeUpdate();
            }
            conn.commit();
        } catch (SQLException e) {
            lastError = e.getMessage();
            if (conn != null) try { conn.rollback(); } catch (SQLException ignored) {}
        } finally { if (conn != null) try { conn.close(); } catch (SQLException ignored) {} }
    }

    public DefaultTableModel getRecentLogs() {
        String[] cols = {"ID", "User", "Role", "Action", "Details", "Timestamp"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        String sql = "SELECT log_id, username, role, action_type, details, created_at " +
                     "FROM audit_logs ORDER BY created_at DESC LIMIT 100";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("log_id"), rs.getString("username"), rs.getString("role"),
                    rs.getString("action_type"), rs.getString("details"), rs.getTimestamp("created_at")
                });
            }
        } catch (SQLException e) {
            lastError = e.getMessage();
        }
        return model;
    }

    // ------------------------------------------------------------------ //
    //  Vehicles
    // ------------------------------------------------------------------ //

    private DefaultTableModel buildVehicleModel(ResultSet rs) throws SQLException {
        String[] cols = {"ID", "Model", "Category", "Rate/Day", "Status", "Fuel",
                         "Odometer", "Svc Interval", "Last Svc Odo"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        while (rs.next()) {
            model.addRow(new Object[]{
                rs.getInt("vehicle_id"), rs.getString("model_name"), rs.getString("category"),
                rs.getDouble("daily_rate"), rs.getString("status"), rs.getString("fuel_type"),
                rs.getInt("odometer_km"), rs.getInt("service_interval_km"), rs.getInt("last_service_odometer")
            });
        }
        return model;
    }

    public DefaultTableModel getAllVehicles() {
        String sql = "SELECT vehicle_id, model_name, category, daily_rate, status, fuel_type, " +
                     "odometer_km, service_interval_km, last_service_odometer FROM vehicles";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return buildVehicleModel(rs);
        } catch (SQLException e) {
            lastError = e.getMessage();
            return new DefaultTableModel();
        }
    }

    public DefaultTableModel getAvailableVehicles() {
        String sql = "SELECT vehicle_id, model_name, category, daily_rate, status, fuel_type, " +
                     "odometer_km, service_interval_km, last_service_odometer FROM vehicles WHERE status='Available'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return buildVehicleModel(rs);
        } catch (SQLException e) {
            lastError = e.getMessage();
            return new DefaultTableModel();
        }
    }

    public boolean addVehicle(String modelName, String category, double dailyRate, String status, String fuelType) {
        String sql = "INSERT INTO vehicles(model_name, category, daily_rate, status, fuel_type) VALUES(?,?,?,?,?)";
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            if (conn == null) { lastError = "Database unavailable"; return false; }
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, modelName);
            ps.setString(2, category);
            ps.setDouble(3, dailyRate);
            ps.setString(4, status);
            ps.setString(5, fuelType);
            if (ps.executeUpdate() != 1) throw new SQLException("Vehicle was not inserted");
            }
            conn.commit(); return true;
        } catch (SQLException e) {
            lastError = e.getMessage();
            if (conn != null) try { conn.rollback(); } catch (SQLException ignored) {}
            return false;
        } finally { if (conn != null) try { conn.close(); } catch (SQLException ignored) {} }
    }

    public boolean updateVehicleStatus(int vehicleId, String status) {
        if (!Arrays.asList("Available", "Rented", "Maintenance", "Service Due").contains(status)) {
            lastError = "Unsupported vehicle status"; return false;
        }
        String sql = "UPDATE vehicles SET status=? WHERE vehicle_id=? AND status<>'Rented'";
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            if (conn == null) { lastError = "Database unavailable"; return false; }
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, vehicleId);
            if (ps.executeUpdate() != 1) throw new SQLException("Vehicle was not updated");
            }
            conn.commit(); return true;
        } catch (SQLException e) {
            lastError = e.getMessage();
            if (conn != null) try { conn.rollback(); } catch (SQLException ignored) {}
            return false;
        } finally { if (conn != null) try { conn.close(); } catch (SQLException ignored) {} }
    }

    public boolean updateVehicleDailyRate(int vehicleId, double newRate) {
        if (newRate <= 0 || Double.isNaN(newRate) || Double.isInfinite(newRate)) { lastError = "Daily rate must be positive"; return false; }
        String sql = "UPDATE vehicles SET daily_rate=? WHERE vehicle_id=?";
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            if (conn == null) { lastError = "Database unavailable"; return false; }
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, newRate);
            ps.setInt(2, vehicleId);
            if (ps.executeUpdate() != 1) throw new SQLException("Vehicle rate was not updated");
            }
            conn.commit(); return true;
        } catch (SQLException e) {
            lastError = e.getMessage();
            if (conn != null) try { conn.rollback(); } catch (SQLException ignored) {}
            return false;
        } finally { if (conn != null) try { conn.close(); } catch (SQLException ignored) {} }
    }

    public boolean recordVehicleService(int vehicleId) {
        String sql = "UPDATE vehicles SET last_service_odometer=odometer_km, status='Available' WHERE vehicle_id=? AND status<>'Rented'";
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            if (conn == null) { lastError = "Database unavailable"; return false; }
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, vehicleId);
            if (ps.executeUpdate() != 1) throw new SQLException("Service record was not updated");
            }
            conn.commit(); return true;
        } catch (SQLException e) {
            lastError = e.getMessage();
            if (conn != null) try { conn.rollback(); } catch (SQLException ignored) {}
            return false;
        } finally { if (conn != null) try { conn.close(); } catch (SQLException ignored) {} }
    }

    // ------------------------------------------------------------------ //
    //  Customers
    // ------------------------------------------------------------------ //

    public DefaultTableModel getAllCustomers() {
        String[] cols = {"ID", "Name", "Phone", "License"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        String sql = "SELECT customer_id, full_name, phone_number, license_number FROM customers";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("customer_id"), rs.getString("full_name"),
                    rs.getString("phone_number"), rs.getString("license_number")
                });
            }
        } catch (SQLException e) {
            lastError = e.getMessage();
        }
        return model;
    }

    public boolean addCustomer(String fullName, String phone, String license) {
        String sql = "INSERT INTO customers(full_name, phone_number, license_number) VALUES(?,?,?)";
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            if (conn == null) { lastError = "Database unavailable"; return false; }
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, fullName);
            ps.setString(2, phone);
            ps.setString(3, license);
            if (ps.executeUpdate() != 1) throw new SQLException("Customer was not inserted");
            }
            conn.commit(); return true;
        } catch (SQLException e) {
            lastError = e.getMessage();
            if (conn != null) try { conn.rollback(); } catch (SQLException ignored) {}
            return false;
        } finally { if (conn != null) try { conn.close(); } catch (SQLException ignored) {} }
    }

    // ------------------------------------------------------------------ //
    //  Rentals
    // ------------------------------------------------------------------ //

    public DefaultTableModel getCustomerRentalHistory(int customerId) {
        String[] cols = {"Rental ID", "Vehicle", "Days", "Total", "Late Fee", "Status", "Date"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        String sql = "SELECT r.rental_id, v.model_name, r.rental_days, r.total_cost, r.late_fee, " +
                     "r.status, r.rental_date FROM rentals r " +
                     "JOIN vehicles v ON r.vehicle_id=v.vehicle_id " +
                     "WHERE r.customer_id=? ORDER BY r.rental_date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("rental_id"), rs.getString("model_name"), rs.getInt("rental_days"),
                    rs.getDouble("total_cost"), rs.getDouble("late_fee"),
                    rs.getString("status"), rs.getTimestamp("rental_date")
                });
            }
        } catch (SQLException e) {
            lastError = e.getMessage();
        }
        return model;
    }

    public DefaultTableModel getActiveRentals() {
        String[] cols = {"Rental ID", "Vehicle", "Customer", "Days", "Total",
                         "Start Date", "Expected Return", "Start Odo", "Start Fuel (%)", "Fuel Surcharge", "Status"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        String sql = "SELECT r.rental_id, v.model_name, c.full_name, r.rental_days, r.total_cost, " +
                     "r.rental_date, r.expected_return_date, r.start_odometer, r.start_fuel_pct, r.fuel_surcharge, " +
                     "CASE WHEN r.expected_return_date IS NOT NULL AND NOW()>r.expected_return_date THEN 'OVERDUE' ELSE 'Active' END AS display_status " +
                     "FROM rentals r " +
                     "JOIN vehicles v ON r.vehicle_id=v.vehicle_id " +
                     "JOIN customers c ON r.customer_id=c.customer_id " +
                     "WHERE r.status='Active' ORDER BY r.rental_date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("rental_id"), rs.getString("model_name"), rs.getString("full_name"),
                    rs.getInt("rental_days"), rs.getDouble("total_cost"),
                    rs.getTimestamp("rental_date"), rs.getTimestamp("expected_return_date"),
                    rs.getInt("start_odometer"), rs.getInt("start_fuel_pct"),
                    rs.getDouble("fuel_surcharge"), rs.getString("display_status")
                });
            }
        } catch (SQLException e) {
            lastError = e.getMessage();
        }
        return model;
    }

    public boolean bookVehicle(int vehicleId, int customerId, int rentalDays, double totalCost,
                               double discountAmount, double taxAmount, int startOdometer,
                               java.util.Date expectedReturnDate) {
        String insertSql = "INSERT INTO rentals(vehicle_id, customer_id, rental_days, total_cost, " +
                           "discount_amount, tax_amount, start_odometer, start_fuel_pct, expected_return_date, status) " +
                           "SELECT ?,?,?,?,?,?,?,fuel_level_pct,?,'Active' FROM vehicles " +
                           "WHERE vehicle_id=? AND status='Available'";
        String updateSql = "UPDATE vehicles SET status='Rented' WHERE vehicle_id=? AND status='Available'";
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            if (conn == null) { lastError = "Database unavailable"; return false; }
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                ps.setInt(1, vehicleId);
                ps.setInt(2, customerId);
                ps.setInt(3, rentalDays);
                ps.setDouble(4, totalCost);
                ps.setDouble(5, discountAmount);
                ps.setDouble(6, taxAmount);
                ps.setInt(7, startOdometer);
                if (expectedReturnDate != null)
                    ps.setTimestamp(8, new java.sql.Timestamp(expectedReturnDate.getTime()));
                else
                    ps.setNull(8, java.sql.Types.TIMESTAMP);
                ps.setInt(9, vehicleId);
                if (ps.executeUpdate() != 1) throw new SQLException("Vehicle is no longer available");
            }
            try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                ps.setInt(1, vehicleId);
                if (ps.executeUpdate() != 1) throw new SQLException("Vehicle is no longer available");
            }
            conn.commit();
            return true;
        } catch (SQLException e) {
            lastError = e.getMessage();
            if (conn != null) try { conn.rollback(); } catch (SQLException ignored) {}
            return false;
        } finally {
            if (conn != null) try { conn.close(); } catch (SQLException ignored) {}
        }
    }

    /** Backward-compatible entry point retained for the original sample utility. */
    public boolean rentVehicle(int vehicleId, int customerId, int rentalDays, double totalCost) {
        java.util.Date expected = new java.util.Date(System.currentTimeMillis() + rentalDays * 86400000L);
        return bookVehicle(vehicleId, customerId, rentalDays, totalCost, 0.0, 0.0, 0, expected);
    }

    /** Backward-compatible overload; assumes a full fuel return. */
    public boolean returnVehicleWithDetails(int rentalId, int endOdometer) {
        return returnVehicleWithDetails(rentalId, endOdometer, 100);
    }

    public boolean dispatchRoadsideFuel(int rentalId, double fuelAndCalloutCost) {
        if (fuelAndCalloutCost < 0 || Double.isNaN(fuelAndCalloutCost) || Double.isInfinite(fuelAndCalloutCost)) {
            lastError = "Fuel and callout cost must be a valid non-negative amount";
            return false;
        }
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            if (conn == null) { lastError = "Database unavailable"; return false; }
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE rentals SET fuel_surcharge=fuel_surcharge+?, total_cost=total_cost+? " +
                    "WHERE rental_id=? AND status='Active'")) {
                ps.setBigDecimal(1, java.math.BigDecimal.valueOf(fuelAndCalloutCost));
                ps.setBigDecimal(2, java.math.BigDecimal.valueOf(fuelAndCalloutCost));
                ps.setInt(3, rentalId);
                if (ps.executeUpdate() != 1) throw new SQLException("Rental is no longer active");
            }
            conn.commit();
            return true;
        } catch (SQLException e) {
            lastError = e.getMessage();
            if (conn != null) try { conn.rollback(); } catch (SQLException ignored) {}
            return false;
        } finally { if (conn != null) try { conn.close(); } catch (SQLException ignored) {} }
    }

    public boolean refuelVehicleToFull(int vehicleId) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            if (conn == null) { lastError = "Database unavailable"; return false; }
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE vehicles SET fuel_level_pct=100, status='Available' " +
                    "WHERE vehicle_id=? AND status='Low Fuel'")) {
                ps.setInt(1, vehicleId);
                if (ps.executeUpdate() != 1) throw new SQLException("Vehicle is not marked Low Fuel");
            }
            conn.commit();
            return true;
        } catch (SQLException e) {
            lastError = e.getMessage();
            if (conn != null) try { conn.rollback(); } catch (SQLException ignored) {}
            return false;
        } finally { if (conn != null) try { conn.close(); } catch (SQLException ignored) {} }
    }

    public double getRentalFuelSurcharge(int rentalId) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT fuel_surcharge FROM rentals WHERE rental_id=?")) {
            ps.setInt(1, rentalId);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? rs.getDouble(1) : 0.0; }
        } catch (SQLException e) { lastError = e.getMessage(); return 0.0; }
    }

    public boolean returnVehicleWithDetails(int rentalId, int endOdometer, int endFuelPct) {
        if (endFuelPct < 0 || endFuelPct > 100) { lastError = "End fuel must be between 0 and 100"; return false; }
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            if (conn == null) { lastError = "Database unavailable"; return false; }
            conn.setAutoCommit(false);

            // Step 1: fetch rental + vehicle info
            String selectSql = "SELECT r.expected_return_date, r.start_fuel_pct, r.vehicle_id, v.daily_rate, " +
                               "v.last_service_odometer, v.service_interval_km " +
                               "FROM rentals r JOIN vehicles v ON r.vehicle_id=v.vehicle_id " +
                               "WHERE r.rental_id=?";
            Timestamp expectedReturn;
            int vehicleId;
            int startFuelPct;
            double dailyRate;
            int lastServiceOdometer, serviceIntervalKm;
            try (PreparedStatement ps = conn.prepareStatement(selectSql)) {
                ps.setInt(1, rentalId);
                ResultSet rs = ps.executeQuery();
                if (!rs.next()) { conn.rollback(); return false; }
                expectedReturn       = rs.getTimestamp("expected_return_date");
                startFuelPct         = rs.getInt("start_fuel_pct");
                vehicleId            = rs.getInt("vehicle_id");
                dailyRate            = rs.getDouble("daily_rate");
                lastServiceOdometer  = rs.getInt("last_service_odometer");
                serviceIntervalKm    = rs.getInt("service_interval_km");
            }

            // Step 2: calculate late fee
            long now = System.currentTimeMillis();
            double lateFee = 0.0;
            if (expectedReturn != null && now > expectedReturn.getTime()) {
                int overdueDays = (int) Math.ceil((now - expectedReturn.getTime()) / 86400000.0);
                lateFee = overdueDays * dailyRate * 1.5;
            }

            double fuelShortage = Math.max(0, startFuelPct - endFuelPct) * 25.0;

            // Step 3: update rental
            String updateRental = "UPDATE rentals SET status='Completed', end_odometer=?, end_fuel_pct=?, late_fee=?, " +
                                  "fuel_surcharge=fuel_surcharge+?, total_cost=total_cost+?+? " +
                                  "WHERE rental_id=? AND status='Active'";
            try (PreparedStatement ps = conn.prepareStatement(updateRental)) {
                ps.setInt(1, endOdometer);
                ps.setInt(2, endFuelPct);
                ps.setDouble(3, lateFee);
                ps.setDouble(4, fuelShortage);
                ps.setDouble(5, lateFee);
                ps.setDouble(6, fuelShortage);
                ps.setInt(7, rentalId);
                if (ps.executeUpdate() != 1) throw new SQLException("Rental is no longer active");
            }

            // Step 4 & 5: determine new vehicle status and update
            String newStatus = endFuelPct <= 15 ? "Low Fuel" :
                    ((endOdometer - lastServiceOdometer >= serviceIntervalKm) ? "Service Due" : "Available");
            String updateVehicle = "UPDATE vehicles SET status=?, odometer_km=?, fuel_level_pct=? WHERE vehicle_id=?";
            try (PreparedStatement ps = conn.prepareStatement(updateVehicle)) {
                ps.setString(1, newStatus);
                ps.setInt(2, endOdometer);
                ps.setInt(3, endFuelPct);
                ps.setInt(4, vehicleId);
                if (ps.executeUpdate() != 1) throw new SQLException("Vehicle record was not updated");
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            lastError = e.getMessage();
            if (conn != null) try { conn.rollback(); } catch (SQLException ignored) {}
            return false;
        } finally {
            if (conn != null) try { conn.close(); } catch (SQLException ignored) {}
        }
    }

    // ------------------------------------------------------------------ //
    //  Reports & Stats
    // ------------------------------------------------------------------ //

    public java.util.Map<String, Double> getRevenueByCategory() {
        Map<String, Double> map = new LinkedHashMap<>();
        String sql = "SELECT v.category, SUM(r.total_cost) AS revenue " +
                     "FROM rentals r JOIN vehicles v ON r.vehicle_id=v.vehicle_id " +
                     "WHERE r.status='Completed' GROUP BY v.category";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) map.put(rs.getString("category"), rs.getDouble("revenue"));
        } catch (SQLException e) {
            lastError = e.getMessage();
        }
        return map;
    }

    public boolean exportRentalsToCSV(java.io.File file) {
        String sql = "SELECT r.rental_id, v.model_name, c.full_name, r.rental_days, r.total_cost, " +
                     "r.late_fee, r.discount_amount, r.tax_amount, r.status, r.rental_date, " +
                     "r.expected_return_date, r.start_odometer, r.end_odometer " +
                     "FROM rentals r " +
                     "JOIN vehicles v ON r.vehicle_id=v.vehicle_id " +
                     "JOIN customers c ON r.customer_id=c.customer_id " +
                     "ORDER BY r.rental_date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery();
             PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            pw.println("Rental ID,Vehicle,Customer,Days,Total Cost,Late Fee,Discount,Tax," +
                       "Status,Rental Date,Expected Return,Start Odo,End Odo");
            while (rs.next()) {
                pw.printf("%d,%s,%s,%d,%.2f,%.2f,%.2f,%.2f,%s,%s,%s,%d,%d%n",
                    rs.getInt("rental_id"),
                    escapeCsv(rs.getString("model_name")),
                    escapeCsv(rs.getString("full_name")),
                    rs.getInt("rental_days"),
                    rs.getDouble("total_cost"),
                    rs.getDouble("late_fee"),
                    rs.getDouble("discount_amount"),
                    rs.getDouble("tax_amount"),
                    escapeCsv(rs.getString("status")),
                    rs.getTimestamp("rental_date"),
                    rs.getTimestamp("expected_return_date"),
                    rs.getInt("start_odometer"),
                    rs.getInt("end_odometer"));
            }
            return true;
        } catch (Exception e) {
            lastError = e.getMessage();
            return false;
        }
    }

    private String escapeCsv(String s) {
        if (s == null) return "";
        if (s.contains(",") || s.contains("\"") || s.contains("\n"))
            return "\"" + s.replace("\"", "\"\"") + "\"";
        return s;
    }

    public double calculateTotalRevenue() {
        String sql = "SELECT SUM(total_cost) FROM rentals WHERE status='Completed'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) {
            lastError = e.getMessage();
        }
        return 0.0;
    }

    public int getTotalFleetCount() {
        String sql = "SELECT COUNT(*) FROM vehicles";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { lastError = e.getMessage(); }
        return 0;
    }

    public int getAvailableCount() {
        String sql = "SELECT COUNT(*) FROM vehicles WHERE status='Available'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { lastError = e.getMessage(); }
        return 0;
    }

    public int getActiveRentalCount() {
        String sql = "SELECT COUNT(*) FROM rentals WHERE status='Active'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { lastError = e.getMessage(); }
        return 0;
    }
}
