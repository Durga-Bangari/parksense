import { FormEvent, useEffect, useState } from "react";

type Recommendation = {
  spotId: string;
  spotName: string;
  address: string;
  category: string;
  latitude: number;
  longitude: number;
  providerType: string;
  distanceMeters: number;
  predictedAvailability: number;
  predictedPrice: number;
  score: number;
  explanation: string;
};

type RecommendationResponse = {
  bestOptionSummary: string;
  recommendations: Recommendation[];
};

type SearchHistoryItem = {
  id: number;
  latitude: number;
  longitude: number;
  arrivalTime: string;
  searchedAt: string;
  bestOptionSummary: string;
};

type ProviderDiagnostics = {
  configuredProviderType: string;
  activeProviderType: string;
  providerReady: boolean;
  statusMessage: string;
  fallbackToMockOnFailure: boolean;
  searchRadiusMeters: number;
};

type HealthResponse = {
  status: string;
  application: string;
};

type SearchForm = {
  latitude: string;
  longitude: string;
  arrivalTime: string;
};

type SearchPreset = {
  label: string;
  description: string;
  form: SearchForm;
};

const configuredApiBaseUrl = import.meta.env.VITE_API_BASE_URL?.trim();

const API_BASE_URL = configuredApiBaseUrl
  ? configuredApiBaseUrl.replace(/\/$/, "")
  : "http://localhost:8080/api/v1";

const highlights: string[] = [
  "Heuristic parking recommendation engine",
  "Google Maps-ready provider architecture",
  "Persistent recent search history",
  "Frontend-ready API contracts for web and mobile"
];

const initialFormState: SearchForm = {
  latitude: "47.6",
  longitude: "-122.3",
  arrivalTime: "2026-04-12T18:00"
};

const searchPresets: SearchPreset[] = [
  {
    label: "Downtown Evening",
    description: "Seattle core with after-work demand",
    form: {
      latitude: "47.6062",
      longitude: "-122.3321",
      arrivalTime: "2026-04-12T18:00"
    }
  },
  {
    label: "Waterfront Lunch",
    description: "Midday parking near the waterfront",
    form: {
      latitude: "47.6075",
      longitude: "-122.3425",
      arrivalTime: "2026-04-12T12:30"
    }
  },
  {
    label: "Weekend Market",
    description: "Weekend morning search near Pike Place",
    form: {
      latitude: "47.6097",
      longitude: "-122.3425",
      arrivalTime: "2026-04-18T10:00"
    }
  }
];

function buildGoogleMapsLink(latitude: number, longitude: number) {
  return `https://www.google.com/maps/search/?api=1&query=${latitude},${longitude}`;
}

function toDatetimeLocalValue(dateTime: string) {
  return dateTime.slice(0, 16);
}

function readFormFromUrl(): SearchForm {
  const searchParams = new URLSearchParams(window.location.search);

  return {
    latitude: searchParams.get("latitude") ?? initialFormState.latitude,
    longitude: searchParams.get("longitude") ?? initialFormState.longitude,
    arrivalTime: searchParams.get("arrivalTime") ?? initialFormState.arrivalTime
  };
}

function App() {
  const [form, setForm] = useState<SearchForm>(readFormFromUrl);
  const [result, setResult] = useState<RecommendationResponse | null>(null);
  const [historyItems, setHistoryItems] = useState<SearchHistoryItem[]>([]);
  const [providerDiagnostics, setProviderDiagnostics] =
    useState<ProviderDiagnostics | null>(null);
  const [healthStatus, setHealthStatus] = useState<HealthResponse | null>(null);
  const [errorMessage, setErrorMessage] = useState<string>("");
  const [historyErrorMessage, setHistoryErrorMessage] = useState<string>("");
  const [diagnosticsErrorMessage, setDiagnosticsErrorMessage] =
    useState<string>("");
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [isHistoryLoading, setIsHistoryLoading] = useState<boolean>(true);
  const [isDiagnosticsLoading, setIsDiagnosticsLoading] =
    useState<boolean>(true);

  async function loadRecentSearches() {
    setIsHistoryLoading(true);
    setHistoryErrorMessage("");

    try {
      const response = await fetch(`${API_BASE_URL}/search-history?limit=5`);

      if (!response.ok) {
        throw new Error("Unable to load recent searches.");
      }

      const searchHistory = (await response.json()) as SearchHistoryItem[];
      setHistoryItems(searchHistory);
    } catch {
      setHistoryErrorMessage(
        "Recent searches are unavailable right now. Start the backend to view history."
      );
      setHistoryItems([]);
    } finally {
      setIsHistoryLoading(false);
    }
  }

  async function loadDiagnostics() {
    setIsDiagnosticsLoading(true);
    setDiagnosticsErrorMessage("");

    try {
      const [healthResponse, providerResponse] = await Promise.all([
        fetch(`${API_BASE_URL}/health`),
        fetch(`${API_BASE_URL}/provider`)
      ]);

      if (!healthResponse.ok || !providerResponse.ok) {
        throw new Error("Unable to load backend diagnostics.");
      }

      const healthPayload = (await healthResponse.json()) as HealthResponse;
      const providerPayload =
        (await providerResponse.json()) as ProviderDiagnostics;

      setHealthStatus(healthPayload);
      setProviderDiagnostics(providerPayload);
    } catch {
      setDiagnosticsErrorMessage(
        "Backend diagnostics are unavailable right now. Start the API to view system status."
      );
      setHealthStatus(null);
      setProviderDiagnostics(null);
    } finally {
      setIsDiagnosticsLoading(false);
    }
  }

  useEffect(() => {
    void loadRecentSearches();
    void loadDiagnostics();
  }, []);

  useEffect(() => {
    const searchParams = new URLSearchParams();
    searchParams.set("latitude", form.latitude);
    searchParams.set("longitude", form.longitude);
    searchParams.set("arrivalTime", form.arrivalTime);

    const nextUrl = `${window.location.pathname}?${searchParams.toString()}`;
    window.history.replaceState(null, "", nextUrl);
  }, [form]);

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setIsLoading(true);
    setErrorMessage("");

    try {
      const response = await fetch(`${API_BASE_URL}/recommendations`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify({
          latitude: Number(form.latitude),
          longitude: Number(form.longitude),
          arrivalTime: form.arrivalTime
        })
      });

      if (!response.ok) {
        const fallbackMessage = "Unable to fetch parking recommendations right now.";

        try {
          const errorPayload = (await response.json()) as { details?: string[] };
          setErrorMessage(errorPayload.details?.[0] ?? fallbackMessage);
        } catch {
          setErrorMessage(fallbackMessage);
        }

        setResult(null);
        return;
      }

      const recommendationResponse =
        (await response.json()) as RecommendationResponse;
      setResult(recommendationResponse);
      await loadRecentSearches();
    } catch {
      setErrorMessage(
        "ParkSense could not reach the backend. Make sure the Spring Boot API is running on localhost:8080."
      );
      setResult(null);
    } finally {
      setIsLoading(false);
    }
  }

  return (
    <main className="page-shell">
      <section className="hero-panel">
        <div className="hero-copy-block">
          <p className="eyebrow">ParkSense</p>
          <h1>Find a stronger parking option before you even leave.</h1>
          <p className="hero-copy">
            Search by destination coordinates and arrival time to get ranked
            parking recommendations from the ParkSense backend.
          </p>

          <div className="hero-actions">
            <a className="primary-link" href={`${API_BASE_URL}/health`}>
              Backend Health
            </a>
            <a className="secondary-link" href={`${API_BASE_URL}/provider`}>
              Provider Status
            </a>
          </div>
        </div>

        <form className="search-panel" onSubmit={handleSubmit}>
          <div className="panel-header">
            <span className="card-label">Recommendation Search</span>
            <p>Use the live backend API to rank nearby parking options.</p>
          </div>

          <div className="preset-grid">
            {searchPresets.map((preset) => (
              <button
                key={preset.label}
                className="preset-card"
                type="button"
                onClick={() => {
                  setForm(preset.form);
                  setErrorMessage("");
                }}
              >
                <strong>{preset.label}</strong>
                <span>{preset.description}</span>
              </button>
            ))}
          </div>

          <label className="field-group">
            <span>Latitude</span>
            <input
              type="number"
              step="0.0001"
              value={form.latitude}
              onChange={(event) =>
                setForm((currentForm) => ({
                  ...currentForm,
                  latitude: event.target.value
                }))
              }
              required
            />
          </label>

          <label className="field-group">
            <span>Longitude</span>
            <input
              type="number"
              step="0.0001"
              value={form.longitude}
              onChange={(event) =>
                setForm((currentForm) => ({
                  ...currentForm,
                  longitude: event.target.value
                }))
              }
              required
            />
          </label>

          <label className="field-group">
            <span>Arrival Time</span>
            <input
              type="datetime-local"
              value={form.arrivalTime}
              onChange={(event) =>
                setForm((currentForm) => ({
                  ...currentForm,
                  arrivalTime: event.target.value
                }))
              }
              required
            />
          </label>

          <button className="submit-button" type="submit" disabled={isLoading}>
            {isLoading ? "Loading Recommendations..." : "Get Recommendations"}
          </button>

          <button
            className="reset-button"
            type="button"
            onClick={() => {
              setForm(initialFormState);
              setErrorMessage("");
            }}
          >
            Reset to default search
          </button>

          {errorMessage ? <p className="status-error">{errorMessage}</p> : null}
        </form>
      </section>

      <section className="info-grid">
        {highlights.map((highlight) => (
          <article className="info-card" key={highlight}>
            <span className="card-label">Current capability</span>
            <p>{highlight}</p>
          </article>
        ))}
      </section>

      <section className="status-panel">
        <div className="results-header">
          <div>
            <span className="card-label">System Status</span>
            <h2>Backend and provider readiness</h2>
          </div>
          <button
            className="secondary-link refresh-button"
            type="button"
            onClick={() => {
              void loadDiagnostics();
            }}
            disabled={isDiagnosticsLoading}
          >
            {isDiagnosticsLoading ? "Refreshing..." : "Refresh Status"}
          </button>
        </div>

        {diagnosticsErrorMessage ? (
          <p className="status-error history-status">{diagnosticsErrorMessage}</p>
        ) : null}

        {healthStatus && providerDiagnostics ? (
          <div className="status-grid">
            <article className="status-card">
              <div className="status-topline">
                <span className="result-provider">Backend</span>
                <span
                  className={
                    healthStatus.status === "UP" ? "status-pill is-good" : "status-pill"
                  }
                >
                  {healthStatus.status}
                </span>
              </div>
              <h3>{healthStatus.application}</h3>
              <p className="result-explanation">
                The Spring Boot API is reachable and ready to serve frontend requests.
              </p>
            </article>

            <article className="status-card">
              <div className="status-topline">
                <span className="result-provider">Provider</span>
                <span
                  className={
                    providerDiagnostics.providerReady
                      ? "status-pill is-good"
                      : "status-pill is-warning"
                  }
                >
                  {providerDiagnostics.providerReady ? "Ready" : "Needs attention"}
                </span>
              </div>
              <h3>{providerDiagnostics.activeProviderType}</h3>
              <p className="result-explanation">
                {providerDiagnostics.statusMessage}
              </p>

              <dl className="metric-grid">
                <div>
                  <dt>Configured</dt>
                  <dd>{providerDiagnostics.configuredProviderType}</dd>
                </div>
                <div>
                  <dt>Fallback</dt>
                  <dd>
                    {providerDiagnostics.fallbackToMockOnFailure ? "Enabled" : "Disabled"}
                  </dd>
                </div>
                <div>
                  <dt>Radius</dt>
                  <dd>{providerDiagnostics.searchRadiusMeters} m</dd>
                </div>
                <div>
                  <dt>Mode</dt>
                  <dd>{providerDiagnostics.activeProviderType}</dd>
                </div>
              </dl>
            </article>
          </div>
        ) : (
          <div className="empty-state">
            <p>
              {isDiagnosticsLoading
                ? "Loading backend diagnostics from ParkSense..."
                : "Backend health and provider readiness will appear here once the API is available."}
            </p>
          </div>
        )}
      </section>

      <section className="results-panel">
        <div className="results-header">
          <div>
            <span className="card-label">Recommendation Results</span>
            <h2>Ranked parking options</h2>
          </div>
          {result ? <p className="summary-badge">{result.bestOptionSummary}</p> : null}
        </div>

        {result ? (
          <div className="results-grid">
            {result.recommendations.map((recommendation) => (
              <article className="result-card" key={recommendation.spotId}>
                <div className="result-topline">
                  <span className="result-provider">
                    {recommendation.providerType}
                  </span>
                  <span className="result-score">
                    Score {recommendation.score.toFixed(1)}
                  </span>
                </div>

                <h3>{recommendation.spotName}</h3>
                <p className="result-address">{recommendation.address}</p>
                <div className="location-actions">
                  <a
                    className="map-link"
                    href={buildGoogleMapsLink(
                      recommendation.latitude,
                      recommendation.longitude
                    )}
                    target="_blank"
                    rel="noreferrer"
                  >
                    Open in Google Maps
                  </a>
                  <span className="location-coordinates">
                    {recommendation.latitude.toFixed(4)},{" "}
                    {recommendation.longitude.toFixed(4)}
                  </span>
                </div>
                <p className="result-explanation">{recommendation.explanation}</p>

                <dl className="metric-grid">
                  <div>
                    <dt>Availability</dt>
                    <dd>{Math.round(recommendation.predictedAvailability * 100)}%</dd>
                  </div>
                  <div>
                    <dt>Price</dt>
                    <dd>${recommendation.predictedPrice.toFixed(2)}</dd>
                  </div>
                  <div>
                    <dt>Distance</dt>
                    <dd>{Math.round(recommendation.distanceMeters)} m</dd>
                  </div>
                  <div>
                    <dt>Category</dt>
                    <dd>{recommendation.category}</dd>
                  </div>
                </dl>
              </article>
            ))}
          </div>
        ) : (
          <div className="empty-state">
            <p>
              Submit a search to see ranked parking recommendations from the
              Spring Boot backend.
            </p>
          </div>
        )}
      </section>

      <section className="history-panel">
        <div className="results-header">
          <div>
            <span className="card-label">Recent Activity</span>
            <h2>Latest parking searches</h2>
          </div>
          <button
            className="secondary-link refresh-button"
            type="button"
            onClick={() => {
              void loadRecentSearches();
            }}
            disabled={isHistoryLoading}
          >
            {isHistoryLoading ? "Refreshing..." : "Refresh History"}
          </button>
        </div>

        {historyErrorMessage ? (
          <p className="status-error history-status">{historyErrorMessage}</p>
        ) : null}

        {historyItems.length > 0 ? (
          <div className="history-grid">
            {historyItems.map((historyItem) => (
              <article className="history-card" key={historyItem.id}>
                <div className="history-topline">
                  <span className="result-provider">Search #{historyItem.id}</span>
                  <span className="history-time">
                    {new Date(historyItem.searchedAt).toLocaleString()}
                  </span>
                </div>

                <p className="history-summary">{historyItem.bestOptionSummary}</p>
                <div className="location-actions">
                  <a
                    className="map-link"
                    href={buildGoogleMapsLink(
                      historyItem.latitude,
                      historyItem.longitude
                    )}
                    target="_blank"
                    rel="noreferrer"
                  >
                    Open search area in Google Maps
                  </a>
                  <button
                    className="map-link action-link"
                    type="button"
                    onClick={() => {
                      setForm({
                        latitude: historyItem.latitude.toString(),
                        longitude: historyItem.longitude.toString(),
                        arrivalTime: toDatetimeLocalValue(historyItem.arrivalTime)
                      });
                      setErrorMessage("");
                      window.scrollTo({ top: 0, behavior: "smooth" });
                    }}
                  >
                    Use this search
                  </button>
                </div>

                <dl className="metric-grid">
                  <div>
                    <dt>Latitude</dt>
                    <dd>{historyItem.latitude.toFixed(4)}</dd>
                  </div>
                  <div>
                    <dt>Longitude</dt>
                    <dd>{historyItem.longitude.toFixed(4)}</dd>
                  </div>
                  <div>
                    <dt>Arrival</dt>
                    <dd>{new Date(historyItem.arrivalTime).toLocaleString()}</dd>
                  </div>
                  <div>
                    <dt>Searched At</dt>
                    <dd>{new Date(historyItem.searchedAt).toLocaleTimeString()}</dd>
                  </div>
                </dl>
              </article>
            ))}
          </div>
        ) : (
          <div className="empty-state">
            <p>
              {isHistoryLoading
                ? "Loading recent parking searches from the backend..."
                : "Your recent recommendation lookups will appear here after you search."}
            </p>
          </div>
        )}
      </section>
    </main>
  );
}

export default App;
