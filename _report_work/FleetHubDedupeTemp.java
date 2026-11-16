import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
public class FleetHubDedupeTemp {
  private static final String DUP = "NOT EXISTS (SELECT 1 FROM rentals r WHERE r.vehicle_id=v.vehicle_id) AND EXISTS (SELECT 1 FROM vehicles k WHERE LOWER(TRIM(k.model_name))=LOWER(TRIM(v.model_name)) AND (EXISTS (SELECT 1 FROM rentals kr WHERE kr.vehicle_id=k.vehicle_id) OR k.vehicle_id<v.vehicle_id))";
  private static String csv(String value) { return "\"" + (value == null ? "" : value.replace("\"", "\"\"")) + "\""; }
  public static void main(String[] args) throws Exception {
    String backup = "_report_work/vehicle_duplicate_backup_2026-11-16.csv";
    String sql = "DELETE FROM vehicles WHERE vehicle_id IN (SELECT vehicle_id FROM (SELECT v.vehicle_id FROM vehicles v WHERE " + DUP + ") duplicate_ids)";
    try (Connection c=DBConnection.getConnection()) {
      c.setAutoCommit(false);
      try {
        int candidates;
        try (Statement s=c.createStatement(); ResultSet r=s.executeQuery("SELECT COUNT(*) FROM vehicles v WHERE " + DUP)) { r.next(); candidates=r.getInt(1); }
        if (candidates != 60) throw new SQLException("Expected 60 unreferenced duplicate rows, found " + candidates + "; no rows were deleted.");
        try (Statement s=c.createStatement(); ResultSet r=s.executeQuery("SELECT v.* FROM vehicles v WHERE " + DUP + " ORDER BY v.vehicle_id");
             BufferedWriter w=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(backup), StandardCharsets.UTF_8))) {
          ResultSetMetaData md=r.getMetaData();
          for(int i=1;i<=md.getColumnCount();i++){if(i>1)w.write(",");w.write(csv(md.getColumnLabel(i)));} w.newLine();
          while(r.next()){for(int i=1;i<=md.getColumnCount();i++){if(i>1)w.write(",");Object value=r.getObject(i);w.write(csv(value==null?"":value.toString()));}w.newLine();}
        }
        int deleted;
        try(Statement s=c.createStatement()){deleted=s.executeUpdate(sql);}
        if(deleted != candidates) throw new SQLException("Delete count did not match backup count; transaction rolled back.");
        c.commit();
        try(Statement s=c.createStatement();ResultSet r=s.executeQuery("SELECT COUNT(*),COUNT(DISTINCT LOWER(TRIM(model_name))) FROM vehicles")){r.next();System.out.println("Deleted="+deleted+", remaining="+r.getInt(1)+", distinctModels="+r.getInt(2)+", backup="+backup);}
      } catch(Exception e) { try{c.rollback();}catch(SQLException ignored){} throw e; }
    }
  }
}
