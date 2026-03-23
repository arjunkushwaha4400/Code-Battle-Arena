// pages/BattleArena.jsx

import React, { useState, useEffect, useCallback, useRef } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { apiService } from '../services/apiService';
import { websocketService } from '../services/websocketService';
import Loading from '../components/Loading';
import CodeEditor from '../components/CodeEditor';
// Timer replaced with inline hook — avoids external component rendering unwanted icons
function useCountdown(totalSeconds, startTime, isRunning, onTimeUp) {
  const [remaining, setRemaining] = React.useState(totalSeconds);
  React.useEffect(() => {
    if (!isRunning || !startTime) return;
    const tick = () => {
      const elapsed = Math.floor((Date.now() - startTime.getTime()) / 1000);
      const left = Math.max(0, totalSeconds - elapsed);
      setRemaining(left);
      if (left === 0 && onTimeUp) onTimeUp();
    };
    tick();
    const id = setInterval(tick, 1000);
    return () => clearInterval(id);
  }, [isRunning, startTime, totalSeconds]);
  const m = String(Math.floor(remaining / 60)).padStart(2, '0');
  const s = String(remaining % 60).padStart(2, '0');
  const urgent  = remaining < 300;  // last 5 min → red
  const warning = remaining < 600;  // last 10 min → amber
  return { display: `${m}:${s}`, urgent, warning };
}
import TestResults from '../components/TestResults';
import ChatBox from '../components/ChatBox';
import Countdown from '../components/Countdown';

const BATTLE_TIME_SECONDS = 1800;

// ── Injected global styles ─────────────────────────────────────────────────────
const GLOBAL_CSS = `
  .arena-root {
    background: #0b0d12 !important;
    color: #e2e8f0 !important;
    min-height: 100vh;
  }
  .arena-card {
    background: #0f1219 !important;
    border: 1px solid #1e2535 !important;
    border-radius: 8px !important;
    color: #e2e8f0 !important;
  }
  .arena-card .card-header {
    background: #13161f !important;
    border-bottom: 1px solid #1e2535 !important;
    color: #e2e8f0 !important;
  }
  .arena-card .card-body {
    background: #0f1219 !important;
    color: #e2e8f0 !important;
  }
  .arena-card pre {
    background: #0b0d12 !important;
    border: 1px solid #1e2535 !important;
    color: #e2e8f0 !important;
    border-radius: 4px !important;
  }
  .arena-card .sticky-top {
    background: #13161f !important;
  }
  .arena-topbar {
    background: #0f1219;
    border-bottom: 1px solid #1e2535;
    padding: 0 20px;
    height: 52px;
    display: flex;
    align-items: center;
    gap: 12px;
  }
  .arena-badge-room {
    font-size: 13px;
    font-weight: 700;
    color: #60a5fa;
    background: #1a2744;
    padding: 3px 10px;
    border-radius: 5px;
    border: 1px solid #1d3a7a;
    letter-spacing: .1em;
    font-family: monospace;
  }
  .arena-badge-live {
    font-size: 10px;
    font-weight: 700;
    letter-spacing: .08em;
    text-transform: uppercase;
    padding: 3px 9px;
    border-radius: 4px;
    background: #052e1c;
    color: #34d399;
    border: 1px solid #064e35;
  }
  .arena-badge-wait {
    font-size: 10px;
    font-weight: 700;
    letter-spacing: .08em;
    text-transform: uppercase;
    padding: 3px 9px;
    border-radius: 4px;
    background: #2d1f06;
    color: #fbbf24;
    border: 1px solid #4d3209;
  }
  .arena-badge-done {
    font-size: 10px;
    font-weight: 700;
    letter-spacing: .08em;
    text-transform: uppercase;
    padding: 3px 9px;
    border-radius: 4px;
    background: #1a1e2a;
    color: #64748b;
    border: 1px solid #1e2535;
  }
  .arena-timer {
    display: flex;
    align-items: center;
    gap: 8px;
    background: #13161f;
    border: 1px solid #1e2535;
    border-radius: 6px;
    padding: 5px 14px;
  }
  .arena-timer-dot { width: 7px; height: 7px; border-radius: 50%; background: #34d399; }
  .arena-timer-val { font-size: 17px; font-weight: 700; color: #e2e8f0; font-variant-numeric: tabular-nums; letter-spacing: .06em; }
  .arena-btn-forfeit {
    background: transparent;
    border: 1px solid #4d1a1a;
    color: #f87171;
    border-radius: 5px;
    padding: 5px 14px;
    font-size: 12px;
    font-weight: 600;
    cursor: pointer;
    transition: background .15s;
  }
  .arena-btn-forfeit:hover { background: #2d1414; }
  .arena-players-bar {
    display: grid;
    grid-template-columns: 1fr 52px 1fr;
    align-items: center;
    gap: 10px;
    padding: 8px 20px;
    background: #0f1219;
    border-bottom: 1px solid #1e2535;
  }
  .arena-pcard {
    display: flex;
    align-items: center;
    gap: 10px;
    background: #13161f;
    border-radius: 8px;
    padding: 8px 14px;
  }
  .arena-pcard-me   { border: 1px solid #1d3a7a; }
  .arena-pcard-opp  { border: 1px solid #4d1a1a; flex-direction: row-reverse; }
  .arena-pcard-wait { border: 1px solid #1e2535; justify-content: center; }
  .arena-plabel-me  { font-size: 9px; font-weight: 700; letter-spacing: .1em; text-transform: uppercase; color: #60a5fa; margin-bottom: 2px; }
  .arena-plabel-opp { font-size: 9px; font-weight: 700; letter-spacing: .1em; text-transform: uppercase; color: #f87171; margin-bottom: 2px; text-align: right; }
  .arena-pname { font-size: 14px; font-weight: 700; color: #e2e8f0; }
  .arena-prating { font-size: 11px; color: #64748b; background: #0b0d12; border: 1px solid #1e2535; border-radius: 3px; padding: 1px 6px; }
  .arena-vs { font-size: 13px; font-weight: 800; color: #2e3a55; text-align: center; letter-spacing: .1em; }
  .arena-pstatus-me  { margin-left: auto; font-size: 11px; font-weight: 700; }
  .arena-pstatus-opp { margin-right: auto; font-size: 11px; font-weight: 700; }
  .arena-panel-label {
    font-size: 10px;
    font-weight: 700;
    color: #64748b;
    letter-spacing: .1em;
    text-transform: uppercase;
  }
  .arena-diff-easy   { font-size:10px;font-weight:700;letter-spacing:.08em;text-transform:uppercase;padding:2px 8px;border-radius:4px;background:#052e1c;color:#34d399;border:1px solid #064e35; }
  .arena-diff-medium { font-size:10px;font-weight:700;letter-spacing:.08em;text-transform:uppercase;padding:2px 8px;border-radius:4px;background:#2d1f06;color:#fbbf24;border:1px solid #4d3209; }
  .arena-diff-hard   { font-size:10px;font-weight:700;letter-spacing:.08em;text-transform:uppercase;padding:2px 8px;border-radius:4px;background:#2d1414;color:#f87171;border:1px solid #4d1a1a; }
  .arena-section-label {
    font-size: 9px;
    font-weight: 700;
    letter-spacing: .12em;
    text-transform: uppercase;
    color: #60a5fa;
    margin: 14px 0 5px;
  }
  .arena-section-label-amber { color: #fbbf24; }
  .arena-code-block {
    background: #0b0d12 !important;
    border: 1px solid #1e2535 !important;
    border-radius: 5px;
    padding: 7px 10px;
    font-size: 12px;
    font-family: 'Cascadia Code','Fira Code',monospace;
    color: #e2e8f0 !important;
    line-height: 1.6;
    margin: 0;
  }
  .arena-constraints-block {
    background: #2d1f06 !important;
    border: 1px solid #4d3209 !important;
    border-radius: 5px;
    padding: 7px 10px;
    font-size: 12px;
    font-family: 'Cascadia Code','Fira Code',monospace;
    color: #e2e8f0 !important;
    line-height: 1.6;
  }
  .arena-tc-box {
    background: #0b0d12;
    border: 1px solid #1e2535;
    border-radius: 7px;
    padding: 10px 12px;
    margin-bottom: 8px;
  }
  .arena-tc-label { font-size:9px;font-weight:700;letter-spacing:.1em;text-transform:uppercase;color:#2e3a55;margin-bottom:4px; }
  .arena-submit-btn {
    flex: 1;
    background: #052e1c;
    color: #34d399;
    border: 1px solid #064e35;
    border-radius: 6px;
    padding: 11px 0;
    font-size: 13px;
    font-weight: 700;
    cursor: pointer;
    letter-spacing: .05em;
    transition: all .15s;
  }
  .arena-submit-btn:disabled {
    background: #1a1e2a;
    color: #475569;
    border-color: #1e2535;
    cursor: not-allowed;
  }
  .arena-submit-btn:not(:disabled):hover { background: #064e35; }
  .arena-hint-btn {
    background: #2d1f06;
    color: #fbbf24;
    border: 1px solid #4d3209;
    border-radius: 6px;
    padding: 11px 16px;
    font-size: 13px;
    font-weight: 700;
    cursor: pointer;
    white-space: nowrap;
    transition: all .15s;
  }
  .arena-hint-btn:disabled {
    background: #1a1e2a;
    color: #475569;
    border-color: #1e2535;
    cursor: not-allowed;
  }
  .arena-hint-card {
    background: #2d1f06;
    border: 1px solid #4d3209;
    border-radius: 7px;
    padding: 10px 12px;
    margin-bottom: 10px;
  }
  .arena-hint-label { font-size:9px;font-weight:700;letter-spacing:.1em;text-transform:uppercase;color:#fbbf24;margin-bottom:5px; }
  .arena-chat-header {
    padding: 10px 14px;
    border-bottom: 1px solid #1e2535;
    display: flex;
    align-items: center;
    gap: 8px;
    background: #13161f;
    flex-shrink: 0;
  }
  .arena-chat-dot { width:6px;height:6px;border-radius:50%;background:#34d399; }
  .arena-ready-card-on  { background:#052e1c;border:1px solid #064e35;border-radius:10px;padding:16px 28px;text-align:center;min-width:130px;transition:all .3s; }
  .arena-ready-card-off { background:#13161f;border:1px solid #1e2535;border-radius:10px;padding:16px 28px;text-align:center;min-width:130px;transition:all .3s; }
  .arena-ready-btn-on  { background:#052e1c;color:#34d399;border:1px solid #064e35;border-radius:8px;padding:12px 44px;font-size:14px;font-weight:700;cursor:pointer;letter-spacing:.05em;transition:all .2s; }
  .arena-ready-btn-off { background:#60a5fa;color:#0b0d12;border:none;border-radius:8px;padding:12px 44px;font-size:14px;font-weight:700;cursor:pointer;letter-spacing:.05em;transition:all .2s; }
  .arena-big-code {
    font-size: 52px;
    font-weight: 800;
    color: #60a5fa;
    background: #1a2744;
    padding: 18px 36px;
    border-radius: 12px;
    border: 1px solid #1d3a7a;
    letter-spacing: .28em;
    font-family: monospace;
  }
  .arena-end-overlay {
    position: fixed;
    inset: 0;
    background: rgba(0,0,0,0.92);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 9998;
  }
  .arena-end-card {
    background: #0f1219;
    border-radius: 16px;
    overflow: hidden;
    min-width: 420px;
    text-align: center;
  }
  .arena-end-won  { border: 1px solid #064e35; }
  .arena-end-lost { border: 1px solid #4d1a1a; }
  .arena-end-header-won  { background:#052e1c;border-bottom:1px solid #064e35;padding:36px 48px; }
  .arena-end-header-lost { background:#2d1414;border-bottom:1px solid #4d1a1a;padding:36px 48px; }
  .arena-end-body { padding: 28px 36px; }
  .arena-end-title-won  { font-size:30px;font-weight:800;color:#34d399;letter-spacing:.06em; }
  .arena-end-title-lost { font-size:30px;font-weight:800;color:#f87171;letter-spacing:.06em; }
  .arena-back-btn { background:#60a5fa;color:#0b0d12;border:none;border-radius:8px;padding:11px 32px;font-size:13px;font-weight:700;cursor:pointer;letter-spacing:.05em; }
  .arena-error-bar {
    background: #2d1414;
    border: 1px solid #4d1a1a;
    color: #f87171;
    font-size: 13px;
    padding: 8px 20px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 0;
    border-radius: 0;
  }
  .arena-warn-badge {
    font-size:10px;font-weight:700;color:#fbbf24;background:#2d1f06;
    border:1px solid #4d3209;border-radius:4px;padding:3px 9px;letter-spacing:.06em;
  }
  @keyframes arena-spin { to { transform: rotate(360deg); } }
  .arena-spinner {
    width:8px;height:8px;border-radius:50%;
    border:2px solid #2e3a55;border-top-color:#64748b;
    animation: arena-spin .8s linear infinite;
    display:inline-block;
  }
  /* scrollbar styling */
  .arena-scroll::-webkit-scrollbar { width: 4px; }
  .arena-scroll::-webkit-scrollbar-track { background: transparent; }
  .arena-scroll::-webkit-scrollbar-thumb { background: #1e2535; border-radius: 2px; }
  /* prob text */
  .arena-prob-text { font-size:13px;line-height:1.75;color:#94a3b8; }
  .arena-prob-text strong { color:#e2e8f0; }
  .arena-prob-text code { color:#a78bfa;background:#1e1040;padding:1px 5px;border-radius:3px;font-size:11px; }
`;

function BattleArena() {
  const { roomCode } = useParams();
  const navigate = useNavigate();
  const { user } = useAuth();

  // Room state
  const [room, setRoom] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [wsConnected, setWsConnected] = useState(false);

  // Battle state
  const [isReady, setIsReady] = useState(false);
  const [battleStarted, setBattleStarted] = useState(false);
  const [battleEnded, setBattleEnded] = useState(false);
  const [showCountdown, setShowCountdown] = useState(false);
  const [countdownValue, setCountdownValue] = useState(3);
  const [startTime, setStartTime] = useState(null);
  const [winner, setWinner] = useState(null);

  // Code state
  const [code, setCode] = useState('');
  const [language, setLanguage] = useState('JAVA');
  const [submitting, setSubmitting] = useState(false);
  const [submission, setSubmission] = useState(null);

  // Opponent state
  const [opponentSubmitted, setOpponentSubmitted] = useState(false);
  const [opponentPassed, setOpponentPassed] = useState(0);

  // Hints state
  const [hintsUsed, setHintsUsed] = useState(0);
  const [currentHint, setCurrentHint] = useState(null);
  const [requestingHint, setRequestingHint] = useState(false);

  // Chat state
  const [chatMessages, setChatMessages] = useState([]);

  // Refs
  const unsubscribersRef = useRef([]);
  const pollingIntervalRef = useRef(null);
  const roomIdRef = useRef(null);
  const codeInitializedRef = useRef(false);
  const battleStartedRef = useRef(false);
  const battleEndedRef = useRef(false);

  useEffect(() => { battleStartedRef.current = battleStarted; }, [battleStarted]);
  useEffect(() => { battleEndedRef.current = battleEnded; }, [battleEnded]);

  useEffect(() => {
    if (!roomCode) { navigate('/lobby'); return; }
    initializeRoom();
    return () => { cleanup(); };
  }, [roomCode]);

  const cleanup = () => {
    unsubscribersRef.current.forEach(unsub => { try { unsub(); } catch (e) {} });
    unsubscribersRef.current = [];
    if (pollingIntervalRef.current) { clearInterval(pollingIntervalRef.current); pollingIntervalRef.current = null; }
  };

  const initializeRoom = async () => {
    try {
      const roomData = await loadRoom();
      await setupWebSocket(roomData.id);
      startPolling();
    } catch (err) {
      console.error('Failed to initialize room:', err);
      setError(err.message || 'Failed to load room');
    } finally {
      setLoading(false);
    }
  };

  const loadRoom = async () => {
    const roomData = await apiService.getRoomByCode(roomCode);
    console.log('Loaded room data:', roomData);
    roomIdRef.current = roomData.id;
    updateRoomState(roomData);
    return roomData;
  };

  const updateRoomState = (roomData) => {
    setRoom(roomData);
    const myParticipant = roomData.participants?.find(p => p.userId === user?.id);
    if (myParticipant) { setIsReady(myParticipant.isReady); setHintsUsed(myParticipant.hintsUsed || 0); }
    if (roomData.problem && !codeInitializedRef.current) { codeInitializedRef.current = true; setInitialCode(roomData.problem, language); }
    if (roomData.status === 'IN_PROGRESS' && !battleStartedRef.current) {
      battleStartedRef.current = true; setBattleStarted(true);
      setStartTime(roomData.startedAt ? new Date(roomData.startedAt) : new Date());
    } else if (roomData.status === 'COMPLETED' && !battleEndedRef.current) {
      battleEndedRef.current = true; setBattleEnded(true); setWinner(roomData.winnerId || null);
    }
    const opponent = roomData.participants?.find(p => p.userId !== user?.id);
    if (opponent) { setOpponentSubmitted(opponent.hasSubmitted || false); setOpponentPassed(opponent.testCasesPassed || 0); }
  };

  const startPolling = () => {
    pollingIntervalRef.current = setInterval(async () => {
      if (battleEndedRef.current) { clearInterval(pollingIntervalRef.current); pollingIntervalRef.current = null; return; }
      try { const roomData = await apiService.getRoomByCode(roomCode); updateRoomState(roomData); } catch (e) { console.error('Polling error:', e); }
    }, 3000);
  };

  const setupWebSocket = async (roomUuid) => {
    try {
      await websocketService.connect();
      setWsConnected(true);
      websocketService.subscribeToRoom(roomUuid);
      const handlers = [
        websocketService.onMessage('PLAYER_JOINED', handlePlayerJoined),
        websocketService.onMessage('PLAYER_LEFT', handlePlayerLeft),
        websocketService.onMessage('PLAYER_READY', handlePlayerReady),
        websocketService.onMessage('COUNTDOWN', handleCountdown),
        websocketService.onMessage('BATTLE_START', handleBattleStart),
        websocketService.onMessage('BATTLE_END', handleBattleEnd),
        websocketService.onMessage('SUBMISSION_RESULT', handleSubmissionResult),
        websocketService.onMessage('CODE_SUBMITTED', handleCodeSubmitted),
        websocketService.onMessage('OPPONENT_SUBMITTED', handleOpponentSubmitted),
        websocketService.onMessage('OPPONENT_RESULT', handleOpponentResult),
        websocketService.onMessage('HINT_RESPONSE', handleHintResponse),
        websocketService.onMessage('CHAT_MESSAGE', handleChatMessage),
        websocketService.onMessage('ERROR', handleWsError),
      ];
    // ✅ Unsubscribe any previously registered handlers BEFORE pushing new ones
    // This prevents handler stacking when setupWebSocket is called more than once
    unsubscribersRef.current.forEach(unsub => { try { unsub(); } catch (e) {} });
    unsubscribersRef.current = [];
    unsubscribersRef.current.push(...handlers);
    } catch (err) { console.error('WebSocket setup failed:', err); }
  };

  const setInitialCode = (problem, lang) => {
    const map = {
      JAVA: problem.starterCodeJava || '// Write your Java code here\n\nimport java.util.*;\n\npublic class Main {\n    public static void main(String[] args) {\n        Scanner sc = new Scanner(System.in);\n        // Your code here\n    }\n}',
      PYTHON: problem.starterCodePython || '# Write your Python code here\n\n# Read input\n# Your code here',
      JAVASCRIPT: problem.starterCodeJavascript || '// Write your JavaScript code here\n\nconst readline = require("readline");\nconst rl = readline.createInterface({ input: process.stdin });\n\n// Your code here',
    };
    setCode(map[lang] || '// Write your code here');
  };

  // ── WebSocket handlers (logic UNCHANGED) ────────────────────────────────────

  const handlePlayerJoined = useCallback((message) => {
    console.log('Player joined:', message.payload);
    if (message.payload) updateRoomState(message.payload); else loadRoom();
  }, []);

  const handlePlayerLeft = useCallback((message) => {
    console.log('Player left:', message.payload);
    loadRoom();
  }, []);

  const handlePlayerReady = useCallback((message) => {
    console.log('Player ready update:', message.payload);
    if (message.payload) updateRoomState(message.payload); else loadRoom();
  }, []);

  const handleCountdown = useCallback((message) => {
    const seconds = message.payload?.seconds || message.payload;
    setCountdownValue(seconds);
    setShowCountdown(true);
  }, []);

  const handleBattleStart = useCallback((message) => {
    setShowCountdown(false);
    battleStartedRef.current = true;
    setBattleStarted(true);
    setStartTime(new Date());
    if (message.payload?.problem) {
      setRoom(prev => prev ? { ...prev, problem: message.payload.problem, status: 'IN_PROGRESS' } : null);
      if (!codeInitializedRef.current) { codeInitializedRef.current = true; setInitialCode(message.payload.problem, language); }
    } else if (message.payload?.id) { updateRoomState(message.payload); }
    loadRoom();
  }, [language]);

  const handleBattleEnd = useCallback((message) => {
    battleEndedRef.current = true;
    setBattleEnded(true);
    setWinner(message.payload?.winnerId || null);
    if (pollingIntervalRef.current) { clearInterval(pollingIntervalRef.current); pollingIntervalRef.current = null; }
  }, []);

  const handleSubmissionResult = useCallback((message) => {
    setSubmission(message.payload);
    setSubmitting(false);
    if (message.payload?.status === 'ACCEPTED') loadRoom();
  }, []);

  const handleCodeSubmitted = useCallback((message) => {
    setSubmission(message.payload);
    setSubmitting(false);
  }, []);

  const handleOpponentSubmitted = useCallback(() => { setOpponentSubmitted(true); }, []);

  const handleOpponentResult = useCallback((message) => {
    setOpponentPassed(message.payload?.testCasesPassed || 0);
    if (message.payload?.status === 'ACCEPTED') loadRoom();
  }, []);

  const handleHintResponse = useCallback((message) => {
    setCurrentHint(message.payload?.hint || message.payload);
    setHintsUsed(prev => prev + 1);
    setRequestingHint(false);
  }, []);

  const handleChatMessage = useCallback((message) => {
    const chatMsg = message.payload;
    if (chatMsg.userId === user?.id) return;
    setChatMessages(prev => [...prev, {
      ...chatMsg,
      id: chatMsg.id || `${chatMsg.userId}-${chatMsg.timestamp}-${Date.now()}`,
    }]);
  }, [user?.id]);

  const handleWsError = useCallback((message) => {
    setError(typeof message.payload === 'string' ? message.payload : 'An error occurred');
  }, []);

  // ── User actions (logic UNCHANGED) ──────────────────────────────────────────

  const handleReady = async () => {
    if (!room) return;
    const newReady = !isReady;
    setIsReady(newReady);
    websocketService.sendReady(room.id, newReady);
    setTimeout(() => loadRoom(), 500);
  };

  const handleSubmit = async () => {
    if (!room || !code.trim() || submitting) return;
    setSubmitting(true);
    setError(null);
    try {
      const result = await apiService.submitCode({ roomId: room.id, code, language });
      setSubmission(result);
    } catch (err) {
      setError(err.message || 'Failed to submit code');
      setSubmitting(false);
    }
  };

  const handleRequestHint = async () => {
    if (!room || hintsUsed >= 3 || requestingHint) return;
    setRequestingHint(true);
    try {
      const hint = await apiService.getHint({ problemId: room.problem?.id, problemDescription: room.problem?.description, currentCode: code, hintLevel: hintsUsed + 1, programmingLanguage: language });
      setCurrentHint(hint.hint);
      setHintsUsed(prev => prev + 1);
    } catch (err) {
      websocketService.sendHintRequest(room.id, code, hintsUsed + 1);
    } finally {
      setRequestingHint(false);
    }
  };

  const handleSendChat = (message) => {
    if (!room) return;
    websocketService.sendChatMessage(room.id, message);
    setChatMessages(prev => [...prev, {
      id: Date.now().toString(),
      userId: user?.id,
      username: user?.username,
      message,
      timestamp: new Date().toISOString(),
    }]);
  };

  const handleLeave = async () => {
    if (!room) { navigate('/lobby'); return; }
    if (battleStarted && !battleEnded) {
      if (!window.confirm('Are you sure you want to leave? You will forfeit the battle.')) return;
    }
    try { if (!battleEnded) await apiService.leaveRoom(room.id); } catch (err) { console.error('Failed to leave room:', err); }
    cleanup();
    navigate('/lobby');
  };

  const handleTimeUp = () => { loadRoom(); };

  const handleLanguageChange = (newLang) => {
    setLanguage(newLang);
    if (room?.problem && !battleStartedRef.current) setInitialCode(room.problem, newLang);
  };

  const handleCountdownComplete = () => {
    setShowCountdown(false);
    battleStartedRef.current = true;
    setBattleStarted(true);
    setStartTime(new Date());
    loadRoom();
  };

  // ── Countdown hook ───────────────────────────────────────────────────────────
  const timer = useCountdown(BATTLE_TIME_SECONDS, startTime, battleStarted && !battleEnded, handleTimeUp);

  const getOpponent = () => room?.participants?.find(p => p.userId !== user?.id);
  const getMyParticipant = () => room?.participants?.find(p => p.userId === user?.id);
  const allPlayersReady = () => {
    if (!room?.participants || room.participants.length < 2) return false;
    return room.participants.every(p => p.isReady);
  };

  // ── Loading / error states ───────────────────────────────────────────────────

  if (loading) return (
    <div style={{ minHeight: '100vh', background: '#0b0d12', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
      <Loading message="Loading battle arena..." />
    </div>
  );

  if (error && !room) return (
    <div style={{ minHeight: '100vh', background: '#0b0d12', display: 'flex', alignItems: 'center', justifyContent: 'center', flexDirection: 'column', gap: 16 }}>
      <div style={{ background: '#2d1414', border: '1px solid #4d1a1a', color: '#f87171', borderRadius: 8, padding: '12px 24px', fontSize: 14 }}>{error}</div>
      <button onClick={() => navigate('/lobby')} style={{ background: '#1a2744', border: '1px solid #1d3a7a', color: '#60a5fa', borderRadius: 6, padding: '9px 20px', fontSize: 13, fontWeight: 700, cursor: 'pointer' }}>← Back to Lobby</button>
    </div>
  );

  if (!room) return (
    <div style={{ minHeight: '100vh', background: '#0b0d12', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
      <div style={{ color: '#64748b', fontSize: 14 }}>Room not found</div>
    </div>
  );

  const opponent = getOpponent();
  const myParticipant = getMyParticipant();
  const won = winner === user?.id;

  const statusBadgeClass = room.status === 'IN_PROGRESS' ? 'arena-badge-live' : room.status === 'WAITING' ? 'arena-badge-wait' : 'arena-badge-done';
  const statusLabel = room.status === 'IN_PROGRESS' ? '⬤ Live' : room.status === 'WAITING' ? '⏳ Waiting' : '✓ Done';
  const diffClass = room.problem?.difficulty === 'EASY' ? 'arena-diff-easy' : room.problem?.difficulty === 'MEDIUM' ? 'arena-diff-medium' : 'arena-diff-hard';

  return (
    <div className="arena-root">
      {/* inject styles once */}
      <style>{GLOBAL_CSS}</style>

      {showCountdown && <Countdown seconds={countdownValue} onComplete={handleCountdownComplete} />}

      {/* ── Battle End Overlay ── */}
      {battleEnded && (
        <div className="arena-end-overlay">
          <div className={`arena-end-card ${won ? 'arena-end-won' : 'arena-end-lost'}`}>
            <div className={won ? 'arena-end-header-won' : 'arena-end-header-lost'}>
              <div style={{ fontSize: 52, marginBottom: 10 }}>{won ? '🏆' : '💀'}</div>
              <div className={won ? 'arena-end-title-won' : 'arena-end-title-lost'}>
                {won ? 'VICTORY' : 'DEFEAT'}
              </div>
            </div>
            <div className="arena-end-body">
              <p style={{ fontSize: 14, color: '#64748b', marginBottom: 20, lineHeight: 1.7 }}>
                {won ? 'Congratulations! You solved it first.' : winner ? 'Your opponent was faster. Better luck next time!' : 'The battle has ended.'}
              </p>
              {submission && (
                <div style={{ background: '#13161f', border: '1px solid #1e2535', borderRadius: 8, padding: '10px 16px', marginBottom: 20, fontSize: 13 }}>
                  <span style={{ color: '#64748b' }}>Your result: </span>
                  <span style={{ fontWeight: 700, color: submission.status === 'ACCEPTED' ? '#34d399' : '#fbbf24' }}>
                    {submission.testCasesPassed}/{submission.totalTestCases} test cases passed
                  </span>
                </div>
              )}
              <button className="arena-back-btn" onClick={() => navigate('/lobby')}>Back to Lobby</button>
            </div>
          </div>
        </div>
      )}

      {/* ── Top Bar ── */}
      <div className="arena-topbar">
        <span style={{ fontSize: 15, fontWeight: 800, color: '#e2e8f0', letterSpacing: '.03em' }}>⚔ Coding Arena</span>
        <span className="arena-badge-room">{room.roomCode}</span>
        <span className={statusBadgeClass}>{statusLabel}</span>
        {!wsConnected && <span className="arena-warn-badge">⚠ Reconnecting</span>}
        {error && (
          <span style={{ fontSize: 12, color: '#f87171', background: '#2d1414', border: '1px solid #4d1a1a', borderRadius: 5, padding: '3px 10px', display: 'flex', alignItems: 'center', gap: 8 }}>
            {error}
            <button onClick={() => setError(null)} style={{ background: 'none', border: 'none', color: '#f87171', cursor: 'pointer', fontSize: 14 }}>✕</button>
          </span>
        )}
        <div style={{ flex: 1 }} />
        {battleStarted && startTime && !battleEnded && (
          <div className="arena-timer" style={{
            borderColor: timer.urgent ? '#4d1a1a' : timer.warning ? '#4d3209' : '#1e2535',
            background:  timer.urgent ? '#1a0a0a'  : timer.warning ? '#1a1206'  : '#13161f',
          }}>
            <div className="arena-timer-dot" style={{
              background: timer.urgent ? '#f87171' : timer.warning ? '#fbbf24' : '#34d399',
            }} />
            <span className="arena-timer-val" style={{
              color:      timer.urgent ? '#f87171' : timer.warning ? '#fbbf24' : '#e2e8f0',
              fontSize: 18,
              minWidth: 64,
              display: 'inline-block',
              textAlign: 'center',
            }}>
              {timer.display}
            </span>
          </div>
        )}
        <button className="arena-btn-forfeit" onClick={handleLeave}>
          {battleStarted && !battleEnded ? '🏳 Forfeit' : '← Leave'}
        </button>
      </div>

      {/* ── Players Bar ── */}
      <div className="arena-players-bar">
        {/* Me */}
        <div className="arena-pcard arena-pcard-me">
          <div>
            <div className="arena-plabel-me">You</div>
            <div className="arena-pname">{user?.username}</div>
          </div>
          <span className="arena-prating">{myParticipant?.rating || user?.rating}</span>
          <div className="arena-pstatus-me">
            {!battleStarted
              ? <span style={{ color: isReady ? '#34d399' : '#64748b', fontSize: 11, fontWeight: 700 }}>{isReady ? '✓ Ready' : '○ Not Ready'}</span>
              : submission && <span style={{ color: submission.status === 'ACCEPTED' ? '#34d399' : '#fbbf24', fontSize: 11, fontWeight: 700 }}>{submission.testCasesPassed}/{submission.totalTestCases} passed</span>
            }
          </div>
        </div>

        <div className="arena-vs">VS</div>

        {/* Opponent */}
        {opponent ? (
          <div className="arena-pcard arena-pcard-opp">
            <div style={{ textAlign: 'right' }}>
              <div className="arena-plabel-opp">Opponent</div>
              <div className="arena-pname">{opponent.username}</div>
            </div>
            <span className="arena-prating">{opponent.rating}</span>
            <div className="arena-pstatus-opp">
              {!battleStarted
                ? <span style={{ color: opponent.isReady ? '#34d399' : '#64748b', fontSize: 11, fontWeight: 700 }}>{opponent.isReady ? '✓ Ready' : '○ Not Ready'}</span>
                : opponentSubmitted && <span style={{ color: '#fbbf24', fontSize: 11, fontWeight: 700 }}>{opponentPassed} passed</span>
              }
            </div>
          </div>
        ) : (
          <div className="arena-pcard arena-pcard-wait">
            <span className="arena-spinner" />
            <span style={{ fontSize: 13, color: '#64748b' }}>Waiting for opponent...</span>
          </div>
        )}
      </div>

      {/* ── Waiting Room ── */}
      {!battleStarted && (
        <div className="container-fluid mt-3">
          <div className="row" style={{ minHeight: 'calc(100vh - 200px)' }}>
            <div className="col-md-8">
              <div className="arena-card card h-100">
                <div className="card-body d-flex align-items-center justify-content-center flex-column" style={{ gap: 28, padding: 48 }}>
                  {room.participants.length < 2 ? (
                    <>
                      <div style={{ fontSize: 15, fontWeight: 600, color: '#64748b' }}>Share this code with your opponent</div>
                      <div className="arena-big-code">{room.roomCode}</div>
                      <div style={{ fontSize: 13, color: '#2e3a55' }}>Waiting for opponent to join...</div>
                    </>
                  ) : (
                    <>
                      <div style={{ fontSize: 16, fontWeight: 600, color: '#e2e8f0' }}>Both players must be ready to start</div>
                      <div className="d-flex gap-4 align-items-center">
                        <div className={isReady ? 'arena-ready-card-on' : 'arena-ready-card-off'}>
                          <div style={{ fontSize: 14, fontWeight: 700, color: '#e2e8f0', marginBottom: 5 }}>{user?.username}</div>
                          <div style={{ fontSize: 12, fontWeight: 700, color: isReady ? '#34d399' : '#64748b' }}>{isReady ? '✓ Ready' : '○ Waiting'}</div>
                        </div>
                        <div style={{ fontSize: 22, color: '#2e3a55' }}>⚔</div>
                        <div className={opponent?.isReady ? 'arena-ready-card-on' : 'arena-ready-card-off'}>
                          <div style={{ fontSize: 14, fontWeight: 700, color: '#e2e8f0', marginBottom: 5 }}>{opponent?.username}</div>
                          <div style={{ fontSize: 12, fontWeight: 700, color: opponent?.isReady ? '#34d399' : '#64748b' }}>{opponent?.isReady ? '✓ Ready' : '○ Waiting'}</div>
                        </div>
                      </div>
                      <button className={isReady ? 'arena-ready-btn-on' : 'arena-ready-btn-off'} onClick={handleReady}>
                        {isReady ? '✓ Ready! (click to cancel)' : '🎮 Click when Ready'}
                      </button>
                      {allPlayersReady() && (
                        <div className="d-flex align-items-center gap-2" style={{ color: '#34d399', fontSize: 13, fontWeight: 600 }}>
                          <div style={{ width: 8, height: 8, borderRadius: '50%', background: '#34d399' }} />
                          Starting battle...
                        </div>
                      )}
                    </>
                  )}
                </div>
              </div>
            </div>
            <div className="col-md-4">
              <div className="arena-card card h-100" style={{ display: 'flex', flexDirection: 'column', overflow: 'hidden' }}>
                <div className="arena-chat-header">
                  <div className="arena-chat-dot" />
                  <span className="arena-panel-label">Battle Chat</span>
                </div>
                <div style={{ flex: 1, overflow: 'hidden' }}>
                  <ChatBox messages={chatMessages} onSendMessage={handleSendChat} currentUserId={user?.id || ''} disabled={false} />
                </div>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* ── Battle Arena ── */}
      {battleStarted && room.problem && (
        <div style={{ display: 'grid', gridTemplateColumns: '310px 1fr 270px', height: 'calc(100vh - 110px)', overflow: 'hidden' }}>

          {/* Problem panel */}
          <div className="arena-card arena-scroll" style={{ display: 'flex', flexDirection: 'column', overflow: 'hidden', borderRadius: 0, borderTop: 'none', borderBottom: 'none', borderLeft: 'none' }}>
            <div style={{ background: '#13161f', borderBottom: '1px solid #1e2535', padding: '10px 14px', display: 'flex', alignItems: 'center', justifyContent: 'space-between', flexShrink: 0 }}>
              <span className="arena-panel-label">Problem</span>
              <span className={diffClass}>{room.problem.difficulty}</span>
            </div>
            <div className="arena-scroll" style={{ flex: 1, overflowY: 'auto', padding: '14px 16px' }}>
              <div style={{ fontSize: 16, fontWeight: 700, color: '#e2e8f0', marginBottom: 12 }}>{room.problem.title}</div>
              <div className="arena-prob-text"
                dangerouslySetInnerHTML={{ __html: room.problem.description.replace(/\n/g, '<br/>') }} />

              {room.problem.inputFormat && (<>
                <div className="arena-section-label">Input Format</div>
                <pre className="arena-code-block">{room.problem.inputFormat}</pre>
              </>)}
              {room.problem.outputFormat && (<>
                <div className="arena-section-label">Output Format</div>
                <pre className="arena-code-block">{room.problem.outputFormat}</pre>
              </>)}
              {room.problem.constraints && (<>
                <div className="arena-section-label arena-section-label-amber">Constraints</div>
                <pre className="arena-constraints-block">{room.problem.constraints}</pre>
              </>)}
              {room.problem.sampleTestCases?.length > 0 && (<>
                <div className="arena-section-label">Examples</div>
                {room.problem.sampleTestCases.map((tc, idx) => (
                  <div key={tc.id || idx} className="arena-tc-box">
                    <div style={{ fontSize: 9, fontWeight: 700, letterSpacing: '.1em', color: '#64748b', textTransform: 'uppercase', marginBottom: 8 }}>Example {idx + 1}</div>
                    <div className="row g-2">
                      <div className="col-6">
                        <div className="arena-tc-label">Input</div>
                        <pre className="arena-code-block">{tc.input}</pre>
                      </div>
                      <div className="col-6">
                        <div className="arena-tc-label">Output</div>
                        <pre className="arena-code-block">{tc.expectedOutput}</pre>
                      </div>
                    </div>
                  </div>
                ))}
              </>)}
            </div>
          </div>

          {/* Code editor — keep as-is, just wrap in dark container */}
          <div style={{ display: 'flex', flexDirection: 'column', background: '#0b0d12', borderLeft: '1px solid #1e2535', borderRight: '1px solid #1e2535', overflow: 'hidden' }}>
            <div style={{ background: '#13161f', borderBottom: '1px solid #1e2535', padding: '9px 14px', flexShrink: 0 }}>
              <span className="arena-panel-label">Code Editor</span>
            </div>
            {/* CodeEditor fills remaining height */}
            <div style={{ flex: 1, overflow: 'hidden', minHeight: 0 }}>
              <CodeEditor
                code={code}
                language={language}
                onChange={setCode}
                onLanguageChange={handleLanguageChange}
                disabled={battleEnded}
                height="100%"
              />
            </div>
            {/* Submit bar always visible at bottom */}
            <div style={{ background: '#0f1219', borderTop: '1px solid #1e2535', padding: '10px 14px', display: 'flex', gap: 10, flexShrink: 0 }}>
              <button
                className="arena-submit-btn"
                onClick={handleSubmit}
                disabled={submitting || battleEnded || !code.trim()}
              >
                {submitting ? '⏳ Submitting...' : '▶ Submit Code'}
              </button>
              <button
                className="arena-hint-btn"
                onClick={handleRequestHint}
                disabled={hintsUsed >= 3 || requestingHint || battleEnded}
                title={hintsUsed >= 3 ? 'No hints remaining' : `${3 - hintsUsed} hints left`}
              >
                {requestingHint ? '⏳' : `💡 ${3 - hintsUsed}`}
              </button>
            </div>
          </div>

          {/* Right panel */}
          <div style={{ display: 'flex', flexDirection: 'column', overflow: 'hidden', background: '#0f1219' }}>
            {/* Test results */}
            <div style={{ background: '#13161f', borderBottom: '1px solid #1e2535', padding: '10px 14px', flexShrink: 0 }}>
              <span className="arena-panel-label">Test Results</span>
            </div>
            <div style={{ padding: '10px 12px', borderBottom: '1px solid #1e2535', flexShrink: 0 }}>
              <TestResults submission={submission} />
            </div>

            {/* Hint */}
            {currentHint && (
              <div className="arena-hint-card" style={{ margin: '10px 12px', flexShrink: 0 }}>
                <div className="arena-hint-label">💡 Hint {hintsUsed}</div>
                <div style={{ fontSize: 12, color: '#e2e8f0', lineHeight: 1.65 }}>{currentHint}</div>
              </div>
            )}

            {/* Chat */}
            <div style={{ flex: 1, display: 'flex', flexDirection: 'column', overflow: 'hidden', minHeight: 0 }}>
              <div className="arena-chat-header">
                <div className="arena-chat-dot" />
                <span className="arena-panel-label">Battle Chat</span>
              </div>
              <div style={{ flex: 1, overflow: 'hidden' }}>
                <ChatBox messages={chatMessages} onSendMessage={handleSendChat} currentUserId={user?.id || ''} disabled={battleEnded} />
              </div>
            </div>
          </div>

        </div>
      )}
    </div>
  );
}

export default BattleArena;