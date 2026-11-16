public class FleetHubDuplicateCheckTemp {
  public static void main(String[] args) throws Exception {
    try (java.sql.Connection c = DBConnection.getConnection(); java.sql.Statement s = c.createStatement()) {
      try (java.sql.ResultSet r = s.executeQuery("SELECT v.vehicle_id,v.model_name,v.category,v.daily_rate,v.status,v.fuel_type,COUNT(r.rental_id) rental_count FROM vehicles v LEFT JOIN rentals r ON r.vehicle_id=v.vehicle_id GROUP BY v.vehicle_id,v.model_name,v.category,v.daily_rate,v.status,v.fuel_type ORDER BY v.vehicle_id")) {
        while (r.next()) System.out.printf("%d\t%s\t%s\t%.2f\t%s\t%s\t%d%n",r.getInt(1),r.getString(2),r.getString(3),r.getDouble(4),r.getString(5),r.getString(6),r.getInt(7));
      }
    }
  }
}
