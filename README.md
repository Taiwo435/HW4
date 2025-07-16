# JavaFX Role-Based Q&A Management System

This is a JavaFX desktop application built to manage users, questions, and answers with role-based access. It features a login system, profile-based navigation, form validations, and database-backed CRUD operations. Designed with modularity and clarity in mind, the system is suitable for educational use, assignments, or as a boilerplate for more complex JavaFX applications.

---

## Features

- **User Authentication**
  - Login and account setup screens
  - Password strength validation
  - Username recognition

- **Role-Based Interfaces**
  - Admin, Staff, Instructor, Reviewer, and Student roles
  - Each role has a distinct home page with role-specific functionalities

- **Question & Answer Management**
  - Create, edit, update, and delete questions and answers
  - Evaluate and review responses

- **Messaging System**
  - Users can send and receive messages
  - Reviewer requests and communication between roles

- **Admin Dashboard**
  - View all users
  - Manage roles and account statuses
  - See contribution summaries

- **Form Validators**
  - Email and password format checkers
  - Input field validation

- **JUnit Testing**
  - Includes test classes such as `JunitTest.java` and `TestException.java`

---

## Project Structure

```
JavaFX-Question-Automation-Platform/
│
├── src/
│   ├── application/
│   │   ├── WelcomeLoginPage.java           # Initial screen
│   │   ├── SetupLoginSelectionPage.java
│   │   ├── UserLoginPage.java
│   │   ├── SetupAccountPage.java
│   │   ├── AdminHomePage.java
│   │   ├── Staff.java
│   │   ├── Instructor.java
│   │   ├── Reviewer.java
│   │   ├── Student.java
│   │   ├── UserHomePage.java
│   │   ├── InvitationPage.java
│   │   ├── MessagePage.java
│   │   ├── QuestionPage.java
│   │
│   │   ├── EmailEvaluator.java
│   │   ├── PasswordEvaluator.java
│   │   ├── UserNameRecognizer.java
│   │   ├── RoleSetting.java
│   │   └── ContributionSummary.java
│   │
│   │   ├── User.java
│   │   ├── Role.java
│   │   ├── question.java
│   │   ├── questions.java
│   │   ├── answer.java
│   │   ├── answers.java
│   │   ├── reviews.java
│   │   └── Time.java
│   │
│   │   ├── updateAnswer.java
│   │   ├── UpdateQuestion.java
│   │   └── setPassword.java
│   │
│   │   ├── JunitTest.java
│   │   └── TestException.java
│   │
│   └── databasePart1/
│       └── DatabaseHelper.java             # MySQL connection and query handling
│
├── Junit docs.pdf
├── staff docs.pdf
├── staffTestable docs.pdf 
└── README.md
```

---

## Tech Stack

- **Java** (JDK 17+ recommended)
- **JavaFX** for GUI
- **MySQL** or SQLite (via `DatabaseHelper.java`)
- **JUnit** for unit testing

---

## Getting Started

### Prerequisites

- Java SDK installed (version 11 or higher)
- JavaFX SDK
- MySQL server (or update DB code to use SQLite)
- An IDE like IntelliJ, Eclipse, or VS Code

### Installation & Running

## Getting Started

### Installation & Usage

1. **Clone the repository**

```bash
git clone https://github.com/Taiwo435/JavaFX-Question-Automation-Platform.git
cd JavaFX-Question-Automation-Platform
```

2. Open `DatabaseHelper.java`  
   - Configure the **JDBC URL**, **username**, and **password** according to your MySQL setup.

3. Create a new MySQL database  
   - Run the necessary `CREATE TABLE` SQL statements for the following entities:  
     - `User`  
     - `Question`  
     - `Answer`  
     - etc.

4. Ensure your MySQL server is **running and accessible**.

5. Clear existing test data  
   - Drop any pre-existing users/admins in the database to avoid conflict.

6. Run the application  
   - Launch `StartCSE360.java` to create your initial admin account (email, password, username).  
   - Use this admin account to generate codes and assign roles when creating new users.

---

##  Screencasts

- [ScreenCast 1](https://drive.google.com/file/d/1pO3w092p7j_WEwiRdGEcwc5zc2giEiSs/view?usp=sharing): Overview of the application code, Junit code and Junit tests.
- [ScreenCast 2](https://drive.google.com/file/d/1WthSxB49bPkbKSj7sZtBZlvlEkh92FuE/view?usp=sharing): Demonstrates UI walkthrough and shows how user stories work in the interface.



