import React, { useState, useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { apiService } from '../services/apiService';
import { websocketService } from '../services/websocketService';
import Loading from '../components/Loading';

// ─── Inline styles ────────────────────────────────────────────────────────────
const styles = `
  @import url('https://fonts.googleapis.com/css2?family=Rajdhani:wght@400;500;600;700&family=JetBrains+Mono:wght@400;600&display=swap');

  .lobby-root {
    min-height: 100vh;
    background: #080b14;
    background-image:
      radial-gradient(ellipse 80% 50% at 50% -10%, rgba(56,189,248,0.08) 0%, transparent 60%),
      linear-gradient(180deg, #080b14 0%, #0d1220 100%);
    font-family: 'Rajdhani', sans-serif;
    color: #e2e8f0;
    padding: 2rem 1rem 4rem;
  }

  /* ── grid lines bg ── */
  .lobby-root::before {
    content: '';
    position: fixed;
    inset: 0;
    background-image:
      linear-gradient(rgba(56,189,248,0.03) 1px, transparent 1px),
      linear-gradient(90deg, rgba(56,189,248,0.03) 1px, transparent 1px);
    background-size: 40px 40px;
    pointer-events: none;
    z-index: 0;
  }

  .lobby-inner {
    position: relative;
    z-index: 1;
    max-width: 1100px;
    margin: 0 auto;
  }

  /* ── Header ── */
  .lobby-header {
    display: flex;
    align-items: center;
    gap: 1rem;
    margin-bottom: 2.5rem;
    animation: fadeSlideDown 0.5s ease both;
  }

  .lobby-title {
    font-size: 2.4rem;
    font-weight: 700;
    letter-spacing: 0.06em;
    text-transform: uppercase;
    background: linear-gradient(90deg, #38bdf8, #818cf8);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
    margin: 0;
    line-height: 1;
  }

  .lobby-subtitle {
    font-size: 0.9rem;
    color: #64748b;
    letter-spacing: 0.15em;
    text-transform: uppercase;
    font-family: 'JetBrains Mono', monospace;
    margin-top: 0.25rem;
  }

  /* ── Alert ── */
  .lobby-alert {
    border-radius: 8px;
    padding: 0.75rem 1.25rem;
    margin-bottom: 1.5rem;
    font-size: 0.95rem;
    letter-spacing: 0.03em;
    display: flex;
    align-items: center;
    gap: 0.6rem;
    animation: fadeSlideDown 0.4s ease both;
  }

  .lobby-alert-warn {
    background: rgba(251,191,36,0.1);
    border: 1px solid rgba(251,191,36,0.3);
    color: #fbbf24;
  }

  .lobby-alert-error {
    background: rgba(239,68,68,0.1);
    border: 1px solid rgba(239,68,68,0.3);
    color: #f87171;
  }

  .lobby-alert button {
    margin-left: auto;
    background: none;
    border: none;
    color: inherit;
    cursor: pointer;
    opacity: 0.7;
    font-size: 1.1rem;
    line-height: 1;
  }

  .lobby-alert button:hover { opacity: 1; }

  /* ── Cards grid ── */
  .lobby-cards {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 1.25rem;
    margin-bottom: 2rem;
  }

  @media (max-width: 900px) {
    .lobby-cards { grid-template-columns: 1fr; }
  }

  /* ── Single card ── */
  .lcard {
    border-radius: 12px;
    overflow: hidden;
    background: rgba(15,23,42,0.8);
    border: 1px solid rgba(255,255,255,0.06);
    backdrop-filter: blur(12px);
    display: flex;
    flex-direction: column;
    transition: transform 0.2s ease, border-color 0.2s ease, box-shadow 0.2s ease;
    animation: fadeSlideUp 0.5s ease both;
  }

  .lcard:nth-child(2) { animation-delay: 0.07s; }
  .lcard:nth-child(3) { animation-delay: 0.14s; }

  .lcard:hover {
    transform: translateY(-4px);
    border-color: rgba(56,189,248,0.25);
    box-shadow: 0 16px 40px rgba(0,0,0,0.5), 0 0 0 1px rgba(56,189,248,0.1);
  }

  /* accent variants */
  .lcard-quick:hover { border-color: rgba(56,189,248,0.35); }
  .lcard-create:hover { border-color: rgba(52,211,153,0.35); }
  .lcard-join:hover   { border-color: rgba(167,139,250,0.35); }

  /* ── Card header ── */
  .lcard-head {
    padding: 1rem 1.25rem;
    display: flex;
    align-items: center;
    gap: 0.7rem;
    border-bottom: 1px solid rgba(255,255,255,0.05);
  }

  .lcard-quick .lcard-head { background: linear-gradient(135deg, rgba(56,189,248,0.15), rgba(56,189,248,0.05)); }
  .lcard-create .lcard-head { background: linear-gradient(135deg, rgba(52,211,153,0.15), rgba(52,211,153,0.05)); }
  .lcard-join .lcard-head   { background: linear-gradient(135deg, rgba(167,139,250,0.15), rgba(167,139,250,0.05)); }

  .lcard-icon {
    font-size: 1.4rem;
    line-height: 1;
  }

  .lcard-title {
    font-size: 1.15rem;
    font-weight: 700;
    letter-spacing: 0.08em;
    text-transform: uppercase;
    margin: 0;
  }

  .lcard-quick .lcard-title { color: #38bdf8; }
  .lcard-create .lcard-title { color: #34d399; }
  .lcard-join .lcard-title   { color: #a78bfa; }

  /* ── Card body ── */
  .lcard-body {
    padding: 1.25rem;
    flex: 1;
    display: flex;
    flex-direction: column;
    gap: 1rem;
  }

  .lcard-desc {
    font-size: 0.9rem;
    color: #64748b;
    letter-spacing: 0.03em;
    margin: 0;
  }

  /* ── Form elements ── */
  .lfield label {
    display: block;
    font-size: 0.78rem;
    letter-spacing: 0.12em;
    text-transform: uppercase;
    color: #94a3b8;
    margin-bottom: 0.4rem;
    font-family: 'JetBrains Mono', monospace;
  }

  .lselect, .linput {
    width: 100%;
    background: rgba(255,255,255,0.04);
    border: 1px solid rgba(255,255,255,0.1);
    border-radius: 7px;
    color: #e2e8f0;
    padding: 0.6rem 0.9rem;
    font-family: 'Rajdhani', sans-serif;
    font-size: 1rem;
    font-weight: 600;
    letter-spacing: 0.05em;
    outline: none;
    transition: border-color 0.2s, background 0.2s;
    box-sizing: border-box;
    appearance: none;
    -webkit-appearance: none;
  }

  .lselect:focus, .linput:focus {
    border-color: rgba(56,189,248,0.5);
    background: rgba(56,189,248,0.05);
  }

  .lselect option { background: #0d1220; color: #e2e8f0; }

  .linput {
    text-align: center;
    font-size: 1.4rem;
    letter-spacing: 0.4em;
    font-family: 'JetBrains Mono', monospace;
  }

  .linput::placeholder { color: #334155; letter-spacing: 0.2em; }

  /* ── Buttons ── */
  .lbtn {
    width: 100%;
    padding: 0.75rem 1rem;
    border: none;
    border-radius: 8px;
    font-family: 'Rajdhani', sans-serif;
    font-size: 1.05rem;
    font-weight: 700;
    letter-spacing: 0.1em;
    text-transform: uppercase;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 0.5rem;
    transition: opacity 0.15s, transform 0.15s, box-shadow 0.15s;
    margin-top: auto;
  }

  .lbtn:disabled { opacity: 0.45; cursor: not-allowed; transform: none !important; }
  .lbtn:not(:disabled):hover { opacity: 0.9; transform: translateY(-1px); }
  .lbtn:not(:disabled):active { transform: translateY(0); }

  .lbtn-quick {
    background: linear-gradient(135deg, #0ea5e9, #2563eb);
    color: #fff;
    box-shadow: 0 4px 20px rgba(14,165,233,0.3);
  }

  .lbtn-create {
    background: linear-gradient(135deg, #059669, #0d9488);
    color: #fff;
    box-shadow: 0 4px 20px rgba(5,150,105,0.3);
  }

  .lbtn-join {
    background: linear-gradient(135deg, #7c3aed, #6d28d9);
    color: #fff;
    box-shadow: 0 4px 20px rgba(124,58,237,0.3);
  }

  .lbtn-cancel {
    background: rgba(239,68,68,0.1);
    border: 1px solid rgba(239,68,68,0.3);
    color: #f87171;
  }

  /* ── Matchmaking state ── */
  .mm-state {
    background: rgba(14,165,233,0.07);
    border: 1px solid rgba(14,165,233,0.2);
    border-radius: 8px;
    padding: 1rem;
    display: flex;
    align-items: center;
    gap: 0.9rem;
  }

  .mm-spinner {
    width: 28px;
    height: 28px;
    border: 3px solid rgba(56,189,248,0.2);
    border-top-color: #38bdf8;
    border-radius: 50%;
    flex-shrink: 0;
    animation: spin 0.8s linear infinite;
  }

  .mm-label {
    font-size: 0.95rem;
    font-weight: 600;
    color: #38bdf8;
    letter-spacing: 0.05em;
  }

  .mm-sub {
    font-size: 0.78rem;
    color: #64748b;
    font-family: 'JetBrains Mono', monospace;
    margin-top: 0.15rem;
  }

  /* ── Active rooms table ── */
  .rooms-section {
    background: rgba(15,23,42,0.8);
    border: 1px solid rgba(255,255,255,0.06);
    border-radius: 12px;
    overflow: hidden;
    animation: fadeSlideUp 0.5s 0.2s ease both;
  }

  .rooms-head {
    padding: 1rem 1.5rem;
    border-bottom: 1px solid rgba(255,255,255,0.06);
    display: flex;
    align-items: center;
    gap: 0.6rem;
    background: rgba(255,255,255,0.02);
  }

  .rooms-head-title {
    font-size: 1rem;
    font-weight: 700;
    letter-spacing: 0.1em;
    text-transform: uppercase;
    color: #94a3b8;
    margin: 0;
  }

  .rooms-table {
    width: 100%;
    border-collapse: collapse;
  }

  .rooms-table th {
    padding: 0.65rem 1.25rem;
    font-size: 0.72rem;
    letter-spacing: 0.15em;
    text-transform: uppercase;
    color: #475569;
    font-family: 'JetBrains Mono', monospace;
    font-weight: 400;
    text-align: left;
    border-bottom: 1px solid rgba(255,255,255,0.05);
  }

  .rooms-table td {
    padding: 0.85rem 1.25rem;
    border-bottom: 1px solid rgba(255,255,255,0.04);
    font-size: 0.95rem;
    vertical-align: middle;
  }

  .rooms-table tr:last-child td { border-bottom: none; }

  .rooms-table tr:hover td { background: rgba(255,255,255,0.02); }

  .room-code {
    font-family: 'JetBrains Mono', monospace;
    font-size: 1rem;
    font-weight: 600;
    color: #38bdf8;
    letter-spacing: 0.1em;
  }

  .badge-status {
    display: inline-block;
    padding: 0.2rem 0.65rem;
    border-radius: 20px;
    font-size: 0.72rem;
    font-weight: 700;
    letter-spacing: 0.1em;
    text-transform: uppercase;
    font-family: 'JetBrains Mono', monospace;
  }

  .badge-waiting    { background: rgba(251,191,36,0.15); color: #fbbf24; border: 1px solid rgba(251,191,36,0.3); }
  .badge-progress   { background: rgba(52,211,153,0.15); color: #34d399; border: 1px solid rgba(52,211,153,0.3); }
  .badge-completed  { background: rgba(148,163,184,0.1); color: #94a3b8; border: 1px solid rgba(148,163,184,0.2); }
  .badge-abandoned  { background: rgba(239,68,68,0.1);   color: #f87171; border: 1px solid rgba(239,68,68,0.25); }

  .tbl-btn {
    padding: 0.3rem 0.8rem;
    border-radius: 6px;
    font-family: 'Rajdhani', sans-serif;
    font-size: 0.85rem;
    font-weight: 700;
    letter-spacing: 0.06em;
    cursor: pointer;
    border: none;
    transition: opacity 0.15s;
    margin-right: 0.4rem;
  }

  .tbl-btn:hover { opacity: 0.8; }

  .tbl-btn-continue {
    background: rgba(14,165,233,0.15);
    color: #38bdf8;
    border: 1px solid rgba(14,165,233,0.3);
  }

  .tbl-btn-leave {
    background: rgba(239,68,68,0.1);
    color: #f87171;
    border: 1px solid rgba(239,68,68,0.3);
  }

  /* ── WS indicator ── */
  .ws-dot {
    display: inline-block;
    width: 7px;
    height: 7px;
    border-radius: 50%;
    margin-right: 0.4rem;
    vertical-align: middle;
  }

  .ws-dot-on  { background: #34d399; box-shadow: 0 0 6px #34d399; }
  .ws-dot-off { background: #f87171; animation: pulse-red 1s infinite; }

  /* ── Animations ── */
  @keyframes fadeSlideDown {
    from { opacity: 0; transform: translateY(-16px); }
    to   { opacity: 1; transform: translateY(0); }
  }

  @keyframes fadeSlideUp {
    from { opacity: 0; transform: translateY(20px); }
    to   { opacity: 1; transform: translateY(0); }
  }

  @keyframes spin {
    to { transform: rotate(360deg); }
  }

  @keyframes pulse-red {
    0%, 100% { opacity: 1; }
    50%       { opacity: 0.3; }
  }
`;

// ─── Component ────────────────────────────────────────────────────────────────
function Lobby() {
  const navigate = useNavigate();
  const [activeRooms, setActiveRooms] = useState([]);
  const [loading, setLoading] = useState(true);
  const [creating, setCreating] = useState(false);
  const [joining, setJoining] = useState(false);
  const [matchmaking, setMatchmaking] = useState(false);
  const [roomCode, setRoomCode] = useState('');
  const [difficulty, setDifficulty] = useState('MEDIUM');
  const [error, setError] = useState(null);
  const [queueStatus, setQueueStatus] = useState(null);
  const [wsConnected, setWsConnected] = useState(false);

  const handleMatchFound = useCallback((message) => {
    const data = message.payload;
    setMatchmaking(false);
    setQueueStatus(null);
    if (data.roomCode) {
      navigate(`/battle/${data.roomCode}`, { replace: true });
    } else if (data.roomId) {
      apiService.getRoomById(data.roomId).then(room => {
        navigate(`/battle/${room.roomCode}`, { replace: true });
      });
    }
  }, [navigate]);

  useEffect(() => {
    loadActiveRooms();
    setupWebSocket();
    return () => { websocketService.clearHandlers('MATCH_FOUND'); };
  }, []);

  useEffect(() => {
    if (wsConnected) {
      const unsub = websocketService.onMessage('MATCH_FOUND', handleMatchFound);
      return () => unsub();
    }
  }, [wsConnected, handleMatchFound]);

  const setupWebSocket = async () => {
    try {
      await websocketService.connect();
      setWsConnected(true);
    } catch {
      setError('Failed to connect to game server. Please refresh.');
    }
  };

  const loadActiveRooms = async () => {
    try {
      const rooms = await apiService.getMyRooms();
      setActiveRooms(rooms);
    } catch { /* silent */ }
    finally { setLoading(false); }
  };

  const handleCreateRoom = async () => {
    setCreating(true); setError(null);
    try {
      const room = await apiService.createRoom({ difficulty, isRanked: true });
      navigate(`/battle/${room.roomCode}`);
    } catch (e) {
      setError(e.message || 'Failed to create room');
      setCreating(false);
    }
  };

  const handleJoinRoom = async () => {
    if (!roomCode.trim()) { setError('Please enter a room code'); return; }
    setJoining(true); setError(null);
    try {
      const room = await apiService.joinRoom(roomCode.trim().toUpperCase());
      navigate(`/battle/${room.roomCode}`);
    } catch (e) {
      setError(e.message || 'Failed to join room');
      setJoining(false);
    }
  };

  const handleFindMatch = async () => {
    setMatchmaking(true); setError(null);
    try {
      if (!websocketService.isConnected()) {
        await websocketService.connect(); setWsConnected(true);
      }
      const response = await apiService.joinMatchmaking({ preferredDifficulty: difficulty, isRanked: true });
      setQueueStatus(response);
      if (response.status === 'MATCHED' && response.roomCode) {
        navigate(`/battle/${response.roomCode}`);
      }
    } catch (e) {
      setError(e.message || 'Failed to join matchmaking');
      setMatchmaking(false);
    }
  };

  const handleCancelMatchmaking = async () => {
    try { await apiService.leaveMatchmaking(); } catch { /* silent */ }
    setMatchmaking(false); setQueueStatus(null);
  };

  const handleContinueRoom = (code) => navigate(`/battle/${code}`);

  const handleLeaveRoom = async (roomId, e) => {
    e.stopPropagation();
    try { await apiService.leaveRoom(roomId); loadActiveRooms(); } catch { /* silent */ }
  };

  const badgeClass = (status) => {
    switch (status) {
      case 'WAITING':     return 'badge-status badge-waiting';
      case 'IN_PROGRESS': return 'badge-status badge-progress';
      case 'ABANDONED':   return 'badge-status badge-abandoned';
      default:            return 'badge-status badge-completed';
    }
  };

  if (loading) return (
    <div style={{ background: '#080b14', minHeight: '100vh', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
      <Loading message="Loading lobby..." />
    </div>
  );

  return (
    <>
      <style>{styles}</style>
      <div className="lobby-root">
        <div className="lobby-inner">

          {/* Header */}
          <div className="lobby-header">
            <div>
              <h1 className="lobby-title">⚔ Battle Lobby</h1>
              <div className="lobby-subtitle">
                <span className={`ws-dot ${wsConnected ? 'ws-dot-on' : 'ws-dot-off'}`}></span>
                {wsConnected ? 'Server connected' : 'Connecting…'}
              </div>
            </div>
          </div>

          {/* Alerts */}
          {!wsConnected && (
            <div className="lobby-alert lobby-alert-warn">
              ⚠ Connecting to game server…
            </div>
          )}
          {error && (
            <div className="lobby-alert lobby-alert-error">
              ✕ {error}
              <button onClick={() => setError(null)}>✕</button>
            </div>
          )}

          {/* Cards */}
          <div className="lobby-cards">

            {/* Quick Match */}
            <div className="lcard lcard-quick">
              <div className="lcard-head">
                <span className="lcard-icon">⚡</span>
                <h3 className="lcard-title">Quick Match</h3>
              </div>
              <div className="lcard-body">
                <p className="lcard-desc">Find an opponent with a similar rating and go head-to-head.</p>
                <div className="lfield">
                  <label>Difficulty</label>
                  <select className="lselect" value={difficulty} onChange={e => setDifficulty(e.target.value)} disabled={matchmaking}>
                    <option value="EASY">Easy</option>
                    <option value="MEDIUM">Medium</option>
                    <option value="HARD">Hard</option>
                  </select>
                </div>

                {matchmaking ? (
                  <>
                    <div className="mm-state">
                      <div className="mm-spinner"></div>
                      <div>
                        <div className="mm-label">Searching for opponent…</div>
                        {queueStatus?.queuePosition && (
                          <div className="mm-sub">Queue position: #{queueStatus.queuePosition}</div>
                        )}
                      </div>
                    </div>
                    <button className="lbtn lbtn-cancel" onClick={handleCancelMatchmaking}>
                      ✕ Cancel Search
                    </button>
                  </>
                ) : (
                  <button className="lbtn lbtn-quick" onClick={handleFindMatch} disabled={!wsConnected}>
                    🔍 Find Match
                  </button>
                )}
              </div>
            </div>

            {/* Create Room */}
            <div className="lcard lcard-create">
              <div className="lcard-head">
                <span className="lcard-icon">➕</span>
                <h3 className="lcard-title">Create Room</h3>
              </div>
              <div className="lcard-body">
                <p className="lcard-desc">Start a private room and share the code with a friend.</p>
                <div className="lfield">
                  <label>Difficulty</label>
                  <select className="lselect" value={difficulty} onChange={e => setDifficulty(e.target.value)} disabled={creating}>
                    <option value="EASY">Easy</option>
                    <option value="MEDIUM">Medium</option>
                    <option value="HARD">Hard</option>
                  </select>
                </div>
                <button className="lbtn lbtn-create" onClick={handleCreateRoom} disabled={creating}>
                  {creating ? <><span style={{width:16,height:16,border:'2px solid rgba(255,255,255,0.3)',borderTopColor:'#fff',borderRadius:'50%',display:'inline-block',animation:'spin 0.7s linear infinite'}}></span> Creating…</> : '🏠 Create Room'}
                </button>
              </div>
            </div>

            {/* Join Room */}
            <div className="lcard lcard-join">
              <div className="lcard-head">
                <span className="lcard-icon">🚪</span>
                <h3 className="lcard-title">Join Room</h3>
              </div>
              <div className="lcard-body">
                <p className="lcard-desc">Enter a 6-character room code to jump into an existing battle.</p>
                <div className="lfield">
                  <label>Room Code</label>
                  <input
                    className="linput"
                    type="text"
                    placeholder="ABC123"
                    value={roomCode}
                    onChange={e => setRoomCode(e.target.value.toUpperCase())}
                    maxLength={6}
                    disabled={joining}
                  />
                </div>
                <button className="lbtn lbtn-join" onClick={handleJoinRoom} disabled={joining || !roomCode.trim()}>
                  {joining ? <><span style={{width:16,height:16,border:'2px solid rgba(255,255,255,0.3)',borderTopColor:'#fff',borderRadius:'50%',display:'inline-block',animation:'spin 0.7s linear infinite'}}></span> Joining…</> : '🎯 Join Room'}
                </button>
              </div>
            </div>

          </div>

          {/* Active Rooms */}
          {activeRooms.length > 0 && (
            <div className="rooms-section">
              <div className="rooms-head">
                <span style={{fontSize:'1.1rem'}}>📋</span>
                <h4 className="rooms-head-title">Your Active Rooms</h4>
                <span style={{marginLeft:'auto',fontFamily:"'JetBrains Mono',monospace",fontSize:'0.75rem',color:'#475569'}}>{activeRooms.length} room{activeRooms.length !== 1 ? 's' : ''}</span>
              </div>
              <div style={{overflowX:'auto'}}>
                <table className="rooms-table">
                  <thead>
                    <tr>
                      <th>Room Code</th>
                      <th>Status</th>
                      <th>Players</th>
                      <th>Created</th>
                      <th>Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    {activeRooms.map(room => (
                      <tr key={room.id}>
                        <td><span className="room-code">{room.roomCode}</span></td>
                        <td><span className={badgeClass(room.status)}>{room.status.replace('_', ' ')}</span></td>
                        <td style={{fontFamily:"'JetBrains Mono',monospace",fontSize:'0.9rem',color:'#94a3b8'}}>
                          {room.participants.length} / {room.maxPlayers}
                        </td>
                        <td style={{color:'#64748b',fontSize:'0.88rem',fontFamily:"'JetBrains Mono',monospace"}}>
                          {new Date(room.createdAt).toLocaleTimeString()}
                        </td>
                        <td>
                          <button className="tbl-btn tbl-btn-continue" onClick={() => handleContinueRoom(room.roomCode)}>
                            Continue →
                          </button>
                          {room.status === 'WAITING' && (
                            <button className="tbl-btn tbl-btn-leave" onClick={e => handleLeaveRoom(room.id, e)}>
                              Leave
                            </button>
                          )}
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          )}

        </div>
      </div>
    </>
  );
}

export default Lobby;