import java.security.MessageDigest;
import java.sql.*;

/** Idempotent, additive schema migration. Existing rows and tables are preserved. */
public class AlterDB {
    public static void main(String[] args) {
        try (Connection c=DBConnection.getConnection()) {
            if(c==null) throw new SQLException("No database connection; check config.properties and the MySQL driver.");
            addColumn(c,"rentals","expected_return_date","DATETIME NULL");
            addColumn(c,"rentals","late_fee","DECIMAL(15,2) NOT NULL DEFAULT 0.00");
            addColumn(c,"rentals","discount_amount","DECIMAL(15,2) NOT NULL DEFAULT 0.00");
            addColumn(c,"rentals","tax_amount","DECIMAL(15,2) NOT NULL DEFAULT 0.00");
            addColumn(c,"rentals","start_odometer","INT NOT NULL DEFAULT 0");
            addColumn(c,"rentals","end_odometer","INT NOT NULL DEFAULT 0");
            addColumn(c,"rentals","start_fuel_pct","INT NOT NULL DEFAULT 100");
            addColumn(c,"rentals","end_fuel_pct","INT NOT NULL DEFAULT 100");
            addColumn(c,"rentals","fuel_surcharge","DECIMAL(15,2) NOT NULL DEFAULT 0.00");
            addColumn(c,"vehicles","odometer_km","INT NOT NULL DEFAULT 0");
            addColumn(c,"vehicles","fuel_level_pct","INT NOT NULL DEFAULT 100");
            addColumn(c,"vehicles","service_interval_km","INT NOT NULL DEFAULT 10000");
            addColumn(c,"vehicles","last_service_odometer","INT NOT NULL DEFAULT 0");
            try(Statement s=c.createStatement()){s.execute("ALTER TABLE vehicles MODIFY COLUMN status VARCHAR(30) NOT NULL");}
            try(Statement s=c.createStatement()){
                s.execute("CREATE TABLE IF NOT EXISTS users (user_id INT AUTO_INCREMENT PRIMARY KEY, username VARCHAR(50) UNIQUE NOT NULL, password_hash VARCHAR(64) NOT NULL, role ENUM('ADMIN','STAFF') NOT NULL)");
                s.execute("CREATE TABLE IF NOT EXISTS audit_logs (log_id INT AUTO_INCREMENT PRIMARY KEY, username VARCHAR(50), role VARCHAR(20), action_type VARCHAR(50), details VARCHAR(255), created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");
            }
            seed(c,"admin","admin123","ADMIN"); seed(c,"staff","staff123","STAFF");
            System.out.println("FleetHub schema migration complete.");
        } catch(Exception e){System.err.println("Migration failed: "+e.getMessage());}
    }
    private static void addColumn(Connection c,String table,String column,String definition)throws SQLException{
        DatabaseMetaData md=c.getMetaData();try(ResultSet rs=md.getColumns(c.getCatalog(),null,table,column)){if(rs.next())return;}
        try(Statement s=c.createStatement()){s.execute("ALTER TABLE `"+table+"` ADD COLUMN `"+column+"` "+definition);}
    }
    private static void seed(Connection c,String user,String password,String role)throws Exception{
        String sql="INSERT IGNORE INTO users(username,password_hash,role) VALUES(?,?,?)";
        try(PreparedStatement ps=c.prepareStatement(sql)){ps.setString(1,user);ps.setString(2,sha256(password));ps.setString(3,role);ps.executeUpdate();}
    }
    private static String sha256(String value)throws Exception{
        byte[] b=MessageDigest.getInstance("SHA-256").digest(value.getBytes("UTF-8"));StringBuilder out=new StringBuilder();for(byte x:b)out.append(String.format("%02x",x&255));return out.toString();
    }
}
