package com.arena.battle.dto.websocket;

import com.arena.common.enums.ProgrammingLanguage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WsSubmitMessage {
    private UUID roomId;
    private String code;
    private ProgrammingLanguage language;
}