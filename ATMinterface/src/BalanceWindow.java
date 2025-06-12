import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class BalanceWindow extends JFrame {

    public BalanceWindow(int userId) {
        setTitle("Account Balance");
        setSize(400, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Background panel
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Gradient background
                Graphics2D g2d = (Graphics2D) g;
                Color color1 = new Color(238, 245, 255);
                Color color2 = new Color(200, 225, 255);
                GradientPaint gp = new GradientPaint(0, 0, color1, 0, getHeight(), color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new GridBagLayout());

        // Rounded card panel
        JPanel card = new JPanel();
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(300, 120));
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 205, 230), 2),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        // Label inside card
        JLabel balanceLabel = new JLabel("Fetching your balance...");
        balanceLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        balanceLabel.setForeground(new Color(34, 40, 49));
        balanceLabel.setHorizontalAlignment(SwingConstants.CENTER);

        card.add(balanceLabel, BorderLayout.CENTER);
        mainPanel.add(card);

        add(mainPanel, BorderLayout.CENTER);

        // Fetch balance from DB
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT balance FROM users WHERE user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                double balance = rs.getDouble("balance");
                balanceLabel.setText("💰 Your Balance: ₹ " + String.format("%.2f", balance));
            } else {
                balanceLabel.setText("⚠️ User not found!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            balanceLabel.setText("❌ Error fetching balance.");
        }

        setVisible(true);
    }
}
