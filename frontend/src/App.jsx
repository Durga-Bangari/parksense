const highlights = [
  "Heuristic parking recommendation engine",
  "Google Maps-ready provider architecture",
  "Persistent recent search history",
  "Frontend-ready API contracts for web and mobile"
];

function App() {
  return (
    <main className="page-shell">
      <section className="hero-panel">
        <p className="eyebrow">ParkSense</p>
        <h1>Smart parking recommendations with a product-ready backend.</h1>
        <p className="hero-copy">
          This frontend is the starting point for a full ParkSense experience.
          The backend already supports recommendation scoring, provider
          diagnostics, and persistent recent search history.
        </p>

        <div className="hero-actions">
          <a className="primary-link" href="http://localhost:8080/api/v1/health">
            Backend Health
          </a>
          <a className="secondary-link" href="http://localhost:8080/api/v1/provider">
            Provider Status
          </a>
        </div>
      </section>

      <section className="info-grid">
        {highlights.map((highlight) => (
          <article className="info-card" key={highlight}>
            <span className="card-label">Current capability</span>
            <p>{highlight}</p>
          </article>
        ))}
      </section>
    </main>
  );
}

export default App;
