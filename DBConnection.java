import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/** Central MySQL connection factory; config.properties is optional. */
public final class DBConnection {
    private static final String DEF_URL = "jdbc:mysql://localhost:3306/fleetwise?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String URL, USER, PASSWORD;
    static {
        Properties p = new Properties();
        try (InputStream in = new FileInputStream("config.properties")) { p.load(in); }
        catch (Exception ignored) { /* use the established local development defaults */ }
        URL = p.getProperty("db.url", DEF_URL).trim();
        USER = p.getProperty("db.username", "root").trim();
        PASSWORD = p.getProperty("db.password", "tarun");
    }
    private DBConnection() {}
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL Connector/J is not on the classpath", e);
        } catch (SQLException e) {
            throw e;
        }
    }
}
