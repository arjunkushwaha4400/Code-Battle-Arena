package com.arena.common.enums;

public enum WebSocketMessageType {
    // Connection events
    JOIN_ROOM,
    LEAVE_ROOM,
    PLAYER_JOINED,
    PLAYER_LEFT,
    PLAYER_READY,

    // Battle events
    BATTLE_START,
    BATTLE_END,
    COUNTDOWN,

    // Submission events
    CODE_SUBMITTED,
    SUBMISSION_RESULT,
    OPPONENT_SUBMITTED,
    OPPONENT_RESULT,

    // AI events
    HINT_REQUEST,
    HINT_RESPONSE,

    // Chat events
    CHAT_MESSAGE,

    // System events
    ERROR,
    HEARTBEAT,
    MATCH_FOUND
}