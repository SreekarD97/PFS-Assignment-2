import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InventoryApp extends JFrame {
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/nutracker_db";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextArea inventoryArea;

    public InventoryApp() {
        setTitle("Inventory Tracking App");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel loginPanel = new JPanel(new GridLayout(3, 2));
        loginPanel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        loginPanel.add(usernameField);

        loginPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        loginPanel.add(passwordField);

        JButton loginButton = new JButton("Login");
        loginPanel.add(loginButton);

        inventoryArea = new JTextArea();
        inventoryArea.setEditable(false);

        add(loginPanel, BorderLayout.NORTH);
        add(new JScrollPane(inventoryArea), BorderLayout.CENTER);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                if (authenticateUser(username, password)) {
                    loadInventory();
                } else {
                    JOptionPane.showMessageDialog(InventoryApp.this, "Login failed!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    private boolean authenticateUser(String username, String password) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String query = "SELECT password, salt FROM users WHERE username = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String storedHash = resultSet.getString("password");
                String storedSalt = resultSet.getString("salt");
                return PasswordUtils.verifyPassword(password, storedHash, storedSalt);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void loadInventory() {
        inventoryArea.setText("");
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String query = "SELECT * FROM inventory";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                inventoryArea.append("Item ID: " + resultSet.getInt("item_id") + "\n");
                inventoryArea.append("Item Name: " + resultSet.getString("item_name") + "\n");
                inventoryArea.append("Quantity: " + resultSet.getInt("quantity") + "\n");
                inventoryArea.append("\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new InventoryApp().setVisible(true);
            }
        });
    }
}
