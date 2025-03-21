package password;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class PasswordManager {
    private static final String URL = "jdbc:mysql://localhost:3306/PasswordManager";
    private static final String USER = "root";
    private static final String PASSWORD = "Rushi@123";

    private JFrame frame;
    private JTextField siteField, userField;
    private JPasswordField passField;
    private JTextArea displayArea;

    public PasswordManager() {
        frame = new JFrame("Password Manager");
        frame.setSize(400, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(6, 2));

        JLabel siteLabel = new JLabel("Website:");
        siteField = new JTextField();
        JLabel userLabel = new JLabel("Username:");
        userField = new JTextField();
        JLabel passLabel = new JLabel("Password:");
        passField = new JPasswordField();
        
        JButton addButton = new JButton("Add Password");
        JButton retrieveButton = new JButton("Retrieve Password");
        JButton deleteButton = new JButton("Delete Password");
        JButton listButton = new JButton("List All");
        
        displayArea = new JTextArea();
        displayArea.setEditable(false);

        frame.add(siteLabel); frame.add(siteField);
        frame.add(userLabel); frame.add(userField);
        frame.add(passLabel); frame.add(passField);
        frame.add(addButton); frame.add(retrieveButton);
        frame.add(deleteButton); frame.add(listButton);
        frame.add(new JScrollPane(displayArea));
        
        addButton.addActionListener(e -> addPassword());
        retrieveButton.addActionListener(e -> retrievePassword());
        deleteButton.addActionListener(e -> deletePassword());
        listButton.addActionListener(e -> listPasswords());
        
        frame.setVisible(true);
    }

    private void addPassword() {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO credentials (site_name, username, password) VALUES (?, ?, ?)")
        ) {
            String site = siteField.getText().trim();
            String username = userField.getText().trim();
            String password = new String(passField.getPassword()).trim();

            if (site.isEmpty() || username.isEmpty() || password.isEmpty()) {
                displayArea.setText("⚠️ Please fill in all fields.");
                return;
            }

            String encryptedPassword = EncryptionUtil.encrypt(password);
            stmt.setString(1, site);
            stmt.setString(2, username);
            stmt.setString(3, encryptedPassword);

            int rowsInserted = stmt.executeUpdate();

            if (rowsInserted > 0) {
                displayArea.setText("✅ Password added successfully!");
            } else {
                displayArea.setText("❌ Insertion failed.");
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Print detailed SQL errors
            displayArea.setText("Database Error: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            displayArea.setText("Error: " + e.getMessage());
        }
    }


    private void retrievePassword() {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("SELECT username, password FROM credentials WHERE username = ? AND site_name = ?")
        ) {
            // Set both username and site name
            stmt.setString(1, userField.getText());  // Using userField instead of usernameField
            stmt.setString(2, siteField.getText());  // Using siteField as is
            ResultSet rs = stmt.executeQuery();
            
            StringBuilder result = new StringBuilder("🔑 Retrieved Credentials:\n");
            boolean found = false;

            while (rs.next()) { // Iterate through the results
                found = true;
                String decryptedPassword = EncryptionUtil.decrypt(rs.getString("password"));
                result.append("Username: ").append(rs.getString("username"))
                      .append("\nPassword: ").append(decryptedPassword)
                      .append("\n----------------------\n");
            }

            if (!found) {
                displayArea.setText("❌ No password found for the given username and website.");
            } else {
                displayArea.setText(result.toString());
            }

        } catch (Exception e) {
            displayArea.setText("Error: " + e.getMessage());
        }
    }

    private void deletePassword() {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM credentials WHERE username = ? AND site_name = ?")
        ) {
            // Set both username and site name for deletion
            stmt.setString(1, userField.getText());  // Using userField instead of usernameField
            stmt.setString(2, siteField.getText());  // Using siteField as is
            int rowsAffected = stmt.executeUpdate();
            displayArea.setText(rowsAffected > 0 ? "🗑️ Password deleted successfully!" : "❌ No password found for the given username and website.");
        } catch (Exception e) {
            displayArea.setText("Error: " + e.getMessage());
        }
    }


    private void listPasswords() {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("SELECT site_name, username FROM credentials");
             ResultSet rs = stmt.executeQuery()
        ) {
            StringBuilder result = new StringBuilder("📋 Stored Passwords:\n");
            while (rs.next()) {
                result.append("Website: ").append(rs.getString("site_name"))
                      .append(" | Username: ").append(rs.getString("username"))
                      .append("\n");
            }
            displayArea.setText(result.toString());
        } catch (Exception e) {
            displayArea.setText("Error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(PasswordManager::new);
    }
}
