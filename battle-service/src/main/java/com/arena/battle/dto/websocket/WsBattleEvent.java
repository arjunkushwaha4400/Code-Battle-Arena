package com.arena.battle.dto.websocket;

import com.arena.common.enums.WebSocketMessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WsBattleEvent<T> {

    private WebSocketMessageType type;
    private T payload;

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    public static <T> WsBattleEvent<T> of(WebSocketMessageType type, T payload) {
        return WsBattleEvent.<T>builder()
                .type(type)
                .payload(payload)
                .timestamp(LocalDateTime.now())
                .build();
    }
}