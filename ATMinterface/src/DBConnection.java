import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/atm_db";
    private static final String USER = "root"; // use your MySQL username
    private static final String PASSWORD = "Jayesh@12345"; // use your MySQL password

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // load the MySQL driver
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Optional: Test the connection
    public static void main(String[] args) {
        Connection conn = getConnection();
        if (conn != null) {
            System.out.println("✅ Connected to ATM Database!");
        } else {
            System.out.println("❌ Connection failed.");
        }
    }
}
