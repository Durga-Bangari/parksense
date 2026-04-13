# ParkSense

ParkSense is an AI-ready backend system for parking recommendation. In this first version, the project uses rule-based heuristics and mock data to predict parking availability, estimate price, and rank nearby parking spots for a given destination and arrival time.

## Project snapshot

- Phase 1: heuristic recommendation engine with mock parking data
- Phase 2: real-data-ready provider architecture with Google Maps integration scaffolding
- Phase 3: persistence support for search history and app-oriented backend workflows
- Phase 4: React + TypeScript frontend for search, history, diagnostics, and sharing
- Phase 5: destination-first search foundation with mock geocoding
 
## Phase 6 direction

Phase 6 upgrades destination lookup from mock-only behavior toward real geocoding. The first step is to make destination geocoding selectable the same way parking data providers already are, so ParkSense can keep the current mock mode for local development while preparing a Google Maps geocoding path.

Current Phase 6 foundation:

- configurable geocoding provider selection
- default mock geocoding for local development
- Google Maps geocoding provider placeholder for the real integration path

## Why this project

Drivers often spend extra time searching for parking, pay more than expected, or end up parking farther away than they want. ParkSense is designed to address that problem with a backend architecture that can grow from deterministic heuristics into a future ML-powered recommendation engine.

## Tech stack

- Java 17
- Spring Boot
- Maven
- REST API
- Spring Data JPA
- H2 database

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

## Phase 2 outcome

Phase 2 moves ParkSense from a mock-only backend toward a real-data-ready service. The recommendation pipeline can still run locally with mock data, but it now has the provider abstractions, external client layer, fallback behavior, diagnostics, and caching needed for future website or mobile app integration.

## Phase 3 direction

Phase 3 adds persistence and app-backend foundations. We are starting with Spring Data JPA and an in-memory H2 database so ParkSense can begin storing backend state while staying simple to run locally.

Current Phase 3 foundation:

- H2 database configuration for local development
- Spring Data JPA support for persistence
- `SearchHistory` entity for storing recommendation lookup metadata
- JPA repository support for recent-search retrieval
- recent search history API support for future app history screens

Why this matters for a future app:

- recommendation requests now create backend state instead of being one-off computations
- a web or mobile client can show recent searches without recomputing past requests
- the persistence layer is now structured so H2 can later be swapped for PostgreSQL

Current Phase 2 foundation:

- `ParkingDataProvider` abstraction for parking data sources
- mock provider remains the active implementation for local development
- recommendation service now depends on a provider contract instead of concrete mock storage
- typed provider configuration for future external API integration
- provider selection config with a Google Maps integration placeholder

## Phase 5 direction

Phase 5 starts shifting ParkSense from coordinate-first demo input toward destination-first search that real users can understand. The first step is a geocoding abstraction with a mock destination provider so the backend can translate place names into coordinates before we wire that flow into the public API.

Current Phase 5 foundation:

- `DestinationGeocodingProvider` abstraction for destination lookup
- mock geocoding provider for local development and demos
- seeded destinations such as Seattle Convention Center, Pike Place Market, Space Needle, and Seattle Waterfront
- destination-based recommendation endpoint that geocodes a place name before ranking parking options

## Persistence flow

1. A client sends `POST /api/v1/recommendations` or `POST /api/v1/recommendations/by-destination`
2. ParkSense builds ranked parking recommendations
3. The best-option summary and request metadata are stored in `search_history`
4. A client can later call `GET /api/v1/search-history` to render recent activity

This keeps the first persistence slice simple while still adding meaningful app behavior.

## Provider configuration

The backend is now prepared for future external provider integration through configuration:

```properties
parksense.provider.type=mock
parksense.provider.geocoding-type=mock
parksense.provider.google-maps-api-key=
parksense.provider.search-radius-meters=1500
```

For local development, the active provider should stay `mock`. When you want to test the real provider path, switch the type to `google-maps` and supply a valid API key.

Supported provider types today:

- `mock`
- `google-maps`

## Running modes

### Mock mode

- fastest way to run the project locally
- deterministic recommendation flow for demos and development
- no external credentials required

### Google Maps mode

- uses the Google Places Nearby Search path to fetch parking candidates
- maps external place data into the internal `ParkingSpot` model
- supports fallback-to-mock behavior and short-lived caching

An example configuration is available in [application-googlemaps.example.properties](C:/GitProjects/Projects/smart-parking-finder/src/main/resources/application-googlemaps.example.properties:1).

## Google Maps integration path

The backend now includes a typed Google Maps Places client and DTOs for the Nearby Search API. This keeps external request and response handling separate from the internal `ParkingSpot` model.

Current Google Maps configuration:

```properties
parksense.provider.type=mock
parksense.provider.geocoding-type=mock
parksense.provider.google-maps-base-url=https://places.googleapis.com/v1
parksense.provider.google-maps-geocoding-base-url=https://maps.googleapis.com/maps/api/geocode
parksense.provider.google-maps-api-key=
parksense.provider.search-radius-meters=1500
parksense.provider.fallback-to-mock-on-failure=true
parksense.provider.cache-enabled=true
parksense.provider.cache-ttl-seconds=300
```

When `parksense.provider.type=google-maps`, the backend is prepared to call the Google Places Nearby Search endpoint and map parking-related places into the recommendation pipeline.

The Google provider now also:

- filters incomplete external place records before mapping
- falls back to mock parking data when enabled and the external call fails or returns nothing usable
- translates provider configuration and request failures into clean API error responses
- caches successful nearby search results for a short configurable TTL

## App-ready backend notes

This backend is now structured so a future React, Next.js, Android, iOS, or Flutter client can consume one stable REST contract while the provider implementation evolves behind the scenes.

Useful frontend-facing pieces already in place:

- frontend-ready recommendation fields such as coordinates, address, category, and provider type
- stable request and response models for recommendation lookups
- diagnostics endpoint for checking provider readiness in development or staging
- centralized validation and error handling for predictable client behavior

## Frontend-ready response fields

Recommendation responses now include:

- address and category for display-friendly parking cards
- spot coordinates for map rendering
- `providerType` so web or mobile clients can display or debug the data source

## Current product flow

1. Accept destination text and arrival time from the frontend
2. Geocode the destination into coordinates through the destination provider layer
3. Fetch nearby parking spots from the active parking data provider
4. Predict availability using time-based heuristics
5. Predict price using demand-based logic
6. Rank parking spots using a weighted recommendation score
7. Return ranked results with a best-option summary and plain-English explanations

## Project structure

The codebase will follow a clean layered structure as the project grows:

- `controller`
- `service`
- `model`
- `repository`
- `provider`
- `util`
- `exception`

## API overview

The current user-facing flow uses destination text and an arrival time:

```json
{
  "destination": "Seattle Convention Center",
  "arrivalTime": "2026-04-12T18:00:00"
}
```

ParkSense also keeps the original coordinate-based endpoint for internal reuse, testing, and provider-level workflows:

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

### Run the frontend

```bash
cd frontend
npm install
npm run dev
```

The frontend expects the backend API base URL in `VITE_API_BASE_URL`. For local development, you can copy `frontend/.env.example` and keep the default `http://localhost:8080/api/v1` value.

The frontend also keeps the current search form in the URL query string so a recommendation scenario can be refreshed, bookmarked, or shared, and the UI includes a copy-link action for quick demos.

The frontend now uses the TypeScript React app under `frontend/src/*.tsx`; the original JavaScript scaffold files were removed during cleanup.

The current frontend experience is destination-first: users enter a place name and arrival time, while the backend handles geocoding before ranking parking options.

### Test the application

```bash
mvn test
```

### Local database console

While running locally, the H2 console is available at `http://localhost:8080/h2-console`.

Use these defaults:

- JDBC URL: `jdbc:h2:mem:parksense`
- username: `sa`
- password: leave blank

### Migrate to PostgreSQL later

The persistence layer is intentionally built with Spring Data JPA so moving from H2 to PostgreSQL is mostly a configuration change plus a production driver dependency.

An example production-style config is available in [application-postgresql.example.properties](C:/GitProjects/Projects/smart-parking-finder/src/main/resources/application-postgresql.example.properties:1).

### Switch to Google Maps mode

1. Copy the settings from `src/main/resources/application-googlemaps.example.properties`
2. Put your real Google Maps API key into `parksense.provider.google-maps-api-key`
3. Set `parksense.provider.type=google-maps`
4. Start the app and check `GET /api/v1/provider`

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

### Recent search history endpoint

```bash
GET /api/v1/search-history
```

Optional query parameter:

- `limit` to control how many recent searches are returned, capped at `50`

Example request:

```bash
GET /api/v1/search-history?limit=5
```

Example response:

```json
[
  {
    "id": 1,
    "latitude": 47.6,
    "longitude": -122.3,
    "arrivalTime": "2026-04-12T18:00:00",
    "searchedAt": "2026-04-12T17:45:00",
    "bestOptionSummary": "Central Garage is the top recommendation based on availability, price, and distance"
  }
]
```

### Recommendation endpoint

```bash
POST /api/v1/recommendations
```

Example `curl` request:

```bash
curl -X POST http://localhost:8080/api/v1/recommendations \
  -H "Content-Type: application/json" \
  -d "{\"latitude\":47.6,\"longitude\":-122.3,\"arrivalTime\":\"2026-04-12T18:00:00\"}"
```

Example request:

```json
{
  "latitude": 47.6,
  "longitude": -122.3,
  "arrivalTime": "2026-04-12T18:00:00"
}
```

### Destination recommendation endpoint

```bash
POST /api/v1/recommendations/by-destination
```

Example request:

```json
{
  "destination": "Seattle Convention Center",
  "arrivalTime": "2026-04-12T18:00:00"
}
```

This endpoint currently uses the mock geocoding provider introduced in Phase 5 and then reuses the same recommendation engine behind the coordinate-based flow.

For the current frontend and real-user demo flow, this is the primary endpoint.

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

### Provider error behavior

When the Google Maps provider is active:

- missing provider configuration returns `503 Service Unavailable`
- upstream provider request failures return `502 Bad Gateway`
- optional fallback can still serve mock recommendations when enabled

## Recruiter-friendly positioning

This project is intentionally built in stages. The current version focuses on production-style backend design, modular prediction-oriented architecture, and clean extension points for future ML and LLM integrations.

Phase 3 makes the project feel more like a real backend product by adding persistence, request history, and an API shape that a future frontend can build on without changing the recommendation core.
