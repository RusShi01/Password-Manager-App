package password;

import java.sql.*;
import java.util.Scanner;

public class UserAuth {
    private static final String URL = "jdbc:mysql://localhost:3306/PasswordManager";
    private static final String USER = "root";
    private static final String PASSWORD = "Rushi@123";

    public static boolean login(Scanner scanner) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("SELECT password FROM users WHERE username = ?")) {

            System.out.print("👤 Enter Username: ");
            String username = scanner.nextLine();
            System.out.print("🔑 Enter Password: ");
            String password = scanner.nextLine();

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String encryptedPassword = rs.getString("password");
                String decryptedPassword = EncryptionUtil.decrypt(encryptedPassword);

                if (decryptedPassword.equals(password)) {
                    System.out.println("✅ Login Successful!");
                    return true;
                } else {
                    System.out.println("❌ Incorrect Password!");
                }
            } else {
                System.out.println("❌ User not found!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void register(Scanner scanner) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO users (username, password) VALUES (?, ?)")) {

            System.out.print("👤 Create Username: ");
            String username = scanner.nextLine();
            System.out.print("🔑 Create Password: ");
            String password = scanner.nextLine();

            String encryptedPassword = EncryptionUtil.encrypt(password);
            stmt.setString(1, username);
            stmt.setString(2, encryptedPassword);
            stmt.executeUpdate();

            System.out.println("✅ Registration Successful!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
