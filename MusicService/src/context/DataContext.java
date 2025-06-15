package context;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataContext {  
  static {
    try {
      Class.forName("org.postgresql.Driver");
    } catch (ClassNotFoundException ex) {
      ex.printStackTrace();
    }
  }
  
  public static Connection getConnection() throws SQLException {
    return DriverManager.getConnection(
      DatabaseConfig.getDBUrl(),
      DatabaseConfig.getDBUsername(),
      DatabaseConfig.getDBPassword()
    );
  }
}
