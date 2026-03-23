package com.arena.battle.mapper;

import com.arena.battle.dto.BattleRoomResponse;
import com.arena.battle.dto.ParticipantResponse;
import com.arena.battle.entity.BattleRoom;
import com.arena.battle.entity.RoomParticipant;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = {ProblemMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RoomMapper {

    @Mapping(target = "problem", source = "problem")
    @Mapping(target = "participants", source = "participants")
    BattleRoomResponse toResponse(BattleRoom room);

    List<BattleRoomResponse> toResponseList(List<BattleRoom> rooms);

    ParticipantResponse toParticipantResponse(RoomParticipant participant);

    List<ParticipantResponse> toParticipantResponseList(List<RoomParticipant> participants);
}