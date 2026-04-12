import { FormEvent, useState } from "react";

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

type SearchForm = {
  latitude: string;
  longitude: string;
  arrivalTime: string;
};

const API_BASE_URL = "http://localhost:8080/api/v1";

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

function App() {
  const [form, setForm] = useState<SearchForm>(initialFormState);
  const [result, setResult] = useState<RecommendationResponse | null>(null);
  const [errorMessage, setErrorMessage] = useState<string>("");
  const [isLoading, setIsLoading] = useState<boolean>(false);

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
    </main>
  );
}

export default App;
