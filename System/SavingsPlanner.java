
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Scanner;

public class SavingsPlanner {

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

        boolean running = true;

        while (running) {

            displayHeader();

            System.out.println("  [1] Create Savings Plan");
            System.out.println("  [2] Exit");

            line();
            System.out.print("  Enter your choice: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {

                case "1":
                    try {
                        calculatePlan();
                    } catch (IllegalArgumentException error) {
                        System.out.println();
                        line();
                        System.out.println("  ERROR: " + error.getMessage());
                        line();
                    }

                    pause();
                    break;

                case "2":
                    System.out.println();
                    line();
                    System.out.println("  Thank you for using");
                    System.out.println("  STUDENT SAVINGS PLANNER!");
                    line();
                    running = false;
                    break;

                default:
                    System.out.println();
                    System.out.println(
                        "  Invalid choice. Please select 1 or 2.");
                    pause();
                    break;
            }
        }

        scanner.close();
    }

    // Display the main terminal interface
    private static void displayHeader() {

        System.out.println("\n");
        line();
        System.out.println("  |                                          |");
        System.out.println("  |        STUDENT SAVINGS PLANNER           |");
        System.out.println("  |                                          |");
        System.out.println("  |     Plan your savings. Reach your goal.  |");
        System.out.println("  |                                          |");
        line();

        System.out.println();
        System.out.println("  MAIN MENU");
        line();
    }

    // Display a horizontal line
    private static void line() {

        System.out.println(
            "  +------------------------------------------+");
    }

    // Read and calculate savings information
    private static void calculatePlan() {

        System.out.println();
        line();
        System.out.println("             SAVINGS INFORMATION");
        line();

        System.out.print("  Savings Goal Name: ");
        String goalName = scanner.nextLine().trim();

        if (goalName.isEmpty()) {
            throw new IllegalArgumentException(
                "Savings goal name is required.");
        }

        double targetAmount = readAmount(
            "Target Amount (PHP)", false);

        double currentSavings = readAmount(
            "Current Savings (PHP)", true);

        double monthlySavings = readAmount(
            "Monthly Savings (PHP)", true);

        // Create the savings plan object
        SavingsPlan plan = new SavingsPlan(
            goalName,
            targetAmount,
            currentSavings,
            monthlySavings
        );

        displayResults(plan);
    }

    // Read and validate monetary input
    private static double readAmount(
            String label, boolean allowZero) {

        System.out.print("  " + label + ": ");

        String input = scanner.nextLine().trim();

        if (input.isEmpty()) {
            throw new IllegalArgumentException(
                label + " is required.");
        }

        if (!input.matches("\\d+(\\.\\d{1,2})?")) {
            throw new IllegalArgumentException(
                label + " must be a valid amount "
                + "with up to two decimal places.");
        }

        double amount;

        try {
            amount = Double.parseDouble(input);

        } catch (NumberFormatException error) {
            throw new IllegalArgumentException(
                label + " must be a valid number.");
        }

        if (!Double.isFinite(amount)
                || amount > 1_000_000_000_000.0) {

            throw new IllegalArgumentException(
                label + " is too large or invalid.");
        }

        if (allowZero && amount < 0) {
            throw new IllegalArgumentException(
                label + " cannot be negative.");
        }

        if (!allowZero && amount <= 0) {
            throw new IllegalArgumentException(
                label + " must be greater than zero.");
        }

        return amount;
    }

    // Format amounts into Philippine pesos
    private static String formatMoney(double amount) {

        DecimalFormat formatter =
            new DecimalFormat("#,##0.00");

        return "PHP " + formatter.format(amount);
    }

    // Create a visual savings progress bar
    private static String createProgressBar(double progress) {

        int totalBars = 20;

        int filledBars =
            (int) Math.round(progress / 100.0 * totalBars);

        StringBuilder bar = new StringBuilder("[");

        for (int i = 0; i < totalBars; i++) {

            if (i < filledBars) {
                bar.append("#");
            } else {
                bar.append("-");
            }
        }

        bar.append("]");

        return bar.toString();
    }

    // Display the savings results dashboard
    private static void displayResults(SavingsPlan plan) {

        double progress = plan.calculateProgress();

        System.out.println();
        line();
        System.out.println("               SAVINGS DASHBOARD");
        line();

        System.out.println("  Goal Name        : "
            + plan.getGoalName());

        System.out.println("  Status           : "
            + plan.getStatus());

        System.out.println("  Target Amount    : "
            + formatMoney(plan.getTargetAmount()));

        System.out.println("  Current Savings  : "
            + formatMoney(plan.getCurrentSavings()));

        System.out.println("  Remaining Amount : "
            + formatMoney(plan.calculateRemaining()));

        System.out.println();
        System.out.println("  SAVINGS PROGRESS");

        System.out.println("  " + createProgressBar(progress));

        System.out.printf(
            Locale.US,
            "  %.2f%% completed%n",
            progress
        );

        Integer months = plan.calculateMonths();

        System.out.println();

        if (months == null) {

            System.out.println(
                "  Estimated Time   : Cannot estimate");

            System.out.println(
                "  Completion Date  : Cannot estimate");

        } else if (months == 0) {

            System.out.println(
                "  Estimated Time   : 0 months");

            System.out.println(
                "  Completion Date  : Already achieved");

        } else {

            System.out.println("  Estimated Time   : "
                + months
                + (months == 1 ? " month" : " months"));

            LocalDate completionDate =
                plan.getEstimatedCompletionDate();

            DateTimeFormatter dateFormat =
                DateTimeFormatter.ofPattern(
                    "MMMM d, yyyy", Locale.ENGLISH);

            System.out.println("  Completion Date  : "
                + completionDate.format(dateFormat));
        }

        line();

        System.out.println(
            "  Your savings plan has been calculated!");

        line();
    }

    // Pause before returning to the main menu
    private static void pause() {

        System.out.println();
        System.out.print("  Press ENTER to continue...");

        scanner.nextLine();
    }
}


// =============================================
// SAVINGS PLAN CLASS
// Handles data, validation, and calculations
// =============================================

class SavingsPlan {

    private static final double MAX_AMOUNT =
        1_000_000_000_000.0;

    private final String goalName;
    private final double targetAmount;
    private final double currentSavings;
    private final double monthlySavings;
    private final LocalDate startDate;

    // Constructor
    public SavingsPlan(
            String goalName,
            double targetAmount,
            double currentSavings,
            double monthlySavings) {

        if (goalName == null || goalName.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Savings goal name is required.");
        }

        validateAmount(
            targetAmount, "Target savings amount", false);

        validateAmount(
            currentSavings, "Current savings", true);

        validateAmount(
            monthlySavings, "Planned monthly savings", true);

        this.goalName = goalName.trim();
        this.targetAmount = targetAmount;
        this.currentSavings = currentSavings;
        this.monthlySavings = monthlySavings;
        this.startDate = LocalDate.now();
    }

    // Validate savings amounts
    private static void validateAmount(
            double amount, String label, boolean allowZero) {

        if (!Double.isFinite(amount) || amount > MAX_AMOUNT) {
            throw new IllegalArgumentException(
                label + " must be a valid amount within the limit.");
        }

        if (allowZero && amount < 0) {
            throw new IllegalArgumentException(
                label + " cannot be negative.");
        }

        if (!allowZero && amount <= 0) {
            throw new IllegalArgumentException(
                label + " must be greater than zero.");
        }
    }

    // Return the savings goal name
    public String getGoalName() {
        return goalName;
    }

    // Return the target amount
    public double getTargetAmount() {
        return targetAmount;
    }

    // Return the current savings
    public double getCurrentSavings() {
        return currentSavings;
    }

    // Return the planned monthly savings
    public double getMonthlySavings() {
        return monthlySavings;
    }

    // Calculate the remaining savings
    public double calculateRemaining() {

        return Math.max(
            0,
            targetAmount - currentSavings
        );
    }

    // Calculate savings progress percentage
    public double calculateProgress() {

        return Math.min(
            100,
            (currentSavings / targetAmount) * 100
        );
    }

    // Check if the savings goal is achieved
    public boolean isGoalReached() {

        return currentSavings >= targetAmount;
    }

    // Estimate the number of months needed
    public Integer calculateMonths() {

        if (isGoalReached()) {
            return 0;
        }

        if (monthlySavings == 0) {
            return null;
        }

        return (int) Math.ceil(
            calculateRemaining() / monthlySavings
        );
    }

    // Calculate the estimated completion date
    public LocalDate getEstimatedCompletionDate() {

        Integer months = calculateMonths();

        if (months == null || months == 0) {
            return null;
        }

        return startDate.plusMonths(months);
    }

    // Return the savings status
    public String getStatus() {

        if (isGoalReached()) {
            return "Goal Achieved";
        }

        return "You still need to save";
    }
}
