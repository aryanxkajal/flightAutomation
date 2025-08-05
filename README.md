# ✈️ FlightAutomation Test Framework

Welcome to the official repository for the **FlightAutomation** project — a Selenium + Cucumber based automation suite designed to validate core functionalities of a flight booking web application.

This project follows modular best practices using Page Object Model, Excel-driven test data, detailed HTML reporting, and screenshot capture on failure.

---

## 🚀 How to Collaborate?

To ensure smooth and standardized team collaboration, please follow these steps:

### 1. Clone the Repository

```bash
git clone https://github.com/your-org/FlightAutomation.git
cd FlightAutomation
````

### 2. Create a New Feature Branch

Before starting any work, create a new branch off `main`.
🚫 **Never commit directly to `main`**.

```bash
git checkout -b feature/<your-feature-name>
```

🔁 Replace `<your-feature-name>` with a short, descriptive label
Example:

* `feature/flight-search-tests`
* `bugfix/booking-validation`

### 3. Make and Test Your Changes Locally

* Develop your feature/module inside the new branch.
* Run your tests locally to ensure nothing is broken:

```bash
mvn clean test
```

* Check reports and screenshots (in `target/`) and fix all failures.

### 4. Stage and Commit Changes

```bash
git add .
git commit -m "✅ Added search validation logic for flight name"
```

📌 Use **clear, concise commit messages** that describe what you did.

### 5. Push Your Branch to GitHub

```bash
git push origin feature/<your-feature-name>
```

### 6. Create a Pull Request (PR)

* Visit the GitHub repository.
* Click **“Compare & Pull Request”**.
* Set the base branch to `main` and select your feature branch.
* Add a detailed description of your changes.
* Assign reviewers.

✅ Wait for CI and code reviews to pass.

### 7. Respond to Review Feedback

* If any requested changes are made:

  * Commit them to your feature branch.
  * The PR will automatically update.

### 8. Keep Your Branch Updated with Main

Keep your branch in sync with `main` to avoid merge conflicts:

```bash
git checkout main
git pull origin main
git checkout feature/<your-feature-name>
git merge main
```

### 9. Merge Only After All Checks Pass

Only merge your PR after:

* ✅ All tests pass
* ✅ All reviewers have approved
* ✅ There are **no conflicts**

---

## 📁 Project Modules

| Module             | Description                                             |
| ------------------ | ------------------------------------------------------- |
| **Login**          | Verifies login functionality using properties file      |
| **Flight Enquiry** | Validates enquiry form submission and reset behavior    |
| **Flight Booking** | Tests booking validations with positive & negative data |
| **Flight Search**  | Verifies search by flight number, name, and type        |
| **Reset Button**   | Validates reset button functionality for forms          |

---

## 🛠 Tech Stack

* **Language**: Java 11
* **Automation**: Selenium WebDriver
* **Test Framework**: Cucumber (Gherkin syntax)
* **Build Tool**: Maven
* **Excel Data Handling**: Apache POI
* **Screenshot on Failure**: Automatically via Hooks
* **Reports**: Cucumber HTML Report at `target/cucumber-reports.html`

---

## 📊 Test Data Format

Stored in: `src/test/resources/Card.xlsx`

| Sheet Name               | Usage             |
| ------------------------ | ----------------- |
| `flight_booking_details` | Booking test data |
| `flight_search_details`  | Search test data  |

---

## 🤝 Contributors

| Name                | Contributions                                     |
| ------------------- | ------------------------------------------------- |
| **Subhanjan Dutta** | Login, Flight Enquiry, Flight Booking Module      |
| **Aryan Kajal**     | Flight Search Module, Jira Setup, GitHub Workflow |

---

## 🧾 License

This project is maintained for educational, training, and learning purposes.
