import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;

public class ATMMenuFrame extends JFrame {

    private int userId;

    // Icons map for buttons
    private final Map<String, Icon> iconMap = new HashMap<>();

    public ATMMenuFrame(int userId) {
        this.userId = userId;

        setTitle("ATM Main Menu");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Load icons (using basic UIManager icons or your own icons)
        loadIcons();

        // Gradient background panel
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                Color startColor = new Color(135, 206, 250);
                Color endColor = new Color(255, 255, 255);
                GradientPaint gp = new GradientPaint(0, 0, startColor, 0, getHeight(), endColor);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        mainPanel.setLayout(new BorderLayout(0, 25));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        // Welcome label with shadow effect
        JLabel welcomeLabel = new JLabel("Welcome, User ID: " + userId, SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        welcomeLabel.setForeground(new Color(25, 118, 210));
        welcomeLabel.setBorder(new DropShadowBorder(Color.GRAY, 5, 0.3f, 5, true, true, true, true));

        mainPanel.add(welcomeLabel, BorderLayout.NORTH);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new GridLayout(3, 2, 20, 20));
        buttonPanel.setOpaque(false);

        // Create and add buttons with icons
        buttonPanel.add(createMenuButton("Check Balance"));
        buttonPanel.add(createMenuButton("Withdraw"));
        buttonPanel.add(createMenuButton("Deposit"));
        buttonPanel.add(createMenuButton("Transfer"));
        buttonPanel.add(createMenuButton("Transaction History"));
        buttonPanel.add(createMenuButton("Logout"));

        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        // Footer label
        JLabel footer = new JLabel("© 2025 Your Bank. All rights reserved.", SwingConstants.CENTER);
        footer.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        footer.setForeground(new Color(100, 100, 100));
        mainPanel.add(footer, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    private void loadIcons() {
        // Using built-in basic icons as placeholders (you can replace with your own .png files)
        iconMap.put("Check Balance", UIManager.getIcon("FileView.directoryIcon"));
        iconMap.put("Withdraw", UIManager.getIcon("OptionPane.errorIcon"));
        iconMap.put("Deposit", UIManager.getIcon("OptionPane.informationIcon"));
        iconMap.put("Transfer", UIManager.getIcon("FileChooser.newFolderIcon"));
        iconMap.put("Transaction History", UIManager.getIcon("FileView.fileIcon"));
        iconMap.put("Logout", UIManager.getIcon("InternalFrame.closeIcon"));
    }

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);

        // Set font size smaller only for "Transaction History"
        if ("Transaction History".equals(text)) {
            button.setFont(new Font("Segoe UI", Font.BOLD, 14));  // smaller font
        } else {
            button.setFont(new Font("Segoe UI", Font.BOLD, 18));  // normal font
        }

        button.setForeground(Color.BLUE); // text color white
        button.setBackground(new Color(30, 144, 255)); // DodgerBlue
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Set icon if available, with some gap
       // Icon icon = iconMap.get(text);
       // if (icon != null) {
        //    button.setIcon(icon);
        //    button.setHorizontalTextPosition(SwingConstants.RIGHT);
        //    button.setIconTextGap(12);
      //  }

        // Rounded corners
        button.setBorder(new RoundedBorder(15));

        // Shadow border
        button.setBorder(BorderFactory.createCompoundBorder(
                new DropShadowBorder(new Color(0, 0, 0, 70), 4, 0.3f, 4, true, true, true, true),
                button.getBorder()
        ));

        // Hover effect with color animation
        button.addMouseListener(new MouseAdapter() {
            Color original = button.getBackground();
            Color hoverColor = original.brighter();

            @Override
            public void mouseEntered(MouseEvent e) {
                animateColor(button, original, hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                animateColor(button, button.getBackground(), original);
            }
        });

        // Add action listeners...
        switch (text) {
            case "Check Balance":
                button.addActionListener(e -> new BalanceWindow(userId));
                break;
            case "Withdraw":
                button.addActionListener(e -> new WithdrawWindow(userId));
                break;
            case "Deposit":
                button.addActionListener(e -> new DepositWindow(userId));
                break;
            case "Transfer":
                button.addActionListener(e -> new TransferWindow(userId));
                break;
            case "Transaction History":
                button.addActionListener(e -> new TransactionHistoryWindow(userId));
                break;
            case "Logout":
                button.addActionListener(e -> {
                    dispose();
                    new LoginFrame();
                });
                break;
        }

        return button;
    }


    // Helper method to animate color changes smoothly
    private void animateColor(JButton button, Color start, Color end) {
        final int steps = 10;
        final int delay = 15; // ms
        Timer timer = new Timer(delay, null);
        timer.addActionListener(new ActionListener() {
            int step = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                float ratio = (float) step / steps;
                int red = (int) (start.getRed() + ratio * (end.getRed() - start.getRed()));
                int green = (int) (start.getGreen() + ratio * (end.getGreen() - start.getGreen()));
                int blue = (int) (start.getBlue() + ratio * (end.getBlue() - start.getBlue()));
                button.setBackground(new Color(red, green, blue));
                step++;
                if (step > steps) {
                    timer.stop();
                }
            }
        });
        timer.start();
    }

    // Custom rounded border class
    private static class RoundedBorder extends AbstractBorder {
        private final int radius;

        RoundedBorder(int radius) {
            this.radius = radius;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.GRAY);
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(radius + 1, radius + 1, radius + 1, radius + 1);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.left = insets.top = insets.right = insets.bottom = radius + 1;
            return insets;
        }
    }

    // Drop shadow border implementation (simple)
    private static class DropShadowBorder extends AbstractBorder {
        private final Color shadowColor;
        private final int size;
        private final float opacity;
        private final int cornerSize;
        private final boolean left, top, right, bottom;

        public DropShadowBorder(Color shadowColor, int size, float opacity, int cornerSize,
                                boolean left, boolean top, boolean right, boolean bottom) {
            this.shadowColor = shadowColor;
            this.size = size;
            this.opacity = opacity;
            this.cornerSize = cornerSize;
            this.left = left;
            this.top = top;
            this.right = right;
            this.bottom = bottom;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(new Color(shadowColor.getRed(), shadowColor.getGreen(), shadowColor.getBlue(), (int)(opacity * 255)));
            for (int i = 0; i < size; i++) {
                int alpha = (int) (opacity * 255 * (1.0f - (float)i / size));
                g2.setColor(new Color(shadowColor.getRed(), shadowColor.getGreen(), shadowColor.getBlue(), alpha));
                g2.drawRoundRect(x + i, y + i, width - i * 2 - 1, height - i * 2 - 1, cornerSize, cornerSize);
            }
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(size, size, size, size);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.left = insets.top = insets.right = insets.bottom = size;
            return insets;
        }
    }

}
