import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class SavePlanApp {
    private final JFrame frame = new JFrame("SavePlan Java");
    private final JTextField goalNameField = new JTextField();
    private final JTextField targetField = new JTextField();
    private final JTextField currentField = new JTextField();
    private final JTextField monthlyField = new JTextField();
    private final JLabel errorLabel = new JLabel(" ");
    private final JLabel goalResult = new JLabel(" ");
    private final JLabel targetResult = new JLabel(" ");
    private final JLabel savedResult = new JLabel(" ");
    private final JLabel remainingResult = new JLabel(" ");
    private final JLabel percentageResult = new JLabel(" ");
    private final JLabel monthsResult = new JLabel(" ");
    private final JLabel dateResult = new JLabel(" ");
    private final JLabel statusResult = new JLabel(" ");
    private final JPanel resultsPanel = new JPanel(new GridLayout(8, 2, 8, 9));

    public SavePlanApp() {
        createWindow();
    }

    private void createWindow() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout(12, 12));
        frame.getContentPane().setBackground(new Color(242, 239, 228));

        JLabel title = new JLabel("SavePlan Java");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setForeground(new Color(63, 76, 56));

        JLabel subtitle = new JLabel("Plan one savings goal in Philippine pesos");
        JPanel heading = new JPanel(new GridLayout(2, 1, 0, 4));
        heading.setOpaque(false);
        heading.add(title);
        heading.add(subtitle);
        heading.setBorder(BorderFactory.createEmptyBorder(16, 18, 4, 18));
        frame.add(heading, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 9, 10));
        formPanel.setOpaque(false);
        formPanel.add(new JLabel("Savings goal name:"));
        formPanel.add(goalNameField);
        formPanel.add(new JLabel("Target amount (₱):"));
        formPanel.add(targetField);
        formPanel.add(new JLabel("Current savings (₱):"));
        formPanel.add(currentField);
        formPanel.add(new JLabel("Monthly savings (₱):"));
        formPanel.add(monthlyField);

        JButton calculateButton = new JButton("Calculate Plan");
        calculateButton.addActionListener(event -> calculatePlan());

        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener(event -> resetPlan());
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 8, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(calculateButton);
        buttonPanel.add(resetButton);

        JPanel inputArea = new JPanel(new BorderLayout(8, 12));
        inputArea.setOpaque(false);
        inputArea.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Savings Information"));
        inputArea.add(formPanel, BorderLayout.CENTER);
        inputArea.add(buttonPanel, BorderLayout.SOUTH);

        JPanel center = new JPanel(new BorderLayout(10, 10));
        center.setOpaque(false);
        center.setBorder(BorderFactory.createEmptyBorder(0, 18, 16, 18));
        errorLabel.setForeground(new Color(158, 38, 38));
        center.add(inputArea, BorderLayout.NORTH);
        center.add(errorLabel, BorderLayout.CENTER);

        resultsPanel.setOpaque(false);
        resultsPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Plan Results"));
        addResultRow("Goal name:", goalResult);
        addResultRow("Status:", statusResult);
        addResultRow("Your savings goal:", targetResult);
        addResultRow("Already saved:", savedResult);
        addResultRow("Still need to save:", remainingResult);
        addResultRow("Goal progress:", percentageResult);
        addResultRow("Estimated time:", monthsResult);
        addResultRow("Estimated completion date:", dateResult);
        resultsPanel.setVisible(false);
        center.add(resultsPanel, BorderLayout.SOUTH);

        frame.add(center, BorderLayout.CENTER);
        frame.pack();
        frame.setMinimumSize(frame.getSize());
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void addResultRow(String label, JLabel value) {
        resultsPanel.add(new JLabel(label));
        resultsPanel.add(value);
    }

    private double readAmount(JTextField field, String label, boolean allowZero) {
        String text = field.getText().trim();
        if (text.isEmpty()) {
            throw new IllegalArgumentException(label + " is required.");
        }

        double amount;
        try {
            amount = Double.parseDouble(text);
        } catch (NumberFormatException error) {
            throw new IllegalArgumentException(label + " must be a valid number.");
        }

        if (!Double.isFinite(amount)) {
            throw new IllegalArgumentException(label + " must be a valid number.");
        }
        if (allowZero && amount < 0) {
            throw new IllegalArgumentException(label + " cannot be negative.");
        }
        if (!allowZero && amount <= 0) {
            throw new IllegalArgumentException(label + " must be greater than zero.");
        }
        return amount;
    }

    private String formatMoney(double amount) {
        DecimalFormat formatter = new DecimalFormat(
                "#,##0.00", DecimalFormatSymbols.getInstance(Locale.US));
        return "₱" + formatter.format(amount);
    }

    private void calculatePlan() {
        errorLabel.setText(" ");
        resultsPanel.setVisible(false);

        try {
            String goalName = goalNameField.getText().trim();
            if (goalName.isEmpty()) {
                throw new IllegalArgumentException("Savings goal name is required.");
            }

            double targetAmount = readAmount(targetField, "Target savings amount", false);
            double currentSavings = readAmount(currentField, "Current savings", true);
            double monthlySavings = readAmount(
                    monthlyField, "Planned monthly savings", true);

            SavingsPlan plan = new SavingsPlan(
                    goalName, targetAmount, currentSavings, monthlySavings);
            displayResults(plan);
        } catch (IllegalArgumentException error) {
            errorLabel.setText(error.getMessage());
        }

        frame.pack();
    }

    private void resetPlan() {
        goalNameField.setText("");
        targetField.setText("");
        currentField.setText("");
        monthlyField.setText("");
        errorLabel.setText(" ");
        goalResult.setText(" ");
        statusResult.setText(" ");
        targetResult.setText(" ");
        savedResult.setText(" ");
        remainingResult.setText(" ");
        percentageResult.setText(" ");
        monthsResult.setText(" ");
        dateResult.setText(" ");
        resultsPanel.setVisible(false);
        frame.pack();
    }

    private void displayResults(SavingsPlan plan) {
        goalResult.setText(plan.getGoalName());
        statusResult.setText(plan.getStatus());
        double remainingAmount = plan.calculateRemaining();
        double targetAmount = plan.getTargetAmount();
        double currentSavings = plan.getCurrentSavings();
        targetResult.setText(formatMoney(targetAmount));
        savedResult.setText(formatMoney(currentSavings));
        remainingResult.setText(formatMoney(remainingAmount));
        percentageResult.setText(String.format(Locale.US, "%.2f%%", plan.calculateProgress()));

        Integer months = plan.calculateMonths();
        if (months == null) {
            monthsResult.setText("Cannot estimate");
            dateResult.setText("Cannot estimate without monthly savings");
        } else {
            monthsResult.setText(months + (months == 1 ? " month" : " months"));

            if (plan.getEstimatedCompletionDate() == null) {
                dateResult.setText("Already achieved");
            } else {
                DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(
                        "MMMM d, yyyy", Locale.ENGLISH);
                dateResult.setText(plan.getEstimatedCompletionDate().format(dateFormat));
            }
        }

        resultsPanel.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SavePlanApp::new);
    }
}
