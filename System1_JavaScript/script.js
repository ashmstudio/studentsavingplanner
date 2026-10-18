const savingsForm = document.getElementById("savings-form");
const errorMessage = document.getElementById("error-message");
const results = document.getElementById("results");

function formatMoney(amount) {
  const formattedAmount = amount.toLocaleString("en-PH", {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  });
  return `₱${formattedAmount}`;
}

function addCalendarMonths(date, months) {
  const targetMonth = new Date(date.getFullYear(), date.getMonth() + months, 1);
  const lastDayOfTargetMonth = new Date(
    targetMonth.getFullYear(),
    targetMonth.getMonth() + 1,
    0
  ).getDate();
  const day = Math.min(date.getDate(), lastDayOfTargetMonth);

  return new Date(
    targetMonth.getFullYear(),
    targetMonth.getMonth(),
    day
  );
}

function formatDate(date) {
  return date.toLocaleDateString("en-PH", {
    year: "numeric",
    month: "long",
    day: "numeric"
  });
}

function getSavingsInput() {
  return {
    goalName: document.getElementById("goal-name").value.trim(),
    targetAmount: document.getElementById("target-amount").value.trim(),
    currentSavings: document.getElementById("current-savings").value.trim(),
    monthlySavings: document.getElementById("monthly-savings").value.trim()
  };
}

function validateInput(input) {
  if (input.goalName === "") {
    return "Savings goal name is required.";
  }

  const amounts = [
    { value: input.targetAmount, label: "Target savings amount" },
    { value: input.currentSavings, label: "Current savings" },
    { value: input.monthlySavings, label: "Planned monthly savings" }
  ];

  for (const amount of amounts) {
    if (amount.value === "") {
      return `${amount.label} is required.`;
    }
    if (!Number.isFinite(Number(amount.value))) {
      return `${amount.label} must be a valid number.`;
    }
  }

  if (Number(input.targetAmount) <= 0) {
    return "Target savings amount must be greater than zero.";
  }
  if (Number(input.currentSavings) < 0) {
    return "Current savings cannot be negative.";
  }
  if (Number(input.monthlySavings) < 0) {
    return "Planned monthly savings cannot be negative.";
  }

  return "";
}

function calculateRemaining(targetAmount, currentSavings) {
  return Math.max(0, targetAmount - currentSavings);
}

function calculateProgress(targetAmount, currentSavings) {
  return Math.min(100, (currentSavings / targetAmount) * 100);
}

function calculateMonths(remainingAmount, monthlySavings) {
  if (monthlySavings === 0) {
    return null;
  }
  return Math.ceil(remainingAmount / monthlySavings);
}

function displayResults(input) {
  const targetAmount = Number(input.targetAmount);
  const currentSavings = Number(input.currentSavings);
  const monthlySavings = Number(input.monthlySavings);
  const remainingAmount = calculateRemaining(targetAmount, currentSavings);
  const percentage = calculateProgress(targetAmount, currentSavings);
  const goalReached = currentSavings >= targetAmount;
  let monthsText = "0 months";
  let completionDateText = "Already achieved";

  if (!goalReached && monthlySavings === 0) {
    monthsText = "Cannot estimate";
    completionDateText = "Cannot estimate without monthly savings";
  } else if (!goalReached) {
    const months = calculateMonths(remainingAmount, monthlySavings);
    monthsText = `${months} ${months === 1 ? "month" : "months"}`;
    const completionDate = addCalendarMonths(new Date(), months);
    completionDateText = formatDate(completionDate);
  }

  document.getElementById("result-goal").textContent = `Goal name: ${input.goalName}`;
  document.getElementById("target-result").textContent = formatMoney(targetAmount);
  document.getElementById("saved-result").textContent = formatMoney(currentSavings);
  document.getElementById("remaining-result").textContent = formatMoney(remainingAmount);
  document.getElementById("percentage-result").textContent = `${percentage.toFixed(2)}%`;
  document.getElementById("months-result").textContent = monthsText;
  document.getElementById("date-result").textContent = completionDateText;

  const status = document.getElementById("status");
  status.textContent = goalReached
    ? "Goal Achieved"
    : "You still need to save";
  status.classList.toggle("still-saving", !goalReached);

  results.hidden = false;
}

savingsForm.addEventListener("submit", function (event) {
  event.preventDefault();
  errorMessage.hidden = true;
  results.hidden = true;

  const input = getSavingsInput();
  const validationError = validateInput(input);
  if (validationError !== "") {
    errorMessage.textContent = validationError;
    errorMessage.hidden = false;
    return;
  }

  displayResults(input);
});

savingsForm.addEventListener("reset", function () {
  errorMessage.hidden = true;
  errorMessage.textContent = "";
  results.hidden = true;
  document.getElementById("result-goal").textContent = "";
  document.getElementById("target-result").textContent = "";
  document.getElementById("saved-result").textContent = "";
  document.getElementById("remaining-result").textContent = "";
  document.getElementById("percentage-result").textContent = "";
  document.getElementById("months-result").textContent = "";
  document.getElementById("date-result").textContent = "";

  const status = document.getElementById("status");
  status.textContent = "";
  status.classList.remove("still-saving");
});
