# JoyLeeBook

**JoyLeeBook** is a web-based novel reading platform developed using **Java Servlet MVC**.  
The goal of this project is to provide a modern, maintainable, and extensible web application for managing and reading stories online.

---

## Technologies Used

| Component        | Technology                      |
|------------------|---------------------------------|
| **Language**     | Java (Servlet 6 / Jakarta EE)   |
| **Frontend**     | HTML5, Tailwind CSS, JSP, JSTL  |
| **Database**     | SQL Server 2019                 |
| **Web Server**   | Apache Tomcat 10.1              |
| **IDE**          | NetBeans 17                     |
| **Build Tool**   | Maven                           |
| **Architecture** | MVC (Model - View - Controller) |

---

## Project Structure


---

## Setup and Run Instructions

### 1. System Requirements

- **JDK:** 17
- **Apache Tomcat:** 10.1+
- **SQL Server:** 2019 or higher
- **NetBeans:** 17 (or any IDE that supports Maven)

---

### 2. Database Configuration

1. Open **SQL Server Management Studio (SSMS)**.
2. Create a new database:
   ```sql
   CREATE DATABASE JoyLeeBookDB;
3. Import the JoyLeeBookDB.sql file (if available) containing sample data.

4. Update database connection in:

---

### 3. Run the Application
1. Open the project in **NetBeans**.
2. Right-click on the project and select **Run**.
3. Access the application at `http://localhost:8080/JoyLeeBook`.
4. Log in using the default credentials:
   - **Username:** admin
   - **Password:** admin123
   - (You can change these credentials after logging in.)
    
---

### 4. Features
- User Registration and Authentication
- Browse and Search Stories
- Read Stories Online
- Admin Panel for Story Management
- User Profile Management
- User Comments and Ratings
- Author Dashboard for Story Submission
- Etc.

---

### 5. User Interface
- Built with Tailwind CSS
- Minimalistic design focused on readability
- Optional light/dark theme support

---
### License
This project is created for educational and research purposes.
Please provide credit if you use or modify any part of this codebase.