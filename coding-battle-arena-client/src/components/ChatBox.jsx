// components/ChatBox.jsx
import React, { useState, useRef, useEffect } from 'react';

function ChatBox({ messages, onSendMessage, currentUserId, disabled = false }) {
  const [input, setInput] = useState('');
  const messagesEndRef = useRef(null);

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages]);

  const handleSubmit = (e) => {
    e.preventDefault();
    if (input.trim() && !disabled) {
      onSendMessage(input.trim());
      setInput('');
    }
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', height: '100%', overflow: 'hidden', background: '#0f1219' }}>
      {/* Messages */}
      <div style={{
        flex: 1, overflowY: 'auto', padding: '10px 12px',
        display: 'flex', flexDirection: 'column', gap: 8,
        scrollbarWidth: 'thin', scrollbarColor: '#1e2535 transparent',
      }}>
        {messages.length === 0 ? (
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', height: '100%', minHeight: 60, fontSize: 12, color: '#2e3a55', fontStyle: 'italic' }}>
            No messages yet
          </div>
        ) : (
          messages.map((msg, index) => {
            const isMe = msg.userId === currentUserId;
            return (
              <div key={msg.id || index} style={{ display: 'flex', flexDirection: 'column', alignItems: isMe ? 'flex-end' : 'flex-start', gap: 2 }}>
                <span style={{ fontSize: 10, color: '#475569', fontWeight: 600, letterSpacing: '.04em' }}>
                  {msg.username}
                </span>
                <div style={{
                  background: isMe ? '#1a2744' : '#13161f',
                  color: isMe ? '#93c5fd' : '#e2e8f0',
                  border: `1px solid ${isMe ? '#1d3a7a' : '#1e2535'}`,
                  borderRadius: isMe ? '10px 10px 2px 10px' : '10px 10px 10px 2px',
                  padding: '7px 11px',
                  fontSize: 13,
                  lineHeight: 1.5,
                  maxWidth: '85%',
                  wordBreak: 'break-word',
                }}>
                  {msg.message}
                </div>
              </div>
            );
          })
        )}
        <div ref={messagesEndRef} />
      </div>

      {/* Input */}
      <form onSubmit={handleSubmit} style={{
        display: 'flex', gap: 6, padding: '8px 10px',
        borderTop: '1px solid #1e2535', flexShrink: 0,
        background: '#13161f',
      }}>
        <input
          type="text"
          value={input}
          onChange={e => setInput(e.target.value)}
          placeholder={disabled ? 'Chat disabled' : 'Type a message...'}
          disabled={disabled}
          style={{
            flex: 1, background: '#0b0d12', border: '1px solid #1e2535',
            color: '#e2e8f0', borderRadius: 5, padding: '6px 10px',
            fontSize: 12, outline: 'none', fontFamily: 'inherit',
            opacity: disabled ? 0.5 : 1,
          }}
        />
        <button type="submit" disabled={disabled || !input.trim()}
          style={{
            background: (disabled || !input.trim()) ? '#1a1e2a' : '#1a2744',
            color: (disabled || !input.trim()) ? '#475569' : '#60a5fa',
            border: `1px solid ${(disabled || !input.trim()) ? '#1e2535' : '#1d3a7a'}`,
            borderRadius: 5, padding: '6px 13px', fontSize: 12, fontWeight: 700,
            cursor: (disabled || !input.trim()) ? 'not-allowed' : 'pointer',
            letterSpacing: '.04em', whiteSpace: 'nowrap', transition: 'all .15s',
          }}>
          Send
        </button>
      </form>
    </div>
  );
}

export default ChatBox;