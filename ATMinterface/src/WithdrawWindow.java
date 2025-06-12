import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class WithdrawWindow extends JFrame {

    public WithdrawWindow(int userId) {
        setTitle("Withdraw Money");
        setSize(400, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Main Panel
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(240, 248, 255)); // light blue background

        // Heading
        JLabel heading = new JLabel("💸 Withdraw Money");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 22));
        heading.setForeground(new Color(25, 25, 112)); // navy blue
        heading.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Label
        JLabel label = new JLabel("Enter amount to withdraw:");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));

        // Amount input
        JTextField amountField = new JTextField();
        amountField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        amountField.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        // Withdraw Button
        JButton withdrawBtn = new JButton("Withdraw");
        withdrawBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        withdrawBtn.setBackground(new Color(0, 123, 255));
        withdrawBtn.setForeground(Color.BLUE);
        withdrawBtn.setFocusPainted(false);
        withdrawBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        withdrawBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        withdrawBtn.setPreferredSize(new Dimension(120, 40));
        withdrawBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        withdrawBtn.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Add components to panel
        panel.add(heading);
        panel.add(Box.createVerticalStrut(20));
        panel.add(label);
        panel.add(amountField);
        panel.add(Box.createVerticalStrut(15));
        panel.add(withdrawBtn);

        add(panel); // add panel to frame

        // ActionListener
        withdrawBtn.addActionListener(e -> {
            String amtText = amountField.getText().trim();
            if (amtText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter an amount.");
                return;
            }

            try {
                double amount = Double.parseDouble(amtText);

                Connection conn = DBConnection.getConnection();
                PreparedStatement checkStmt = conn.prepareStatement("SELECT balance FROM users WHERE user_id = ?");
                checkStmt.setInt(1, userId);
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next()) {
                    double balance = rs.getDouble("balance");

                    if (balance >= amount) {
                        // Update balance
                        PreparedStatement updateStmt = conn.prepareStatement("UPDATE users SET balance = balance - ? WHERE user_id = ?");
                        updateStmt.setDouble(1, amount);
                        updateStmt.setInt(2, userId);
                        updateStmt.executeUpdate();

                        // Insert transaction
                        PreparedStatement insertTxnStmt = conn.prepareStatement(
                                "INSERT INTO transactions (user_id, type, amount, transaction_date) VALUES (?, 'WITHDRAW', ?, NOW())");
                        insertTxnStmt.setInt(1, userId);
                        insertTxnStmt.setDouble(2, amount);
                        insertTxnStmt.executeUpdate();

                        JOptionPane.showMessageDialog(this, "₹" + amount + " withdrawn successfully!");
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(this, "Insufficient balance!");
                    }
                }

                conn.close();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid amount.");
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Database error.");
            }
        });

        setVisible(true);
    }
}
