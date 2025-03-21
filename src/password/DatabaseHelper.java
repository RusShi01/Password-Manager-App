package password;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseHelper {
    // Update these details as per your MySQL configuration
    private static final String URL = "jdbc:mysql://localhost:3306/PasswordManager"; // Change 'your_database' to your actual DB name
    private static final String USER = "root"; // Change to your MySQL username
    private static final String PASSWORD = "Rushi@123"; // Change to your MySQL password

    public static Connection connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // Load MySQL JDBC Driver
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connected to MySQL successfully!");
            return conn;
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("MySQL Connection Failed!");
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        connect(); // Test the MySQL connection
    }
}
