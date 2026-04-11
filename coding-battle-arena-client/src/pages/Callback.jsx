import React, { useEffect, useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { authService } from '../services/authService';
import Loading from '../components/Loading';

function Callback() {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const [error, setError] = useState(null);

  useEffect(() => {
    const handleCallback = async () => {
      const code = searchParams.get('code');
      const state = searchParams.get('state');
      const errorParam = searchParams.get('error');
      const errorDescription = searchParams.get('error_description');

      if (errorParam) {
        setError(errorDescription || errorParam);
        return;
      }

      if (!code || !state) {
        setError('Invalid callback parameters');
        return;
      }

      const success = await authService.handleCallback(code, state);

      if (success) {
        window.location.href = '/';
      } else {
        setError('Authentication failed. Please try again.');
      }
    };

    handleCallback();
  }, [searchParams, navigate]);

  if (error) {
    return (
      <div className="container mt-5">
        <div className="row justify-content-center">
          <div className="col-md-6">
            <div className="alert alert-danger">
              <h4 className="alert-heading">Authentication Error</h4>
              <p className="mb-0">{error}</p>
            </div>
            <button
              className="btn btn-primary"
              onClick={() => navigate('/login')}
            >
              Try Again
            </button>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="container mt-5">
      <Loading message="Completing authentication..." />
    </div>
  );
}

export default Callback;