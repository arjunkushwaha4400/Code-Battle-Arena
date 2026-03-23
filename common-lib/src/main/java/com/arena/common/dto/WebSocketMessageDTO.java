package com.arena.common.dto;

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
public class WebSocketMessageDTO<T> {

    private WebSocketMessageType type;
    private T payload;

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    public static <T> WebSocketMessageDTO<T> of(WebSocketMessageType type, T payload) {
        return WebSocketMessageDTO.<T>builder()
                .type(type)
                .payload(payload)
                .build();
    }
}