# Expanded MotorPH Payroll System

## Project Overview

The **Expanded MotorPH Payroll System** is a Java Swing-based desktop application developed as part of the **MO-IT113 Advanced Object-Oriented Programming** course.

The system provides a complete payroll and employee management solution that supports:

* Employee Management
* Attendance Tracking
* Payroll Processing
* Leave Management
* Payroll History Management
* User Account Administration
* Role-Based Access Control
* Two-Factor Authentication (2FA)
* Dashboard with Visual Data Analytics
* JasperReports-Based Report Generation

Unlike the original version that relied on CSV and text-file storage, the expanded system uses a **MySQL database with JDBC persistence**, providing improved data integrity, scalability, and maintainability.

The application follows Object-Oriented Programming principles and implements a layered architecture consisting of GUI, Service, DAO, and Model layers, with the DAO layer implemented against interfaces to support dependency inversion and testability.

---

# System Architecture

The system follows a layered architecture to maintain separation of concerns and support future scalability.

### GUI Layer

Handles:

* User interface
* User interaction
* Input collection
* Display of results
* Dashboard chart rendering (JFreeChart)

### Service Layer

Handles:

* Business logic
* Validation
* Payroll processing
* Authentication workflows
* Report orchestration

### DAO Layer

Handles:

* JDBC database access
* SQL queries
* CRUD operations
* Data persistence

Every DAO implements a corresponding interface (`IAccountDAO`, `IAttendanceDAO`, `ICompensationDAO`, `ICredentialDAO`, `IDepartmentDAO`, `IEmployeeDAO`, `ILeaveBalanceDAO`, `ILeaveDAO`, `IOvertimeDAO`, `IPayrollDAO`), so service-layer and UI code depend on a contract rather than a concrete class. This makes DAOs mockable for unit testing and lets an implementation be swapped without changes to callers.

### Model Layer

Represents business entities such as:

* Employee
* Attendance
* Payroll
* Payslip
* Leave
* Payroll Record
* Account
* Department
* Role

### Database Layer

MySQL relational database consisting of a normalized schema containing:

* Employee records
* Payroll records
* Attendance logs
* Leave requests
* User credentials
* Roles and permissions
* Deduction records

The architecture flow is:

GUI → Service → DAO (via interface) → MySQL Database

The GUI layer never accesses the database directly.

---

# Object-Oriented Programming Concepts Applied

The system demonstrates several Advanced OOP concepts:

### Encapsulation

All entity attributes are private and accessed through methods.

### Inheritance

Specialized employee and attendance-related classes extend common base classes.

### Polymorphism

Attendance summary generation uses factory-based polymorphism.

### Abstraction

DAO and Service layers abstract persistence and business operations from the user interface. Every DAO is accessed through an interface rather than a concrete class.

### Composition

Payroll records aggregate employees, deductions, attendance summaries, and generated payslips.

### Factory Pattern

`AttendanceSummaryFactory` creates role-specific attendance summaries.

### Strategy Pattern

Payroll calculations are implemented using interchangeable calculator strategies (`StandardPayrollCalculator`, `OvertimePayrollCalculator`) behind the `PayrollCalculator` interface.

### Dependency Inversion

DAO interfaces decouple the service layer from concrete persistence implementations, and `DeductionCalculator` delegates bracket-table lookups to a single shared `GovernmentContributionCalculator` rather than each caller maintaining its own copy of the rates.

### Layered Architecture

Strict separation between presentation, business logic, persistence, and data models.

---

# Major System Features

## Authentication and Security

### Login System

* Username/password authentication
* Role-based access control

### Two-Factor Authentication (2FA)

* OTP verification during login
* Two-step authentication process

### Password Security

* PBKDF2 password hashing
* Automatic migration of legacy plaintext passwords
* Secure credential storage

---

## Employee Management

* Add Employee
* Update Employee
* Delete Employee
* Search Employees
* View Employee Information
* Department Assignment
* Position Assignment

---

## Attendance Management

* Time In
* Time Out
* Attendance Tracking
* Attendance Summaries
* Role-Based Attendance Access
* Attendance Record Maintenance

---

## Payroll Management

### Payroll Processing

The system supports:

* Payroll computation
* Overtime computation
* Government-mandated deduction computation
* Payroll record persistence
* Payroll deletion
* Payroll history retrieval

### Payroll Components

* Basic Salary
* Rice Subsidy
* Phone Allowance
* Clothing Allowance
* Overtime Pay
* Government Deductions
* Tax Deductions

### Government-Mandated Deductions

Deductions are computed using the official Philippine government bracket and tiered tables rather than flat percentages:

| Deduction | Basis |
|---|---|
| **SSS** | Employee share = 5% of Monthly Salary Credit (MSC). MSC is bracketed in ₱500 steps from a ₱5,000 floor to a ₱35,000 ceiling. (RA 11199, SSS Circular 2024-006) |
| **PhilHealth** | 5% of monthly salary, split 2.5% employee / 2.5% employer. Salary floor ₱10,000, ceiling ₱100,000. (RA 11223, Universal Health Care Act) |
| **Pag-IBIG (HDMF)** | 1% of salary if ≤ ₱1,500, otherwise 2%. Capped at ₱200/month based on the ₱10,000 Maximum Fund Salary. (HDMF Circular No. 460) |
| **Withholding Tax** | TRAIN Law (RA 10963) monthly graduated bracket table, applied to *taxable income* (gross pay minus the SSS, PhilHealth, and Pag-IBIG employee shares) — not raw gross pay. |

These calculations are centralized in `GovernmentContributionCalculator`, the single source of truth for bracket rates. `CompensationDAO` and `DeductionCalculator` both delegate to it, so the payroll UI and any future reporting feature always compute the same deduction amounts.

### Payroll History

A dedicated Payroll History module allows users to:

* Browse previous payroll runs
* View payroll details
* Delete payroll records
* Review historical payroll information

---

## Leave Management

* Submit Leave Requests
* View Leave History
* Approve Leave Requests
* Reject Leave Requests
* Leave Status Tracking

---

## Account Management

* Add Accounts
* Edit Accounts
* Delete Accounts
* Assign Roles
* Manage Credentials

---

## Dashboard

The Dashboard module provides an at-a-glance visual summary of payroll and workforce data, rendered using **JFreeChart 1.5.4**.

* Visual charts summarizing payroll and attendance data
* Data pulled through the existing Service/DAO layers, so the dashboard follows the same layered architecture as the rest of the system
* Addresses Milestone 2 review feedback, which noted that `Dashboard.java` existed but lacked a charting library and visual data

---

# Report Generation

The system uses **JasperReports** for PDF report generation.

### Employee Reports

#### Payslip Report

Displays:

* Employee information
* Payroll period
* Earnings
* Deductions
* Net Pay

### HR Reports

#### Department Payroll Summary Report

Displays:

* Department payroll information
* Employee payroll summaries
* Payroll totals

### Attendance Reports

#### Employee Timecard Report

Displays:

* Attendance records
* Hours worked
* Attendance summaries

Reports are generated through the Reports module and exported as PDF files.

---

# Database Implementation

The system uses:

* MySQL Database
* JDBC Connectivity
* DAO Pattern, accessed through interfaces
* Normalized Relational Schema

Key database entities include:

* employees
* payroll
* payroll_history
* attendance
* leave_requests
* credentials
* roles
* deductions
* deduction_types
* compensation
* compensation_details

Government contribution rates (SSS, PhilHealth, Pag-IBIG, withholding tax brackets) are computed in code via `GovernmentContributionCalculator` rather than looked up from database bracket tables, ensuring a single, testable, verifiable source for statutory deduction amounts.

The final schema contains approximately **21 normalized tables** supporting the complete payroll workflow.

---

# Testing

## Automated Unit Testing

The project includes automated JUnit tests covering:

### Configuration Layer

* Database connectivity

### DAO Layer

* Payroll persistence
* Payroll deletion
* Database CRUD operations

### Model Layer

* Attendance Summary Factory
* Role-based object generation

### Business Logic Layer

* Deduction calculations
* Payroll calculations

### Service Layer

* Payroll History services
* Business rule validation

### Test Results

* Tests Executed: 21
* Failures: 0
* Errors: 0
* Skipped: 0
* Build Status: SUCCESS

The test suite validates payroll calculations, database persistence, deduction integrity, and attendance summary generation.

---

# Technologies Used

### Programming Language

* Java 21

### User Interface

* Java Swing

### Database

* MySQL

### Persistence

* JDBC

### Data Visualization

* JFreeChart 1.5.4

### Reporting

* JasperReports

### Testing

* JUnit 5
* Maven

### Security

* PBKDF2 Password Hashing
* OTP-Based Two-Factor Authentication

---

# Development Status

* **Milestone 2:** Reviewed and approved — architecture, OOP design (abstract `Person` with `getDisplayName()` overloads, `PayrollCalculator` strategy interface, `AttendanceSummaryFactory`), and the test planning document were all cited as strong. The only noted gap, the Dashboard lacking a charting library and visual data, has since been addressed with JFreeChart 1.5.4.
* **Current phase:** Preparing for the Terminal Assessment. 

---

# Repository

GitHub Repository:

https://github.com/iamdayangg/AOOP-Warriors-TA

---

# Development Team

## AOOP Warriors

Section: S2101

### Members

* Diane Ababao
* Patrick Louis Pineda
* Emilyn Maguad
* Jeah May Pareja
* Noeme Escarlan

---

# Course Information

**Course:** MO-IT113 – Advanced Object-Oriented Programming

This project was developed as an academic requirement demonstrating the application of advanced object-oriented design principles, layered architecture, database persistence, software testing, security implementation, and reporting integration.