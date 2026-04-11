import React, { useState, useEffect, useRef } from 'react';

function Timer({
  totalSeconds = 1800, // Default 30 minutes
  startTime,
  isRunning,
  onTimeUp,
  serverTime // For synchronization
}) {
  const [remainingSeconds, setRemainingSeconds] = useState(totalSeconds);
  const intervalRef = useRef(null);
  const startTimeRef = useRef(null);

  useEffect(() => {
    if (startTime) {
      startTimeRef.current = new Date(startTime).getTime();
    }
  }, [startTime]);

  useEffect(() => {
    if (!isRunning) {
      if (intervalRef.current) {
        clearInterval(intervalRef.current);
        intervalRef.current = null;
      }
      return;
    }

    // Calculate initial remaining time based on server time
    const calculateRemaining = () => {
      if (!startTimeRef.current) return totalSeconds;

      const now = Date.now();
      const elapsed = Math.floor((now - startTimeRef.current) / 1000);
      const remaining = Math.max(0, totalSeconds - elapsed);
      return remaining;
    };

    // Set initial value
    setRemainingSeconds(calculateRemaining());

    // Update every second
    intervalRef.current = setInterval(() => {
      const remaining = calculateRemaining();
      setRemainingSeconds(remaining);

      if (remaining <= 0) {
        clearInterval(intervalRef.current);
        intervalRef.current = null;
        if (onTimeUp) {
          onTimeUp();
        }
      }
    }, 1000);

    return () => {
      if (intervalRef.current) {
        clearInterval(intervalRef.current);
        intervalRef.current = null;
      }
    };
  }, [isRunning, totalSeconds, onTimeUp]);

  // Sync with server time if provided
  useEffect(() => {
    if (serverTime && startTimeRef.current) {
      const serverDate = new Date(serverTime).getTime();
      const elapsed = Math.floor((serverDate - startTimeRef.current) / 1000);
      const remaining = Math.max(0, totalSeconds - elapsed);
      setRemainingSeconds(remaining);
    }
  }, [serverTime, totalSeconds]);

  const formatTime = (seconds) => {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
  };

  const getTimerColor = () => {
    if (remainingSeconds <= 60) return 'text-danger';
    if (remainingSeconds <= 300) return 'text-warning';
    return 'text-dark';
  };

  return (
    <div className="d-flex align-items-center">
      <span className="me-2">⏱️</span>
      <span className={`font-monospace fs-4 fw-bold ${getTimerColor()}`}>
        {formatTime(remainingSeconds)}
      </span>
      {remainingSeconds <= 60 && isRunning && (
        <span className="ms-2 text-danger">⚠️</span>
      )}
    </div>
  );
}

export default Timer;