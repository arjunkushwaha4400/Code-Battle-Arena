import React, { useState, useEffect } from 'react';

function Countdown({ seconds, onComplete }) {
  const [count, setCount] = useState(seconds);

  useEffect(() => {
    if (count <= 0) {
      if (onComplete) {
        onComplete();
      }
      return;
    }

    const timer = setTimeout(() => {
      setCount(count - 1);
    }, 1000);

    return () => clearTimeout(timer);
  }, [count, onComplete]);

  const getMessage = () => {
    if (count === 0) return 'GO!';
    return count;
  };

  const getColor = () => {
    if (count === 0) return 'text-success';
    if (count === 1) return 'text-danger';
    if (count === 2) return 'text-warning';
    return 'text-primary';
  };

  return (
    <div
      className="position-fixed top-0 start-0 w-100 h-100 d-flex align-items-center justify-content-center"
      style={{
        backgroundColor: 'rgba(0,0,0,0.85)',
        zIndex: 9999,
        animation: 'fadeIn 0.3s'
      }}
    >
      <div className="text-center">
        <div
          className={`display-1 fw-bold ${getColor()}`}
          style={{
            fontSize: '15rem',
            animation: 'pulse 1s infinite',
            textShadow: '0 0 30px currentColor'
          }}
        >
          {getMessage()}
        </div>
        <p className="lead text-white mt-4">
          {count > 0 ? 'Get Ready!' : 'Battle Started!'}
        </p>
      </div>

      <style>{`
        @keyframes pulse {
          0%, 100% { transform: scale(1); }
          50% { transform: scale(1.1); }
        }
        @keyframes fadeIn {
          from { opacity: 0; }
          to { opacity: 1; }
        }
      `}</style>
    </div>
  );
}

export default Countdown;