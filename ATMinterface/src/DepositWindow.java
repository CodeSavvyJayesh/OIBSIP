import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class DepositWindow extends JFrame {

    private int userId;
    private JTextField amountField;

    public DepositWindow(int userId) {
        this.userId = userId;

        setTitle("Deposit Money");
        setSize(400, 230);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel with gradient background
        JPanel mainPanel = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, new Color(238, 250, 255), 0, getHeight(), new Color(200, 230, 255));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new GridBagLayout());

        // Card panel for input
        JPanel card = new JPanel();
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(320, 140));
        card.setLayout(new GridLayout(3, 1, 10, 10));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(160, 200, 240), 2),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        // Label
        JLabel label = new JLabel("💵 Enter amount to deposit:");
        label.setFont(new Font("Segoe UI", Font.BOLD, 16));
        label.setForeground(new Color(30, 30, 30));

        // Text Field
        amountField = new JTextField();
        amountField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        amountField.setHorizontalAlignment(JTextField.CENTER);

        // Button
        JButton depositBtn = new JButton("Deposit");
        depositBtn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        depositBtn.setBackground(new Color(0, 123, 255));
        depositBtn.setForeground(Color.BLUE);
        depositBtn.setFocusPainted(false);
        depositBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        depositBtn.addActionListener(e -> depositAmount());

        // Add components to card
        card.add(label);
        card.add(amountField);
        card.add(depositBtn);

        mainPanel.add(card);
        add(mainPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    private void depositAmount() {
        String amountText = amountField.getText().trim();
        if (amountText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter an amount.", "⚠️ Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountText);
            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Amount must be positive.", "⚠️ Invalid Input", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid amount entered.", "❌ Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                JOptionPane.showMessageDialog(this, "Database connection error.", "❌ Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String updateSQL = "UPDATE users SET balance = balance + ? WHERE user_id = ?";
            try (PreparedStatement pst = conn.prepareStatement(updateSQL)) {
                pst.setDouble(1, amount);
                pst.setInt(2, userId);

                int rows = pst.executeUpdate();
                if (rows > 0) {
                    String insertTxnSql = "INSERT INTO transactions (user_id, type, amount, transaction_date) VALUES (?, 'DEPOSIT', ?, NOW())";
                    try (PreparedStatement pst2 = conn.prepareStatement(insertTxnSql)) {
                        pst2.setInt(1, userId);
                        pst2.setDouble(2, amount);
                        pst2.executeUpdate();
                    }

                    JOptionPane.showMessageDialog(this, "✅ ₹" + String.format("%.2f", amount) + " deposited successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Deposit failed. User not found.", "❌ Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "❌ DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
