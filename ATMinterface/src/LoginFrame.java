import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginFrame extends JFrame {

    private JTextField userIdField;
    private JPasswordField pinField;
    private JButton loginButton;

    public LoginFrame() {
        setTitle("ATM Login");
        setSize(400, 300);  // Increased window size
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                Color color1 = new Color(66, 135, 245);
                Color color2 = new Color(33, 75, 167);
                int w = getWidth();
                int h = getHeight();
                GradientPaint gp = new GradientPaint(0, 0, color1, 0, h, color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        backgroundPanel.setLayout(new GridBagLayout());
        backgroundPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel formPanel = new JPanel();
        formPanel.setBackground(Color.white);
        formPanel.setPreferredSize(new Dimension(320, 200));  // Increased width and height
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(33, 75, 167), 2),
                BorderFactory.createEmptyBorder(15, 25, 15, 25)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("ATM Login");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24)); // Slightly bigger title font
        titleLabel.setForeground(new Color(33, 75, 167));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(titleLabel, gbc);

        JLabel userIdLabel = new JLabel("User ID:");
        userIdLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        formPanel.add(userIdLabel, gbc);

        userIdField = new JTextField();
        userIdField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        gbc.gridx = 1;
        formPanel.add(userIdField, gbc);

        JLabel pinLabel = new JLabel("PIN:");
        pinLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        gbc.gridy = 2;
        gbc.gridx = 0;
        formPanel.add(pinLabel, gbc);

        pinField = new JPasswordField();
        pinField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        gbc.gridx = 1;
        formPanel.add(pinField, gbc);

        loginButton = new JButton("Login");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        loginButton.setBackground(new Color(173, 216, 230));  // Brighter blue
        loginButton.setForeground(Color.BLUE);  // White text for contrast
        loginButton.setFocusPainted(false);

        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(loginButton, gbc);

        backgroundPanel.add(formPanel);

        add(backgroundPanel);

        loginButton.addActionListener(e -> validateLogin());

        setVisible(true);
    }

    private void validateLogin() {
        String userIdText = userIdField.getText().trim();
        String pin = new String(pinField.getPassword());

        if (userIdText.isEmpty() || !userIdText.matches("\\d+")) {
            JOptionPane.showMessageDialog(this, "Please enter a valid numeric User ID.");
            return;
        }

        if (pin.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your PIN.");
            return;
        }

        int userId = Integer.parseInt(userIdText);

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM users WHERE user_id = ? AND pin = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.setString(2, pin);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "✅ Login Successful!");
                dispose();
                new ATMMenuFrame(userId);
            } else {
                JOptionPane.showMessageDialog(this, "❌ Invalid User ID or PIN");
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(LoginFrame::new);
    }
}
