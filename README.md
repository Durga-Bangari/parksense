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

## Phase 1 features

- Spring Boot REST API with layered backend structure
- Mock parking data provider for local development
- Time-based parking availability prediction
- Demand-based parking price estimation
- Weighted recommendation scoring using availability, price, and distance
- Plain-English explanation generation for each recommendation
- Top recommendation summary in the API response
- Request validation and centralized error handling

## Phase 2 direction

Phase 2 starts by separating the recommendation engine from the mock data source. This makes the backend easier to extend into a real website or mobile app because external providers such as Google Maps can later plug into the same internal recommendation flow.

Current Phase 2 foundation:

- `ParkingDataProvider` abstraction for parking data sources
- mock provider remains the active implementation for local development
- recommendation service now depends on a provider contract instead of concrete mock storage
- typed provider configuration for future external API integration
- provider selection config with a Google Maps integration placeholder

## Provider configuration

The backend is now prepared for future external provider integration through configuration:

```properties
parksense.provider.type=mock
parksense.provider.google-maps-api-key=
parksense.provider.search-radius-meters=1500
```

For now, the active provider remains `mock`. Later, we can switch this configuration when a real provider such as Google Maps is added.

Supported provider types today:

- `mock`
- `google-maps` placeholder

## Google Maps integration path

The backend now includes a typed Google Maps Places client and DTOs for the Nearby Search API. This keeps external request and response handling separate from the internal `ParkingSpot` model.

Current Google Maps configuration:

```properties
parksense.provider.type=mock
parksense.provider.google-maps-base-url=https://places.googleapis.com/v1
parksense.provider.google-maps-api-key=
parksense.provider.search-radius-meters=1500
parksense.provider.fallback-to-mock-on-failure=true
```

When `parksense.provider.type=google-maps`, the backend is prepared to call the Google Places Nearby Search endpoint and map parking-related places into the recommendation pipeline.

The Google provider now also:

- filters incomplete external place records before mapping
- falls back to mock parking data when enabled and the external call fails or returns nothing usable

## Frontend-ready response fields

Recommendation responses now include:

- address and category for display-friendly parking cards
- spot coordinates for map rendering
- `providerType` so web or mobile clients can display or debug the data source

## Planned backend flow

1. Accept destination coordinates and arrival time
2. Fetch nearby parking spots from a mock provider
3. Predict availability using time-based heuristics
4. Predict price using demand-based logic
5. Rank parking spots using a weighted recommendation score
6. Return ranked results with a best-option summary and plain-English explanations

## Project structure

The codebase will follow a clean layered structure as the project grows:

- `controller`
- `service`
- `model`
- `provider`
- `util`
- `exception`

## API overview

The main endpoint accepts destination coordinates and an arrival time:

```json
{
  "latitude": 47.6,
  "longitude": -122.3,
  "arrivalTime": "2026-04-12T18:00:00"
}
```

It returns a top recommendation summary plus ranked parking options:

```json
{
  "bestOptionSummary": "Central Garage is the top recommendation based on availability, price, and distance",
  "recommendations": [
    {
      "spotId": "P1",
      "spotName": "Central Garage",
      "address": "1200 4th Ave, Seattle, WA",
      "category": "garage",
      "latitude": 47.6097,
      "longitude": -122.3331,
      "providerType": "mock",
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

### Test the application

```bash
mvn test
```

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

### Provider diagnostics endpoint

```bash
GET /api/v1/provider
```

Example response:

```json
{
  "configuredProviderType": "mock",
  "activeProviderType": "mock",
  "providerReady": true,
  "statusMessage": "Provider configuration is ready",
  "fallbackToMockOnFailure": true,
  "searchRadiusMeters": 1500
}
```

### Recommendation endpoint

```bash
POST /api/v1/recommendations
```

Example request:

```json
{
  "latitude": 47.6,
  "longitude": -122.3,
  "arrivalTime": "2026-04-12T18:00:00"
}
```

Example response:

```json
{
  "bestOptionSummary": "Central Garage is the top recommendation based on availability, price, and distance",
  "recommendations": [
    {
      "spotId": "P1",
      "spotName": "Central Garage",
      "address": "1200 4th Ave, Seattle, WA",
      "category": "garage",
      "latitude": 47.6097,
      "longitude": -122.3331,
      "providerType": "mock",
      "distanceMeters": 200.0,
      "predictedAvailability": 0.75,
      "predictedPrice": 12.5,
      "score": 8.7,
      "explanation": "High availability and low price near your destination"
    }
  ]
}
```

### Validation example

If invalid coordinates are provided, the API returns a `400 Bad Request` response with validation details.

## Recruiter-friendly positioning

This project is intentionally built in stages. The current version focuses on production-style backend design, modular prediction-oriented architecture, and clean extension points for future ML and LLM integrations.
