import React from 'react';
import { Link, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const styles = `
  @import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600&display=swap');

  .nav-wrap {
    background: #111827;
    border-bottom: 1px solid #1f2937;
    font-family: 'Inter', sans-serif;
    position: sticky;
    top: 0;
    z-index: 100;
  }

  .nav-inner {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 1.5rem;
    height: 56px;
    display: flex;
    align-items: center;
    gap: 2rem;
  }

  .nav-brand {
    text-decoration: none;
    font-size: 1.05rem;
    font-weight: 600;
    color: #f9fafb;
    letter-spacing: 0.02em;
    display: flex;
    align-items: center;
    gap: 0.45rem;
    flex-shrink: 0;
  }

  .nav-brand:hover { color: #f9fafb; }

  .nav-links {
    display: flex;
    align-items: center;
    gap: 0.1rem;
    flex: 1;
  }

  .nav-link {
    text-decoration: none;
    color: #9ca3af;
    font-size: 0.9rem;
    font-weight: 500;
    padding: 0.35rem 0.75rem;
    border-radius: 6px;
    transition: color 0.15s, background 0.15s;
  }

  .nav-link:hover {
    color: #f3f4f6;
    background: #1f2937;
  }

  .nav-link.active {
    color: #f9fafb;
    background: #1f2937;
  }

  .nav-right {
    display: flex;
    align-items: center;
    gap: 0.5rem;
  }

  .nav-user {
    text-decoration: none;
    display: flex;
    align-items: center;
    gap: 0.5rem;
    color: #d1d5db;
    font-size: 0.9rem;
    font-weight: 500;
    padding: 0.35rem 0.75rem;
    border-radius: 6px;
    transition: background 0.15s, color 0.15s;
  }

  .nav-user:hover {
    background: #1f2937;
    color: #f9fafb;
  }

  .nav-rating {
    font-size: 0.75rem;
    font-weight: 600;
    color: #6b7280;
    background: #1f2937;
    border: 1px solid #374151;
    padding: 0.1rem 0.45rem;
    border-radius: 10px;
  }

  .nav-divider {
    width: 1px;
    height: 20px;
    background: #1f2937;
  }

  .nav-logout {
    background: none;
    border: none;
    color: #9ca3af;
    font-family: 'Inter', sans-serif;
    font-size: 0.9rem;
    font-weight: 500;
    padding: 0.35rem 0.75rem;
    border-radius: 6px;
    cursor: pointer;
    transition: color 0.15s, background 0.15s;
  }

  .nav-logout:hover {
    color: #f87171;
    background: rgba(239,68,68,0.08);
  }

  .nav-login {
    text-decoration: none;
    color: #9ca3af;
    font-size: 0.9rem;
    font-weight: 500;
    padding: 0.35rem 0.75rem;
    border-radius: 6px;
    transition: color 0.15s, background 0.15s;
  }

  .nav-login:hover {
    color: #f9fafb;
    background: #1f2937;
  }
`;

function Navbar() {
  const { isAuthenticated, user, logout } = useAuth();
  const location = useLocation();

  const isActive = (path) =>
    location.pathname === path ? 'nav-link active' : 'nav-link';

  return (
    <>
      <style>{styles}</style>
      <nav className="nav-wrap">
        <div className="nav-inner">

          <Link className="nav-brand" to="/">
            ⚔️ Coding Arena
          </Link>

          <div className="nav-links">
            <Link className={isActive('/')} to="/">Home</Link>
            {isAuthenticated && (
              <Link className={isActive('/lobby')} to="/lobby">Lobby</Link>
            )}
            <Link className={isActive('/leaderboard')} to="/leaderboard">Leaderboard</Link>
          </div>

          <div className="nav-right">
            {isAuthenticated ? (
              <>
                <Link className="nav-user" to="/profile">
                  👤 {user?.username || 'Profile'}
                  {user?.rating != null && (
                    <span className="nav-rating">{user.rating}</span>
                  )}
                </Link>
                <div className="nav-divider" />
                <button className="nav-logout" onClick={logout}>
                  Logout
                </button>
              </>
            ) : (
              <Link className="nav-login" to="/login">Login</Link>
            )}
          </div>

        </div>
      </nav>
    </>
  );
}

export default Navbar;