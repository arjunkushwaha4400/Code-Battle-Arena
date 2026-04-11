import React, { useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

function Login() {
  const { isAuthenticated, login } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const from = location.state?.from?.pathname || '/';

  useEffect(() => {
    if (isAuthenticated) {
      navigate(from, { replace: true });
    }
  }, [isAuthenticated, navigate, from]);

  const handleLogin = () => {
    login();
  };

  return (
    <div className="container mt-5">
      <div className="row justify-content-center">
        <div className="col-md-6 col-lg-4">
          <div className="card">
            <div className="card-body text-center">
              <h2 className="card-title mb-4">⚔️ Coding Battle Arena</h2>
              <p className="text-muted mb-4">
                Compete in real-time coding battles against other players
              </p>

              <button
                className="btn btn-primary btn-lg w-100 mb-3"
                onClick={handleLogin}
              >
                🔐 Sign In / Sign Up
              </button>

              <p className="text-muted small">
                Sign in with your account or Google
              </p>

              <hr />

              <h6 className="mb-3">Features:</h6>
              <ul className="list-unstyled text-start">
                <li className="mb-2">✓ Real-time 1v1 coding battles</li>
                <li className="mb-2">✓ AI-powered hints</li>
                <li className="mb-2">✓ Global leaderboard</li>
                <li className="mb-2">✓ Multiple languages (Java, Python, JS)</li>
              </ul>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default Login;