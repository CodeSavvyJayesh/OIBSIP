import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;

public class TransactionHistoryWindow extends JFrame {
    private int userId;

    public TransactionHistoryWindow(int userId) {
        this.userId = userId;
        setTitle("📋 Transaction History");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Table model
        String[] columns = {"Date", "Amount (₹)", "Type"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        JTable table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setBackground(Color.WHITE);
        table.setGridColor(new Color(230, 230, 230));
        table.setShowHorizontalLines(true);

        // Center align cells
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // Date
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer); // Amount
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer); // Type

        // Header customization
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 15));
        header.setForeground(Color.BLUE);
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Color.WHITE);

        // Title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(240, 248, 255));
        JLabel titleLabel = new JLabel("🧾 Your Transactions");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(0, 102, 204));
        titlePanel.add(titleLabel);

        add(titlePanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        fetchTransactions(model);
        setVisible(true);
    }

    private void fetchTransactions(DefaultTableModel model) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT transaction_date, amount, type FROM transactions WHERE user_id = ? ORDER BY transaction_date DESC";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String date = rs.getString("transaction_date");
                double amount = rs.getDouble("amount");
                String type = rs.getString("type");
                model.addRow(new Object[]{date, String.format("%.2f", amount), type});
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching transactions.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
