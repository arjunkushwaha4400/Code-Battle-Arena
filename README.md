# Code Battle Arena

Code Battle Arena is a real-time competitive coding platform built with a microservices architecture. It enables users to join coding battles, solve problems in real time, receive AI-powered assistance, and interact through live WebSocket-based communication.

The platform combines a React frontend with Spring Boot backend services, using PostgreSQL, Redis, RabbitMQ, Keycloak, Docker, and Ollama to support scalable, secure, and intelligent coding battles.

---

## Features

- Real-time coding battles
- Battle room and matchmaking support
- WebSocket/STOMP-based live updates
- AI-powered hints and code analysis
- Docker-based code execution worker
- API Gateway for centralized routing
- Authentication and authorization with Keycloak
- PostgreSQL with Flyway migrations
- Redis caching and fast-access state management
- RabbitMQ for asynchronous communication
- Shared common library for reusable DTOs and utilities

---

## Architecture

This repository follows a microservices-based design with the following modules:

- **api-gateway** – central entry point for routing and security
- **user-service** – user management and profile operations
- **battle-service** – battle rooms, matchmaking, submissions, and WebSocket events
- **ai-service** – AI hints and code analysis using Spring AI + Ollama
- **execution-worker** – Docker-based code execution service
- **common-lib** – shared DTOs, exceptions, and utilities
- **coding-battle-arena-client** – frontend built with React and Vite

---

## Project Structure

```text
Code-Battle-Arena/
├── ai-service/
├── api-gateway/
├── battle-service/
├── coding-battle-arena-client/
├── common-lib/
├── docker/
├── execution-worker/
├── user-service/
├── docker-compose.exm.yml
├── pom.xml
└── README.md

Tech Stack
Backend
Java 21
Spring Boot 3.3.5
Spring Cloud Gateway
Spring Security OAuth2 Resource Server
Spring WebSocket
Spring Data JPA
Spring AI
Resilience4j
Flyway
Maven
Frontend
React 18
Vite
React Router DOM
Axios
Bootstrap
SockJS
STOMP.js
Infrastructure
PostgreSQL
Redis
RabbitMQ
Keycloak
Ollama
Docker
Infrastructure Services
The provided docker-compose.exm.yml provisions the following dependencies:

PostgreSQL for user-service
PostgreSQL for battle-service
PostgreSQL for Keycloak
Redis
RabbitMQ
Keycloak
Ollama
Getting Started
Prerequisites
Make sure the following are installed:

Java 21
Maven
Node.js and npm
Docker
Git
1. Clone the repository
Bash

git clone https://github.com/arjunkushwaha4400/Code-Battle-Arena.git
cd Code-Battle-Arena
2. Start infrastructure services
Bash

docker compose -f docker-compose.exm.yml up -d
3. Build backend modules
Bash

mvn clean install
4. Run backend services
Run each service in a separate terminal.

API Gateway
Bash

cd api-gateway
mvn spring-boot:run
User Service
Bash

cd ../user-service
mvn spring-boot:run
Battle Service
Bash

cd ../battle-service
mvn spring-boot:run
AI Service
Bash

cd ../ai-service
mvn spring-boot:run
Execution Worker
Bash

cd ../execution-worker
mvn spring-boot:run
5. Run the frontend
Bash

cd ../coding-battle-arena-client
npm install
npm run dev
Configuration
Each backend service contains an example configuration file under src/main/resources, such as:

application-example.yml
Create an application.yml for each service based on the provided example file.

Typical configuration includes:

Service ports
PostgreSQL connection details
Redis and RabbitMQ settings
Keycloak / OAuth2 issuer configuration
Ollama endpoint
Execution worker settings
Security
Authentication and authorization are handled using Keycloak and OAuth2 Resource Server support in the backend services.

Default Keycloak admin credentials from the Docker Compose example:

Username: admin
Password: admin
Suggested Startup Order
Start infrastructure services with Docker Compose
Start backend services:
user-service
battle-service
ai-service
execution-worker
api-gateway
Start the frontend client
