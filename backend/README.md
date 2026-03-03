# Autoflex — Backend

A RESTful API for industrial inventory management, built with **Spring Boot + JPA + Oracle Database** as part of a full-stack assessment project.

---

## Overview

This API manages products, raw materials, and their associations. It exposes a production suggestion endpoint that calculates which products can be manufactured based on current stock levels, prioritizing higher-value products first using a greedy algorithm implemented entirely in Java.

---

## Tech Stack

| Layer            | Technology                         |
|------------------|------------------------------------|
| Framework        | Spring Boot 3.5                    |
| Language         | Java 21                            |
| Persistence      | Spring Data JPA + Hibernate        |
| Database         | Oracle Database (via `ojdbc11`)    |
| Containerization | Docker + Docker Compose            |
| Build Tool       | Maven                              |
| API Docs         | Springdoc OpenAPI (Swagger UI)     |
| Testing          | JUnit 5 + Mockito + Spring MockMvc |

---

## Features

- **Products** — Full CRUD with paginated listing
- **Raw Materials** — Full CRUD with stock quantity tracking
- **Associations** — Link multiple raw materials to a product with required quantities per unit
- **Production Suggestion** — Calculates producible quantities per product ordered by highest price, consuming stock sequentially to respect shared raw material constraints

---

# Getting Started

### Prerequisites

- Java 21+
- Maven 3.8+
- Docker + Docker Compose (recommended)

### Running with Docker Compose

```bash
docker-compose up --build
```

The API will be available at `http://localhost:8080/api`.

> On first run, Oracle DB takes up to 90 seconds to initialize. The `healthcheck` polls the database using `sqlplus` every 20 seconds (up to 30 retries) before the app container starts. The app also has `restart: on-failure` as a safety net for edge cases.

## API Documentation

Once the application is running, the full interactive API documentation is available via **Swagger UI**:

```
http://localhost:8080/swagger-ui.html
```

It documents all endpoints, request/response DTOs, HTTP status codes, and allows you to execute requests directly from the browser — no external client needed.