import React, { useState, useEffect } from 'react';
import { apiService } from '../services/apiService';
import Loading from '../components/Loading';

function Leaderboard() {
  const [leaderboard, setLeaderboard] = useState([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [error, setError] = useState(null);

  useEffect(() => {
    loadLeaderboard();
  }, [page]);

  const loadLeaderboard = async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await apiService.getLeaderboard(page, 20);
      setLeaderboard(response.content);
      setTotalPages(response.totalPages);
    } catch (error) {
      setError(error.message || 'Failed to load leaderboard');
    } finally {
      setLoading(false);
    }
  };

  const getRankBadge = (rank) => {
    if (rank === 1) return '🥇';
    if (rank === 2) return '🥈';
    if (rank === 3) return '🥉';
    return rank;
  };

  const getRankTitleColor = (title) => {
    switch (title) {
      case 'Grandmaster':
        return 'text-danger';
      case 'Master':
        return 'text-warning';
      case 'Expert':
        return 'text-primary';
      case 'Intermediate':
        return 'text-info';
      default:
        return 'text-secondary';
    }
  };

  return (
    <div className="container mt-4">
      <h2 className="mb-4">🏆 Leaderboard</h2>

      {error && <div className="alert alert-danger">{error}</div>}

      {loading ? (
        <Loading message="Loading leaderboard..." />
      ) : (
        <>
          <div className="card">
            <div className="table-responsive">
              <table className="table table-hover mb-0">
                <thead className="table-dark">
                  <tr>
                    <th style={{ width: '80px' }}>Rank</th>
                    <th>Player</th>
                    <th>Rating</th>
                    <th>Wins</th>
                    <th>Losses</th>
                    <th>Win Rate</th>
                    <th>Title</th>
                  </tr>
                </thead>
                <tbody>
                  {leaderboard.map((entry) => (
                    <tr key={entry.userId}>
                      <td className="fw-bold">{getRankBadge(entry.rank)}</td>
                      <td>{entry.username}</td>
                      <td>
                        <strong>{entry.rating}</strong>
                      </td>
                      <td className="text-success">{entry.wins}</td>
                      <td className="text-danger">{entry.losses}</td>
                      <td>{entry.winRate.toFixed(1)}%</td>
                      <td>
                        <span className={getRankTitleColor(entry.rankTitle)}>
                          {entry.rankTitle}
                        </span>
                      </td>
                    </tr>
                  ))}
                  {leaderboard.length === 0 && (
                    <tr>
                      <td colSpan={7} className="text-center text-muted py-4">
                        No players yet. Be the first to compete!
                      </td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>
          </div>

          {/* Pagination */}
          {totalPages > 1 && (
            <nav className="mt-3">
              <ul className="pagination justify-content-center">
                <li className={`page-item ${page === 0 ? 'disabled' : ''}`}>
                  <button
                    className="page-link"
                    onClick={() => setPage((p) => Math.max(0, p - 1))}
                    disabled={page === 0}
                  >
                    Previous
                  </button>
                </li>
                <li className="page-item disabled">
                  <span className="page-link">
                    {page + 1} / {totalPages}
                  </span>
                </li>
                <li className={`page-item ${page >= totalPages - 1 ? 'disabled' : ''}`}>
                  <button
                    className="page-link"
                    onClick={() => setPage((p) => Math.min(totalPages - 1, p + 1))}
                    disabled={page >= totalPages - 1}
                  >
                    Next
                  </button>
                </li>
              </ul>
            </nav>
          )}
        </>
      )}

      {/* Rank Titles */}
      <div className="card mt-4">
        <div className="card-header">
          <h5 className="mb-0">📊 Rank Titles</h5>
        </div>
        <div className="card-body">
          <div className="row text-center">
            <div className="col">
              <span className="text-danger fw-bold">Grandmaster</span>
              <br />
              <small className="text-muted">2400+</small>
            </div>
            <div className="col">
              <span className="text-warning fw-bold">Master</span>
              <br />
              <small className="text-muted">2000-2399</small>
            </div>
            <div className="col">
              <span className="text-primary fw-bold">Expert</span>
              <br />
              <small className="text-muted">1600-1999</small>
            </div>
            <div className="col">
              <span className="text-info fw-bold">Intermediate</span>
              <br />
              <small className="text-muted">1200-1599</small>
            </div>
            <div className="col">
              <span className="text-secondary fw-bold">Beginner</span>
              <br />
              <small className="text-muted">800-1199</small>
            </div>
            <div className="col">
              <span className="text-secondary fw-bold">Novice</span>
              <br />
              <small className="text-muted">&lt;800</small>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default Leaderboard;