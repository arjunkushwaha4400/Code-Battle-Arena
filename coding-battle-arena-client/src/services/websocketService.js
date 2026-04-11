// services/websocketService.js

import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { authService } from './authService';

class WebSocketService {
  constructor() {
    this.client = null;
    this.connected = false;
    this.roomId = null;
    this.messageHandlers = new Map();
    this.subscriptions = [];
    this.roomSubscriptions = [];
    this.reconnecting = false;
    // ✅ Track recent chat messages to deduplicate across topics
    this.recentChatKeys = new Set();
  }

  async connect() {
    if (this.connected && this.client?.connected) {
      console.log('WebSocket already connected');
      return Promise.resolve();
    }

    if (this.reconnecting) {
      return new Promise((resolve) => {
        const checkConnection = setInterval(() => {
          if (this.connected) {
            clearInterval(checkConnection);
            resolve();
          }
        }, 100);
      });
    }

    this.reconnecting = true;

    const token = await authService.getAccessToken();
    if (!token) {
      this.reconnecting = false;
      throw new Error('Not authenticated');
    }

    return new Promise((resolve, reject) => {
      if (this.client) {
        try {
          this.client.deactivate();
        } catch (e) {
          console.log('Error deactivating old client:', e);
        }
      }

      this.client = new Client({
        webSocketFactory: () => new SockJS('http://localhost:8082/ws/battle'),
        connectHeaders: {
          Authorization: `Bearer ${token}`,
        },
        debug: (str) => {
          if (str.includes('CONNECTED') || str.includes('SUBSCRIBED') || str.includes('MESSAGE')) {
            console.log('STOMP:', str);
          }
        },
        reconnectDelay: 3000,
        heartbeatIncoming: 4000,
        heartbeatOutgoing: 4000,
        onConnect: () => {
          console.log('WebSocket connected successfully');
          this.connected = true;
          this.reconnecting = false;
          this.subscribeToUserQueue();
          resolve();
        },
        onDisconnect: () => {
          console.log('WebSocket disconnected');
          this.connected = false;
          this.subscriptions = [];
          this.roomSubscriptions = [];
          this.recentChatKeys.clear(); // ✅ clear dedup cache on disconnect
        },
        onStompError: (frame) => {
          console.error('STOMP error:', frame.headers['message']);
          this.reconnecting = false;
          reject(new Error(frame.headers['message']));
        },
        onWebSocketError: (error) => {
          console.error('WebSocket error:', error);
          this.reconnecting = false;
        },
      });

      this.client.activate();
    });
  }

  disconnect() {
    console.log('Disconnecting WebSocket...');
    if (this.client) {
      try {
        this.client.deactivate();
      } catch (e) {
        console.log('Error during disconnect:', e);
      }
      this.client = null;
      this.connected = false;
      this.roomId = null;
      this.messageHandlers.clear();
      this.subscriptions = [];
      this.roomSubscriptions = [];
      this.recentChatKeys.clear();
    }
  }

  isConnected() {
    return this.connected && this.client?.connected;
  }

  subscribeToUserQueue() {
    if (!this.client || !this.connected) {
      console.log('Cannot subscribe - not connected');
      return;
    }

    const userId = authService.getUserId();
    if (!userId) {
      console.log('Cannot subscribe - no user ID');
      return;
    }

    console.log('Subscribing to user queue:', userId);

    const subscription = this.client.subscribe(
      '/user/queue/events',
      (message) => {
        console.log('Received user queue message:', message.body);
        this.handleMessage(message);
      }
    );
    this.subscriptions.push(subscription);
  }

  subscribeToRoom(roomId) {
    if (!this.client || !this.connected) {
      console.error('Cannot subscribe to room - not connected');
      return;
    }

    this.unsubscribeFromRoom();
    this.roomId = roomId;
    this.recentChatKeys.clear(); // ✅ clear dedup cache when switching rooms
    console.log('Subscribing to room:', roomId);

    // ✅ Room topic: handles ALL message types including CHAT_MESSAGE
    // Backend only publishes to this topic (confirmed from logs)
    // Deduplication is handled via recentChatKeys so if backend ever
    // publishes to both topics, we won't show duplicates
    const roomSub = this.client.subscribe(`/topic/room/${roomId}`, (message) => {
      console.log('Received room message:', message.body);
      try {
        const parsed = JSON.parse(message.body);

        // ✅ For chat messages: deduplicate using userId+timestamp key
        // This handles the case where backend publishes to both topics
        if (parsed.type === 'CHAT_MESSAGE') {
          const chatKey = this.buildChatKey(parsed.payload);
          if (this.recentChatKeys.has(chatKey)) {
            console.log('Duplicate chat message skipped (room topic):', chatKey);
            return;
          }
          this.recentChatKeys.add(chatKey);
          // ✅ Auto-expire key after 5s so memory doesn't grow forever
          setTimeout(() => this.recentChatKeys.delete(chatKey), 5000);
        }

        this.handleMessage(message);
      } catch (e) {
        console.error('Failed to parse room message:', e);
      }
    });
    this.roomSubscriptions.push(roomSub);

    // ✅ Also subscribe to /chat topic — deduplicate with same key
    // If backend publishes here too, the key will already exist → skipped
    const chatSub = this.client.subscribe(`/topic/room/${roomId}/chat`, (message) => {
      console.log('Received chat topic message:', message.body);
      try {
        const parsed = JSON.parse(message.body);

        if (parsed.type === 'CHAT_MESSAGE') {
          const chatKey = this.buildChatKey(parsed.payload);
          if (this.recentChatKeys.has(chatKey)) {
            console.log('Duplicate chat message skipped (/chat topic):', chatKey);
            return;
          }
          this.recentChatKeys.add(chatKey);
          setTimeout(() => this.recentChatKeys.delete(chatKey), 5000);
        }

        this.handleMessage(message);
      } catch (e) {
        console.error('Failed to parse chat message:', e);
      }
    });
    this.roomSubscriptions.push(chatSub);
  }

  // ✅ Build a unique key for a chat message using userId + timestamp
  // Works even when backend sends no id field
  buildChatKey(payload) {
    if (!payload) return `unknown-${Date.now()}`;
    const userId = payload.userId || 'unknown';
    const timestamp = payload.timestamp || Date.now();
    const msg = (payload.message || '').substring(0, 20); // first 20 chars
    return `${userId}-${timestamp}-${msg}`;
  }

  unsubscribeFromRoom() {
    this.roomSubscriptions.forEach(sub => {
      try { sub.unsubscribe(); } catch (e) {}
    });
    this.roomSubscriptions = [];
    this.roomId = null;
  }

  handleMessage(message) {
    try {
      const wsMessage = JSON.parse(message.body);
      console.log('Processing message type:', wsMessage.type);

      const handlers = this.messageHandlers.get(wsMessage.type) || [];
      console.log(`Found ${handlers.length} handlers for type ${wsMessage.type}`);
      handlers.forEach((handler) => {
        try {
          handler(wsMessage);
        } catch (e) {
          console.error('Error in message handler:', e);
        }
      });

      const globalHandlers = this.messageHandlers.get('*') || [];
      globalHandlers.forEach((handler) => {
        try {
          handler(wsMessage);
        } catch (e) {
          console.error('Error in global handler:', e);
        }
      });
    } catch (error) {
      console.error('Failed to parse WebSocket message:', error, message.body);
    }
  }

  onMessage(type, handler) {
    console.log('Registering handler for:', type);
    const handlers = this.messageHandlers.get(type) || [];
    handlers.push(handler);
    this.messageHandlers.set(type, handlers);

    return () => {
      const currentHandlers = this.messageHandlers.get(type) || [];
      const index = currentHandlers.indexOf(handler);
      if (index > -1) {
        currentHandlers.splice(index, 1);
        this.messageHandlers.set(type, currentHandlers);
      }
    };
  }

  clearHandlers(type) {
    if (type) {
      this.messageHandlers.delete(type);
    } else {
      this.messageHandlers.clear();
    }
  }

  sendJoinRoom(roomCode) {
    this.send('/app/room/join', { roomCode });
  }

  sendLeaveRoom(roomId) {
    this.send('/app/room/leave', roomId);
  }

  sendReady(roomId, isReady) {
    console.log('Sending ready:', roomId, isReady);
    this.send('/app/room/ready', { roomId, isReady });
  }

  sendSubmitCode(roomId, code, language) {
    this.send('/app/room/submit', { roomId, code, language });
  }

  sendHintRequest(roomId, currentCode, hintLevel) {
    this.send('/app/room/hint', { roomId, currentCode, hintLevel });
  }

  sendChatMessage(roomId, message) {
    this.send('/app/room/chat', { roomId, message });
  }

  send(destination, body) {
    if (!this.client || !this.connected) {
      console.error('WebSocket not connected, cannot send to:', destination);
      return false;
    }

    console.log('Sending to', destination, body);
    this.client.publish({
      destination,
      body: JSON.stringify(body),
    });
    return true;
  }
}

export const websocketService = new WebSocketService();