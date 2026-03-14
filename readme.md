# Card Management System with Stripe Integration

A Spring Boot application designed to manage credit/debit card records, featuring real-time integration with the Stripe API for secure payment processing. This project demonstrates industry-standard practices in RESTful API design, external service integration, and high-traceability logging.

---

## 🚀 Key Features

- **Full CRUD Operations**: Efficient management of card records using a layered architecture (Controller -> Service -> Repository).
- **Pagination & Sorting**: Optimized data retrieval implemented via Spring Data JPA for large datasets.
- **Stripe Integration (Nested Call)**: Real-time communication with the **Stripe API** to create Payment Intents.
- **Professional Logging**: Structured, "pretty-printed" JSON logs for all transaction flows to ensure auditability.
- **Enterprise Database**: Powered by **Microsoft SQL Server** for robust data persistence.

---

## 🛠️ Tech Stack

- **Backend**: Java 17, Spring Boot 3.x
- **Database**: Microsoft SQL Server
- **Dependency Management**: Maven
- **External API**: Stripe REST API
- **Utilities**: Lombok, RestTemplate, Jackson

---

## 📋 Prerequisites

Before running the application, ensure you have:
- **JDK 17** or higher installed.
- **Maven** installed and configured.
- **Microsoft SQL Server** instance running.
- A **Stripe Secret Key** (Available at [Stripe Dashboard](https://dashboard.stripe.com/test/apikeys)).

---

## ⚙️ Setup and Installation

### 1. Database Configuration
1. Create a new database in your SQL Server (e.g., named `CardDB`).
2. Open `src/main/resources/application.properties` and update the connection string with your local credentials:

```properties
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=CardDB;encrypt=true;trustServerCertificate=true
spring.datasource.username=your_username
spring.datasource.password=your_password
# This will automatically create tables based on your Entity classes
spring.jpa.hibernate.ddl-auto=update
```

### 2. Configure Stripe API Key
In the same `application.properties` file, add your Stripe secret key:

```properties
stripe.api.key=sk_test_your_actual_key_here
```

### 3. Build and Run
Execute the following commands in your terminal:

```bash
mvn clean install
mvn spring-boot:run
```
The server will be available at `http://localhost:8080`.

---

## 🧪 Testing the API

### Postman Collection
A pre-configured Postman Collection is provided in the folder of this repository.
1. Import `postman_collection.json` into Postman.
2. The collection includes pre-defined requests for:
   - **CRUD**: Create, Update, Delete, and Get Card details.
   - **Pagination**: Get cards with `page` and `size` parameters.
   - **Stripe Nested Call**: Create a Stripe Payment Intent using a Card ID from your local DB.

---

## 📝 Traceability & Logging
Following professional standards, this application logs all external API interactions in a "pretty-printed" JSON format. This ensures that every transaction can be audited and debugged easily.

**Example Log Output:**
```json
=== STRIPE SUCCESS & SAVED === 
Transaction ID: pi_3TAvyFI3fR4QNqUH12y1eFWl
{
  "id" : "pi_3TAvyFI3fR4QNqUH12y1eFWl",
  "amount" : 5000,
  "currency" : "myr",
  "status" : "requires_payment_method"
}
```

---

## 🛡️ Security Disclaimer
This project uses Spring `@Value` for configuration. For production deployments, sensitive credentials such as database passwords and API keys should be managed via **Environment Variables** or a **Secret Vault** (e.g., AWS Secrets Manager).