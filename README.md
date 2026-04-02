This is a fantastic project\! The architecture is robust and modern. To make your README truly stand out on GitHub, you should use **Visual Hierarchy**, **Badges**, and **Technical Callouts**.

Here is a revamped version of your README with improved formatting, iconography, and a professional layout.

-----

# ⚔️ Code Battle Arena

[](https://www.oracle.com/java/)
[](https://spring.io/projects/spring-boot)
[](https://reactjs.org/)
[](https://www.docker.com/)

**Code Battle Arena** is a high-performance, real-time competitive coding platform. Engineered with a **microservices architecture**, it allows developers to engage in head-to-head battles, receive AI-driven insights, and execute code safely within isolated environments.

-----

## 🚀 Key Features

  * **Real-time Battles:** Seamless matchmaking and live coding rooms.
  * **Live Communication:** WebSocket & STOMP-based synchronization for instant updates.
  * **AI Sidekick:** Intelligent hints and code analysis powered by **Spring AI + Ollama**.
  * **Sandboxed Execution:** Secure, Docker-based code execution worker for evaluating submissions.
  * **Enterprise Security:** Centralized auth and identity management via **Keycloak**.
  * **Resilient Design:** Circuit breaking and rate limiting with **Resilience4j**.

-----

## 🏗️ System Architecture

The platform is divided into specialized modules to ensure scalability and maintainability:

| Service | Responsibility |
| :--- | :--- |
| **`api-gateway`** | Central entry point; handles routing, rate limiting, and security. |
| **`user-service`** | Manages user identities, profiles, and persistence. |
| **`battle-service`** | Core logic for matchmaking, rooms, and WebSocket events. |
| **`ai-service`** | Generates real-time hints using local LLMs. |
| **`execution-worker`** | Isolated Docker containers for running user-submitted code. |
| **`common-lib`** | Shared DTOs, custom exceptions, and utility classes. |

-----

## 🛠️ Tech Stack

### **Backend**

  * **Language:** Java 21
  * **Framework:** Spring Boot 3.3.5 (Cloud Gateway, Security, Data JPA, AI)
  * **Messaging:** RabbitMQ (Asynchronous tasks)
  * **Caching:** Redis (State management)
  * **Database:** PostgreSQL

### **Frontend**

  * **Library:** React 18 + Vite
  * **State/Routing:** React Router DOM, Axios
  * **Real-time:** SockJS & STOMP.js
  * **UI:** Bootstrap / CSS3

-----

## 🚦 Getting Started

### Prerequisites

  * **Java 21** & **Maven**
  * **Node.js** (v18+) & **npm**
  * **Docker Desktop**
  * **Ollama** (for AI features)

### 1\. Clone & Infrastructure

```bash
git clone https://github.com/arjunkushwaha4400/Code-Battle-Arena.git
cd Code-Battle-Arena

# Spin up DBs, Redis, RabbitMQ, and Keycloak
docker compose -f docker-compose.exm.yml up -d
```

### 2\. Build & Run Services

```bash
# Install shared library and build all modules
mvn clean install

# Recommended Startup Order:
# 1. user-service -> 2. battle-service -> 3. ai-service 
# 4. execution-worker -> 5. api-gateway
mvn spring-boot:run -pl <service-name>
```

### 3\. Launch Frontend

```bash
cd coding-battle-arena-client
npm install
npm run dev
```

-----

## 🔒 Security & Config

The platform uses **OAuth2 Resource Server** patterns.

  * **Admin Console:** `http://localhost:8080` (Default Keycloak port)
  * **Credentials:** `admin` / `admin`

> [\!IMPORTANT]
> Always create a local `application.yml` for each service based on the `application-example.yml` templates provided in the resources folder.

-----

## 📂 Project Structure

```text
Code-Battle-Arena/
├── ai-service/                # Spring AI + Ollama Integration
├── api-gateway/               # Spring Cloud Gateway
├── battle-service/            # Matchmaking & WebSockets
├── execution-worker/          # Docker Sandbox Logic
├── user-service/              # Profile Management
├── common-lib/                # Shared Java Logic
└── coding-battle-arena-client/# React Frontend
```

-----
