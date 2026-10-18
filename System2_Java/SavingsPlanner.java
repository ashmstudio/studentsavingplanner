import java.time.LocalDate;

class SavingsPlan {
    private final String goalName;
    private final double targetAmount;
    private final double currentSavings;
    private final double monthlySavings;
    private final LocalDate startDate;

    public SavingsPlan(String goalName, double targetAmount, double currentSavings, double monthlySavings) {
        this.goalName = goalName;
        this.targetAmount = targetAmount;
        this.currentSavings = currentSavings;
        this.monthlySavings = monthlySavings;
        this.startDate = LocalDate.now();
    }

    public double calculateRemaining() {
        return Math.max(0, targetAmount - currentSavings);
    }

    public String getGoalName() {
        return goalName;
    }

    public double getTargetAmount() {
        return targetAmount;
    }

    public double getCurrentSavings() {
        return currentSavings;
    }

    public double calculateProgress() {
        return Math.min(100, (currentSavings / targetAmount) * 100);
    }

    public Integer calculateMonths() {
        if (isGoalReached()) {
            return 0;
        }
        if (monthlySavings == 0) {
            return null;
        }
        return (int) Math.ceil(calculateRemaining() / monthlySavings);
    }

    public LocalDate getEstimatedCompletionDate() {
        if (isGoalReached() || monthlySavings == 0) {
            return null;
        }
        return startDate.plusMonths(calculateMonths());
    }

    public boolean isGoalReached() {
        return currentSavings >= targetAmount;
    }

    public String getStatus() {
        if (isGoalReached()) {
            return "Goal Achieved";
        }
        return "You still need to save";
    }
}
