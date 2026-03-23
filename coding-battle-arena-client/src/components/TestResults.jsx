// components/TestResults.jsx
import React from 'react';

function TestResults({ submission }) {

  if (!submission) {
    return (
      <div style={{ padding: '16px 0', textAlign: 'center' }}>
        <div style={{ fontSize: 11, color: '#2e3a55', fontWeight: 700, letterSpacing: '.08em', textTransform: 'uppercase', marginBottom: 6 }}>
          Test Results
        </div>
        <div style={{ fontSize: 12, color: '#3e4d64' }}>
          Submit your code to see results
        </div>
      </div>
    );
  }

  const STATUS = {
    PENDING:            { label: 'Pending',           bg: '#1a1e2a', color: '#64748b', border: '#1e2535' },
    RUNNING:            { label: 'Running...',         bg: '#1a2744', color: '#60a5fa', border: '#1d3a7a' },
    ACCEPTED:           { label: 'Accepted',           bg: '#052e1c', color: '#34d399', border: '#064e35' },
    WRONG_ANSWER:       { label: 'Wrong Answer',       bg: '#2d1414', color: '#f87171', border: '#4d1a1a' },
    TIME_LIMIT_EXCEEDED:{ label: 'Time Limit',         bg: '#2d1f06', color: '#fbbf24', border: '#4d3209' },
    RUNTIME_ERROR:      { label: 'Runtime Error',      bg: '#2d1414', color: '#f87171', border: '#4d1a1a' },
    COMPILATION_ERROR:  { label: 'Compilation Error',  bg: '#2d1414', color: '#f87171', border: '#4d1a1a' },
  };

  const st      = STATUS[submission.status] || { label: submission.status, bg: '#1a1e2a', color: '#64748b', border: '#1e2535' };
  const pct     = submission.totalTestCases > 0 ? (submission.testCasesPassed / submission.totalTestCases) * 100 : 0;
  const accepted = submission.status === 'ACCEPTED';

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 10 }}>

      {/* Status badge */}
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
        <span style={{ fontSize: 10, fontWeight: 700, color: '#64748b', letterSpacing: '.1em', textTransform: 'uppercase' }}>
          Test Results
        </span>
        <span style={{
          fontSize: 10, fontWeight: 700, letterSpacing: '.07em', textTransform: 'uppercase',
          padding: '3px 9px', borderRadius: 4,
          background: st.bg, color: st.color, border: `1px solid ${st.border}`,
        }}>
          {st.label}
        </span>
      </div>

      {/* Score row */}
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', gap: 10 }}>
        <div>
          <div style={{ fontSize: 9, fontWeight: 700, color: '#3e4d64', letterSpacing: '.1em', textTransform: 'uppercase', marginBottom: 3 }}>
            Test Cases
          </div>
          <div style={{ fontSize: 20, fontWeight: 800, color: accepted ? '#34d399' : '#f87171', fontVariantNumeric: 'tabular-nums', letterSpacing: '.04em' }}>
            {submission.testCasesPassed}
            <span style={{ fontSize: 13, color: '#3e4d64', fontWeight: 600 }}>
              /{submission.totalTestCases}
            </span>
          </div>
        </div>
        {submission.executionTimeMs && (
          <div style={{ textAlign: 'right' }}>
            <div style={{ fontSize: 9, fontWeight: 700, color: '#3e4d64', letterSpacing: '.1em', textTransform: 'uppercase', marginBottom: 3 }}>
              Time
            </div>
            <div style={{ fontSize: 14, fontWeight: 700, color: '#60a5fa', fontVariantNumeric: 'tabular-nums' }}>
              {submission.executionTimeMs} ms
            </div>
          </div>
        )}
      </div>

      {/* Progress bar */}
      <div style={{ background: '#0b0d12', border: '1px solid #1e2535', borderRadius: 6, height: 18, overflow: 'hidden' }}>
        <div style={{
          height: '100%',
          width: `${pct}%`,
          background: accepted ? '#052e1c' : '#2d1414',
          borderRight: `2px solid ${accepted ? '#34d399' : '#f87171'}`,
          display: 'flex', alignItems: 'center', justifyContent: 'flex-end',
          paddingRight: 6,
          transition: 'width .4s ease',
          minWidth: pct > 0 ? 32 : 0,
        }}>
          {pct > 10 && (
            <span style={{ fontSize: 10, fontWeight: 700, color: accepted ? '#34d399' : '#f87171' }}>
              {Math.round(pct)}%
            </span>
          )}
        </div>
      </div>

      {/* Error message */}
      {submission.errorMessage && (
        <div style={{
          background: '#1a0808', border: '1px solid #4d1a1a',
          borderRadius: 6, padding: '8px 10px',
        }}>
          <div style={{ fontSize: 9, fontWeight: 700, color: '#f87171', letterSpacing: '.08em', textTransform: 'uppercase', marginBottom: 4 }}>
            Error
          </div>
          <div style={{ fontSize: 11, color: '#94a3b8', lineHeight: 1.6, fontFamily: "'Cascadia Code','Fira Code',monospace", whiteSpace: 'pre-wrap', wordBreak: 'break-word' }}>
            {submission.errorMessage}
          </div>
        </div>
      )}

    </div>
  );
}

export default TestResults;