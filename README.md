# EventTicketting_Backend

# 🎟️ EventEase — Event Ticketing & Management System (Backend)

## 🧾 Overview
**EventEase** is a microservices-based Event Ticketing and Management platform designed to handle end-to-end event workflows — from event creation and seat management to ticket booking, payments, notifications, and discounts.  
This repository contains the **backend microservices** that power the EventEase system.

## 🏗️ Architecture
The system follows a **Spring Boot microservices architecture** with each service containerized using **Docker** and registered under a centralized **Service Registry (Eureka)**.  
API requests are routed through a **Spring Cloud Gateway** which performs JWT-based authentication and CORS handling.

## 🧱 Microservices Breakdown

| Service | Description | Port |
|----------|--------------|------|
| **Service Registry** | Eureka Server for service discovery | 8761 |
| **API Gateway** | Central routing & authentication point | 8080 |
| **User Service** | Handles signup, login, profile management | 8087 |
| **Event Service** | Manages event creation and details | 8082 |
| **Seating Service** | Seat layout and availability management | 8083 |
| **Ticket Service** | Ticket generation, QR code creation | 8084 |
| **Payment Service** | Stripe payment handling | 8085 |
| **Discount Service** | Manages discounts and promo codes | 8086 |
| **Order Service** | Coordinates orders and transaction logic | 8088 |
| **Notification Service** | Sends email confirmations/reminders | 8092 |
| **Admin Service** | Admin dashboard and event analytics | 8090 |

--- 

## ⚙️ Tech Stack
### Backend Frameworks:
- **Spring Boot 3.x**
- **Spring Cloud Netflix (Eureka, Gateway)**
- **Spring Security (JWT)**
- **Spring WebFlux** for reactive gateway

### Messaging & Event Handling:
- **Apache Kafka** (Aiven Cloud) for asynchronous communication  
  Topics include:
  - `order-confirmed`
  - `event-updated`
  - `event-cancelled`
  - `reminder-tasks`
  - `contact-messages`

### Databases:
- **PostgreSQL** for relational data
- **MongoDB** for document-based seat and event layouts (optional extension)

### File & Email Services:
- **Cloudinary** for image uploads
- **SendGrid SMTP** for transactional and reminder emails

### Payment:
- **Stripe API** for secure card payments


## 🧠 Authentication & Security
- **JWT-based authentication** across all services
- Stateless security (no session storage)
- API Gateway verifies tokens via a **custom `JwtAuthenticationManager`**
- **CSRF disabled** (not required for stateless JWT APIs)
- **CORS** dynamically configured via environment variable `ALLOWED_ORIGINS`


## 🐳 Docker & Deployment
Each microservice includes its own **Dockerfile**, using `Eclipse Temurin JDK 21` base images.

### 🧩 Docker Compose
The root `docker-compose.yml` coordinates:
- Zookeeper (for local Kafka setup)
- Kafka Broker
- PostgreSQL database
- All backend microservices
- A shared network `eventnet`

Example startup:
```bash
docker-compose up --build
```

