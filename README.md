# Brokage Firm Backend API

This project implements a backend API for a brokerage firm to manage stock orders. The API allows employees to create, list, and delete stock orders for their customers. The project is built using **Java 17**, **Spring Boot**, **H2 Database**, and **Swagger**.

## Table of Contents
- [Overview](#overview)
- [Technologies Used](#technologies-used)
- [API Endpoints](#api-endpoints)
  - [Create Order](#create-order)
  - [List Orders](#list-orders)
  - [Delete Order](#delete-order)
  - [List Assets](#list-assets)
- [Authentication](#authentication)
- [Database Schema](#database-schema)
  - [Asset](#asset)
  - [Order](#order)
- [Running the Project](#running-the-project)
- [Unit Tests](#unit-tests)
- [Bonus Features](#bonus-features)
  - [1. Customer Table and Authorization per Customer](#1-customer-table-and-authorization-per-customer)
  - [2. Admin User Endpoint to Match Orders](#2-admin-user-endpoint-to-match-orders)
- [License](#license)

## Overview

The backend API for the brokerage firm supports operations for stock orders and asset management. It includes endpoints to:
- Create orders for customers
- List orders by customer and date range
- Delete pending orders
- List assets for a customer

**Requirements:**
- All endpoints require authentication with an **admin user** and **password**.
- All data is stored in an H2 database with entities like `Asset` and `Order`.
- Orders can only be placed against the **TRY** asset (a virtual currency for trading).

## Technologies Used
- **Java 17**: Programming language
- **Spring Boot**: Framework for building the backend
- **H2 Database**: In-memory database for storage
- **Swagger**: API documentation and testing
- **JUnit**: Unit testing framework

## API Endpoints

### Create Order
**POST /orders**

Creates a new order for a given customer with details like:
- `customerId`: The ID of the customer
- `assetName`: Stock the customer wants to buy or sell
- `orderSide`: BUY or SELL
- `size`: Number of shares
- `price`: Price per share

The order is created with a **PENDING** status.

### List Orders
**GET /orders**

Lists orders for a given customer within a specified date range:
- `customerId`: The ID of the customer
- `startDate`: Start of the date range
- `endDate`: End of the date range

### Delete Order
**DELETE /orders/{id}**

Deletes a pending order. Orders with statuses other than **PENDING** cannot be deleted.

### List Assets
**GET /assets**

Lists assets for a given customer, including their `size` and `usableSize`.

## Authentication

The application has a basic authentication system that requires an **admin user** and **password** for access to all endpoints, please edit application.properties file to change the default credentials.

## Database Schema

### Asset
- `customerId`: The ID of the customer
- `assetName`: The name of the asset (e.g., stock name or TRY)
- `size`: The total number of shares the customer owns
- `usableSize`: The number of shares available for trading

### Order
- `customerId`: The ID of the customer placing the order
- `assetName`: The name of the stock the customer wants to buy or sell
- `orderSide`: The type of order (BUY or SELL)
- `size`: The number of shares in the order
- `price`: The price per share
- `status`: The status of the order (PENDING, MATCHED, or CANCELED)
- `createDate`: The date and time when the order was created

## Running the Project

1. **Clone the repository**:
    ```bash
    git clone https://github.com/myrepo/brokage.git
    cd brokage
    ```

2. **Build and run the project**:
    You can run the project using Maven Wrapper (`mvnw`):
    ```bash
    ./mvnw spring-boot:run
    ```
    This will start the Spring Boot application on `http://localhost:8080`.

3. **Swagger UI**:  
    Access the Swagger UI to view and test the API at:  
    `http://localhost:8080/swagger-ui.html`.

## Unit Tests

1. **Run the tests**:  
    Unit tests are written to verify the correctness of the business logic. You can run the tests using Maven:
    ```bash
    ./mvnw test
    ```
    Tests are implemented using **JUnit 5**.



