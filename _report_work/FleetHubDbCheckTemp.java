public class FleetHubDbCheckTemp {
  public static void main(String[] args) throws Exception {
    try (java.sql.Connection c = DBConnection.getConnection();
         java.sql.Statement s = c.createStatement();
         java.sql.ResultSet r = s.executeQuery("SELECT DATABASE(), COUNT(*) FROM vehicles")) {
      if (r.next()) System.out.println("Database=" + r.getString(1) + ", vehicles=" + r.getInt(2));
    }
  }
}
