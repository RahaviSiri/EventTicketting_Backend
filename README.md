# EventTicketting_Backend

# 🎟️ EventEase — Event Ticketing & Management System (Backend)

## 🧾 Overview
**EventEase** is a microservices-based Event Ticketing and Management platform designed to handle end-to-end event workflows — from event creation and seat management to ticket booking, payments, notifications, and discounts.  
This repository contains the **backend microservices** that power the EventEase system.

## 🏗️ Architecture
The system follows a **Spring Boot microservices architecture** with each service containerized using **Docker** and registered under a centralized **Service Registry (Eureka)**.  
API requests are routed through a **Spring Cloud Gateway** which performs JWT-based authentication and CORS handling.

### 🔹 Microservices Included
Service and Description 
**Service Registry** - Eureka Server for service discovery 
**API Gateway** - Entry point for all frontend requests (JWT authentication, CORS) 
**User Service** - Handles user registration, authentication, and profile management 
**Event Service** - Manages event creation, details, and updates 
**Seating Service** - Dynamically manages seat layouts and availability 
**Ticket Service** - Issues and validates tickets, integrates with Stripe payments 
**Payment Service** - Handles Stripe payments and transaction confirmations 
**Discount Service** - Manages percentage/fixed discounts and coupon codes 
**Order Service** - Coordinates order creation and ticket confirmation 
**Notification Service** - Sends email notifications and reminders via SendGrid 
**Admin Service** - Admin dashboard APIs (user management, event analytics) 

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
