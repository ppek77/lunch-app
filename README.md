# Lunch App

A Spring Boot web application for tracking personal lunch costs.

## Tech Stack

- **Java 21** / Spring Boot 4.0.3 / Maven
- **Web:** Spring MVC + Thymeleaf + HTMX (server-side rendering, no SPA)
- **Database:** PostgreSQL + Spring Data JPA + Flyway migrations
- **Security:** Spring Security

## Prerequisites

- Java 21+
- Docker (for the dev database)

## Running Locally

Start the app — Docker Compose PostgreSQL is launched automatically:

```bash
./mvnw spring-boot:run
```

## Running Tests

Start the database first, then run tests:

```bash
docker compose up -d
./mvnw test
```

## Building

```bash
# Build JAR
./mvnw package -DskipTests

# Build OCI container image
./mvnw spring-boot:build-image
```

## Deployment

See [DEPLOY.md](DEPLOY.md) for instructions on deploying to a DigitalOcean Droplet with HTTPS.
