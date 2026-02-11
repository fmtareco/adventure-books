# Adventure Books REST API

## Overview

This project implements a **Spring Boot REST API** that allows users to:

* Browse a collection of interactive adventure books
* Play through a book with health‑based consequences
* Persist game progress per game session
* Manage categories and books

# Deploying and Running the Application

## Go to the application's main folder (where `pom.xml`, `Dockerfile`, and `docker-compose.yml` are located).

## Build the API JAR:
   ```bash
   mvn clean install -DskipTests 
   ```

## Build the API Docker image:
   ```bash
   docker build -t fmtareco/adventure-books .
   ```
This creates the image `fmtareco/adventure-books`.

## Launch the containers:
   ```bash
   docker compose up -d
   ```
## Configure Postgres server

Launch pgAdmin:
http://localhost:5050/browser/

- provide a master password
- create a new Server:
- General/name: devices_server (example)
- Connection/Host name: postgres
- Connection/username: user
- Connection/password: password

## Using an API testing platform (e.g., Postman), test the main endpoint:
   ```
   http://localhost:8080/api/books
   ```
Try different HTTP methods to validate all API operations.

## Access points

```
* API base: `http://localhost:8080/api`
* Swagger UI: `http://localhost:8080/swagger-ui.html`
* Actuator: `http://localhost:8080/actuator`
```

## Example Game Flow

```
POST /api/games/start/102
→ STARTED
GET  /api/games/{id}
POST /api/games/{id}/options/1
POST /api/games/{id}/options/2
...
→ SUCCEEDED or FAILED
...
POST /api/games/{id}/options/0
...
→ RESTARTED
```

## Example Book Game Simulations 

```
GET /api/books/102/validate
```


## Examples API Queries

```

GET /api/books?category=FICTION&condition=OK
GET /api/books?condition=NO_OPTIONS
GET /api/books/102/sections/200
GET /api/games?status=RESTARTED
GET /api/categories
GET /api/books/102/validate
```


# Objectives Covered

### Objectives

1. **List & search books** by title, author, category, difficulty
2. **Retrieve book details** and manage categories
3. **Read a book & jump between sections**
4. **Handle player consequences & health system**
5. **Multiple players with saved progress**
6. **Upload new books via REST API**

---

# Technology Stack

## Core
* Java 21 (LTS)
* Maven project
* Initial dependencies:
* Spring Web 			- to support the Web REST API and embeded Apache Tomcat 
* Spring Data Jpa		- to handle the persistence using Hibernate
* PostgreSQL Driver	    - to include PostgreSQL JDBC Driver
* Docker Compose        - support for docker compose auto launch
* Lombok				- to reduce boilerplate code

## Observability / Logging
* Actuator	            - to monitor application running status, namelly health
* Micrometer            - to collect metrics about running app 
* Prometheus            - to store gathered metrics
* Logback               - structured logging
* AOP				    - to handle selective logging/tracing

## Testing
* JUnit 5
* Mockito
* MockMvc integration tests

## Documentation 
* Spring REST Docs
* OpenAPI (springdoc)



# Project Structure

```

### Application Structure

├── AdventureApplication.java   # main application class
└── annotations             # custom annotations 
└── aspects                 # AOP advices and pointcuts
└── controller              # REST controllers
└── dtos                    # Request/response DTOs & JSON records
└── exceptions              # Global error handling and validation exceptions
└── factory                 # methods to handle the entities (Books) life cycle
└── loader                  # Entity ↔ DTO mapping + initial load from resources
└── model                   # JPA entities (Book, Section, Game, etc.)
└── repository              # Jpa Repository Interfaces for the corresponding domain entities 
└── service                 # Business logic

### Test Structure

├── integration/           # API tests
├── unit/                  # Mockito service tests
└── support/               # Test data builders & JSON helpers
```


# Domain Model

## Book

Represents an adventure book containing:

* Title, author, difficulty
* Categories (many‑to‑many)
* Sections (one‑to‑many)
* Validation status (`book_condition` persisted)

### Validation Rules

A book is **invalid** if:
* No beginning section or more than one
* No ending section
* Invalid section references
* Non‑ending section without options

---

## Section

Each section contains:
* Text content
* Type (`BEGIN`, `NODE`, `END`)
* Options leading to other sections

---

## Game (Player Session)

Represents a running playthrough:
* Linked to a **Book**
* Tracks **position**, **health**, **status**
* Stores **last option taken**, **previous position** and **consequence**

### Game Status Lifecycle

```
STARTED → ACTIVE → SUCCEEDED
                 ↘ FAILED
RESTARTED → ACTIVE
```

---

# Persistence Strategy

## Database

* **PostgreSQL**
* Schema managed via **schema.sql**
* `ddl-auto = none` in all environments

## Relationships

* Book → Sections (**LAZY one‑to‑many**)
* Book ↔ Categories (**many‑to‑many join table**)
* Section → Options (**one‑to‑many**)
* Option → Consequence (**many‑to‑one**)
* Game → Book (**many‑to‑one**)

Indexes are added for:

* Foreign keys
* `book_condition`
* Game status queries

---

# API Design

## Book Endpoints

### List books

```
GET /api/books
```

Query params:

    * `title`
    * `author`
    * `category`
    * `difficulty     # = EASY , MEDIUM, HARD`
    * `condition      # = OK , INVALID_BEGIN, NO_END, INVALID_GOTO, NO_OPTIONS`

Pagination/sort params:
    * `page`
    * `size`
    * `ascending`

Returns **paginated Page<BookSummary>**.

---

### Book details

```
GET /api/books/{id}
```

---

### Add / remove category

```
POST   /api/books/{id}/categories/{name}
DELETE /api/books/{id}/categories/{name}
```

---

### Upload new book

```
POST /api/books
```

Accepts **JSON body** describing the full book structure.

---

## Game Endpoints

### Start game

```
POST /api/games/start/{bookId}
```

---

### Get game state

```
GET /api/games/{gameId}
```

---

### Take option

```
POST /api/games/{gameId}/options/{optionNo}
```

Effects:

* Moves to next section
* Applies consequence
* Updates health & status

---

# Validation & Error Handling

## Layers

* **DTO validation** → Bean Validation annotations
* **Domain validation** → Book/Game rules
* **Persistence errors** → translated via `@ControllerAdvice`

## HTTP responses

* `BAD_REQUEST 400` → invalid input
* `NOT_FOUND 404` → resource not found
* `NOT_ACCEPTABLE 406` → invalid or duplicated resource

---


# 10. Logging & Observability

## Logging

* **Logback configuration**
* Console + rolling file logs
* SQL logging configurable per profile

## Actuator

Exposed endpoints:

```
/actuator/health
/actuator/info
/actuator/metrics
/actuator/loggers
actuator/prometheus
```

---

# Configuration Profiles

## Base (`application.yml`)
* Shared JPA settings
* Logging defaults

## Dev (`application-dev.yml`)
* Local PostgreSQL / Docker
* Verbose SQL logging
* full Actuator exposure

## Prod (`application-prod.yml`)
* Hardened logging
* No debug SQL
* limited Actuator exposure

---

---

# Future Improvements

* Authentication & user accounts
* Game simulation and loops detection (aditional validation) 

---

# Author Notes

This project is designed to demonstrate:

* **Spring Boot architecture**
* **Domain modeling & validation**
* **Testing (unit, integration)**
* **Clean REST design**


