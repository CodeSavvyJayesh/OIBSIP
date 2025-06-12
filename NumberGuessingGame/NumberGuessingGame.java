import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class NumberGuessingGame extends JFrame implements ActionListener {
    private JLabel scoreLabel;
    private int score = 0;

    private JLabel attemptsLabel;
    private JButton restartButtton;

    private JButton hintButton;

    private int hintsUsed = 0;
    private final int MAX_HINTS = 3;  // example max 3 hints allowed


    private String generateHint() {
        switch (hintsUsed) {
            case 1:
                // First hint: Even or Odd
                if (randomNumber % 2 == 0) {
                    return "The number is even.";
                } else {
                    return "The number is odd.";
                }
            case 2:
                // Second hint: Is it greater than 50?
                if (randomNumber > 50) {
                    return "The number is greater than 50.";
                } else {
                    return "The number is 50 or less.";
                }
            case 3:
                // Third hint: Divisible by 5 or not (just an example)
                if (randomNumber % 5 == 0) {
                    return "The number is divisible by 5.";
                } else {
                    return "The number is not divisible by 5.";
                }
            default:
                return "No more hints available.";
        }
    }




    private int attempts = 0;
    private int MAX_ATTEMPTS = 5;
    private JTextField guessField;
    private JButton guessButton;
    private JLabel messageLabel;

    private JCheckBox darkModeToggle;
    private int randomNumber;

    public NumberGuessingGame() {
        setTitle("🎯 Guess The Number Game");
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null); // center window

        // Panel with layout
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(227, 242, 253)); // light gray
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // spacing
        Font font = new Font("SansSerif", Font.BOLD, 16);

        // Prompt
        JLabel promptLabel = new JLabel("Guess a number between 1 to 100:");
        promptLabel.setFont(font);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(promptLabel, gbc);

        // Dark mode toggle checkbox
        darkModeToggle = new JCheckBox("Dark Mode");
        darkModeToggle.setFont(new Font("SansSerif", Font.BOLD, 14));
        darkModeToggle.setBackground(new Color(227, 242, 253)); // same as panel background initially (light)
        darkModeToggle.setForeground(Color.BLACK);
        gbc.gridx = 3;
        gbc.gridy = 0;
        panel.add(darkModeToggle, gbc);

        darkModeToggle.addActionListener(e -> {
            if (darkModeToggle.isSelected()) {
                setDarkMode(true);
            } else {
                setDarkMode(false);
            }
        });





// Guess Field (now at row 1)
        guessField = new JTextField(10);
        guessField.setFont(font);
        guessField.setBackground(Color.WHITE);
        guessField.setForeground(Color.BLACK);
        guessField.setPreferredSize(new Dimension(120, 40));
        guessField.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
        guessField.setMargin(new Insets(0, 8, 0, 8)); // small padding inside
        guessField.setHorizontalAlignment(JTextField.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 1; // shifted down
        gbc.gridwidth = 1;
        guessField.setMinimumSize(new Dimension(60, 30));
        panel.add(guessField, gbc);

// Guess Button (now also at row 1)
        guessButton = new JButton("Guess");
        guessButton.setPreferredSize(new Dimension(120, 40));
        guessButton.setBackground(new Color(129, 212, 250));
        guessButton.setForeground(Color.BLACK);
        guessButton.setFont(font);
        guessButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        gbc.gridx = 1;
        gbc.gridy = 1; // shifted down
        panel.add(guessButton, gbc);


        // hintButton

        hintButton = new JButton("HINT");
        hintButton.setPreferredSize(new Dimension(120,40));
        hintButton.setBackground(new Color(255,179,71));
        hintButton.setForeground(Color.BLACK);
        hintButton.setFont(font);
        hintButton.setBorder(BorderFactory.createLineBorder(Color.BLACK,2));
        gbc.gridx = 3;
        gbc.gridy = 1;
        panel.add(hintButton,gbc);
        hintButton.setActionCommand("Hint");

        hintButton.addActionListener(this);






        //restart button
        restartButtton = new JButton("Restart");
        restartButtton.setPreferredSize(new Dimension(140,50));
        restartButtton.setBackground(new Color(0,51,102));
        restartButtton.setForeground(Color.BLACK);
        restartButtton.setFont(font);
        restartButtton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        restartButtton.setEnabled(false);
        restartButtton.addActionListener(this);

        gbc.gridx = 0;
        gbc.gridy = 5; // below score label
        gbc.gridwidth = 2;
        panel.add(restartButtton,gbc);




        // Message label
        messageLabel = new JLabel("Enter your guess above.");
        messageLabel.setFont(font);
        messageLabel.setForeground(Color.DARK_GRAY);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        panel.add(messageLabel, gbc);

        // Attempts label
        attemptsLabel = new JLabel("Attempts Left: " + (MAX_ATTEMPTS - attempts));
        attemptsLabel.setFont(font);
        gbc.gridy = 3;
        panel.add(attemptsLabel, gbc);
        scoreLabel = new JLabel(("Score :" + score));
        scoreLabel.setFont(font);
        gbc.gridy=4;
        panel.add(scoreLabel,gbc);

        // Add panel to frame
        add(panel);

        guessButton.addActionListener(this);
        generateRandomNumber();
        setVisible(true);
    }

    private void generateRandomNumber() {
        Random rand = new Random();
        randomNumber = rand.nextInt(100) + 1;
    }
    private void setDarkMode(boolean dark) {
        Color bgColor = dark ? new Color(30, 30, 30) : new Color(227, 242, 253);
        Color fgColor = dark ? Color.WHITE : Color.BLACK;

        getContentPane().setBackground(bgColor);

        for (Component comp : getContentPane().getComponents()) {
            comp.setBackground(bgColor);
            comp.setForeground(fgColor);

            if (comp instanceof JPanel) {
                for (Component inner : ((JPanel) comp).getComponents()) {
                    // Don't override button styles
                    if (inner instanceof JButton || inner instanceof JCheckBox || inner instanceof JTextField) {
                        continue;
                    }
                    inner.setBackground(bgColor);
                    inner.setForeground(fgColor);
                }
            }
        }

        // Optional: Update dark mode toggle separately
        darkModeToggle.setBackground(bgColor);
        darkModeToggle.setForeground(fgColor);
    }




    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("Hint")) {
            if (hintsUsed >= MAX_HINTS) {
                messageLabel.setText("No more hints left!");
            } else {
                hintsUsed++;
                String hint = generateHint();
                messageLabel.setText("Hint: " + hint + " (" + (MAX_HINTS - hintsUsed) + " hints left)");
            }
            return;  // stop further processing
        }
        if (e.getSource() == restartButtton) {
            // Reset everything
            attempts = 0;
            score = 0;
            hintsUsed = 0;
            guessField.setText("");
            guessButton.setEnabled(true);
            restartButtton.setEnabled(false);
            messageLabel.setText("Enter your guess above.");
            attemptsLabel.setText("Attempts Left: " + (MAX_ATTEMPTS - attempts));
            scoreLabel.setText("Score: " + score);
            generateRandomNumber();
            return;
        }



        String guessText = guessField.getText();
        try {
            if (attempts >= MAX_ATTEMPTS) {
                messageLabel.setText("No more attempts left! The number was: " + randomNumber);
                guessButton.setEnabled(false);
                return;
            }

            int guess = Integer.parseInt(guessText);
            if (guess < 1 || guess > 100) {
                messageLabel.setText("Please enter a valid number (1 to 100).");
                guessField.setText("");
                return;
            }
            attempts++;
            attemptsLabel.setText("Attempts left: " + (MAX_ATTEMPTS - attempts));

            if (guess == randomNumber - 1 || guess == randomNumber - 2) {
                messageLabel.setText("Very close on lower side! Try again.");
            } else if (guess < randomNumber - 2) {
                messageLabel.setText("Too low! Try again.");
            } else if (guess == randomNumber + 1 || guess == randomNumber + 2) {
                messageLabel.setText("Very close on higher side! Try again.");
            } else if (guess > randomNumber + 2) {
                messageLabel.setText("Too high! Try again.");
            } else {
                score = (MAX_ATTEMPTS - attempts + 1) * 10; // 1st try = 50, 2nd = 40, etc.
                messageLabel.setText("Correct! You guessed it in " + attempts + " attempt.");
                scoreLabel.setText("Score: " + score);
                guessButton.setEnabled(false);
                guessField.setText("");
                restartButtton.setEnabled(true); // allow restart after game ends
                return;

            }

            // Clear the text field after wrong guess
            guessField.setText("");

            if (attempts == MAX_ATTEMPTS && guess != randomNumber) {
                if (attempts == MAX_ATTEMPTS && guess != randomNumber) {
                    score = 0; // No points
                    messageLabel.setText("Out of attempts! The correct number was: " + randomNumber);
                    scoreLabel.setText("Score: " + score);
                    guessButton.setEnabled(false);
                    restartButtton.setEnabled(true); // allow restart after game ends

                }

            }

        } catch (NumberFormatException ex) {
            messageLabel.setText("Please enter a valid number.");
            guessField.setText(""); // Clear invalid input too
        }
    }

    public static void main(String[] args) {
        new NumberGuessingGame();
    }
}
