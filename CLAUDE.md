# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

A Spring Boot web application for tracking lunch costs. Uses server-side rendering with Thymeleaf + HTMX rather than a separate frontend framework.

**Package:** `info.pekny.lunchapp`

## Commands

```bash
# Run the application (starts Docker Compose PostgreSQL automatically)
./mvnw spring-boot:run

# Start the database (required before running tests)
docker compose up -d

# Run all tests
./mvnw test

# Run a single test class
./mvnw test -Dtest=LunchAppApplicationTests

# Build JAR (skip tests)
./mvnw package -DskipTests

# Build OCI container image
./mvnw spring-boot:build-image
```

## Tech Stack

- **Java 21**, Spring Boot 4.0.3, Maven
- **Web:** Spring MVC + Thymeleaf (SSR) + HTMX (`htmx-spring-boot-thymeleaf` v5.0.0)
- **Database:** PostgreSQL via Spring Data JPA + Flyway migrations
- **Security:** Spring Security + `thymeleaf-extras-springsecurity6`
- **Boilerplate reduction:** Lombok

## Architecture

Layered Spring Boot monolith:

```
HTTP → Spring Security → MVC Controllers → Services → JPA Repositories → PostgreSQL
                                 ↓
                        Thymeleaf templates (+ HTMX for partial updates)
```

- **Controllers** handle HTTP, render Thymeleaf templates
- **HTMX** enables dynamic partial page updates without a SPA (no heavy JS framework)
- **Flyway** migrations live in `src/main/resources/db/migration/`
- **Thymeleaf** templates live in `src/main/resources/templates/`
- **Spring Docker Compose** integration auto-starts the `compose.yaml` PostgreSQL service during development

## Database

Dev PostgreSQL runs via Docker Compose (`compose.yaml`). Credentials: user=`myuser`, password=`secret`, db=`mydatabase`. Port is randomized — Spring Boot discovers it automatically.

Schema changes go through Flyway migrations (versioned SQL files in `db/migration/`).
