import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class InventoryApp {
    // JDBC Connection
    private static Connection connection;
    // Main application frame
    private static JFrame frame;
    // Username input field
    private static JTextField usernameField;
    // Password input field
    private static JPasswordField passwordField;
    // Text area to display inventory
    private static JTextArea inventoryTextArea;

    public static void main(String[] args) {
        // Database connection setup
        try {
            // Load the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Connect to the MySQL database
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/nu_tracker_inventory", // Database URL
                    "root", // Database username
                    "yourpassword" // Database password
            );
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Initialize UI components
        frame = new JFrame("Nu Tracker Inventory");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        // Login panel
        JPanel loginPanel = new JPanel(new GridLayout(3, 2));
        usernameField = new JTextField(); // Username input field
        passwordField = new JPasswordField(); // Password input field
        JButton loginButton = new JButton("Login"); // Login button

        // Add components to login panel
        loginPanel.add(new JLabel("Username:")); // Label for username field
        loginPanel.add(usernameField);
        loginPanel.add(new JLabel("Password:")); // Label for password field
        loginPanel.add(passwordField);
        loginPanel.add(loginButton);

        // Add login panel to frame
        frame.getContentPane().add(loginPanel, BorderLayout.CENTER);
        frame.setVisible(true);

        // Action listener for login button
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get username and password from input fields
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                // Authenticate user
                if (authenticateUser(username, password)) {
                    // Show inventory UI if authentication is successful
                    showInventoryUI();
                } else {
                    // Show error message if authentication fails
                    JOptionPane.showMessageDialog(frame, "Invalid credentials", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    // Method to authenticate user
    private static boolean authenticateUser(String username, String password) {
        try {
            // SQL query to check username and password
            String query = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, username); // Set username parameter
            stmt.setString(2, password); // Set password parameter
            ResultSet rs = stmt.executeQuery();
            return rs.next(); // Return true if a matching user is found
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Return false if an error occurs
        }
    }

    // Method to show inventory UI
    private static void showInventoryUI() {
        // Clear the main frame
        frame.getContentPane().removeAll();
        frame.repaint();

        // Inventory panel
        JPanel inventoryPanel = new JPanel(new BorderLayout());
        inventoryTextArea = new JTextArea(); // Text area to display inventory
        JButton loadInventoryButton = new JButton("Load Inventory"); // Button to load inventory

        // Add components to inventory panel
        inventoryPanel.add(new JScrollPane(inventoryTextArea), BorderLayout.CENTER);
        inventoryPanel.add(loadInventoryButton, BorderLayout.SOUTH);

        // Add inventory panel to frame
        frame.getContentPane().add(inventoryPanel);
        frame.revalidate();

        // Action listener for load inventory button
        loadInventoryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Load inventory when button is clicked
                loadInventory();
            }
        });
    }

    // Method to load inventory
    private static void loadInventory() {
        try {
            // SQL query to get all inventory items
            String query = "SELECT * FROM inventory";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            StringBuilder inventoryData = new StringBuilder();
            // Iterate through the result set and append inventory data
            while (rs.next()) {
                inventoryData.append("Item ID: ").append(rs.getInt("id"))
                        .append(", Name: ").append(rs.getString("item_name"))
                        .append(", Quantity: ").append(rs.getInt("quantity"))
                        .append(", Location: ").append(rs.getString("location"))
                        .append("\n");
            }
            // Display inventory data in text area
            inventoryTextArea.setText(inventoryData.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
