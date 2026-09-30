import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class FleetDAO {

    public DefaultTableModel getAvailableVehicles() {
        DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Model", "Category", "Rate/Day", "Status", "Fuel"}, 0);
        String query = "SELECT * FROM vehicles WHERE status = 'Available'";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("vehicle_id"),
                        rs.getString("model_name"),
                        rs.getString("category"),
                        rs.getDouble("daily_rate"),
                        rs.getString("status"),
                        rs.getString("fuel_type")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return model;
    }
    
    public DefaultTableModel getAllVehicles() {
        DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Model", "Category", "Rate/Day", "Status", "Fuel"}, 0);
        String query = "SELECT * FROM vehicles";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("vehicle_id"),
                        rs.getString("model_name"),
                        rs.getString("category"),
                        rs.getDouble("daily_rate"),
                        rs.getString("status"),
                        rs.getString("fuel_type")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return model;
    }

    public DefaultTableModel getActiveRentals() {
        DefaultTableModel model = new DefaultTableModel(new String[]{"Rental ID", "Vehicle ID", "Customer ID", "Days", "Total Cost", "Date"}, 0);
        String query = "SELECT * FROM rentals WHERE status = 'Active'";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("rental_id"),
                        rs.getInt("vehicle_id"),
                        rs.getInt("customer_id"),
                        rs.getInt("rental_days"),
                        rs.getDouble("total_cost"),
                        rs.getTimestamp("rental_date")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return model;
    }

    public boolean addVehicle(String modelName, String category, double dailyRate, String status, String fuelType) {
        String query = "INSERT INTO vehicles (model_name, category, daily_rate, status, fuel_type) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, modelName);
            pstmt.setString(2, category);
            pstmt.setDouble(3, dailyRate);
            pstmt.setString(4, status);
            pstmt.setString(5, fuelType);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean rentVehicle(int vehicleId, int customerId, int rentalDays, double totalCost) {
        Connection conn = DBConnection.getConnection();
        if (conn == null) return false;

        String insertRental = "INSERT INTO rentals (vehicle_id, customer_id, rental_days, total_cost, status) VALUES (?, ?, ?, ?, 'Active')";
        String updateVehicle = "UPDATE vehicles SET status = 'Rented' WHERE vehicle_id = ?";

        try {
            conn.setAutoCommit(false);
            
            try (PreparedStatement pstmtRental = conn.prepareStatement(insertRental);
                 PreparedStatement pstmtVehicle = conn.prepareStatement(updateVehicle)) {
                
                pstmtRental.setInt(1, vehicleId);
                pstmtRental.setInt(2, customerId);
                pstmtRental.setInt(3, rentalDays);
                pstmtRental.setDouble(4, totalCost);
                pstmtRental.executeUpdate();

                pstmtVehicle.setInt(1, vehicleId);
                pstmtVehicle.executeUpdate();
                
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                return false;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean returnVehicle(int rentalId, int vehicleId) {
        Connection conn = DBConnection.getConnection();
        if (conn == null) return false;

        String updateRental = "UPDATE rentals SET status = 'Completed' WHERE rental_id = ?";
        String updateVehicle = "UPDATE vehicles SET status = 'Available' WHERE vehicle_id = ?";

        try {
            conn.setAutoCommit(false);
            
            try (PreparedStatement pstmtRental = conn.prepareStatement(updateRental);
                 PreparedStatement pstmtVehicle = conn.prepareStatement(updateVehicle)) {
                
                pstmtRental.setInt(1, rentalId);
                pstmtRental.executeUpdate();

                pstmtVehicle.setInt(1, vehicleId);
                pstmtVehicle.executeUpdate();
                
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                return false;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public double calculateTotalRevenue() {
        String query = "SELECT SUM(total_cost) FROM rentals WHERE status = 'Completed'";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }
}
