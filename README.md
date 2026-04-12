# ParkSense

ParkSense is an AI-ready backend system for parking recommendation. In this first version, the project uses rule-based heuristics and mock data to predict parking availability, estimate price, and rank nearby parking spots for a given destination and arrival time.

## Why this project

Drivers often spend extra time searching for parking, pay more than expected, or end up parking farther away than they want. ParkSense is designed to address that problem with a backend architecture that can grow from deterministic heuristics into a future ML-powered recommendation engine.

## Tech stack

- Java 17
- Spring Boot
- Maven
- REST API

## Phase 1 scope

- Backend only
- Mock parking data
- No external API integration
- No database
- No real ML model yet
- No LLM integration yet

## Current progress

- Spring Boot project bootstrap
- Health check endpoint
- Core recommendation models
- Mock parking spot repository with seed data
- Distance calculation utility for location-aware ranking

## Planned backend flow

1. Accept destination coordinates and arrival time
2. Fetch nearby parking spots from a mock repository
3. Predict availability using time-based heuristics
4. Predict price using demand-based logic
5. Rank parking spots using a weighted recommendation score
6. Return plain-English recommendation explanations

## Project structure

The codebase will follow a clean layered structure as the project grows:

- `controller`
- `service`
- `model`
- `repository`
- `util`
- `exception`

## Core API shape

The recommendation API will be built around this request structure:

```json
{
  "latitude": 47.6,
  "longitude": -122.3,
  "arrivalTime": "2026-04-12T18:00:00"
}
```

And will return ranked parking recommendations like:

```json
{
  "recommendations": [
    {
      "spotId": "P1",
      "spotName": "Central Garage",
      "distanceMeters": 200.0,
      "predictedAvailability": 0.75,
      "predictedPrice": 12.5,
      "score": 8.7,
      "explanation": "High availability and low price near your destination"
    }
  ]
}
```

## Getting started

### Prerequisites

- Java 17+
- Maven 3.9+

### Run the application

```bash
mvn spring-boot:run
```

The application starts on `http://localhost:8080`.

### First endpoint

```bash
GET /api/v1/health
```

Example response:

```json
{
  "status": "UP",
  "application": "ParkSense"
}
```

## Recruiter-friendly positioning

This project is intentionally built in stages. The current version focuses on production-style backend design, modular prediction-oriented architecture, and clean extension points for future ML and LLM integrations.
