# 🏦 Digital Banking System API

A robust, enterprise-level RESTful digital banking system built with **Spring Boot 3**. The system simulates real-world banking operations including secure authentication, multi-account management, credit lifecycle, automated notification engines, and admin back-office management.

---

## 🚀 Key Features

- **🔐 Enterprise Security:** JWT-based stateless authentication with RBAC (Role-Based Access Control) for `USER` and `ADMIN`.
- **👤 User Management:** Secure registration, authentication, profile management, and account binding.
- **💳 Account & Card Operations:** Multi-status accounts (ACTIVE, BLOCKED, CLOSED), card generation (PAN, CVV, Expiry), masking, and card replacement flows.
- **💸 Financial Transactions:** Fast deposit, withdrawal, and inter-account money transfers with validation rules.
- **📊 Credit & Loan Engine:** Complete loan lifecycle management (take credit, calculate monthly payments with interest rates, pay off loans, and track remaining balances).
- **🔔 Real-time & Automated Notifications:** Dynamic notification system alerting both users and admins on key events (payments, transactions, loan updates).
- **⏱️ Automated Task Scheduling:** Background Spring `@Scheduled` tasks to process payment dues, trigger timely payment alerts, and maintain loan statuses.
- **🛡️ Admin Back-Office Module:** Dedicated administrative oversight for user monitoring, system notifications, and transaction tracking.
- **⚡ Robust Error Handling:** Centralized `GlobalExceptionHandler` with standardized API error response payloads.

---

## 🏗️ Tech Stack

- **Core Framework:** Java 17+, Spring Boot 3.x
- **Security:** Spring Security, JWT (JSON Web Tokens)
- **Data & Persistence:** Spring Data JPA, Hibernate, PostgreSQL / MySQL
- **Scheduling:** Spring Task Scheduling (`@EnableScheduling`)
- **Utilities & Tools:** Lombok, MapStruct (Mappers), Maven
- **Architecture:** Layered Architecture (Controller → Service → Repository) with DTO Pattern

---

## 🏦 Core Modules Breakdown

### 🔐 1. Authentication & Security Module
- JWT token issuance and validation.
- Role-based authorization (`ROLE_USER`, `ROLE_ADMIN`).
- Password encryption using BCrypt.

### 💳 2. Account & Card Management
- Automatic default account creation upon user registration.
- Account status lifecycle management and ownership validation.
- Secure card operations: dynamic masking controls, limits, activation, blocking, and replacement.

### 💸 3. Transaction Engine
- Atomicity and consistency guaranteed using Spring `@Transactional`.
- Deposit and withdrawal operations.
- Inter-account fund transfers with strict balance checks.
- Full transaction history with status tracking (`PENDING`, `SUCCESS`, `FAILED`).

### 📊 4. Credit & Loan Management System
- Custom credit application and eligibility checks.
- Automated monthly payment calculation with annual interest rates.
- Loan repayment flow with remaining balance deduction and auto-closing (`PAID_OFF`).

### 🔔 5. Notification & Event Engine
- User notifications for transaction alerts, loan approvals, and repayment reminders.
- Global Admin Broadcasts (`createNotificationForAdmins`) for system-wide monitoring.
- Read/Unread status management and count metrics.

### ⏱️ 6. Automated Scheduler Service
- Asynchronous background task execution using `@Scheduled`.
- Auto-detection of upcoming credit payment deadlines.
- Automatic dispatching of due payment alerts to active borrowers.

---

## 🔒 Business Rules & Safeguards

- Transactions are strictly allowed only for `ACTIVE` accounts.
- Zero/Negative balances strictly block transfers and withdrawals.
- Context-aware authorization checks guarantee users can only access their own accounts and cards.
- Database operations are bound within transactional context to prevent partial state updates.

---

## 📈 Future Roadmap & Planned Improvements

- [ ] **Microservices Architecture:** Decompose into independent services (Auth Service, Account Service, Notification Service).
- [ ] **Redis Caching:** Integrate Redis for fast session management, token blacklisting, and caching user profiles.
- [ ] **RabbitMQ / Kafka Integration:** Asynchronous event-driven architecture for high-throughput notification processing.
- [ ] **Third-Party Payment Gateway Integration:** Simulate external card-to-card transfers and online payment processors.
- [ ] **Docker & CI/CD Pipeline:** Containerize application with Docker Compose and set up GitHub Actions for automated testing & deployment.
- [ ] **Swagger / OpenApi 3 Documentation:** Interactive API documentation for seamless frontend integration.

---

## 📂 Project Status

**Active Development** — Continuously evolving with enterprise banking features, performance optimizations, and security enhancements.
