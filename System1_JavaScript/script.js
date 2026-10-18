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

function readAmount(fieldId, label, minimum, allowZero) {
  const text = document.getElementById(fieldId).value.trim();

  if (text === "") {
    return { error: `${label} is required.` };
  }

  const amount = Number(text);
  if (!Number.isFinite(amount)) {
    return { error: `${label} must be a valid number.` };
  }

  if (allowZero ? amount < minimum : amount <= minimum) {
    const rule = allowZero ? "cannot be negative." : "must be greater than zero.";
    return { error: `${label} ${rule}` };
  }

  return { value: amount };
}

function showResults(goalName, targetAmount, currentSavings, monthlySavings) {
  const remainingAmount = Math.max(0, targetAmount - currentSavings);
  const percentage = Math.min(100, (currentSavings / targetAmount) * 100);
  const goalReached = currentSavings >= targetAmount;
  let months = 0;
  let completionDateText = "Already achieved";

  if (!goalReached) {
    months = Math.ceil(remainingAmount / monthlySavings);
    const completionDate = addCalendarMonths(new Date(), months);
    completionDateText = formatDate(completionDate);
  }

  document.getElementById("result-goal").textContent = `Savings goal: ${goalName}`;
  document.getElementById("remaining-result").textContent = formatMoney(remainingAmount);
  document.getElementById("percentage-result").textContent = `${percentage.toFixed(2)}%`;
  document.getElementById("months-result").textContent =
    `${months} ${months === 1 ? "month" : "months"}`;
  document.getElementById("date-result").textContent = completionDateText;

  const status = document.getElementById("status");
  status.textContent = goalReached ? "Goal Reached" : "Still Saving";
  status.classList.toggle("still-saving", !goalReached);

  results.hidden = false;
}

savingsForm.addEventListener("submit", function (event) {
  event.preventDefault();
  errorMessage.hidden = true;
  results.hidden = true;

  const goalName = document.getElementById("goal-name").value.trim();
  if (goalName === "") {
    errorMessage.textContent = "Savings goal name is required.";
    errorMessage.hidden = false;
    return;
  }

  const target = readAmount("target-amount", "Target savings amount", 0, false);
  if (target.error) {
    errorMessage.textContent = target.error;
    errorMessage.hidden = false;
    return;
  }

  const current = readAmount("current-savings", "Current savings", 0, true);
  if (current.error) {
    errorMessage.textContent = current.error;
    errorMessage.hidden = false;
    return;
  }

  const reached = current.value >= target.value;
  const monthly = readAmount("monthly-savings", "Planned monthly savings", 0, reached);
  if (monthly.error) {
    errorMessage.textContent = reached
      ? monthly.error
      : "Planned monthly savings must be greater than zero until the goal is reached.";
    errorMessage.hidden = false;
    return;
  }

  showResults(goalName, target.value, current.value, monthly.value);
});
