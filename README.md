# studentsavingplanner
 A saving planner using JAVASCRIPT Procedural Programming and JAVA object-oriented Programming.

## SavePlan — Savings Planner

SavePlan is a beginner-friendly college project with two separate applications for planning one savings goal:

- [System1_JavaScript](./System1_JavaScript/) is a browser app built with HTML, CSS, and vanilla JavaScript. Its JavaScript uses procedural programming: variables hold the input values, functions process them in steps, and conditions check inputs and decide which results to show.
- [System2_Java](./System2_Java/) is a desktop app built with Java Swing. Its `SavingsPlan` object stores the goal information and has methods that calculate the plan results. `SavePlanApp` creates the GUI, checks the form, and displays those results.

The layouts are different, but both systems use the same inputs, validation rules, calculations, and date rules. Neither system uses a database, account system, framework, or external library.

## System 1 — SavePlan JS

Open `System1_JavaScript\index.html` in a web browser. No installation or internet connection is needed. Enter a goal name, target amount, current savings, and planned monthly savings, then select **Calculate savings plan**.

The form accepts amounts with a decimal point. For example, enter `10000` or `10000.50` without a currency symbol or thousands separator.

## System 2 — SavePlan Java

Install a JDK (Java Development Kit). The JDK includes both the Java compiler and runtime. Open PowerShell and enter these commands:

```powershell
Set-Location "C:\mycodex\studentsavingplanner\System2_Java"
$jdk = "$env:LOCALAPPDATA\Programs\Eclipse Adoptium\jdk-25.0.4.101-hotspot\bin"
$build = "$env:TEMP\SavePlanJava"
New-Item -ItemType Directory -Path $build -Force | Out-Null
& "$jdk\javac.exe" -encoding UTF-8 -d $build SavePlanApp.java SavingsPlanner.java
& "$jdk\java.exe" -cp $build SavePlanApp
```

The JDK path above matches the detected installation on the original development computer. If your JDK is installed in a different folder, change `$jdk` to that installation's `bin` folder. The `javac` command compiles the two source files into a temporary build folder; the `java` command opens the desktop application.

## Shared rules and calculations

- The goal name is required. The target must be greater than zero. Current savings cannot be negative.
- Planned monthly savings cannot be negative. If the goal is not reached and planned monthly savings is zero, the plan still displays its balance and progress, but shows that the time and date cannot be estimated.
- Every calculation displays the target, amount already saved, and amount still needed as separate values. Remaining amount is calculated as `max(0, target - current savings)`.
- Savings progress is `min(100, current savings / target * 100)`, shown to two decimal places.
- The **Reset** button clears the form, any error message, and the displayed plan so another goal can be entered.
- Months needed is zero if already reached; otherwise, when monthly savings is greater than zero, it is the remaining amount divided by monthly savings, rounded up to a whole month.
- The completion date uses today's date when the plan is calculated plus the estimated calendar months, with the first planned monthly contribution one month later. If the destination month has fewer days, both systems use the last day of that month. The estimate assumes a monthly contribution with no withdrawals, interest, or fees.
- If the target is not met, the status says `You still need to save`, and the remaining amount tells how much more money is needed to reach the goal. The result also shows how many months are estimated to reach the goal.
- Reached and exceeded goals show `Goal Achieved` and `Already achieved`.
- Monetary results use Philippine pesos and two decimal places.

### Example

With `New Laptop`, target `30000`, current savings `7000`, and monthly savings `5000`, both applications show:

- Your savings goal: **₱30,000.00**
- Already saved: **₱7,000.00**
- Still need to save: **₱23,000.00**
- Goal progress: **23.33%**
- Estimated time: **5 months**
- Estimated completion date: **March 18, 2027** when calculated on October 18, 2026
- Status: **You still need to save**

## Beginner defense guide

**Q: What does SavePlan do?**
A: It estimates how many months I need to save for one goal and shows the remaining amount, percentage, date, and status.

**Q: What is procedural programming in System 1?**
A: The JavaScript follows steps. It reads the form values, checks them, calculates the results in functions, and then displays them. It does not use classes.

**Q: Where is OOP used in System 2?**
A: `SavingsPlan` is a class. Each plan object keeps its goal information in attributes and calculates results using methods. `SavePlanApp` handles the window and user input.

**Q: Why are there two programs?**
A: My project requirements ask for the same planner in JavaScript using procedural programming and in Java using OOP.

**Q: Why do the screens look different?**
A: The JavaScript version is a simple browser form, while the Java version is a traditional desktop window made with Swing.

**Q: How do you calculate the remaining amount?**
A: I subtract current savings from the target. If that would be negative, I show zero.

**Q: How do you calculate the percentage?**
A: I divide current savings by the target, multiply by 100, and cap the displayed value at 100 percent.

**Q: What does the Reset button do?**
A: It clears the input fields, any error, and the current results so I can enter a new savings plan.

**Q: Why do you round the months up?**
A: If the savings plan needs even part of another month, that still takes a full month to reach the goal.

**Q: What happens when the goal is already reached?**
A: The remaining amount is zero, the percentage is capped at 100, the status says `Goal Achieved`, the time is zero months, and the date says `Already achieved`. Monthly savings can be zero.

**Q: How do you make sure invalid information is not used?**
A: Both programs check for a blank goal name, invalid or missing numbers, a target that is not greater than zero, and negative savings values. If monthly savings is zero before the goal is reached, they show the progress but explain that completion time cannot be estimated.

**Q: What does the estimated date mean?**
A: It is an estimate based on today's date and the number of whole months calculated from the monthly savings amount. It assumes the user saves the planned amount each month.
