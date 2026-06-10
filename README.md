# digital-banking-api

A RESTful digital banking system built with Spring Boot that simulates core banking operations including user management, account handling, card management, and secure money transfer functionality.

## 🚀 Features

- User registration and authentication
- Account creation with automatic default account setup
- Account status management (ACTIVE, BLOCKED, CLOSED)
- Card management (create, block, activate, replace)
- Secure money transfer between accounts
- Deposit and withdrawal operations
- Transaction history tracking with status support (PENDING, SUCCESS, FAILED)
- Business rule validations (balance checks, account status checks, ownership rules)

## 🏦 Core Modules

### 👤 User Management
- Register and login users
- Password encryption using BCrypt
- User status handling

### 💳 Account Management
- Automatic account creation on registration
- Multi-currency support
- Account status lifecycle management

### 💳 Card Management
- Generate card number, CVV, expiry date
- Card activation, blocking, and replacement
- Card limits per account

### 💸 Transaction System
- Deposit & withdrawal operations
- Transfer between accounts
- Transaction status tracking
- Full transaction history

## 🛠️ Tech Stack

- Java 17+
- Spring Boot
- Spring Data JPA
- Hibernate
- PostgreSQL / MySQL
- Lombok
- Maven

## 📌 Architecture

- Layered architecture (Controller → Service → Repository)
- DTO pattern for request/response separation
- Mapper layer for entity conversion
- Transaction management with Spring `@Transactional`

## 🔒 Business Rules

- Only ACTIVE accounts can perform transactions
- Insufficient balance prevents withdrawal/transfer
- Card ownership validation enforced
- Transaction status tracking for reliability

## 📈 Future Improvements

- JWT authentication & Spring Security integration
- Role-based access control (USER / ADMIN)
- Microservice architecture migration
- Notification system (email/SMS)
- Audit logging system

## 📂 Project Status

This project is currently under active development and being improved with additional banking features and security enhancements.
