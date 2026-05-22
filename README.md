# Hotel Booking System

SE 4458 – Software Architecture & Design of Modern Large Scale Systems Final Project

## Project Information

This project is a cloud-based microservice hotel booking system developed for the SE 4458 final project.

The system allows users to:

- Search hotels by city and dates
- View hotel details
- Make reservations
- View hotel comments and ratings
- Receive AI-based hotel recommendations
- Manage hotels and room availability as admin

The project was developed using a microservice architecture and deployed on AWS cloud services.

---

# System Architecture

The project uses a distributed microservice architecture.

## Services

### 1. API Gateway
Routes all requests to backend services.

### 2. Hotel Service
Responsible for:
- Hotel CRUD operations
- Hotel search
- Room availability management
- Reservation queue

### 3. Comments Service
Responsible for:
- Hotel comments
- Ratings
- Comment statistics

### 4. Notification Service
Responsible for:
- Low capacity notifications
- Reservation notifications

### 5. AI Agent Service
Responsible for:
- AI hotel recommendations
- AI chat assistant
- Integration with hotel search APIs

### 6. Frontend
React-based client application.

---

# Technologies Used

## Backend
- Java 21
- Spring Boot
- Spring Web
- Spring WebFlux
- Spring Data JPA
- Spring Cloud Gateway
- Maven

## Frontend
- React
- Vite
- Axios

## Database
-H2
-MongoDB

## AI
- Ollama
- Llama 3.2

## Cloud & Deployment
- AWS Elastic Beanstalk
- AWS S3
- Docker

---

# Microservice Architecture

```text
Frontend
   |
API Gateway
   |
-------------------------------------------------
|          |              |          |           |
Hotel   Comments   Notification   AI Agent
Service  Service      Service      Service
```


# Functional Requirements Implemented

-Hotel Admin Service
Add hotel
Update hotel
Delete hotel
Manage hotel availability
Admin authentication

-Hotel Search Service
Search hotels by city
Search hotels by date
Search hotels by guest count
Map view support
Discounted prices for logged-in users

-Booking Service
Book hotel
Capacity reduction after booking
Reservation queue support

-Comments Service
Add comments
Add ratings
View comment statistics
Average rating calculation

-Notification Service
Low room capacity notifications
Reservation notifications

-AI Agent Service
AI hotel recommendations
AI chat assistant
Hotel recommendation fallback mode
Integration with hotel search APIs

-Non-Functional Requirements Implemented

Microservice architecture
REST APIs
Dockerized services
Cloud deployment
API Gateway routing
Distributed architecture
NoSQL-ready comments service structure
Scalable deployment design
AWS deployment support
Centralized API access through Gateway

# Local Development Setup
Requirements
Install:

Java 21
Maven
Node.js
MongoDB
H2
Docker Desktop
Ollama

Clone Repository:
git clone https://github.com/Ayfernaz-Baygin/hotel-booking-system.git
cd hotel-booking-system

Start MySQL:
Create a MySQL database manually:

CREATE DATABASE hotel_booking;

Run Backend Services:
Open separate terminals for each service.

Hotel Service:
cd hotel-service
mvn spring-boot:run

Comments Service:
cd comments-service
mvn spring-boot:run

Notification Service:
cd notification-service
mvn spring-boot:run

AI Agent Service:
cd ai-agent-service
mvn spring-boot:run

API Gateway:
cd api-gateway
mvn spring-boot:run

Run Frontend:
cd frontend
npm install
npm run dev

Frontend runs on:
http://localhost:5173

Ollama Setup:

Install Ollama and pull llama model:
ollama pull llama3.2

Run Ollama locally:
ollama serve

Default Ollama endpoint:
http://localhost:11434

Docker Setup:
Build Docker Images
Example:
docker build -t hotel-service .
Docker Compose

Run all services:
docker compose up --build


# Cloud Deployment (AWS)

The system was deployed using AWS Elastic Beanstalk.

Deployment Steps

1. Package Jar
mvn package -DskipTests

2. Copy Jar
Copy-Item .\target\service-name.jar .\service-name.jar -Force

3. Create Deployment Zip
Compress-Archive `
-Path .\Dockerfile, .\service-name.jar `
-DestinationPath "$env:USERPROFILE\Downloads\deploy.zip" `
-Force

4. Upload to Elastic Beanstalk
Create environment
Upload zip
Deploy application

Cloud Deployment URLs:
Frontend
http://hotel-booking-frontend-ayfernaz.s3-website.eu-north-1.amazonaws.com

API Gateway
http://api-gateway-lb-env-env.eba-qwz3nust.eu-north-1.elasticbeanstalk.com

AI Agent Service:
http://Ai-agent-service1-env.eba-jv8rtpgc.eu-north-1.elasticbeanstalk.com/api/v1/ai/chat
This endpoint accepts POST requests and should be tested with Postman or frontend AI chat.

# Swagger Documentation
Hotel Service Swagger
http://hotel-service-h2-env-env.eba-3pkfrmiy.eu-north-1.elasticbeanstalk.com/swagger-ui/index.html

Comments Service Swagger
http://comments-service-env.eba-2n42zb5p.eu-north-1.elasticbeanstalk.com/swagger-ui/index.html

Notification Service Swagger
http://notification-service1-env.eba-hxupqwcm.eu-north-1.elasticbeanstalk.com/swagger-ui/index.html

API Gateway Example Endpoints:
Get All Hotels
http://api-gateway-lb-env-env.eba-qwz3nust.eu-north-1.elasticbeanstalk.com/api/v1/hotels

AI Chat Endpoint
POST /api/v1/ai/chat

Example body:
{
  "prompt": "I want a hotel in Bodrum with pool and breakfast",
  "city": "Bodrum",
  "startDate": "2026-06-02",
  "endDate": "2026-06-05",
  "people": "2"
}

Authentication:
Admin authentication is implemented.
Example admin credentials:
Email: admin@test.com
Password: test

# Assumptions
Payments were intentionally not implemented because the project document explicitly states that transaction support is not required.
AI responses use fallback recommendation mode when Ollama is unavailable.
Hotel images are static URLs.
Queue implementation is simplified for educational purposes.
Authentication is simplified for demonstration purposes.

# Issues Encountered During Development
AWS Deployment Issues
Docker build path problems
Elastic IP limit issues
Elastic Beanstalk deployment failures
Gateway routing configuration issues

Backend Issues
CORS configuration problems
Docker networking problems
Maven clean locking issues
AI service fallback handling

Frontend Issues
API Gateway connection problems
Environment URL mismatches
Deployment synchronization issues


---

# Data Models (ER Design)

The system uses relational data structures for hotel and booking management.

## Entity Relationship Overview

```text
Hotel
 ├── id (PK)
 ├── name
 ├── city
 ├── address
 ├── description
 ├── rating
 ├── totalRooms
 ├── availableRooms
 ├── pricePerNight
 ├── hasPool
 ├── hasWifi
 └── hasBreakfast

        │
        │ 1-to-Many
        ▼

Booking
 ├── id (PK)
 ├── hotelId (FK)
 ├── guestName
 ├── startDate
 ├── endDate
 ├── people
 └── status

        │
        │ 1-to-Many
        ▼

Comment
 ├── id (PK)
 ├── hotelId (FK)
 ├── username
 ├── comment
 └── rating
```

---

# Hotel Entity

Represents hotels stored in the system.

| Field | Type | Description |
|---|---|---|
| id | Long | Primary key |
| name | String | Hotel name |
| city | String | Hotel city |
| address | String | Hotel address |
| description | String | Hotel description |
| rating | Double | Hotel rating |
| totalRooms | Integer | Total room count |
| availableRooms | Integer | Available room count |
| pricePerNight | Double | Price per night |
| hasPool | Boolean | Pool availability |
| hasWifi | Boolean | Wi-Fi availability |
| hasBreakfast | Boolean | Breakfast availability |

---

# Booking Entity

Represents hotel reservations.

| Field | Type | Description |
|---|---|---|
| id | Long | Primary key |
| hotelId | Long | Related hotel |
| guestName | String | Guest full name |
| startDate | LocalDate | Check-in date |
| endDate | LocalDate | Check-out date |
| people | Integer | Guest count |
| status | String | Reservation status |

---

# Comment Entity

Represents user comments and ratings.

| Field | Type | Description |
|---|---|---|
| id | Long | Primary key |
| hotelId | Long | Related hotel |
| username | String | Comment owner |
| comment | String | User comment |
| rating | Integer | Rating score |

---

# AI Agent Workflow
User sends AI request
API Gateway routes request
AI Agent Service receives prompt
AI Agent fetches available hotels
Ollama generates recommendation
Fallback mode activates if Ollama is unavailable
Response returns to frontend

# Project Features Demonstrated
Distributed microservice system
Cloud deployment
API Gateway routing
AI integration
Dockerized architecture
React frontend
MySQL integration
REST APIs
Swagger documentation
AWS deployment

---

# GitHub Repository

https://github.com/Ayfernaz-Baygin/hotel-booking-system.git

---

# Demo Video

[(https://youtu.be/2Ae57OyvQrQ)](https://youtu.be/2Ae57OyvQrQ)](https://youtu.be/2Ae57OyvQrQ)



