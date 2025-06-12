import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class TransferWindow extends JFrame {
    private int userId;
    private JTextField recipientField, amountField;

    public TransferWindow(int userId) {
        this.userId = userId;

        setTitle("💸 Transfer Money");
        setSize(450, 280);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Gradient background panel
        JPanel mainPanel = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, new Color(245, 255, 250), 0, getHeight(), new Color(220, 240, 255));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new GridBagLayout());

        // Card-style inner panel
        JPanel card = new JPanel();
        card.setPreferredSize(new Dimension(350, 180));
        card.setBackground(Color.WHITE);
        card.setLayout(new GridLayout(5, 1, 10, 10));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 200, 240), 2),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        // Recipient label + input
        JLabel recipientLabel = new JLabel("👤 Recipient User ID:");
        recipientLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        recipientField = new JTextField();
        recipientField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        recipientField.setHorizontalAlignment(JTextField.CENTER);

        // Amount label + input
        JLabel amountLabel = new JLabel("💰 Amount to Transfer:");
        amountLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        amountField = new JTextField();
        amountField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        amountField.setHorizontalAlignment(JTextField.CENTER);

        // Transfer button
        JButton transferBtn = new JButton("Transfer");
        transferBtn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        transferBtn.setBackground(new Color(0, 153, 102));
        transferBtn.setForeground(Color.BLUE);
        transferBtn.setFocusPainted(false);
        transferBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        transferBtn.addActionListener(e -> transferMoney());

        // Add to card
        card.add(recipientLabel);
        card.add(recipientField);
        card.add(amountLabel);
        card.add(amountField);
        card.add(transferBtn);

        mainPanel.add(card);
        add(mainPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    private void transferMoney() {
        String recipientText = recipientField.getText().trim();
        String amountText = amountField.getText().trim();

        if (recipientText.isEmpty() || amountText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "⚠️ Missing Input", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int recipientId;
        double amount;

        try {
            recipientId = Integer.parseInt(recipientText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Recipient user ID must be a number.", "❌ Invalid ID", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            amount = Double.parseDouble(amountText);
            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Amount must be greater than zero.", "⚠️ Invalid Amount", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid amount entered.", "❌ Invalid Input", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (recipientId == userId) {
            JOptionPane.showMessageDialog(this, "You cannot transfer money to yourself!", "⚠️ Invalid Operation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) {
                JOptionPane.showMessageDialog(this, "Database connection error.", "❌ Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            conn.setAutoCommit(false);

            // Check sender balance
            String checkBalanceSql = "SELECT balance FROM users WHERE user_id = ?";
            try (PreparedStatement pst1 = conn.prepareStatement(checkBalanceSql)) {
                pst1.setInt(1, userId);
                ResultSet rs = pst1.executeQuery();
                if (!rs.next()) {
                    JOptionPane.showMessageDialog(this, "Sender account not found.", "❌ Error", JOptionPane.ERROR_MESSAGE);
                    conn.rollback();
                    return;
                }

                double senderBalance = rs.getDouble("balance");
                if (senderBalance < amount) {
                    JOptionPane.showMessageDialog(this, "Insufficient balance.", "❌ Not Enough Funds", JOptionPane.ERROR_MESSAGE);
                    conn.rollback();
                    return;
                }
            }

            // Check recipient existence
            String checkRecipientSql = "SELECT user_id FROM users WHERE user_id = ?";
            try (PreparedStatement pst2 = conn.prepareStatement(checkRecipientSql)) {
                pst2.setInt(1, recipientId);
                ResultSet rs2 = pst2.executeQuery();
                if (!rs2.next()) {
                    JOptionPane.showMessageDialog(this, "Recipient account not found.", "❌ Invalid Recipient", JOptionPane.ERROR_MESSAGE);
                    conn.rollback();
                    return;
                }
            }

            // Transfer logic
            try (
                    PreparedStatement deductSender = conn.prepareStatement("UPDATE users SET balance = balance - ? WHERE user_id = ?");
                    PreparedStatement addRecipient = conn.prepareStatement("UPDATE users SET balance = balance + ? WHERE user_id = ?");
                    PreparedStatement logSenderTxn = conn.prepareStatement("INSERT INTO transactions (user_id, type, amount, transaction_date) VALUES (?, 'DEBIT', ?, NOW())");
                    PreparedStatement logRecipientTxn = conn.prepareStatement("INSERT INTO transactions (user_id, type, amount, transaction_date) VALUES (?, 'CREDIT', ?, NOW())")
            ) {
                deductSender.setDouble(1, amount);
                deductSender.setInt(2, userId);
                deductSender.executeUpdate();

                addRecipient.setDouble(1, amount);
                addRecipient.setInt(2, recipientId);
                addRecipient.executeUpdate();

                logSenderTxn.setInt(1, userId);
                logSenderTxn.setDouble(2, amount);
                logSenderTxn.executeUpdate();

                logRecipientTxn.setInt(1, recipientId);
                logRecipientTxn.setDouble(2, amount);
                logRecipientTxn.executeUpdate();

                conn.commit();
                JOptionPane.showMessageDialog(this, "✅ ₹" + String.format("%.2f", amount) + " transferred successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "❌ Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
