# Event Ticketing Backend

A microservices-based backend system for event ticketing built with Spring Boot and Spring Cloud.

## Services Overview

### 1. Service Registry (Eureka Server)
- **Port**: 8761
- **Purpose**: Service discovery and registration for all microservices

### 2. API Gateway
- **Port**: 8080
- **Purpose**: Centralized routing and cross-cutting concerns

### 3. Event Services
- **Port**: 8081
- **Purpose**: Event management, venue management, and seating charts

### 4. User Service
- **Port**: 8082
- **Purpose**: User management and authentication

### 5. Seating Service
- **Port**: 8083
- **Purpose**: Seat management and seating chart operations

### 6. Payment Service
- **Port**: 8085
- **Purpose**: Payment processing with Stripe integration

### 7. Ticket Service
- **Port**: 8086
- **Purpose**: Ticket management and QR code generation

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- PostgreSQL database
- Stripe account (for payment service)

## Database Setup

### Create Databases
```sql
CREATE DATABASE event_db;
CREATE DATABASE user_db;
CREATE DATABASE seating_db;
CREATE DATABASE payment_db;
CREATE DATABASE ticket_db;
```

### Database Credentials
- **Username**: postgres
- **Password**: postgres
- **Host**: localhost
- **Port**: 5432

## Stripe Configuration

Update the Stripe keys in `PaymentService/src/main/resources/application.properties`:
```properties
stripe.secret.key=your_stripe_secret_key
stripe.public.key=your_stripe_public_key
```

## Running the Services

### 1. Start Service Registry
```bash
cd ServiceRegistry
mvn spring-boot:run
```

### 2. Start API Gateway
```bash
cd API-Gateway
mvn spring-boot:run
```

### 3. Start Event Services
```bash
cd EventServices
mvn spring-boot:run
```

### 4. Start User Service
```bash
cd UserService
mvn spring-boot:run
```

### 5. Start Seating Service
```bash
cd SeatingService
mvn spring-boot:run
```

### 6. Start Payment Service
```bash
cd PaymentService
mvn spring-boot:run
```

### 7. Start Ticket Service
```bash
cd TicketService
mvn spring-boot:run
```

## API Endpoints

### Ticket Service (`http://localhost:8086`)

#### Create Ticket
```http
POST /api/tickets
Content-Type: application/json

{
  "eventId": 1,
  "userId": 1,
  "seatNumber": "A1",
  "ticketType": "VIP",
  "price": 150.00,
  "eventDate": "2024-12-31T20:00:00",
  "venueName": "Concert Hall",
  "eventName": "New Year Concert"
}
```

#### Get Ticket by ID
```http
GET /api/tickets/{id}
```

#### Get Tickets by User
```http
GET /api/tickets/user/{userId}
```

#### Update Ticket Status
```http
PUT /api/tickets/{id}/status?status=CONFIRMED
```

#### Confirm Ticket
```http
PUT /api/tickets/{id}/confirm
```

#### Cancel Ticket
```http
PUT /api/tickets/{id}/cancel
```

#### Mark Ticket as Used
```http
PUT /api/tickets/{id}/use
```

### Payment Service (`http://localhost:8085`)

#### Create Payment
```http
POST /api/payments
Content-Type: application/json

{
  "ticketId": 1,
  "userId": 1,
  "amount": 150.00,
  "currency": "USD",
  "paymentMethod": "card",
  "description": "Payment for New Year Concert ticket"
}
```

#### Confirm Payment
```http
PUT /api/payments/{paymentId}/confirm
```

#### Get Payment by ID
```http
GET /api/payments/{id}
```

#### Get Payments by User
```http
GET /api/payments/user/{userId}
```

#### Cancel Payment
```http
PUT /api/payments/{paymentId}/cancel
```

#### Refund Payment
```http
PUT /api/payments/{paymentId}/refund
```

## Features

### Ticket Service
- ✅ Ticket creation and management
- ✅ Unique ticket number generation
- ✅ QR code generation for tickets
- ✅ Multiple ticket statuses (RESERVED, CONFIRMED, CANCELLED, USED, EXPIRED, REFUNDED)
- ✅ Seat validation and conflict prevention
- ✅ Ticket search and filtering
- ✅ Expired ticket detection

### Payment Service
- ✅ Stripe payment integration
- ✅ Payment intent creation
- ✅ Webhook handling for payment status updates
- ✅ Multiple payment statuses (PENDING, PROCESSING, COMPLETED, FAILED, CANCELLED, REFUNDED)
- ✅ Payment tracking and history
- ✅ Refund processing
- ✅ Receipt generation

## Architecture

- **Microservices Architecture**: Each service is independent and can be deployed separately
- **Service Discovery**: Eureka server for service registration and discovery
- **API Gateway**: Centralized routing and cross-cutting concerns
- **Database per Service**: Each service has its own database
- **RESTful APIs**: Standard HTTP endpoints for all services
- **Event-Driven**: Services communicate through REST APIs

## Development

### Adding New Services
1. Create a new Spring Boot project
2. Add Eureka client dependency
3. Configure application properties
4. Implement business logic
5. Register with Eureka server

### Testing
Each service includes test classes and can be tested independently:
```bash
mvn test
```

### Building
```bash
mvn clean install
```

## Monitoring

- **Eureka Dashboard**: `http://localhost:8761`
- **Service Health**: Each service exposes health endpoints
- **Logs**: Check individual service logs for debugging

## Troubleshooting

### Common Issues
1. **Service not registering**: Check Eureka server is running and accessible
2. **Database connection**: Verify PostgreSQL is running and credentials are correct
3. **Port conflicts**: Ensure no other services are using the configured ports
4. **Stripe integration**: Verify API keys are correct and Stripe account is active

### Logs
Check application logs for detailed error messages:
```bash
tail -f logs/application.log
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

## License

This project is licensed under the MIT License.
