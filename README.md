# ✈️ FlightAutomation Test Framework

**FlightAutomation** is a robust Selenium + Cucumber based automation suite designed for validating key modules of a flight booking web application. The project follows best practices such as Page Object Model (POM), Excel-driven test data, and detailed reporting with failure screenshots.

---

## 📁 Project Modules

| Module           | Description                                                 |
|------------------|-------------------------------------------------------------|
| **Login**        | Verifies login functionality using credentials from config |
| **Flight Enquiry** | Validates enquiry form input and reset functionality       |
| **Flight Booking** | Tests ticket booking with positive & negative test data    |
| **Flight Search** | Validates flight search by number, name, and type          |

---

## 🛠 Tech Stack

- **Language**: Java 11  
- **Automation**: Selenium WebDriver  
- **Test Framework**: Cucumber (BDD, Gherkin syntax)  
- **Build Tool**: Maven  
- **Excel Integration**: Apache POI  
- **Screenshot on Failure**: Captured via Hooks and attached to reports  

---

## 📊 Test Data Format

Test data is stored in an Excel file located at:

src/test/resources/Card.xlsx

example:

| Sheet Name               | Purpose                      |
|--------------------------|------------------------------|
| `flight_booking_details` | Data for booking validations |
| `flight_search_details`  | Data for search scenarios    |

---

## 📸 Screenshot Capture

Screenshots are automatically captured and saved when:
- A test scenario fails
- Stored at: `target/screenshots/`
- Embedded in HTML reports

---

## 🤝 Contributors

| Contributor       | Modules Worked On                                  |
|-------------------|-----------------------------------------------------|
| **Subhanjan Dutta** | Login, Flight Enquiry and Flight Booking Module        |
| **Aryan Kajal**     | Flight Search Module, Jira Setup, GitHub Setup      |

---

## 📌 Jira Integration

All test scenarios map to corresponding Jira stories or tasks, enabling full traceability from test to requirement.

---

## 🔒 License

This project is intended for educational and learning purposes only.
