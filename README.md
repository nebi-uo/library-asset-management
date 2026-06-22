# Library Asset Management System

A desktop-based Library Asset Management System developed using **JavaFX** and **SQLite**.  
The application provides a graphical interface for managing library users, books, borrowing transactions, reservations, and fines.

---

## Project Overview

The Library Asset Management System is designed to automate common library operations such as:

- User authentication
- Book management
- Borrowing and returning books
- Tracking available copies
- Managing reservations
- Calculating and tracking fines
- Viewing library statistics

The system uses a local SQLite database, making it easy to run without requiring an external database server.

---

## Technologies Used

### Programming Language
- Java 17

### User Interface
- JavaFX 21

### Database
- SQLite

### Build Tool
- Apache Maven

### Architecture
- MVC (Model-View-Controller)

---

# Features

## 1. User Authentication

The system provides role-based login for different users:

### Supported Roles

- Admin
- Librarian
- Member

Each role has access to different system operations depending on permissions.

---

## 2. Dashboard

After successful login, users are redirected to a dashboard containing:

- Welcome message
- Total number of books
- Total borrowed books
- Total registered members
- Overdue book information

---

## 3. Book Management

Librarians and administrators can manage library assets.

Available operations:

- Add new books
- View books
- Update book information
- Remove books
- Track available copies

Book information includes:

- Title
- Author
- ISBN
- Category
- Total copies
- Available copies

---

## 4. Borrowing System

Members can borrow available books.

The borrowing system:

- Checks book availability
- Updates available copies automatically
- Creates transaction records
- Assigns borrowing and due dates

---

## 5. Returning System

Returned books are processed by:

- Updating transaction records
- Restoring available copies
- Calculating overdue fines when required

---

## 6. Reservation System

Users can reserve unavailable books.

Reservation statuses:

- Pending
- Fulfilled
- Cancelled

---

## 7. Fine Management

The system supports tracking library fines.

Features include:

- Fine amount storage
- Payment status tracking
- Linking fines with borrowing transactions

---

# Database Structure

The application uses SQLite with the following main tables:

## Users

Stores account information.

Fields:

- user_id
- username
- password_hash
- role
- email

---

## Books

Stores library inventory.

Fields:

- book_id
- title
- author
- isbn
- category
- total_copies
- available_copies

---

## Transactions

Stores borrowing records.

Fields:

- transaction_id
- user_id
- book_id
- borrow_date
- due_date
- return_date

---

## Reservations

Stores book reservation information.

Fields:

- reservation_id
- user_id
- book_id
- reservation_date
- status

---

## Fines

Stores overdue penalties.

Fields:

- fine_id
- transaction_id
- fine_amount
- is_paid

---

# Default Login Accounts

The database includes sample accounts for testing.

| Username | Password | Role |
|----------|----------|------|
| system_admin | securepass1 | Admin |
| librarian_desk | securepass2 | Librarian |
| student_user | securepass3 | Member |

---

# Installation Guide

## Requirements

Before running the project, install:

- Java Development Kit (JDK) 17 or higher
- Apache Maven
- Any Java IDE (recommended: IntelliJ IDEA or Eclipse)

---

## Running the Application

### 1. Clone or extract the project

Place the project folder on your computer.

---

### 2. Open the project

Open the project using a Java IDE or terminal.

---

### 3. Build the project

Run:

```bash
mvn clean install

---

### 4. Start the application

mvn javafx:run