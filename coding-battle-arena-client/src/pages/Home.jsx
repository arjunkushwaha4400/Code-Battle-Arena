import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { apiService } from '../services/apiService';

const styles = `
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap');

.home {
  min-height: 100vh;
  background: radial-gradient(circle at top, #0f172a, #020617);
  color: #cbd5e1;
  font-family: 'Inter', sans-serif;
}

/* HERO */
.h-hero {
  padding: 5rem 1.5rem 3.5rem;
  text-align: center;
  position: relative;
}

.h-hero::before {
  content: '';
  position: absolute;
  inset: 0;
  background: radial-gradient(circle at 50% 0%, rgba(59,130,246,0.15), transparent 70%);
  pointer-events: none;
}

.h-title {
  font-size: clamp(2.8rem, 5vw, 4rem);
  font-weight: 700;
  color: #f8fafc;
}

.h-title-accent {
  background: linear-gradient(90deg, #60a5fa, #a78bfa);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.h-desc {
  color: #94a3b8;
  margin-top: 1rem;
  margin-bottom: 2rem;
  font-size: 1.05rem;
}

.h-actions {
  display: flex;
  gap: 1rem;
  justify-content: center;
  z-index: 2;
  position: relative;
}

.h-btn-solid {
  padding: 0.85rem 2rem;
  background: linear-gradient(135deg, #3b82f6, #6366f1);
  color: white;
  border-radius: 12px;
  font-weight: 600;
  text-decoration: none;
  box-shadow: 0 12px 35px rgba(59,130,246,0.5);
  transition: 0.25s;
}

.h-btn-solid:hover {
  transform: translateY(-3px) scale(1.05);
  box-shadow: 0 18px 45px rgba(59,130,246,0.7);
}

.h-btn-soft {
  padding: 0.85rem 2rem;
  border-radius: 12px;
  text-decoration: none;
  color: #cbd5e1;
  background: rgba(255,255,255,0.05);
}

.h-btn-soft:hover {
  background: rgba(255,255,255,0.1);
}

/* BODY */
.h-body {
  max-width: 1000px;
  margin: auto;
  padding: 2rem 1.5rem 4rem;
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

/* STATS */
.h-stats {
  display: flex;
  justify-content: space-between;
  background: linear-gradient(145deg, rgba(15,23,42,0.9), rgba(2,6,23,0.9));
  backdrop-filter: blur(16px);
  border: 1px solid rgba(255,255,255,0.08);
  padding: 1.5rem 2.5rem;
  border-radius: 16px;
  box-shadow: 0 15px 50px rgba(0,0,0,0.5);
}

.h-stat {
  text-align: center;
}

.h-sv {
  font-size: 2rem;
  font-weight: 700;
  color: #60a5fa;
}

.h-stat:nth-child(2) .h-sv { color: #34d399; }
.h-stat:nth-child(3) .h-sv { color: #f87171; }
.h-stat:nth-child(4) .h-sv { color: #facc15; }

.h-sl {
  font-size: 0.75rem;
  color: #64748b;
}

/* GRID */
.h-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1.5rem;
}

/* CARD */
.h-card {
  background: linear-gradient(145deg, rgba(15,23,42,0.85), rgba(2,6,23,0.85));
  backdrop-filter: blur(14px);
  border: 1px solid rgba(255,255,255,0.06);
  border-radius: 16px;
  transition: 0.25s;
  box-shadow: 0 10px 40px rgba(0,0,0,0.4);
}

.h-card:hover {
  transform: translateY(-6px);
  border-color: rgba(59,130,246,0.4);
  box-shadow: 0 20px 60px rgba(0,0,0,0.6);
}

.h-card-hd {
  padding: 1.2rem;
  border-bottom: 1px solid rgba(255,255,255,0.05);
  font-weight: 600;
  color: #e2e8f0;
}

.h-card-bd {
  padding: 1.2rem;
}

/* STEPS */
.h-step {
  display: flex;
  gap: 0.8rem;
  margin-bottom: 0.8rem;
}

.h-sn {
  width: 26px;
  height: 26px;
  background: linear-gradient(135deg, #3b82f6, #6366f1);
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.75rem;
  font-weight: 600;
}

/* TABLE */
.h-lb {
  width: 100%;
}

.h-lb td {
  padding: 0.6rem 0;
  font-size: 0.9rem;
}

.h-lb tr:hover td {
  background: rgba(59,130,246,0.1);
}

.h-lb td:nth-child(2) {
  color: #e2e8f0;
  font-weight: 500;
}

.h-lb td:nth-child(3) {
  color: #60a5fa;
  font-weight: 600;
}

/* FEATURES */
.h-feats {
  display: grid;
  grid-template-columns: repeat(4,1fr);
  gap: 1rem;
}

.h-feat {
  background: linear-gradient(145deg, rgba(15,23,42,0.8), rgba(2,6,23,0.8));
  border: 1px solid rgba(255,255,255,0.06);
  padding: 1.2rem;
  text-align: center;
  border-radius: 12px;
  transition: 0.25s;
  cursor: pointer;
}

.h-feat:hover {
  transform: translateY(-6px) scale(1.03);
  border-color: rgba(99,102,241,0.5);
  box-shadow: 0 10px 40px rgba(0,0,0,0.5);
}

@media (max-width: 768px) {
  .h-grid { grid-template-columns: 1fr; }
  .h-feats { grid-template-columns: 1fr 1fr; }
}
`;

const STEPS = [
  'Enter the lobby and find a match',
  'Get paired with similar rating',
  'Solve faster than opponent',
  'Pass all test cases first',
  'Climb the rankings',
];

export default function Home() {
  const { isAuthenticated, user } = useAuth();
  const [topPlayers, setTopPlayers] = useState([]);

  useEffect(() => {
    apiService.getTopPlayers(5).then(setTopPlayers);
  }, []);

  return (
    <>
      <style>{styles}</style>

      <div className="home">

        <section className="h-hero">
          <h1 className="h-title">
            Code. Compete.<br />
            <span className="h-title-accent">Conquer.</span>
          </h1>

          <p className="h-desc">
            Think ahead. Make your move. Outplay your opponent.
          </p>

          <div className="h-actions">
            <Link to="/lobby" className="h-btn-solid">
              ♟️ Go to Lobby
            </Link>
            <Link to="/leaderboard" className="h-btn-soft">
              🏆 Leaderboard
            </Link>
          </div>
        </section>

        <div className="h-body">

          {isAuthenticated && user && (
            <div className="h-stats">
              <div className="h-stat"><div className="h-sv">{user.rating}</div><div className="h-sl">Rating</div></div>
              <div className="h-stat"><div className="h-sv">{user.wins}</div><div className="h-sl">Wins</div></div>
              <div className="h-stat"><div className="h-sv">{user.losses}</div><div className="h-sl">Losses</div></div>
              <div className="h-stat"><div className="h-sv">{user.rankTitle}</div><div className="h-sl">Rank</div></div>
            </div>
          )}

          <div className="h-grid">

            <div className="h-card">
              <div className="h-card-hd">How to Play</div>
              <div className="h-card-bd">
                {STEPS.map((s, i) => (
                  <div key={i} className="h-step">
                    <span className="h-sn">{i+1}</span>
                    <span>{s}</span>
                  </div>
                ))}
              </div>
            </div>

            <div className="h-card">
              <div className="h-card-hd">Top Players</div>
              <div className="h-card-bd">
                <table className="h-lb">
                  <tbody>
                    {topPlayers.map(p => (
                      <tr key={p.userId}>
                        <td>{p.rank}</td>
                        <td>{p.username}</td>
                        <td>{p.rating}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>

          </div>

          <div className="h-feats">
            <div className="h-feat">⚔️<br/>Live Battles</div>
            <div className="h-feat">🤖<br/>AI Hints</div>
            <div className="h-feat">🏆<br/>Ranked</div>
            <div className="h-feat">🌍<br/>Multi-lang</div>
          </div>

        </div>
      </div>
    </>
  );
}