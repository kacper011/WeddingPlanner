# 💍 Wedding Planner

Wedding Planner is a comprehensive web application designed to help engaged couples organize their wedding with ease. From guest management to budgeting, seating arrangements, and even fun wedding games — this tool covers all the essentials to keep everything on track for the big day.

## ✨ Features

### 🔐 User Account
- Create an account and log in securely (Spring Security).
- Upon registration, users receive a welcome email.

### 👥 Guest List Management
- Add new wedding guests.
- Track guest responses: attending / not attending.
- Three automatic lists:
  - **Confirmed Guests**
  - **Unconfirmed Guests**
  - **After Party Guests**
- Search guests by category/tags added during creation.
- Export guest list to PDF.

### 🪑 Seating Arrangement
- Create and customize tables (round or rectangular).
- Assign guests to specific tables.
- Interactive table layout map for visual placement.

### 🕒 Countdown
- Enter bride's and groom's names along with the wedding date.
- Real-time countdown showing how many days remain until the wedding.

### 🌦️ Weather Forecast
- 10-day weather forecast to plan ahead for the big day.

### 💸 Budget & Expenses
- Add and track wedding expenses with descriptions and amounts.
- View total wedding cost.
- Export expenses summary to PDF.

### 🗓️ Event Calendar
- Add custom events and appointments.
- Email notifications sent to the user 2 days before each event.

### 🎉 Games & Fun
- Quiz about the couple for guests.
- Select questions and generate a printable PDF quiz.

## 🛠️ Technologies Used

### Backend
- Java 21
- Spring Boot 3.3.2
- Spring Security
- Spring Data JPA
- Spring Validation
- Spring Mail (Email sending)
- Spring Web (MVC)
- Lombok

### Frontend
- Thymeleaf (Template Engine)
- Thymeleaf Extras Spring Security

### Database
- MySQL
- MySQL Connector/J

### PDF Export
- OpenPDF (LibrePDF)

### Testing
- JUnit Jupiter
- Mockito
- Spring Security Test

### Infrastructure
- Docker
## 📦 Setup & Run

To run the project locally with Docker:

```bash
docker-compose up --build
