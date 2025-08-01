# DigiWallet

A digital wallet application with customer and employee management, wallet functionality, and Keycloak integration for authentication.

## Table of Contents
- [Overview](#overview)
- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Features](#features)
- [Getting Started](#getting-started)
- [API Documentation](#api-documentation)
- [Authentication](#authentication)
- [Usage Examples](#usage-examples)

## Overview

DigiWallet is a comprehensive digital wallet solution that allows customers to manage their finances through a secure and user-friendly platform. The application provides wallet functionality, customer and employee management, and secure authentication through Keycloak.

## Tech Stack

DigiWallet is built using the following technologies:

- **Java 21**: Latest LTS version of Java providing modern language features and improved performance
- **Spring Boot 3.5.0**: Framework for building production-ready applications with minimal configuration
- **Spring Modulith 1.4.1**: Extension of Spring Boot that supports modular monolith architecture
- **Spring Security**: Provides authentication and authorization capabilities
- **Spring Data JPA**: Simplifies data access layer implementation
- **Keycloak 23.0.2**: Open source Identity and Access Management solution
- **H2 Database**: In-memory database for development and testing
- **Swagger/OpenAPI**: API documentation and testing
- **Docker**: Containerization for easy deployment
- **Maven**: Build and dependency management

### Why Spring Modulith?

Spring Modulith was chosen for this application because it provides:

1. **Modular Architecture**: Enables organizing code into well-defined, loosely coupled modules
2. **Monolith Deployment**: Maintains the simplicity of deploying a single application
3. **Clear Boundaries**: Enforces module boundaries and dependencies
4. **Event-Based Communication**: Facilitates communication between modules through events
5. **Testing Support**: Provides tools for testing modules in isolation
6. **Evolutionary Design**: Allows for future migration to microservices if needed

This approach gives us the benefits of a modular architecture while avoiding the complexity of a distributed microservices system.

## Architecture

DigiWallet follows a modular monolith architecture with the following modules:

1. **Customer Module**: Manages customer information and operations
2. **Employee Module**: Handles employee data and administrative functions
3. **Wallet Module**: Core functionality for wallet operations, transactions, deposits, and withdrawals
4. **Keycloak Adapter**: Integration with Keycloak for authentication and user management
5. **Common Module**: Shared components, security configurations, and utilities

Each module follows a clean architecture pattern with:
- **Application Layer**: Services that orchestrate business logic
- **Domain Layer**: Entities and business rules
- **Infrastructure Layer**: Repositories and external service implementations
- **Interface Layer**: Controllers and DTOs for API endpoints

## Features

- **Customer Management**: Register and manage customer accounts
- **Employee Management**: Administrative functions for employees
- **Wallet Operations**:
  - Create wallets
  - Deposit funds
  - Withdraw funds
  - View transaction history
- **Secure Authentication**: JWT-based authentication via Keycloak
- **Role-Based Access Control**: Different permissions for customers and employees

## Getting Started

### Prerequisites
- Docker and Docker Compose
- Java 21 (for development)
- Maven (for development)

### Running with Docker

The application can be run using Docker Compose:

```bash
docker-compose up -d
```

This will start:
- The DigiWallet application on port 8081
- Keycloak authentication server on port 8080

### Development Setup

1. Clone the repository
2. Build the application:
   ```bash
   ./mvnw clean install
   ```
3. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

## API Documentation

The API documentation is available through Swagger UI at:
```
http://localhost:8081/swagger-ui.html
```

This provides an interactive interface to explore and test all available endpoints.

## Authentication

DigiWallet uses Keycloak for authentication with JWT tokens:

1. **Login**: POST to `/api/auth/login` with username and password
2. **Token Refresh**: POST to `/api/auth/refresh` with a refresh token
3. **Authorization**: Include the JWT token in the Authorization header for protected endpoints:
   ```
   Authorization: Bearer <your-jwt-token>
   ```

### Keycloak Configuration

The Keycloak server is configured with the following features:
- Scripts enabled (`--features=scripts-enabled`) to support script-based authentication flows
- Realm import from `.realm-dump/realm-export.json`
- Two user groups: CUSTOMER and EMPLOYEE with different permissions

### Initial Admin User

The system comes with an initial admin user with the following credentials:
- **Username**: admin
- **Password**: admin
- **Group**: EMPLOYEE

This admin user has full administrative privileges and can be used to create other users, including customers and employees.

### Customer Authentication

When a customer is created, a Keycloak user is automatically created with:
- **Username**: The customer's TCKN (Turkish Citizen ID Number)
- **Password**: admin (default)
- **Group**: CUSTOMER
- **Attributes**: The customer's ID is stored as a "customerId" attribute

To authenticate as a customer:
1. Create a customer using the admin user (see [Customer Registration](#customer-registration))
2. Use the customer's TCKN as the username and "admin" as the password to get an access token:
   ```bash
   curl -X POST http://localhost:8081/api/auth/login \
     -H "Content-Type: application/json" \
     -d '{
       "username": "12345678901",  # Replace with the customer's TCKN
       "password": "admin"
     }'
   ```
3. Use the returned JWT token in the Authorization header for subsequent requests

### User Groups and Privileges

The application implements role-based access control with two main user groups:

#### CUSTOMER Group
Customers have access to manage their own resources:
- **Customer Management**: View their own customer information
- **Wallet Operations**:
  - Create wallets for themselves
  - View their own wallets and transaction history
  - Deposit funds to their own wallets
  - Withdraw funds from their own wallets

#### EMPLOYEE Group
Employees have administrative privileges:
- **Customer Management**:
  - View any customer's information
  - Create new customer accounts
- **Employee Management**:
  - View employee information
  - Create new employee accounts
- **Wallet Operations**:
  - Create wallets for any customer
  - View any customer's wallets and transaction history
  - Deposit funds to any wallet
  - Withdraw funds from any wallet
  - Approve transactions

## Usage Examples

### Customer Registration

```bash
curl -X POST http://localhost:8081/api/customers \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John",
    "surname": "Doe",
    "tckn": "12345678901"
  }'
```

### Authentication

```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "johndoe",
    "password": "password123"
  }'
```

### Create Wallet

```bash
curl -X POST http://localhost:8081/api/wallets \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <your-jwt-token>" \
  -d '{
    "currency": "USD"
  }'
```

### Deposit Funds

```bash
curl -X POST http://localhost:8081/api/wallets/{walletId}/deposit \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <your-jwt-token>" \
  -d '{
    "amount": 100.00,
    "description": "Initial deposit"
  }'
```
