
"use strict";

// 1. Get the important HTML elements

const savingsForm = document.getElementById("savings-form");
const errorMessage = document.getElementById("error-message");
const results = document.getElementById("results");

const MAX_AMOUNT = 1000000000000;


// 2. Format money as Philippine pesos

function formatMoney(amount) {
    return "₱" + amount.toLocaleString("en-PH", {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
    });
}


// 3. Get the user's input

function getSavingsInput() {
    return {
        goalName: document.getElementById("goal-name").value.trim(),
        targetAmount: document.getElementById("target-amount").value.trim(),
        currentSavings: document.getElementById("current-savings").value.trim(),
        monthlySavings: document.getElementById("monthly-savings").value.trim()
    };
}


// 4. Check if the input is valid

function validateInput(input) {
    if (input.goalName === "") {
        return "Please enter your savings goal name.";
    }

    const amounts = [
        { value: input.monthlySavings, username: "Username" },
        { value: input.targetAmount, name: "Target amount" },
        { value: input.currentSavings, name: "Current savings" },
        { value: input.monthlySavings, name: "Monthly savings" }
    ];

    for (const amount of amounts) {
        if (amount.value === "") {
            return amount.name + " is required.";
        }

        if (!/^\d+(\.\d{1,2})?$/.test(amount.value)) {
            return amount.name + " must be a valid amount with up to two decimal places.";
        }

        const number = Number(amount.value);

        if (!Number.isFinite(number) || number > MAX_AMOUNT) {
            return amount.name + " is too large or invalid.";
        }
    }

    if (Number(input.targetAmount) <= 0) {
        return "Target amount must be greater than zero.";
    }

    if (Number(input.currentSavings) < 0) {
        return "Current savings cannot be negative.";
    }

    if (Number(input.monthlySavings) < 0) {
        return "Monthly savings cannot be negative.";
    }

    return "";
}


// 5. Calculate the remaining amount

function calculateRemaining(target, saved) {
    return Math.max(0, target - saved);
}


// 6. Calculate the percentage completed

function calculateProgress(target, saved) {
    return Math.min(100, (saved / target) * 100);
}


// 7. Calculate the number of months needed

function calculateMonths(remaining, monthlySavings) {
    if (monthlySavings === 0) {
        return null;
    }

    return Math.ceil(remaining / monthlySavings);
}


// 8. Calculate the estimated completion date

function calculateCompletionDate(months) {
    const today = new Date();

    // Find the target month
    const targetMonth = new Date(
        today.getFullYear(),
        today.getMonth() + months,
        1
    );

    // Keep the date within the target month's last day
    const lastDay = new Date(
        targetMonth.getFullYear(),
        targetMonth.getMonth() + 1,
        0
    ).getDate();

    const day = Math.min(today.getDate(), lastDay);

    return new Date(
        targetMonth.getFullYear(),
        targetMonth.getMonth(),
        day
    );
}


// 9. Display the results

function displayResults(input) {
    const target = Number(input.targetAmount);
    const saved = Number(input.currentSavings);
    const monthly = Number(input.monthlySavings);

    const remaining = calculateRemaining(target, saved);
    const progress = calculateProgress(target, saved);
    const goalReached = saved >= target;

    // Show the goal name and money amounts
    document.getElementById("result-goal").textContent =
        input.goalName;

    document.getElementById("target-result").textContent =
        formatMoney(target);

    document.getElementById("saved-result").textContent =
        formatMoney(saved);

    document.getElementById("remaining-result").textContent =
        formatMoney(remaining);

    document.getElementById("percentage-result").textContent =
        progress.toFixed(2) + "%";

    // Update the progress bar
    document.getElementById("progress-bar").style.width =
        progress + "%";

    // Show the goal status
    const status = document.getElementById("status");

    if (goalReached) {
        status.textContent = "Goal Achieved";
    } else {
        status.textContent = "You still need to save";
    }

    status.classList.toggle("still-saving", !goalReached);

    // Calculate the estimated time and date
    if (goalReached) {
        document.getElementById("months-result").textContent =
            "0 months";

        document.getElementById("date-result").textContent =
            "Already achieved";

    } else if (monthly === 0) {
        document.getElementById("months-result").textContent =
            "Cannot estimate";

        document.getElementById("date-result").textContent =
            "Cannot estimate without monthly savings";

    } else {
        const months = calculateMonths(remaining, monthly);

        document.getElementById("months-result").textContent =
            months + (months === 1 ? " month" : " months");

        const date = calculateCompletionDate(months);

        document.getElementById("date-result").textContent =
            date.toLocaleDateString("en-PH", {
                year: "numeric",
                month: "long",
                day: "numeric"
            });
    }

    // Make the results visible
    results.hidden = false;
}


// 10. Clear the results and error message

function clearResults() {
    results.hidden = true;

    document.getElementById("result-goal").textContent = "";
    document.getElementById("target-result").textContent = "";
    document.getElementById("saved-result").textContent = "";
    document.getElementById("remaining-result").textContent = "";
    document.getElementById("percentage-result").textContent = "";
    document.getElementById("months-result").textContent = "";
    document.getElementById("date-result").textContent = "";

    document.getElementById("progress-bar").style.width = "0%";

    const status = document.getElementById("status");
    status.textContent = "";
    status.classList.remove("still-saving");
}


// 11. Run the program when Calculate is clicked

savingsForm.addEventListener("submit", function(event) {
    event.preventDefault();

    errorMessage.hidden = true;
    errorMessage.textContent = "";

    clearResults();

    // Step 1: Get the input
    const input = getSavingsInput();

    // Step 2: Validate the input
    const error = validateInput(input);

    if (error !== "") {
        errorMessage.textContent = error;
        errorMessage.hidden = false;
        return;
    }

    // Step 3: Calculate and display the results
    displayResults(input);
});


// 12. Run this when Reset is clicked

savingsForm.addEventListener("reset", function() {
    errorMessage.hidden = true;
    errorMessage.textContent = "";

    clearResults();
});
